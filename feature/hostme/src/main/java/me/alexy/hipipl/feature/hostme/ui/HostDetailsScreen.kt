package me.alexy.hipipl.feature.hostme.ui

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.hippl.model.ContactType
import com.hippl.model.HostUser
import com.hippl.model.MutualReview
import com.hippl.model.Review
import eu.wewox.textflow.material3.TextFlow
import eu.wewox.textflow.material3.TextFlowObstacleAlignment
import me.alexy.hipipl.feature.hostitem.R
import java.time.format.DateTimeFormatter

@Composable
fun HostDetailsScreen(
    modifier: Modifier = Modifier,
    viewModel: HostDetailsViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState.collectAsState()
    HostDetailsScreen(modifier, uiState.value)
}

@Composable
fun HostDetailsScreen(
    modifier: Modifier = Modifier,
    uiState: HostDetailsUiState
) {
    when (uiState) {
        is HostDetailsUiState.Loading -> {
            Box(Modifier.fillMaxSize()) {
                CircularProgressIndicator(Modifier.align(Alignment.Center))
            }
        }

        is HostDetailsUiState.HostLoadSuccess -> {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                HostDetails(uiState.hostDetails)

                Text(
                    style = MaterialTheme.typography.titleLarge,
                    text = stringResource(R.string.reviews_header)
                )
                
                when (uiState.reviewsError) {
                    null ->
                        CircularProgressIndicator(
                            Modifier
                                .padding(10.dp)
                                .align(Alignment.CenterHorizontally)
                        )
                    else -> {
                        Text(uiState.reviewsError)
                    }
                }
            }
        }

        is HostDetailsUiState.Error -> {
            Box(Modifier.fillMaxSize()) {
                Text(uiState.error)
            }
        }
        is HostDetailsUiState.Success -> {
            LazyColumn(
                modifier = modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    HostDetails(uiState.hostDetails)
                }
                item {
                    Text(
                        style = MaterialTheme.typography.titleLarge,
                        text = stringResource(R.string.reviews_header)
                    )
                }
                if (uiState.reviews.isNotEmpty()) {
                    items(uiState.reviews.size) { index ->
                        MutualReview(uiState.reviews[index])
                    }
                } else {
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

@Composable
fun HostDetails(
    host: HostUser
) {
    val placeholder = painterResource(me.alexy.hipipl.core.ui.R.drawable.avatar)

    with(host) {
        val pagerState = rememberPagerState(pageCount = { photos.size })

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth()
        ) { page ->
            AsyncImage(
                modifier = Modifier
                    .aspectRatio(1.0f)
                    .fillMaxWidth(),
                model = photos[page],
                alignment = Alignment.Center,
                contentScale = ContentScale.Crop,
                placeholder = placeholder,
                error = placeholder,
                fallback = placeholder,
                contentDescription = null
            )
        }
        // todo page indicator

        // lang
        val langString = userLanguages.joinToString(", ") { lang ->
            "${lang.langName}: ${lang.level}"
        }
        if (langString.isNotBlank()) {
            Text(stringResource(R.string.lang_format, langString))
        }

        // city
        Text(
            modifier = Modifier.padding(top = 10.dp, bottom = 5.dp),
            style = MaterialTheme.typography.titleLarge,
            text = this.host.city
        )

        // age
        val ageText = if (age > 0) {
            " (${stringResource(R.string.age_format, age)})"
        } else {
            ""
        }

        // name
        val nameText = buildAnnotatedString {
            append(name)
            addStyle(SpanStyle(fontWeight = FontWeight.Bold), 0, name.length)
            append(ageText)
        }
        Text(
            modifier = Modifier.padding(bottom = 5.dp),
            style = MaterialTheme.typography.titleMedium,
            text = nameText
        )

        // rating
        val rating = "$averageRating* ($totalReviews)"
        val ratingString = stringResource(R.string.rating_format, rating)
        val ratingIndex = ratingString.indexOf(rating)
        val ratingText = buildAnnotatedString {
            append(ratingString)
            addStyle(
                SpanStyle(fontWeight = FontWeight.Bold), ratingIndex, ratingIndex + rating.length
            )
        }
        Text(
            modifier = Modifier.background(Color(0xFFFFFF00)),
            text = ratingText
        )

        // donate
        if (donate > 0) {
            Text(
                modifier = Modifier
                    .padding(top = 3.dp, bottom = 3.dp)
                    .background(Color(0xFFC4F9C6))
                ,
                text = stringResource(R.string.donation_format, donate)
            )
        }

        // host description
        Text(text = description)

        // "about me" title
        Text(
            modifier = Modifier.padding(top = 3.dp, bottom = 3.dp),
            style = MaterialTheme.typography.titleMedium,
            text = stringResource(R.string.about_me)
        )

        // user description
        Text(text = this.host.text)

        if (contacts.isNotEmpty()) {
            Text(
                modifier = Modifier.padding(top = 12.dp, bottom = 6.dp),
                style = MaterialTheme.typography.titleLarge,
                text = stringResource(R.string.contacts_header)
            )
            Contacts(contacts)
        }
    }
}

@Composable
private fun ContactIcon(
    type: ContactType,
    modifier: Modifier,
    description: String? = null
) = Icon(
        modifier = modifier,
        imageVector = when(type) {
            ContactType.PHONE -> Icons.Default.Call
            ContactType.OTHER -> Icons.Default.MailOutline
            ContactType.VK -> ImageVector.vectorResource(R.drawable.vk_logo)
            ContactType.TELEGRAM -> ImageVector.vectorResource(R.drawable.telegram_logo)
            ContactType.FACEBOOK -> ImageVector.vectorResource(R.drawable.facebook_logo)
        },
        tint = when(type) {
            ContactType.PHONE, ContactType.OTHER -> Color(0xFF0077FF)
            else -> Color.Unspecified
        },
        contentDescription = "${type.name} $description"
    )

private fun contactIntent(type: ContactType, id: String): Intent? =
    when(type){
        ContactType.VK ->
            Intent(Intent.ACTION_VIEW, Uri.parse("https://vk.com/id$id"))
        ContactType.PHONE ->
            Intent(Intent.ACTION_DIAL, Uri.parse("tel:$id"))
        ContactType.TELEGRAM ->
            Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/$id"))
        ContactType.FACEBOOK ->
            Intent(Intent.ACTION_VIEW, Uri.parse("https://facebook.com/profile.php?id=$id"))
        ContactType.OTHER -> null
    }

@Composable
fun Contacts(contacts: Map<ContactType, String>) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            contacts.entries
                .filterNot { it.key == ContactType.OTHER }
                .sortedBy { it.key }
                .forEach { entry ->
                    val intent = remember { contactIntent(entry.key, entry.value) }

                    intent?.let {
                        ContactIcon(
                            modifier = Modifier
                                .size(64.dp)
                                .weight(1.0f)
                                .clickable {
                                    context.startActivity(it)
                                },
                            type = entry.key,
                            description = entry.key.name,
                        )
                    }
                }
        }
        contacts.entries.firstOrNull { it.key == ContactType.OTHER }?.let { entry ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp, bottom = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    modifier = Modifier
                        .weight(1.0f),
                    text = entry.value
                )
                Icon(
                    modifier = Modifier
                        .size(48.dp)
                        .padding(8.dp)
                        .clickable {
                            clipboardManager.setText(AnnotatedString(entry.value))
                        },
                    tint = Color(0xFF0077FF),
                    imageVector = ImageVector.vectorResource(R.drawable.content_copy),
                    contentDescription = null
                )
            }
        }
    }
}

