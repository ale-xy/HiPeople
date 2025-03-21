package me.alexy.hipipl.feature.hostme.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.hippl.model.ContactType
import com.hippl.model.Gender
import com.hippl.model.HostDetails
import com.hippl.model.HostUser
import com.hippl.model.UserLanguage
import me.alexy.hipipl.core.ui.LightGreen
import me.alexy.hipipl.feature.hostitem.R
import java.time.LocalDateTime

@Composable
fun HostListByLocationScreen(
    modifier: Modifier = Modifier,
    viewModel: HostListForLocationViewModel = hiltViewModel(),
    onNavigateToHostDetails: (Int) -> Unit
) {
    val uiState = viewModel.uiState.collectAsState()

    HostListByLocationScreen(
        modifier = modifier,
        locationName = viewModel.locationName,
        uiState = uiState.value,
        onNavigateToHostDetails = onNavigateToHostDetails
    )
}

@Composable
fun HostListByLocationScreen(
    locationName: String,
    modifier: Modifier = Modifier,
    uiState: HostListUiState,
    onNavigateToHostDetails: (Int) -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(32.dp)
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineMedium,
            text = stringResource(R.string.host_list_in_city, locationName)
        )

        when(uiState) {
            is HostListUiState.Loading -> {
                Box(Modifier.fillMaxSize()) {
                    CircularProgressIndicator(Modifier.align(Alignment.Center))
                }
            }
            is HostListUiState.Error -> {
                Box(Modifier.fillMaxSize()) {
                    Text(uiState.error)
                }
            }
            is HostListUiState.Success -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(
                        count = uiState.list.size,
                        key = { index -> uiState.list[index].host.hostId },
                        itemContent = { index ->
                            HostListItem(
                                uiState.list[index],
                                onNavigateToHostDetails
                            )
                        }
                    )
                }
            }
        }
    }
}


@Composable
fun HostListItem(
    hostUser: HostUser,
    onNavigateToHostDetails: (Int) -> Unit,
) {
    with(hostUser) {
        Row(
            modifier = Modifier
                .clickable { onNavigateToHostDetails(host.hostId) }
                .fillMaxWidth()
                .background(color = LightGreen, shape = RoundedCornerShape(12.dp))
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val placeholder = painterResource(me.alexy.hipipl.core.ui.R.drawable.avatar)

            AsyncImage(
                modifier = Modifier.size(120.dp),
                model = photos.firstOrNull(),
                alignment = Alignment.Center,
                contentScale = ContentScale.Crop,
                placeholder = placeholder,
                error = placeholder,
                fallback = placeholder,
                contentDescription = null
            )

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                val ageText = if (age > 0) stringResource(R.string.age_format, age) else ""
                val nameText = "$name $averageRating* ($totalReviews)" +
                        if (ageText.isNotBlank()) ", $ageText" else ""

                Text(
                    style = MaterialTheme.typography.titleMedium,
                    text = nameText
                )

                val distanceText = if (host.dist > 0) {
                    "(${stringResource(R.string.distance_km_format, host.dist)}" +
                            if (host.direction.isNotBlank()) {
                                " ${host.direction}"
                            } else {
                                ""
                            } + ")"
                } else {
                    ""
                }

                Text(
                    style = MaterialTheme.typography.bodyLarge,
                    text = listOf(host.city, distanceText).joinToString(" ")
                )

                Text(
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    text = host.text.replace("\n\n", "\n")
                )
            }
        }
    }
}

@Preview
@Composable
fun HostListItemPreview() {
    MaterialTheme {
        HostListItem(
            onNavigateToHostDetails = {},
            hostUser = HostUser(
                userId = 1,
                name = "Alex",
                gender = Gender.MALE,
                age = 30,
                description = "Some description",
                totalReviews = 10,
                contacts = mapOf(ContactType.TELEGRAM to "@telegram_id"),
                averageRating = 4.5f,
                photos = listOf("https://example.com/image.jpg"),
                donate = 0,
                userLanguages = listOf(UserLanguage("en", "English", 100)),
                host = HostDetails(
                    hostId = 1,
                    text = "About me text",
                    correct = 1,
                    city = "City",
                    dist = 2,
                    direction = "N",
                    date = LocalDateTime.now(),
                    separateRoom = true,
                    allowKids = false,
                    gender = Gender.MALE
                )
            )
        )
    }
}
