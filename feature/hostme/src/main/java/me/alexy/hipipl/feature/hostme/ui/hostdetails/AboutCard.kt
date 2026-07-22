package me.alexy.hipipl.feature.hostme.ui.hostdetails

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import me.alexy.hipipl.core.designsystem.AppColors
import me.alexy.hipipl.core.designsystem.HiPeopleTheme
import me.alexy.hipipl.core.designsystem.components.ResultCard
import me.alexy.hipipl.feature.hostitem.R

@Composable
fun AboutCard(
    description: String,
    modifier: Modifier = Modifier,
) {
    if (description.isBlank()) return

    ResultCard(modifier = modifier) {
        Text(
            text = stringResource(R.string.user_about),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            color = AppColors.OnSurfaceBodyCopy,
            modifier = Modifier.padding(top = 10.dp),
        )
    }
}

@Preview
@Composable
private fun AboutCardPreview() {
    HiPeopleTheme {
        AboutCard(description = "Friendly host, always happy to show guests around the city.")
    }
}
