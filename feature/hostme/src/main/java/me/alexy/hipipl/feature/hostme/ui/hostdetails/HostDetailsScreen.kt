package me.alexy.hipipl.feature.hostme.ui.hostdetails

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import me.alexy.hipipl.core.presentation.asString
import me.alexy.hipipl.core.designsystem.Blue
import me.alexy.hipipl.core.designsystem.HiPeopleTheme
import me.alexy.hipipl.core.designsystem.LightGreen
import me.alexy.hipipl.core.designsystem.LightPeach
import me.alexy.hipipl.core.designsystem.Yellow
import me.alexy.hipipl.feature.hostitem.R
import org.koin.androidx.compose.koinViewModel

@Composable
fun HostDetailsScreen(
    modifier: Modifier = Modifier,
    viewModel: HostDetailsViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    HostDetailsScreen(modifier, state)
}

@Composable
fun HostDetailsScreen(
    modifier: Modifier = Modifier,
    state: HostDetailsState
) {
    when {
        state.isLoadingHost -> {
            Box(Modifier.fillMaxSize()) {
                CircularProgressIndicator(Modifier.align(Alignment.Center))
            }
        }
        state.hostError != null -> {
            Box(Modifier.fillMaxSize()) {
                Text(state.hostError.asString())
            }
        }
        state.host != null -> {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    HostDetailsContent(state.host)
                }
                item {
                    Text(
                        style = MaterialTheme.typography.titleLarge,
                        text = stringResource(R.string.reviews_header)
                    )
                }
                
                when {
                    state.isLoadingReviews -> {
                        item {
                            CircularProgressIndicator(
                                Modifier.padding(10.dp)
                            )
                        }
                    }
                    state.reviewsError != null -> {
                        item {
                            Text(state.reviewsError.asString())
                        }
                    }
                    state.reviews.isNotEmpty() -> {
                        items(state.reviews.size) { index ->
                            MutualReviewItem(state.reviews[index])
                        }
                    }
                    else -> {
                        item {
                            Text(
                                modifier = Modifier.padding(top = 6.dp, bottom = 6.dp),
                                style = MaterialTheme.typography.bodyLarge,
                                text = stringResource(R.string.no_reviews)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun HostDetailsScreenPreview(
    @PreviewParameter(HostDetailsScreenPreviewParameterProvider::class) state: HostDetailsState,
) {
    HiPeopleTheme {
        HostDetailsScreen(state = state)
    }
}

@Composable
fun HostDetailsContent(host: HostDetailsUi) {
    val placeholder = painterResource(me.alexy.hipipl.core.designsystem.R.drawable.avatar)
    val pagerState = rememberPagerState(pageCount = { host.photos.size })

    Column {
        // Photo pager
        if (host.photos.isNotEmpty()) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxWidth()
            ) { page ->
                AsyncImage(
                    modifier = Modifier
                        .aspectRatio(1.0f)
                        .fillMaxWidth(),
                    model = host.photos[page],
                    alignment = Alignment.Center,
                    contentScale = ContentScale.Crop,
                    placeholder = placeholder,
                    error = placeholder,
                    fallback = placeholder,
                    contentDescription = stringResource(R.string.host_photo_description)
                )
            }
        }

        // Languages
        if (host.languagesText.isNotBlank()) {
            Text(
                modifier = Modifier.padding(top = 10.dp),
                text = stringResource(R.string.lang_format, host.languagesText)
            )
        }

        // City
        Text(
            modifier = Modifier.padding(top = 10.dp, bottom = 5.dp),
            style = MaterialTheme.typography.titleLarge,
            text = host.cityText
        )

        // Name with age
        val nameText = buildAnnotatedString {
            append(host.nameWithAge)
            addStyle(SpanStyle(fontWeight = FontWeight.Bold), 0, host.name.length)
        }
        Text(
            modifier = Modifier.padding(bottom = 5.dp),
            style = MaterialTheme.typography.titleMedium,
            text = nameText
        )

        // Rating
        val ratingIndex = stringResource(R.string.rating_format, host.ratingText)
            .indexOf(host.ratingText)
        val ratingText = buildAnnotatedString {
            val fullText = stringResource(R.string.rating_format, host.ratingText)
            append(fullText)
            if (ratingIndex >= 0) {
                addStyle(
                    SpanStyle(fontWeight = FontWeight.Bold),
                    ratingIndex,
                    ratingIndex + host.ratingText.length
                )
            }
        }
        Text(
            modifier = Modifier.background(Yellow),
            text = ratingText
        )

        // Donation
        if (host.showDonation) {
            Text(
                modifier = Modifier
                    .padding(top = 3.dp, bottom = 3.dp)
                    .background(LightGreen),
                text = stringResource(R.string.donation_format, host.donateAmount)
            )
        }

        // Description
        Text(
            modifier = Modifier.padding(top = 6.dp),
            text = host.description
        )

        // "About me" title
        Text(
            modifier = Modifier.padding(top = 10.dp, bottom = 3.dp),
            style = MaterialTheme.typography.titleMedium,
            text = stringResource(R.string.about_me)
        )

        // Host text
        Text(text = host.hostText)

        // Contacts
        if (host.hasContacts) {
            Text(
                modifier = Modifier.padding(top = 12.dp, bottom = 6.dp),
                style = MaterialTheme.typography.titleLarge,
                text = stringResource(R.string.contacts_header)
            )
            ContactsList(contacts = host.contacts)
        }
    }
}

@Preview
@Composable
private fun HostDetailsContentPreview() {
    HiPeopleTheme {
        HostDetailsContent(host = sampleHostDetailsUi)
    }
}

@Composable
fun ContactsList(contacts: Map<String, String>) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        contacts.forEach { (type, value) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp, bottom = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                val contactIntent = remember(type, value) {
                    when (type) {
                        "VK" -> Intent(Intent.ACTION_VIEW, Uri.parse("https://vk.com/id$value"))
                        "Phone" -> Intent(Intent.ACTION_DIAL, Uri.parse("tel:$value"))
                        "Telegram" -> Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/$value"))
                        "Facebook" -> Intent(Intent.ACTION_VIEW, Uri.parse("https://facebook.com/profile.php?id=$value"))
                        else -> null
                    }
                }

                contactIntent?.let { intent ->
                    ContactIconButton(
                        type = type,
                        onClick = { context.startActivity(intent) }
                    )
                }

                Text(
                    modifier = Modifier.weight(1.0f),
                    text = value
                )

                Icon(
                    modifier = Modifier
                        .size(48.dp)
                        .padding(8.dp)
                        .clickable {
                            clipboardManager.setText(AnnotatedString(value))
                        },
                    tint = Blue,
                    imageVector = ImageVector.vectorResource(R.drawable.content_copy),
                    contentDescription = stringResource(R.string.copy_to_clipboard)
                )
            }
        }
    }
}

@Preview
@Composable
private fun ContactsListPreview() {
    HiPeopleTheme {
        ContactsList(contacts = mapOf("Telegram" to "annahost", "Phone" to "+79001234567"))
    }
}

@Composable
private fun ContactIconButton(
    type: String,
    onClick: () -> Unit
) {
    val (icon, tint) = when (type) {
        "Phone" -> Icons.Default.Call to Blue
        "Other" -> Icons.Default.MailOutline to Blue
        "VK" -> ImageVector.vectorResource(R.drawable.vk_logo) to Color.Unspecified
        "Telegram" -> ImageVector.vectorResource(R.drawable.telegram_logo) to Color.Unspecified
        "Facebook" -> ImageVector.vectorResource(R.drawable.facebook_logo) to Color.Unspecified
        else -> return
    }

    Icon(
        modifier = Modifier
            .size(48.dp)
            .padding(8.dp)
            .clickable(onClick = onClick),
        imageVector = icon,
        tint = tint,
        contentDescription = "$type contact"
    )
}

@Composable
fun MutualReviewItem(mutualReview: MutualReviewUi) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        mutualReview.review?.let { review ->
            ReviewCard(
                review = review,
                backgroundColor = LightPeach
            )
        }
        mutualReview.response?.let { response ->
            ReviewCard(
                review = response,
                backgroundColor = LightGreen
            )
        }
    }
}

