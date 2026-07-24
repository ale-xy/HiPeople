package me.alexy.hipipl.feature.hostme.ui.hostdetails

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Female
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Male
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import me.alexy.hipipl.core.designsystem.AppColors
import me.alexy.hipipl.core.designsystem.GenderAccent
import me.alexy.hipipl.core.designsystem.HiPeopleTheme
import me.alexy.hipipl.core.designsystem.components.NameWithAgeText
import me.alexy.hipipl.core.designsystem.components.PillChip
import me.alexy.hipipl.core.designsystem.components.ReferencesText
import me.alexy.hipipl.core.designsystem.md_theme_light_outline
import me.alexy.hipipl.core.designsystem.toColor
import me.alexy.hipipl.core.domain.ActivityStatus
import me.alexy.hipipl.core.presentation.UiText
import me.alexy.hipipl.core.presentation.asString
import me.alexy.hipipl.feature.hostitem.R

@Composable
fun HostHeaderSection(
    name: String,
    ageText: String,
    genderAccent: GenderAccent,
    totalReviews: Int,
    ratingValueText: String?,
    cityText: String,
    onOpenMap: (preferGoogleMaps: Boolean) -> Unit,
    modifier: Modifier = Modifier,
    lastActivityText: UiText? = null,
    lastActivityStatus: ActivityStatus? = null,
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            NameWithAgeText(name = name, ageText = ageText)
            val genderIcon = when (genderAccent) {
                GenderAccent.MALE -> Icons.Default.Male
                GenderAccent.FEMALE -> Icons.Default.Female
                GenderAccent.SEVERAL -> Icons.Default.Groups
                GenderAccent.UNSPECIFIED -> null
            }
            if (genderIcon != null) {
                Icon(
                    imageVector = genderIcon,
                    contentDescription = null,
                    tint = genderAccent.toColor(),
                    modifier = Modifier.size(18.dp),
                )
            }
        }

        if (lastActivityText != null && lastActivityStatus != null) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .background(color = lastActivityStatus.toColor(), shape = CircleShape),
                )
                Text(
                    text = lastActivityText.asString(),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        ReferencesText(referenceCount = totalReviews, scoreText = ratingValueText)

        if (cityText.isNotBlank()) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Icon(
                    imageVector = Icons.Outlined.LocationOn,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.size(16.dp),
                )
                Text(
                    text = cityText,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                PillChip(
                    text = stringResource(R.string.open_in_google_maps),
                    onClick = { onOpenMap(true) },
                )
                PillChip(
                    text = stringResource(R.string.open_in_other_maps),
                    onClick = { onOpenMap(false) },
                )
            }
        }
    }
}

private fun ActivityStatus.toColor(): Color = when (this) {
    ActivityStatus.TODAY -> AppColors.Success
    ActivityStatus.RECENTLY -> AppColors.Warning
    ActivityStatus.LONG_AGO -> md_theme_light_outline
}

@Preview
@Composable
private fun HostHeaderSectionPreview() {
    HiPeopleTheme {
        HostHeaderSection(
            name = "Anna",
            ageText = " (28 лет)",
            genderAccent = GenderAccent.FEMALE,
            totalReviews = 23,
            ratingValueText = "8.9",
            cityText = "Vidnoye",
            onOpenMap = {},
            lastActivityText = UiText.DynamicString("Was recently"),
            lastActivityStatus = ActivityStatus.RECENTLY,
        )
    }
}
