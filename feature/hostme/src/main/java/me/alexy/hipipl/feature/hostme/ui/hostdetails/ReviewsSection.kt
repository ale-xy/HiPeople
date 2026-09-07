package me.alexy.hipipl.feature.hostme.ui.hostdetails

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import me.alexy.hipipl.core.designsystem.AppColors
import me.alexy.hipipl.core.designsystem.HiPeopleTheme
import me.alexy.hipipl.core.designsystem.components.NetworkImage
import me.alexy.hipipl.core.designsystem.components.ReferencesText
import me.alexy.hipipl.feature.hostitem.R

@Composable
fun ReviewsSection(
    totalReviews: Int,
    reviewGroups: List<ReviewGroupUi>,
    hasMoreReviews: Boolean,
    isLoadingMoreReviews: Boolean,
    isLoading: Boolean,
    errorText: String?,
    onToggleGroupExpanded: (Int) -> Unit,
    onLoadMoreReviews: () -> Unit,
    onAddReview: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = stringResource(R.string.reviews_title),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
            )
            ReferencesText(referenceCount = totalReviews, scoreText = null)
        }

        Text(
            text = stringResource(R.string.button_add_rev),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp))
                .clickable(onClick = onAddReview)
                .padding(vertical = 12.dp),
        )

        when {
            isLoading -> Text(stringResource(R.string.loading))
            errorText != null -> Text(errorText, color = MaterialTheme.colorScheme.error)
            reviewGroups.isEmpty() -> Text(
                text = stringResource(R.string.no_reviews),
                style = MaterialTheme.typography.bodyLarge,
            )
            else -> {
                reviewGroups.forEachIndexed { index, group ->
                    ReviewGroup(
                        group = group,
                        onToggleExpanded = { onToggleGroupExpanded(index) },
                    )
                }
                // Only offered once a full server page has been loaded and it reports more exist -
                // this fetches the next page, it doesn't just reveal locally-buffered groups.
                if (hasMoreReviews) {
                    Text(
                        text = stringResource(
                            if (isLoadingMoreReviews) R.string.loading else R.string.load_more_reviews
                        ),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.secondaryContainer, RoundedCornerShape(14.dp))
                            .clickable(enabled = !isLoadingMoreReviews, onClick = onLoadMoreReviews)
                            .padding(vertical = 12.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun ReviewGroup(
    group: ReviewGroupUi,
    onToggleExpanded: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        group.visibleReceived.forEach { review ->
            ReviewCard(review = review, isMutual = group.isMutual)
        }
        group.visibleResponses.forEach { reply ->
            ReplyCard(reply = reply)
        }
        if (group.hasMore) {
            Text(
                text = stringResource(if (group.isExpanded) R.string.hide_reviews else R.string.show_more_reviews),
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .padding(start = 16.dp)
                    .clickable(onClick = onToggleExpanded),
            )
        }
    }
}

@Composable
private fun ReviewCard(review: ReviewUi, isMutual: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceContainerLow, RoundedCornerShape(14.dp))
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        ReviewHeader(review = review, isMutual = isMutual)
        Text(
            text = review.text,
            style = MaterialTheme.typography.bodyMedium,
            color = AppColors.OnSurfaceBodyCopy,
        )
        review.photoUrl?.let { photoUrl ->
            NetworkImage(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.5f)
                    .background(MaterialTheme.colorScheme.surfaceContainerHigh, RoundedCornerShape(12.dp)),
                model = photoUrl,
                contentScale = ContentScale.Crop,
                contentDescription = stringResource(R.string.review_photo_description),
            )
        }
    }
}

@Composable
private fun ReplyCard(reply: ReviewUi) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp)
            .background(MaterialTheme.colorScheme.surfaceContainerHighest, RoundedCornerShape(14.dp))
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = stringResource(R.string.reply_from_format, reply.authorName),
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
            text = reply.formattedDate,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.outline,
        )
        Text(
            text = reply.text,
            style = MaterialTheme.typography.bodyMedium,
            color = AppColors.OnSurfaceBodyCopy,
        )
        reply.photoUrl?.let { photoUrl ->
            NetworkImage(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.5f)
                    .background(MaterialTheme.colorScheme.surfaceContainerHigh, RoundedCornerShape(12.dp)),
                model = photoUrl,
                contentScale = ContentScale.Crop,
                contentDescription = stringResource(R.string.review_photo_description),
            )
        }
    }
}

@Composable
private fun ReviewHeader(review: ReviewUi, isMutual: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        if (isMutual) {
            Icon(
                imageVector = Icons.Default.Handshake,
                contentDescription = stringResource(R.string.mutual_friend_cd),
                tint = AppColors.Success,
                modifier = Modifier.size(14.dp),
            )
        }
        Text(
            text = review.authorName,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
            text = stringResource(
                R.string.role_label_format,
                stringResource(if (review.isGuest) R.string.review_cs_guest else R.string.review_cs_host),
            ),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.outline,
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = review.formattedDate,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.outline,
        )
    }
}

@Preview
@Composable
private fun ReviewsSectionPreview() {
    HiPeopleTheme {
        ReviewsSection(
            totalReviews = 3,
            reviewGroups = listOf(
                ReviewGroupUi(
                    received = listOf(
                        ReviewUi(1, "Maria Volkova", "12.05.2026", "Great host!", null, true, null)
                    ),
                    responses = listOf(
                        ReviewUi(2, "Anna", "13.05.2026", "Thanks Maria!", null, false, "Maria Volkova")
                    ),
                    isMutual = true,
                    hasMore = false,
                )
            ),
            hasMoreReviews = false,
            isLoadingMoreReviews = false,
            isLoading = false,
            errorText = null,
            onToggleGroupExpanded = {},
            onLoadMoreReviews = {},
            onAddReview = {},
        )
    }
}
