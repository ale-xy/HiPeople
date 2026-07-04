package me.alexy.hipipl.core.designsystem.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import me.alexy.hipipl.core.designsystem.AppColors
import me.alexy.hipipl.core.designsystem.HiPeopleTheme
import me.alexy.hipipl.core.designsystem.components.preview.ReferencesTextPreviewParameterProvider
import me.alexy.hipipl.core.designsystem.components.preview.ReferencesTextPreviewState

/**
 * Star + reference count, with an optional pre-formatted score in parens, e.g. "57 (9.4)".
 * Renders nothing when there are no references, per the design's "omit line entirely" rule.
 */
@Composable
fun ReferencesText(
    referenceCount: Int,
    scoreText: String?,
    modifier: Modifier = Modifier,
) {
    if (referenceCount <= 0) return

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = null,
            tint = AppColors.RatingAccent,
            modifier = Modifier.size(16.dp),
        )
        Text(
            text = referenceCount.toString(),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
        )
        if (scoreText != null) {
            Text(
                text = "($scoreText)",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Preview
@Composable
private fun ReferencesTextPreview(
    @PreviewParameter(ReferencesTextPreviewParameterProvider::class) state: ReferencesTextPreviewState,
) {
    HiPeopleTheme {
        ReferencesText(referenceCount = state.referenceCount, scoreText = state.scoreText)
    }
}
