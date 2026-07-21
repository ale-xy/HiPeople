package me.alexy.hipipl.feature.hostme.ui.hostsearch

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import me.alexy.hipipl.core.designsystem.GenderAccent
import me.alexy.hipipl.core.designsystem.HiPeopleTheme
import me.alexy.hipipl.core.designsystem.components.AmenityChipRow
import me.alexy.hipipl.core.designsystem.components.CompassArrowIcon
import me.alexy.hipipl.core.designsystem.components.NameWithAgeText
import me.alexy.hipipl.core.designsystem.components.ReferencesText
import me.alexy.hipipl.core.designsystem.components.ResultCard
import me.alexy.hipipl.core.designsystem.toColor
import me.alexy.hipipl.feature.hostitem.R

@Composable
internal fun HostResultCard(
    host: HostCardUi,
    onAction: (HostsSearchAction) -> Unit,
    modifier: Modifier = Modifier
) {
    ResultCard(
        onClick = { onAction(HostsSearchAction.OnHostClick(host.hostId, host.userId)) },
        accentColor = host.genderAccent.toColor(),
        modifier = modifier
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                NameWithAgeText(name = host.name, ageText = host.ageText)
                ReferencesText(referenceCount = host.referenceCount, scoreText = host.referenceScoreText)
                AmenityChipRow(
                    separateRoom = host.separateRoom,
                    kidsAllowed = host.kidsAllowed,
                    petsAtHome = host.petsAtHome
                )
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    if (host.hasDistance) {
                        Text(
                            text = "${host.distanceValueText} ${stringResource(R.string.km)}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        CompassArrowIcon(degrees = host.directionDegrees)
                    }
                    Text(
                        text = host.city,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            if (host.photoUrl != null) {
                AsyncImage(
                    model = host.photoUrl,
                    contentDescription = stringResource(R.string.host_photo_description),
                    modifier = Modifier
                        .size(96.dp)
                        .clip(RoundedCornerShape(14.dp)),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}

@Preview
@Composable
private fun HostResultCardPreview() {
    HiPeopleTheme {
        HostResultCard(
            host = HostCardUi(
                hostId = 1,
                userId = 1,
                photoUrl = null,
                name = "Anna",
                ageText = " (28 лет)",
                referenceCount = 57,
                referenceScoreText = "9.4",
                separateRoom = true,
                kidsAllowed = true,
                petsAtHome = false,
                hasDistance = true,
                distanceValueText = "3.2",
                directionDegrees = 45f,
                city = "Moscow",
                genderAccent = GenderAccent.FEMALE,
            ),
            onAction = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}
