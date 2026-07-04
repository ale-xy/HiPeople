package me.alexy.hipipl.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import me.alexy.hipipl.core.designsystem.GenderAccent
import me.alexy.hipipl.core.designsystem.HiPeopleTheme
import me.alexy.hipipl.core.designsystem.toColor
import me.alexy.hipipl.core.designsystem.components.preview.GenderAccentPreviewParameterProvider

@Composable
fun GenderAccentBar(accent: GenderAccent, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .width(4.dp)
            .fillMaxHeight()
            .background(accent.toColor())
    )
}

@Preview
@Composable
private fun GenderAccentBarPreview(
    @PreviewParameter(GenderAccentPreviewParameterProvider::class) accent: GenderAccent,
) {
    HiPeopleTheme {
        GenderAccentBar(accent = accent, modifier = Modifier.height(48.dp))
    }
}
