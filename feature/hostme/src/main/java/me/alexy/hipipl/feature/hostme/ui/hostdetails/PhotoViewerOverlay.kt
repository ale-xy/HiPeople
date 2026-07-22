package me.alexy.hipipl.feature.hostme.ui.hostdetails

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import me.alexy.hipipl.core.designsystem.AppColors
import me.alexy.hipipl.core.designsystem.HiPeopleTheme
import me.alexy.hipipl.core.designsystem.components.NetworkImage
import me.alexy.hipipl.core.designsystem.components.RoundIconButton
import me.alexy.hipipl.feature.hostitem.R

@Composable
fun PhotoViewerOverlay(
    photos: List<String>,
    startIndex: Int,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (photos.isEmpty()) return

    val pagerState = rememberPagerState(initialPage = startIndex.coerceIn(0, photos.lastIndex)) { photos.size }
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AppColors.PhotoViewerBackground),
    ) {
        HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { page ->
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                NetworkImage(
                    modifier = Modifier.fillMaxWidth(0.86f),
                    model = photos[page],
                    contentScale = ContentScale.Fit,
                    contentDescription = stringResource(R.string.host_photo_description),
                    indicatorSize = 32.dp,
                    indicatorColor = MaterialTheme.colorScheme.inverseOnSurface,
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = stringResource(R.string.photo_counter_format, pagerState.currentPage + 1, photos.size),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.inverseOnSurface,
            )
            RoundIconButton(
                icon = Icons.Default.Close,
                contentDescription = stringResource(R.string.cd_close_viewer),
                onClick = onClose,
                size = 36.dp,
                containerColor = Color.White.copy(alpha = 0.12f),
                contentColor = MaterialTheme.colorScheme.inverseOnSurface,
            )
        }

        if (photos.size > 1) {
            RoundIconButton(
                icon = Icons.Default.ChevronLeft,
                contentDescription = stringResource(R.string.cd_previous_photo),
                onClick = {
                    val target = (pagerState.currentPage - 1 + photos.size) % photos.size
                    coroutineScope.launch { pagerState.animateScrollToPage(target) }
                },
                size = 38.dp,
                containerColor = Color.White.copy(alpha = 0.14f),
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(12.dp),
            )
            RoundIconButton(
                icon = Icons.Default.ChevronRight,
                contentDescription = stringResource(R.string.cd_next_photo),
                onClick = {
                    val target = (pagerState.currentPage + 1) % photos.size
                    coroutineScope.launch { pagerState.animateScrollToPage(target) }
                },
                size = 38.dp,
                containerColor = Color.White.copy(alpha = 0.14f),
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(12.dp),
            )
        }
    }
}

@Preview
@Composable
private fun PhotoViewerOverlayPreview() {
    HiPeopleTheme {
        PhotoViewerOverlay(
            photos = listOf("https://example.com/1.jpg", "https://example.com/2.jpg"),
            startIndex = 0,
            onClose = {},
        )
    }
}
