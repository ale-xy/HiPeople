package me.alexy.hipipl.core.designsystem.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import me.alexy.hipipl.core.designsystem.HiPeopleTheme
import me.alexy.hipipl.core.designsystem.components.preview.CompassArrowIconPreviewParameterProvider

/**
 * Compass bearing arrow, rotated to point in the given direction (0 = north/up).
 */
@Composable
fun CompassArrowIcon(degrees: Float, modifier: Modifier = Modifier) {
    Icon(
        imageVector = Icons.Default.ArrowUpward,
        contentDescription = null,
        tint = MaterialTheme.colorScheme.primary,
        modifier = modifier
            .size(14.dp)
            .rotate(degrees),
    )
}

@Preview
@Composable
private fun CompassArrowIconPreview(
    @PreviewParameter(CompassArrowIconPreviewParameterProvider::class) degrees: Float,
) {
    HiPeopleTheme {
        CompassArrowIcon(degrees = degrees)
    }
}
