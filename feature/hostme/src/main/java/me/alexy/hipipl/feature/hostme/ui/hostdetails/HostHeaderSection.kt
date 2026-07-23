package me.alexy.hipipl.feature.hostme.ui.hostdetails

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.platform.LocalContext
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
import me.alexy.hipipl.core.designsystem.toColor
import me.alexy.hipipl.core.presentation.UiText
import me.alexy.hipipl.core.presentation.asString
import me.alexy.hipipl.feature.hostitem.R
import androidx.core.net.toUri

@Composable
fun HostHeaderSection(
    name: String,
    ageText: String,
    genderAccent: GenderAccent,
    totalReviews: Int,
    ratingValueText: String?,
    cityText: String,
    modifier: Modifier = Modifier,
    lastActivityText: UiText? = null,
    vibeLabels: List<String> = emptyList(),
) {
    val context = LocalContext.current

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

        ReferencesText(referenceCount = totalReviews, scoreText = ratingValueText)

        if (lastActivityText != null) {
            Text(
                text = lastActivityText.asString(),
                style = MaterialTheme.typography.labelMedium,
                color = AppColors.Success,
            )
        }

        if (vibeLabels.isNotEmpty()) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                vibeLabels.forEach { label -> PillChip(text = label) }
            }
        }

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
                    onClick = { openMap(context = context, query = cityText, preferGoogleMaps = true) },
                )
                PillChip(
                    text = stringResource(R.string.open_in_other_maps),
                    onClick = { openMap(context = context, query = cityText, preferGoogleMaps = false) },
                )
            }
        }
    }
}

private fun openMap(context: Context, query: String, preferGoogleMaps: Boolean) {
    val uri = "geo:0,0?q=${Uri.encode(query)}".toUri()
    val intent = Intent(Intent.ACTION_VIEW, uri).apply {
        if (preferGoogleMaps) setPackage("com.google.android.apps.maps")
    }
    val resolvedIntent = if (intent.resolveActivity(context.packageManager) != null) {
        intent
    } else {
        Intent(Intent.ACTION_VIEW, uri)
    }
    context.startActivity(resolvedIntent)
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
        )
    }
}
