package me.alexy.hipipl.core.designsystem.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import me.alexy.hipipl.core.designsystem.HiPeopleTheme
import me.alexy.hipipl.core.designsystem.components.preview.SuggestionsDropdownPreviewParameterProvider
import me.alexy.hipipl.core.designsystem.components.preview.SuggestionsDropdownPreviewState

data class SuggestionItemUi(
    val id: String,
    val primaryText: String,
    val secondaryText: String,
)

/**
 * Location suggestions list, meant to be laid out as regular content (e.g. inside a
 * [androidx.compose.material3.ModalBottomSheet]) rather than as a floating overlay.
 */
@Composable
fun SuggestionsDropdown(
    suggestions: List<SuggestionItemUi>,
    onSuggestionClick: (SuggestionItemUi) -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    loadingText: String = "Searching locations…",
) {
    Column(modifier = modifier) {
        if (isLoading) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.primary,
                )
                Text(
                    text = loadingText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        suggestions.forEach { suggestion ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = { onSuggestionClick(suggestion) })
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Icon(
                    imageVector = Icons.Outlined.LocationOn,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Column {
                    Text(
                        text = suggestion.primaryText,
                        style = MaterialTheme.typography.bodyLarge,
                    )
                    if (suggestion.secondaryText.isNotBlank()) {
                        Text(
                            text = suggestion.secondaryText,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun SuggestionsDropdownPreview(
    @PreviewParameter(SuggestionsDropdownPreviewParameterProvider::class) state: SuggestionsDropdownPreviewState,
) {
    HiPeopleTheme {
        SuggestionsDropdown(
            isLoading = state.isLoading,
            suggestions = state.suggestions,
            onSuggestionClick = {},
        )
    }
}
