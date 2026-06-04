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
- Base URL: `https://hipipl.com` (legacy PHP API)

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

```kotlin
class KtorHostDataSource(
    private val client: HttpClient
) : HostRemoteDataSource {
    
    override suspend fun getHost(
        hostId: Int,
        userId: Int,
        token: String
    ): Result<HostUser, DataError.Network> {
        return safeCall {
            client.get(
                urlString = constructRoute(
                    BASE_URL,
                    "/api.php",
                    "action" to "getHost",
                    "hostId" to hostId.toString(),
                    "userId" to userId.toString(),
                    "token" to token
                )
            )
        }.map { response: HostUserDto ->
            response.toHostUser()
        }
    }
}
```

### Safe API Calls

```kotlin
suspend inline fun <reified T> safeCall(
    execute: () -> HttpResponse
): Result<T, DataError.Network> {
    return try {
        val response = execute()
        responseToResult(response)
    } catch (e: UnresolvedAddressException) {
        Result.Error(DataError.Network.NO_INTERNET)
    } catch (e: SerializationException) {
        Result.Error(DataError.Network.SERIALIZATION)
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
    val locationName: String
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

### Testing
```bash
./gradlew test                 # Run unit tests
./gradlew connectedAndroidTest # Run instrumentation tests
```

### Dependency Updates
```bash
./gradlew dependencyUpdates    # Check for dependency updates
```

## Current Limitations & TODOs

1. **Authentication**: Hardcoded `userId=1, token="12345"` (see TODOs in data sources)
2. **API Migration**: Still using legacy `/api.php` endpoint (v1 API pending - see IMPLEMENTATION_PLAN.md)
3. **Offline Support**: No local caching yet (Room integration pending)
4. **Testing**: Unit tests structure in place but minimal coverage
5. **Build Config**: BASE_URL should be BuildConfig field for debug/release variants

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

## Resources

- **Android Skills**: See skills loaded in the project (android-compose-ui, android-data-layer, android-di-koin, etc.)
- **REFACTORING_PLAN.md**: Complete refactoring history (Hilt→Koin, Retrofit→Ktor)
- **IMPLEMENTATION_PLAN.md**: Future feature roadmap (v1 API, auth, favorites, etc.)

## Notes

- Keep the MVI pattern consistent across all screens
- Always pre-format strings in UI models, never in composables
- Use typed errors (UiText) for user-facing messages
- Follow the data-layer skill for new data sources
- Use Koin constructor injection (no manual `get()` calls in ViewModels)
