package me.alexy.hipipl.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import me.alexy.hipipl.core.designsystem.AppColors
import me.alexy.hipipl.core.designsystem.HiPeopleTheme

/**
 * Row of small filled/unfilled bars used to show a proficiency level
 * (e.g. language fluency), out of a fixed [total].
 */
@Composable
fun LevelBars(
    filled: Int,
    modifier: Modifier = Modifier,
    total: Int = 3,
) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
        repeat(total) { index ->
            val isFilled = index < filled
            Box(
                modifier = Modifier
                    .size(width = 16.dp, height = 6.dp)
                    .background(
                        color = if (isFilled) AppColors.Success else MaterialTheme.colorScheme.outlineVariant,
                        shape = RoundedCornerShape(3.dp),
                    ),
            )
        }
    }
}

@Preview
@Composable
private fun LevelBarsPreview() {
    HiPeopleTheme {
        LevelBars(filled = 2)
    }
}
