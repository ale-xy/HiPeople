package com.hipeople.network.repository

import com.hipeople.model.ContactType
import com.hipeople.model.Gender
import com.hipeople.model.HostDetails
import com.hipeople.model.HostUser
import com.hipeople.model.MutualReview
import com.hipeople.model.Photo
import com.hipeople.model.Review
import com.hipeople.model.ReviewType
import com.hipeople.model.UserLanguage
import com.hipeople.network.NetworkDataSource
import com.hipeople.network.model.NetworkContacts
import com.hipeople.network.model.NetworkHost
import com.hipeople.network.model.NetworkHostUser
import com.hipeople.network.model.NetworkMutualReview
import com.hipeople.network.model.NetworkPhoto
import com.hipeople.network.model.NetworkReview
import com.hipeople.network.model.NetworkUserLang
import com.hipeople.repository.HostRepository
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject


class NetworkHostRepository @Inject constructor(
    private val dataSource: NetworkDataSource
) : HostRepository {
    override suspend fun getHostForLocation(locationId: Int): List<HostUser> =
        dataSource.getHostsForLocation(locationId).map { it.toHostUser() }

    override suspend fun getHost(hostId: Int): HostUser =
        dataSource.getHost(hostId).toHostUser()

    override suspend fun getReviews(hostId: Int): List<MutualReview> =
        dataSource.getReviews(hostId).map { it.toMutualReview() }
}

private fun NetworkHostUser.toHostUser() =
    HostUser(
        userId = userId ?: throw IllegalArgumentException("User ID can't be null"),
        name = name.orEmpty(),
        gender = sex.toGender(),
        age = age.orZero(),
        description = descript.orEmpty(),
        totalReviews = totalReviews.orZero(),
        contacts = contacts?.toContactMap() ?: mapOf(),
        averageRating = averageRating ?: 0.0f,
        photos = photos?.filterNotNull()?.map { it.toPhoto() } ?: listOf(),
        donate = donate.orZero(),
        userLanguages =
            userLangs?.map { it.toUserLanguage() } ?: listOf(),
        host =
            hostId?.let { host?.toHostDetails(it) }
                ?: throw IllegalArgumentException("Host ID can't be null"),
    )

private fun Int?.toGender() =
    when(this) {
        1 -> Gender.FEMALE
        2 -> Gender.MALE
        else -> Gender.UNKNOWN
    }

private fun Int?.orZero() : Int = this ?: 0

private fun NetworkContacts.toContactMap(): Map<ContactType, String> {
    val contactMap: MutableMap<ContactType, String> = mutableMapOf()
    vk?.let { contactMap[ContactType.VK] = it.toString() }
    tg?.let { contactMap[ContactType.TELEGRAM] = it }
    tel?.let { contactMap[ContactType.PHONE] = it }
    fb?.let { contactMap[ContactType.FACEBOOK] = it.toString() }
    extra?.let { contactMap[ContactType.OTHER] = it }
    return contactMap.toMap()
}

private fun NetworkUserLang.toUserLanguage() =
    UserLanguage(
        langCode = code,
        langName = name,
        level = lvl
    )

private val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

private fun NetworkHost.toHostDetails(id: Int) =
    HostDetails(
        hostId = id,
        text = text.orEmpty(),
        correct = correct.orZero(),
        city = city.orEmpty(),
        dist = dist.orZero(),
        direction = degree.orEmpty(),
        separateRoom = separate == 1,
        allowKids = kid == 1,
        gender = gender.toGender(),
        date = date?.let { LocalDateTime.parse(date, dateTimeFormatter) } ?: LocalDateTime.now()
    )

//private fun NetworkPhoto.toPhoto() = Photo(id = id, url = url)
private fun NetworkPhoto.toPhoto() = Photo(id = -1, url = this)

private fun NetworkMutualReview.toMutualReview(): MutualReview {
    return MutualReview(
        review = rec?.toReview(),
        response = ans?.toReview()
    )
}

private fun NetworkReview.toReview(): Review {
    return Review(
        id = id,
        receiverId = receiverId,
        authorId = authorId,
        authorName = authorName,
        date = LocalDateTime.parse(date, dateTimeFormatter),
        text = text,
        photo = if (photo.isNullOrBlank()) null else photo,
        type = type.toReviewType(),
        mutal = mutal ?: 0
    )
}

private fun String.toReviewType(): ReviewType {
    return when (this.uppercase()) {
        "GUEST" -> ReviewType.GUEST
        "HOST" -> ReviewType.HOST
        else -> ReviewType.UNKNOWN
    }
}