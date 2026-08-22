# HiPeople - Android Development Guide

## Project Overview

HiPeople is an Android app for finding and connecting with hosts in different cities. The app follows modern Android architecture patterns with Kotlin Multiplatform (KMP) compatibility in mind.

## Architecture

### Module Structure

```
:app                    - Application module, Koin setup, NavHost wiring
:core:domain            - Pure Kotlin (java-library): domain models, Result<T,E>, data source interfaces
:core:data              - Data layer: Ktor HttpClient, DTOs, mappers, data source implementations
:core:presentation      - Presentation utilities: UiText, ObserveAsEvents, DataError.toUiText()
:core:ui                - UI resources: theme, colors, typography, drawables
:feature:hostme         - Host search feature: routes, nav graph, screens, ViewModels, UI models
```

### Technology Stack

**Dependency Injection:** Koin
- `coreDataModule` in `:core:data`
- `hostmePresentationModule` in `:feature:hostme`
- Initialized in `HiPeople.kt` with `startKoin{}`

**Networking:** Ktor
- `HttpClientFactory.create()` in `:core:data`
- OkHttp engine with JSON content negotiation
- Logging only in debug builds
- Base URL: `BuildConfig.BASE_URL` (`https://hipipl.com`), set per build type in `core/data/build.gradle.kts`
- Calls the `/api/v1/*` REST API (`{ success, data, error }` envelope) — see [API Documentation & Web Reference](#api-documentation--web-reference) below

**UI:** Jetpack Compose
- Material 3 design system
- Type-safe Navigation Compose
- Coil for image loading
- Lifecycle-aware state collection

**Error Handling:** Typed Result wrapper
- `Result<T, E>` with `onSuccess`/`onFailure` extensions
- `DataError.Network` for network errors
- `DataError.Local` for local errors
- Mapped to `UiText` in presentation layer

## Architecture Patterns

### MVI (Model-View-Intent)

All screens follow MVI pattern:

```kotlin
// State - single source of truth
data class ScreenState(
    val isLoading: Boolean = false,
    val data: List<ItemUi> = emptyList(),
    val error: UiText? = null
)

// Actions - user interactions
sealed interface ScreenAction {
    data class OnItemClick(val id: Int) : ScreenAction
    data object OnRetry : ScreenAction
}

// Events - one-time side effects (navigation, snackbars)
sealed interface ScreenEvent {
    data class NavigateToDetails(val id: Int) : ScreenEvent
}

// ViewModel
class ScreenViewModel(
    private val dataSource: RemoteDataSource,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    
    private val _state = MutableStateFlow(ScreenState())
    val state: StateFlow<ScreenState> = _state
    
    private val _events = Channel<ScreenEvent>()
    val events: Flow<ScreenEvent> = _events.receiveAsFlow()
    
    fun onAction(action: ScreenAction) {
        when (action) {
            is ScreenAction.OnItemClick -> {
                viewModelScope.launch {
                    _events.send(ScreenEvent.NavigateToDetails(action.id))
                }
            }
        }
    }
}
```

### Root + Screen Composable Split

Each screen has two composables:

1. **Root composable** - handles ViewModel and events:
```kotlin
@Composable
fun Screen(
    onNavigate: (Int) -> Unit,
    viewModel: ScreenViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    
    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is ScreenEvent.NavigateToDetails -> onNavigate(event.id)
        }
    }
    
    Screen(state = state, onAction = viewModel::onAction)
}
```

2. **Screen composable** - pure UI:
```kotlin
@Composable
fun Screen(
    state: ScreenState,
    onAction: (ScreenAction) -> Unit
) {
    // Pure UI rendering
}
```

### UI Models

All formatting happens in mappers, composables receive pre-formatted UI models:

```kotlin
// Domain model
data class HostUser(
    val name: String,
    val age: Int,
    val averageRating: Float,
    val totalReviews: Int,
    // ...
)

// UI model - all strings pre-formatted
data class HostDetailsUi(
    val nameWithAge: String,        // "Alex (30 лет)"
    val ratingText: String,         // "4.5* (10)"
    val languagesText: String,      // "English: B2, Russian: Native"
    val showDonation: Boolean,      // derived from donate > 0
    // ...
)

// Mapper
fun HostUser.toHostDetailsUi(): HostDetailsUi {
    val ageText = if (age > 0) " ($age лет)" else ""
    val nameWithAge = "$name$ageText"
    val ratingText = "$averageRating* ($totalReviews)"
    // ...
}
```

## Data Layer

### Repository Pattern

Following the data-layer skill:
- **Data sources** wrap a single remote API (not repositories)
- **Repositories** combine multiple sources (network + cache)
- Current implementation: only remote data sources (no caching yet)

### Ktor Data Source Example

v1 endpoints return a `{ success, data, error }` envelope; data sources call the `getV1`/`postV1`/`deleteV1` helpers (`core/data/SafeCall.kt`), which auto-unwrap `ApiResponse<T>` via `ApiResponse.unwrap()` (`core/data/dto/ApiResponse.kt`). No `token` query param — auth is cookie/JWT-based (real auth pending, see TODOs).

```kotlin
class KtorHostDataSource(
    private val httpClient: HttpClient
) : HostRemoteDataSource {

    override suspend fun getHost(
        hostId: Int,
        userId: Int?,
    ): Result<HostUser, DataError.Network> {
        val queryParams = mutableMapOf<String, Any>()
        userId?.let { queryParams["user"] = it }

        return httpClient.getV1<HostUserDto>(
            route = "api/v1/hosts/$hostId",
            queryParameters = queryParams
        ).let { result ->
            when (result) {
                is Result.Success -> result.data.toHostUser()
                    ?.let { Result.Success(it) }
                    ?: Result.Error(DataError.Network.SERIALIZATION)
                is Result.Error -> result
            }
        }
    }
}
```

### Safe API Calls

`get`/`post`/`delete` wrap raw responses; `getV1`/`postV1`/`deleteV1` additionally unwrap the v1 envelope:

```kotlin
suspend inline fun <reified T> safeCall(
    execute: () -> HttpResponse
): Result<T, DataError.Network> {
    val response = try {
        execute()
    } catch (e: UnknownHostException) {
        return Result.Error(DataError.Network.NO_INTERNET)
    } catch (e: SerializationException) {
        return Result.Error(DataError.Network.SERIALIZATION)
    } catch (e: Exception) {
        if (e is CancellationException) throw e
        return Result.Error(DataError.Network.UNKNOWN)
    }
    return responseToResult(response)
}

suspend inline fun <reified Response : Any> HttpClient.getV1(
    route: String,
    queryParameters: Map<String, Any?> = mapOf()
): Result<Response, DataError.Network> {
    val envelopeResult = get<ApiResponse<Response>>(route, queryParameters)
    return when (envelopeResult) {
        is Result.Success -> envelopeResult.data.unwrap()
        is Result.Error -> envelopeResult
    }
}
```

## Navigation

Type-safe navigation with serializable route objects:

```kotlin
// Route definitions in feature module
@Serializable
data object LocationSearchRoute

@Serializable
data class HostListByLocationRoute(
    val locationId: Int,
    val locationName: String,
    val locationType: String   // "city" | "country" | "region" — picks city_id/country_id/region_id for the v1 hosts query
)

@Serializable
data class HostDetailsRoute(
    val hostId: Int,
    val userId: Int
)

// NavHost in :app module
NavHost(
    navController = navController,
    startDestination = LocationSearchRoute
) {
    composable<LocationSearchRoute> {
        LocationSearchScreen(
            onNavigateToHostList = { id, name ->
                navController.navigate(HostListByLocationRoute(id, name))
            }
        )
    }
    composable<HostListByLocationRoute> {
        HostListByLocationScreen(
            onNavigateToHostDetails = { hostId, userId ->
                navController.navigate(HostDetailsRoute(hostId, userId))
            }
        )
    }
    composable<HostDetailsRoute> {
        HostDetailsScreen()
    }
}
```

## Compose Best Practices

1. **State Collection**: Use `collectAsStateWithLifecycle()` instead of `collectAsState()`

2. **Colors**: Use theme colors from `:core:ui` instead of hardcoded values
   ```kotlin
   import me.alexy.hipipl.core.ui.Yellow
   import me.alexy.hipipl.core.ui.LightGreen
   import me.alexy.hipipl.core.ui.LightPeach
   import me.alexy.hipipl.core.ui.Blue
   ```

3. **Content Descriptions**: Always provide for accessibility
   ```kotlin
   AsyncImage(
       contentDescription = stringResource(R.string.host_photo_description)
   )
   ```

4. **LazyColumn Items**: Use keyed items
   ```kotlin
   items(items = hosts, key = { it.id }) { host ->
       HostItem(host)
   }
   ```

5. **Remember Keys**: Use keyed remember for derived state
   ```kotlin
   val intent = remember(type, value) {
       createIntent(type, value)
   }
   ```

## Common Commands

### Build
```bash
./gradlew assembleDebug        # Build debug APK
./gradlew assembleRelease      # Build release APK
./gradlew clean                # Clean build artifacts
```

### Dependency Updates
```bash
./gradlew dependencyUpdates    # Check for dependency updates
```

## Current Limitations & TODOs

1. **Authentication**: `/api/v1` migration is done, but real auth isn't — `KtorHostDataSource.TEMP_USER_ID = 1` stands in for a logged-in user; no JWT/session/DataStore yet (see `plans/IMPLEMENTATION_PLAN.md` Phase 1)
2. **Feature parity**: only read-only host browsing (search → list → details + reviews) is implemented; profile/settings, favorites, notifications, write-reviews, map, host CRUD, and localization are all unstarted (`plans/IMPLEMENTATION_PLAN.md` Phases 2-9)
3. **Offline Support**: No local caching yet (Room integration pending, planned alongside Favorites)
4. **Testing**: Still JUnit4 stubs with no real test bodies; JUnit5/Turbine/AssertK migration from `plans/REFACTORING_PLAN.md` Phase 6 hasn't happened
5. **Compose**: no `@Preview` composables anywhere yet

## Adding a New Feature

1. **Create domain models** in `:core:domain`
2. **Create data source interface** in `:core:domain`
3. **Implement data source** in `:core:data` with Ktor
4. **Create DTOs and mappers** in `:core:data`
5. **Register in Koin** (`coreDataModule`)
6. **Create feature module** or add to existing
7. **Create MVI components**: State, Action, Event, ViewModel
8. **Create UI models and mappers**
9. **Create composables**: Root + Screen split
10. **Add to navigation**: Route + NavGraphBuilder extension
11. **Register in Koin** (`featurePresentationModule`)

## API Documentation & Web Reference

Sibling repos hold the source of truth for the `/api/v1` backend the app talks to:

- **`../hipeople-doc/docs/`** — API docs (mostly Russian). `Hi People/Methods/README.md` is the index (base URL, auth/JWT flow, response envelope, HTTP status codes); each endpoint has its own file in `Hi People/Methods/` (e.g. `GET_hosts_ID.md`, `GET_users_ID_reviews.md`, `GET_geo_search.md`). `Hi People/Screens/` documents app screens; `Hi People/АПИ/` has additional API notes.
- **`../front/`** — the production web client (vanilla JS PWA) consuming the same API; it has full feature parity (auth, profile, favorites, messaging, map, etc.) and is the **reference implementation** when porting a feature to Android. Key files: `couch/js/site-api.js` (`SiteApiClient` — canonical request shapes per endpoint), `couch/js/site-auth.js` (auth/JWT flow), `couch/search_hosts.html` (geo search + host list), `couch/host.html` (host details + reviews — see `plans/fix-host-reviews-wrong-host.md` for a worked example of cross-checking Android against this file). Its own `CLAUDE.md` documents its architecture.

When implementing a new `/api/v1` endpoint or feature, check the matching `hipeople-doc` method doc for the contract and the matching `front` page/JS module for how the web app actually calls it — DTOs here should match real API responses, not just the docs (see field-by-field deltas already found in `plans/IMPLEMENTATION_PLAN.md`).

## Resources

- **Android Skills**: See skills loaded in the project (android-compose-ui, android-data-layer, android-di-koin, etc.)

## Notes

- Keep the MVI pattern consistent across all screens
- Always pre-format strings in UI models, never in composables
- Use typed errors (UiText) for user-facing messages
- Follow the data-layer skill for new data sources
- Use Koin constructor injection (no manual `get()` calls in ViewModels)
