package me.alexy.hipipl.feature.hostme.ui.hostdetails

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import me.alexy.hipipl.core.designsystem.HiPeopleTheme
import me.alexy.hipipl.core.designsystem.components.PillChip
import me.alexy.hipipl.core.presentation.UiText
import me.alexy.hipipl.core.presentation.asString

@Composable
fun VibePillsSection(
    vibes: List<UiText>,
    modifier: Modifier = Modifier,
) {
    if (vibes.isEmpty()) return

    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        vibes.forEach { vibe -> PillChip(text = vibe.asString()) }
    }
}

@Preview
@Composable
private fun VibePillsSectionPreview() {
    HiPeopleTheme {
        VibePillsSection(
            vibes = listOf(
                UiText.DynamicString("Extrovert"),
                UiText.DynamicString("Active leisure/sports"),
                UiText.DynamicString("Heart-to-heart evenings"),
                UiText.DynamicString("Loves cooking"),
                UiText.DynamicString("Night owl"),
            )
        )
    }
}
