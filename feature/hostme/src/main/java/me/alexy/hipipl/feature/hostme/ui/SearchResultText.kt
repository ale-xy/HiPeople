package me.alexy.hipipl.feature.hostme.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import me.alexy.hipipl.core.designsystem.HiPeopleTheme

/**
 * Name bold/dark, age regular-weight/smaller/muted - two distinct spans per
 * the mockup, not one uniformly-styled string.
 */
@Composable
internal fun NameWithAgeText(name: String, ageText: String, modifier: Modifier = Modifier) {
    val onSurface = MaterialTheme.colorScheme.onSurface
    val onSurfaceVariant = MaterialTheme.colorScheme.onSurfaceVariant
    val ageFontSize = MaterialTheme.typography.bodyMedium.fontSize

    Text(
        text = buildAnnotatedString {
            withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = onSurface)) {
                append(name)
            }
            if (ageText.isNotEmpty()) {
                withStyle(SpanStyle(fontWeight = FontWeight.Normal, color = onSurfaceVariant, fontSize = ageFontSize)) {
                    append(ageText)
                }
            }
        },
        style = MaterialTheme.typography.titleMedium,
        modifier = modifier
    )
}

@Preview
@Composable
private fun NameWithAgeTextPreview() {
    HiPeopleTheme {
        NameWithAgeText(name = "Anna", ageText = " (28 лет)")
    }
}
