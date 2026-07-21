package me.alexy.hipipl.feature.hostme.ui.hostsearch

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import me.alexy.hipipl.core.designsystem.AppColors
import me.alexy.hipipl.core.designsystem.HiPeopleTheme
import me.alexy.hipipl.core.designsystem.components.ThreeWayToggle
import me.alexy.hipipl.feature.hostitem.R

/**
 * Bottom sheet for filtering host search results by separateRoom, kidsAllowed, and petsAtHome.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun FilterBottomSheet(
    filters: HostSearchFiltersUi,
    onAction: (HostsSearchAction) -> Unit
) {
    val yesLabel = stringResource(R.string.button_yes)
    val noLabel = stringResource(R.string.button_no)
    val unspecifiedLabel = stringResource(R.string.filter_not_specified)

    ModalBottomSheet(
        onDismissRequest = { onAction(HostsSearchAction.OnDismissFilterSheet) },
        scrimColor = AppColors.Scrim.copy(alpha = 0.42f)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            ThreeWayToggle(
                label = stringResource(R.string.add_host_separate),
                value = filters.separateRoom,
                onValueChange = { onAction(HostsSearchAction.OnFilterChange(filters.copy(separateRoom = it))) },
                yesLabel = yesLabel,
                unspecifiedLabel = unspecifiedLabel,
                noLabel = noLabel
            )
            ThreeWayToggle(
                label = stringResource(R.string.add_host_kid),
                value = filters.kidsAllowed,
                onValueChange = { onAction(HostsSearchAction.OnFilterChange(filters.copy(kidsAllowed = it))) },
                yesLabel = yesLabel,
                unspecifiedLabel = unspecifiedLabel,
                noLabel = noLabel
            )
            ThreeWayToggle(
                label = stringResource(R.string.add_host_pet),
                value = filters.petsAtHome,
                onValueChange = { onAction(HostsSearchAction.OnFilterChange(filters.copy(petsAtHome = it))) },
                yesLabel = yesLabel,
                unspecifiedLabel = unspecifiedLabel,
                noLabel = noLabel
            )
            Button(
                onClick = { onAction(HostsSearchAction.OnApplyFilters) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.button_ok))
            }
        }
    }
}

@Preview
@Composable
private fun FilterBottomSheetPreview() {
    HiPeopleTheme {
        FilterBottomSheet(filters = HostSearchFiltersUi(), onAction = {})
    }
}