@Preview
@Composable
private fun MutualReviewItemPreview() {
    HiPeopleTheme {
        MutualReviewItem(mutualReview = sampleMutualReviewUi)
    }
}

@Composable
fun ReviewCard(
    review: ReviewUi,
    backgroundColor: Color
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = backgroundColor, shape = RoundedCornerShape(5.dp))
            .padding(6.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // Author name + receiver if it's a response
        val headerText = if (review.receiverName != null) {
            "${review.authorName} → ${review.receiverName}"
        } else {
            review.authorName
        }
        
        Text(
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            text = headerText
        )

        // Date and type
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                style = MaterialTheme.typography.bodySmall,
                text = review.formattedDate
            )
            Text(
                style = MaterialTheme.typography.bodySmall,
                text = if (review.isGuest) "Guest" else "Host"
            )
        }

        // Review text
        Text(
            style = MaterialTheme.typography.bodyMedium,
            text = review.text
        )

        // Photo if available
        review.photoUrl?.let { photoUrl ->
            AsyncImage(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.5f),
                model = photoUrl,
                contentScale = ContentScale.Crop,
                contentDescription = stringResource(R.string.review_photo_description)
            )
        }
    }
}

@Preview
@Composable
private fun ReviewCardPreview() {
    HiPeopleTheme {
        ReviewCard(review = sampleReviewUi, backgroundColor = LightPeach)
    }
}

private val sampleReviewUi = ReviewUi(
    id = 1,
    authorName = "Max",
    formattedDate = "12.05.2025",
    text = "Great host, very welcoming!",
    photoUrl = null,
    isGuest = true,
    receiverName = null,
)

private val sampleMutualReviewUi = MutualReviewUi(
    review = sampleReviewUi,
    response = sampleReviewUi.copy(id = 2, authorName = "Anna", isGuest = false, receiverName = "Max", text = "Thanks for staying, Max!"),
)

private val sampleHostDetailsUi = HostDetailsUi(
    userId = 1,
    name = "Anna",
    photos = emptyList(),
    languagesText = "English: B2, Russian: Native",
    cityText = "Moscow",
    nameWithAge = "Anna (28 лет)",
    ratingText = "4.5* (10)",
    donateAmount = 3,
    showDonation = true,
    description = "Friendly host, always happy to show guests around the city.",
    hostText = "I have a spare room and love meeting travelers.",
    contacts = mapOf("Telegram" to "annahost", "Phone" to "+79001234567"),
    hasContacts = true,
)
