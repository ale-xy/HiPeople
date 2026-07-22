package me.alexy.hipipl.feature.hostme.ui.hostdetails

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import me.alexy.hipipl.core.designsystem.AppColors
import me.alexy.hipipl.core.designsystem.HiPeopleTheme
import me.alexy.hipipl.core.designsystem.components.ResultCard
import me.alexy.hipipl.feature.hostitem.R

@Composable
fun HostingGuestsCard(
    hostText: String,
    hostingParams: List<HostingParamUi>,
    modifier: Modifier = Modifier,
) {
    ResultCard(modifier = modifier) {
        Text(
            text = stringResource(R.string.host_description),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
        if (hostText.isNotBlank()) {
            Text(
                text = hostText,
                style = MaterialTheme.typography.bodyMedium,
                color = AppColors.OnSurfaceBodyCopy,
                modifier = Modifier.padding(top = 10.dp, bottom = 14.dp),
            )
        }
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            hostingParams.forEach { param -> HostingParamRow(param) }
        }
    }
}

@Composable
private fun HostingParamRow(param: HostingParamUi) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Box(modifier = Modifier.size(34.dp)) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        if (param.isOk) MaterialTheme.colorScheme.secondaryContainer
                        else MaterialTheme.colorScheme.surfaceContainerHighest
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = param.type.icon(),
                    contentDescription = null,
                    tint = if (param.isOk) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.outline,
                    modifier = Modifier.size(17.dp),
                )
            }
            Box(
                modifier = Modifier
                    .size(15.dp)
                    .align(Alignment.BottomEnd)
                    .clip(CircleShape)
                    .background(if (param.isOk) AppColors.Success else MaterialTheme.colorScheme.outline),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = if (param.isOk) Icons.Default.Check else Icons.Default.Close,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(9.dp),
                )
            }
        }
        Text(
            text = stringResource(param.labelRes()),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = AppColors.OnSurfaceBodyCopy,
        )
    }
}

private fun HostingParamType.icon(): ImageVector = when (this) {
    HostingParamType.SEPARATE_ROOM -> Icons.Default.MeetingRoom
    HostingParamType.KIDS -> Icons.Default.ChildCare
    HostingParamType.PETS -> Icons.Default.Pets
}

private fun HostingParamUi.labelRes(): Int = when (type) {
    HostingParamType.SEPARATE_ROOM -> if (isOk) R.string.host_separate_yes else R.string.host_separate_no
    HostingParamType.KIDS -> if (isOk) R.string.host_child_yes else R.string.host_child_no
    HostingParamType.PETS -> if (isOk) R.string.host_pet_yes else R.string.host_pet_no
}

@Preview
@Composable
private fun HostingGuestsCardPreview() {
    HiPeopleTheme {
        HostingGuestsCard(
            hostText = "Traveling since 2015, love cycling and board games.",
            hostingParams = listOf(
                HostingParamUi(HostingParamType.SEPARATE_ROOM, isOk = true),
                HostingParamUi(HostingParamType.KIDS, isOk = false),
            ),
        )
    }
}
