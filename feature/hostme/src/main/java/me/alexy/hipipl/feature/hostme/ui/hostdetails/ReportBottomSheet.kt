package me.alexy.hipipl.feature.hostme.ui.hostdetails

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import me.alexy.hipipl.core.designsystem.AppColors
import me.alexy.hipipl.core.designsystem.HiPeopleTheme
import me.alexy.hipipl.core.presentation.asString
import me.alexy.hipipl.core.presentation.UiText
import me.alexy.hipipl.feature.hostitem.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportBottomSheet(
    text: String,
    error: UiText?,
    modifier: Modifier = Modifier,
    isSending: Boolean = false,
    onTextChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onSend: () -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        scrimColor = AppColors.Scrim.copy(alpha = 0.42f),
        modifier = modifier,
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                text = stringResource(R.string.button_complaint),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            OutlinedTextField(
                value = text,
                onValueChange = onTextChange,
                placeholder = { Text(stringResource(R.string.report_placeholder)) },
                minLines = 4,
                isError = error != null,
                modifier = Modifier.fillMaxWidth(),
            )
            if (error != null) {
                Text(
                    text = error.asString(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .weight(1f)
                        .height(46.dp),
                ) {
                    Text(stringResource(R.string.button_cancel))
                }
                Button(
                    onClick = onSend,
                    enabled = !isSending,
                    modifier = Modifier
                        .weight(1f)
                        .height(46.dp),
                ) {
                    Text(stringResource(R.string.button_send))
                }
            }
        }
    }
}

@Preview
@Composable
private fun ReportBottomSheetPreview() {
    HiPeopleTheme {
        ReportBottomSheet(
            text = "",
            error = null,
            onTextChange = {},
            onDismiss = {},
            onSend = {},
        )
    }
}
