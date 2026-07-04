package me.alexy.hipipl.core.designsystem.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import me.alexy.hipipl.core.designsystem.HiPeopleTheme
import me.alexy.hipipl.core.designsystem.components.preview.LoadingErrorEmptyContentPreviewParameterProvider
import me.alexy.hipipl.core.designsystem.components.preview.LoadingErrorEmptyContentPreviewState

/**
 * Dedupes the loading/error/empty `Box` pattern previously copy-pasted across screens.
 * Takes a plain, already-resolved error string (not UiText) to keep designsystem
 * free of a dependency on core:presentation.
 */
@Composable
fun LoadingErrorEmptyContent(
    isLoading: Boolean,
    error: String?,
    isEmpty: Boolean,
    emptyMessage: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    when {
        isLoading -> Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }

        error != null -> Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = error,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(24.dp),
            )
        }

        isEmpty -> Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = emptyMessage,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(24.dp),
            )
        }

        else -> content()
    }
}

@Preview
@Composable
private fun LoadingErrorEmptyContentPreview(
    @PreviewParameter(LoadingErrorEmptyContentPreviewParameterProvider::class)
    state: LoadingErrorEmptyContentPreviewState,
) {
    HiPeopleTheme {
        LoadingErrorEmptyContent(
            isLoading = state.isLoading,
            error = state.error,
            isEmpty = state.isEmpty,
            emptyMessage = "No hosts found",
        ) {
            Text("Loaded content")
        }
    }
}
