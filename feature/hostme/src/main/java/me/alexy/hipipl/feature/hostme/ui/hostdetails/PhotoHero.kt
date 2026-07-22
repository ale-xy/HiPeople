package me.alexy.hipipl.feature.hostme.ui.hostdetails

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Link
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import me.alexy.hipipl.core.designsystem.AppColors
import me.alexy.hipipl.core.designsystem.HiPeopleTheme
import me.alexy.hipipl.core.designsystem.components.NetworkImage
import me.alexy.hipipl.core.designsystem.components.RoundIconButton
import me.alexy.hipipl.feature.hostitem.R

private val HeroHeight = 340.dp

@Composable
fun PhotoHero(
    photos: List<String>,
    hostName: String,
    isFavorited: Boolean,
    onBackClick: () -> Unit,
    onToggleFavorite: () -> Unit,
    onCopyLink: () -> Unit,
    onOpenViewer: (index: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val pagerState = rememberPagerState(pageCount = { photos.size })
    val coroutineScope = rememberCoroutineScope()
    val hasPhotos = photos.isNotEmpty()

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(HeroHeight)
            .background(AppColors.PhotoViewerBackground)
            .clickable(enabled = hasPhotos) { onOpenViewer(pagerState.currentPage) },
    ) {
        if (hasPhotos) {
            HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { page ->
                NetworkImage(
                    modifier = Modifier.fillMaxSize(),
                    model = photos[page],
                    contentScale = ContentScale.Crop,
                    contentDescription = stringResource(R.string.host_photo_description),
                    indicatorSize = 32.dp,
                    indicatorColor = MaterialTheme.colorScheme.inverseOnSurface,
                )
            }
        } else {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(
                        modifier = Modifier
                            .size(88.dp)
                            .background(color = MaterialTheme.colorScheme.inverseSurface, shape = CircleShape),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = hostName.initials(),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.inverseOnSurface,
                        )
                    }
                    Text(
                        text = stringResource(R.string.no_photos_yet),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.inverseOnSurface.copy(alpha = 0.6f),
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        0f to AppColors.Scrim.copy(alpha = 0.35f),
                        0.22f to AppColors.Scrim.copy(alpha = 0f),
                        0.7f to AppColors.Scrim.copy(alpha = 0f),
                        1f to AppColors.Scrim.copy(alpha = 0.45f),
                    )
                )
        )

        RoundIconButton(
            icon = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = stringResource(R.string.button_back),
            onClick = onBackClick,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(12.dp),
        )

        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            RoundIconButton(
                icon = if (isFavorited) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                contentDescription = stringResource(R.string.button_add_favorite),
                onClick = onToggleFavorite,
                contentColor = if (isFavorited) AppColors.Success else Color.White,
            )
            RoundIconButton(
                icon = Icons.Default.Link,
                contentDescription = stringResource(R.string.button_copy_link),
                onClick = onCopyLink,
            )
        }

        if (hasPhotos && photos.size > 1) {
            RoundIconButton(
                icon = Icons.Default.ChevronLeft,
                contentDescription = stringResource(R.string.cd_previous_photo),
                onClick = {
                    val target = (pagerState.currentPage - 1 + photos.size) % photos.size
                    coroutineScope.launch { pagerState.animateScrollToPage(target) }
                },
                size = 34.dp,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(8.dp),
            )
            RoundIconButton(
                icon = Icons.Default.ChevronRight,
                contentDescription = stringResource(R.string.cd_next_photo),
                onClick = {
                    val target = (pagerState.currentPage + 1) % photos.size
                    coroutineScope.launch { pagerState.animateScrollToPage(target) }
                },
                size = 34.dp,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(8.dp),
            )

            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                repeat(photos.size) { index ->
                    val isActive = index == pagerState.currentPage
                    Box(
                        modifier = Modifier
                            .size(width = if (isActive) 18.dp else 6.dp, height = 6.dp)
                            .background(
                                color = Color.White.copy(alpha = if (isActive) 1f else 0.5f),
                                shape = CircleShape,
                            )
                    )
                }
            }
        }
    }
}

private fun String.initials(): String =
    trim().split(" ").filter { it.isNotBlank() }.take(2).mapNotNull { it.firstOrNull()?.uppercaseChar() }.joinToString("")

@Preview
@Composable
private fun PhotoHeroPreview() {
    HiPeopleTheme {
        PhotoHero(
            photos = emptyList(),
            hostName = "Dmitry Orlov",
            isFavorited = false,
            onBackClick = {},
            onToggleFavorite = {},
            onCopyLink = {},
            onOpenViewer = {},
        )
    }
}
