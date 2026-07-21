package me.alexy.hipipl.feature.hostme.ui.hostsearch

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import me.alexy.hipipl.core.designsystem.AppColors
import me.alexy.hipipl.core.designsystem.HiPeopleTheme
import me.alexy.hipipl.feature.hostitem.R

/**
 * Decorative map preview (no real geo data - map screen is out of scope for this
 * task) matching the design mockup's illustrative pins + "open map" pill button.
 */
@Composable
internal fun HostsMapCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(140.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Brush.linearGradient(listOf(Color(0xFFE9EFE6), Color(0xFFDFE7DF))))
            .clickable(onClick = onClick)
    ) {
        Icon(
            imageVector = Icons.Default.LocationOn,
            contentDescription = null,
            tint = AppColors.RatingAccent,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 56.dp, top = 36.dp)
                .size(28.dp)
        )
        Icon(
            imageVector = Icons.Default.LocationOn,
            contentDescription = null,
            tint = AppColors.RatingAccent,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(end = 64.dp, top = 18.dp)
                .size(28.dp)
        )
        Icon(
            imageVector = Icons.Default.LocationOn,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.secondary,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(top = 24.dp)
                .size(28.dp)
        )
        Surface(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(12.dp),
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.primaryContainer,
            shadowElevation = 3.dp
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = stringResource(R.string.hosts_on_map),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

@Preview
@Composable
private fun HostsMapCardPreview() {
    HiPeopleTheme {
        HostsMapCard(onClick = {}, modifier = Modifier.padding(16.dp))
    }
}
