package me.alexy.hipipl.core.data

import com.hippl.model.ContactType
import com.hippl.model.Gender
import com.hippl.model.HostDetails
import com.hippl.model.HostUser
import com.hippl.model.UserLanguage
import com.hippl.network.NetworkDataSource
import com.hippl.network.model.NetworkContacts
import com.hippl.network.model.NetworkHost
import com.hippl.network.model.NetworkHostUser
import com.hippl.network.model.NetworkUserLang
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

interface HostRepository {
    suspend fun getHostForLocation(locationId: Int): List<HostUser>
}

class DefaultHostRepository @Inject constructor(
    private val dataSource: NetworkDataSource
) : HostRepository {
    override suspend fun getHostForLocation(locationId: Int): List<HostUser> =
        dataSource.getHostsForLocation(locationId).map { it.toHostUser() }
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
        photos = photos?.filterNotNull() ?: listOf(),
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
        date = LocalDateTime.parse(date, dateTimeFormatter)
    )