package me.alexy.hipipl.feature.hostme.ui.hostdetails

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import me.alexy.hipipl.core.designsystem.HiPeopleTheme
import me.alexy.hipipl.core.designsystem.components.LevelBars

@Composable
fun LanguagesSection(
    languages: List<LanguageUi>,
    modifier: Modifier = Modifier,
) {
    if (languages.isEmpty()) return

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(10.dp)) {
        languages.forEach { language ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = language.name,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                LevelBars(filled = language.filledBars)
            }
        }
    }
}

@Preview
@Composable
private fun LanguagesSectionPreview() {
    HiPeopleTheme {
        LanguagesSection(
            languages = listOf(
                LanguageUi(name = "Russian", filledBars = 3),
                LanguageUi(name = "English", filledBars = 2),
            )
        )
    }
}
