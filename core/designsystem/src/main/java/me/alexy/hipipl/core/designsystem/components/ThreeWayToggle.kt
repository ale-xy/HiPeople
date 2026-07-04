package me.alexy.hipipl.core.designsystem.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import me.alexy.hipipl.core.designsystem.HiPeopleTheme
import me.alexy.hipipl.core.designsystem.components.preview.ThreeWayTogglePreviewParameterProvider

enum class TriState { YES, UNSPECIFIED, NO }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThreeWayToggle(
    label: String,
    value: TriState,
    onValueChange: (TriState) -> Unit,
    modifier: Modifier = Modifier,
    yesLabel: String = "Yes",
    unspecifiedLabel: String = "Any",
    noLabel: String = "No",
) {
    val options = listOf(TriState.NO, TriState.UNSPECIFIED, TriState.YES)
    val labels = mapOf(
        TriState.NO to noLabel,
        TriState.UNSPECIFIED to unspecifiedLabel,
        TriState.YES to yesLabel,
    )

    Column(modifier = modifier) {
        Text(text = label, style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(8.dp))
        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            options.forEachIndexed { index, option ->
                SegmentedButton(
                    selected = value == option,
                    onClick = { onValueChange(option) },
                    shape = SegmentedButtonDefaults.itemShape(index = index, count = options.size),
                ) {
                    Text(labels.getValue(option))
                }
            }
        }
    }
}

@Preview
@Composable
private fun ThreeWayTogglePreview(
    @PreviewParameter(ThreeWayTogglePreviewParameterProvider::class) value: TriState,
) {
    HiPeopleTheme {
        ThreeWayToggle(
            label = "Guest has a separate room?",
            value = value,
            onValueChange = {},
        )
    }
}