@Composable
fun MutualReview(mutualReview: MutualReview) {
    with(mutualReview) {
        Column {
            review?.let {
                Review(
                    modifier = Modifier
                        .background(color = Color(0xFFFFE4B5), shape = RoundedCornerShape(5.dp))
                        .padding(6.dp),
                    review = it
                )
            }
            response?.let {
                Review(
                    modifier = Modifier
                        .background(color = Color(0xFFC4F9C6), shape = RoundedCornerShape(5.dp))
                        .padding(6.dp),
                    review = it,
                    receiverName = review?.authorName
                )
            }
        }
    }
}

private val dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

@Composable
fun Review(
    modifier: Modifier,
    review: Review,
    receiverName: String? = null
) {
    with(review) {
        Column(modifier = modifier.fillMaxWidth()) {
            Row(modifier = Modifier.fillMaxWidth()) {

                // name
                val name = if (receiverName != null) {
                    authorName.split(" ").first() + " > " + receiverName
                } else {
                    authorName
                }
                Text(
                    modifier = Modifier.weight(1.0f),
                    fontWeight = FontWeight.Bold,
                    text = name
                )

                // date
                Text(
                    fontWeight = FontWeight.Bold,
                    text = dateTimeFormatter.format(date)
                )
            }

            Row(modifier = Modifier.fillMaxWidth()) {
                // text & image
                TextFlow(
                    modifier = Modifier.weight(1.0f),
                    text = text,
                    obstacleAlignment = TextFlowObstacleAlignment.TopEnd,
                    obstacleContent = {
                        photo?.let {
                            val placeholder =
                                painterResource(me.alexy.hipipl.core.ui.R.drawable.avatar)

                            AsyncImage(
                                modifier = Modifier.size(120.dp),
                                model = it,
                                alignment = Alignment.Center,
                                contentScale = ContentScale.Crop,
                                placeholder = placeholder,
                                error = placeholder,
                                fallback = placeholder,
                                contentDescription = null
                            )
                        }
                    }
                )
            }
        }
    }
}
