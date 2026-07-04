package me.alexy.hipipl.core.designsystem.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import me.alexy.hipipl.core.designsystem.HiPeopleTheme
import me.alexy.hipipl.core.designsystem.components.preview.SearchTextFieldPreviewParameterProvider
import me.alexy.hipipl.core.designsystem.components.preview.SearchTextFieldPreviewState

/**
 * Single-line search field reused for both the location field and the
 * "social network link or phone number" field.
 *
 * When [readOnly] is true, the field renders normally (looks and reads identically) but
 * ignores typing/cursor input; a transparent tap target invokes [onClick] instead — used to
 * make the field act as a trigger that opens a bottom sheet with the real, editable field.
 */
@Composable
fun SearchTextField(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    leadingIcon: @Composable (() -> Unit)? = {
        Icon(imageVector = Icons.Default.Search, contentDescription = null)
    },
    trailingIcon: @Composable (() -> Unit)? = null,
    onTrailingIconClick: (() -> Unit)? = null,
    readOnly: Boolean = false,
    onClick: (() -> Unit)? = null,
) {
    Box(modifier = modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier.fillMaxWidth(),
            readOnly = readOnly,
            placeholder = { Text(placeholder) },
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon?.let { { Spacer(Modifier.size(48.dp)) } },
            singleLine = true,
            shape = MaterialTheme.shapes.medium,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                disabledContainerColor = Color.White,
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
            ),
        )
        if (readOnly && onClick != null) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onClick,
                    )
            )
        }
        if (trailingIcon != null) {
            IconButton(
                onClick = { onTrailingIconClick?.invoke() },
                modifier = Modifier.align(Alignment.CenterEnd)
            ) {
                trailingIcon()
            }
        }
    }
}

@Preview
@Composable
private fun SearchTextFieldPreview(
    @PreviewParameter(SearchTextFieldPreviewParameterProvider::class) state: SearchTextFieldPreviewState,
) {
    HiPeopleTheme {
        SearchTextField(
            query = state.query,
            onQueryChange = {},
            placeholder = state.placeholder,
            trailingIcon = if (state.showTrailingIcon) {
                { Icon(imageVector = Icons.Default.FilterAlt, contentDescription = null) }
            } else {
                null
            },
        )
    }
}
