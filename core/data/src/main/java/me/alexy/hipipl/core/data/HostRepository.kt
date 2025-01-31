package me.alexy.hipipl.core.data

import com.hippl.model.ContactType
import com.hippl.model.Gender
import com.hippl.model.HostDetails
import com.hippl.model.HostUser
import com.hippl.model.Location
import com.hippl.model.UserLanguage
import com.hippl.network.NetworkDataSource
import com.hippl.network.model.NetworkContacts
import com.hippl.network.model.NetworkHost
import com.hippl.network.model.NetworkHostUser
import java.time.LocalDateTime
import javax.inject.Inject

interface HostRepository {
    suspend fun getHostForLocation(location: Location): List<HostUser>
}

class DefaultHostRepository @Inject constructor(
    private val dataSource: NetworkDataSource
) : HostRepository {
    override suspend fun getHostForLocation(location: Location): List<HostUser> =
        dataSource.getHostsForLocation(location.id).map { it.toHostUser() }
}

private fun NetworkHostUser.toHostUser() =
    HostUser(
        userId = userId ?: throw IllegalArgumentException("User ID can't be null"),
        name = name.orEmpty(),
        gender = sex.toGender(),
        age = age?.toInt().orZero(),
        description = descript.orEmpty(),
        totalReviews = totalReviews.orZero(),
        contacts = contacts?.toContactMap() ?: mapOf(),
        averageRating = averageRating ?: 0.0f,
        photos = photos,
        donate = donate.orZero(),
        userLanguages =
            userLangs?.let { listOf(UserLanguage(it, 0)) } ?: listOf(), //todo
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
    fb?.let { contactMap[ContactType.FACEBOOK] = it }
    extra?.let { contactMap[ContactType.OTHER] = it }
    return contactMap.toMap()
}

private fun NetworkHost.toHostDetails(id: Int) =
    HostDetails(
        hostId = id,
        text = text.orEmpty(),
        correct = correct.orZero(),
        city = city.orEmpty(),
        dist = dist.orZero(),
        separateRoom = separate == 1,
        allowKids = kid == 1,
        gender = gender.toGender(),
        date = LocalDateTime.parse(text)
    )