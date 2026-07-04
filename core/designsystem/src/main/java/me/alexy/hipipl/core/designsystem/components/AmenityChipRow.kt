package me.alexy.hipipl.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import me.alexy.hipipl.core.designsystem.HiPeopleTheme
import me.alexy.hipipl.core.designsystem.components.preview.AmenityChipRowPreviewParameterProvider
import me.alexy.hipipl.core.designsystem.components.preview.AmenityChipRowPreviewState

@Composable
fun AmenityChipRow(
    separateRoom: Boolean,
    kidsAllowed: Boolean,
    petsAtHome: Boolean,
    modifier: Modifier = Modifier,
) {
    if (!separateRoom && !kidsAllowed && !petsAtHome) return

    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        if (separateRoom) AmenityChip(Icons.Default.MeetingRoom, "Separate room")
        if (kidsAllowed) AmenityChip(Icons.Default.ChildCare, "Kids allowed")
        if (petsAtHome) AmenityChip(Icons.Default.Pets, "Pets at home")
    }
}

@Composable
private fun AmenityChip(icon: ImageVector, contentDescription: String) {
    Box(
        modifier = Modifier
            .size(28.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.secondaryContainer),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            modifier = Modifier.size(16.dp),
            tint = MaterialTheme.colorScheme.onSecondaryContainer,
        )
    }
}

@Preview
@Composable
private fun AmenityChipRowPreview(
    @PreviewParameter(AmenityChipRowPreviewParameterProvider::class) state: AmenityChipRowPreviewState,
) {
    HiPeopleTheme {
        AmenityChipRow(
            separateRoom = state.separateRoom,
            kidsAllowed = state.kidsAllowed,
            petsAtHome = state.petsAtHome,
        )
    }
}
