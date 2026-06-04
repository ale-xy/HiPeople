package me.alexy.hipipl.core.data.mapper

import me.alexy.hipipl.core.data.dto.ContactsDto
import me.alexy.hipipl.core.data.dto.HostDto
import me.alexy.hipipl.core.data.dto.HostUserDto
import me.alexy.hipipl.core.data.dto.PhotoDto
import me.alexy.hipipl.core.data.dto.UserLangDto
import me.alexy.hipipl.core.domain.ContactType
import me.alexy.hipipl.core.domain.Gender
import me.alexy.hipipl.core.domain.HostDetails
import me.alexy.hipipl.core.domain.HostUser
import me.alexy.hipipl.core.domain.Photo
import me.alexy.hipipl.core.domain.UserLanguage

fun HostUserDto.toHostUser(): HostUser? {
    val userIdValue = userId ?: return null
    val hostIdValue = hostId ?: return null

    return HostUser(
        userId = userIdValue,
        name = name.orEmpty(),
        gender = sex.toGender(),
        age = age ?: 0,
        description = descript.orEmpty(),
        totalReviews = totalReviews ?: 0,
        contacts = contacts?.toContactMap() ?: mapOf(),
        averageRating = averageRating ?: 0.0f,
        photos = photos?.mapNotNull { it?.toPhoto() } ?: listOf(),
        donate = donate ?: 0,
        userLanguages = userLangs?.map { it.toUserLanguage() } ?: listOf(),
        host = host?.toHostDetails(hostIdValue) ?: return null,
    )
}

private fun Int?.toGender(): Gender {
    return when (this) {
        1 -> Gender.FEMALE
        2 -> Gender.MALE
        else -> Gender.UNKNOWN
    }
}

private fun ContactsDto.toContactMap(): Map<ContactType, String> {
    val contactMap: MutableMap<ContactType, String> = mutableMapOf()
    vk?.let { contactMap[ContactType.VK] = it.toString() }
    tg?.let { contactMap[ContactType.TELEGRAM] = it }
    tel?.let { contactMap[ContactType.PHONE] = it }
    fb?.let { contactMap[ContactType.FACEBOOK] = it.toString() }
    extra?.let { contactMap[ContactType.OTHER] = it }
    return contactMap.toMap()
}

private fun UserLangDto.toUserLanguage(): UserLanguage {
    return UserLanguage(
        langCode = code,
        langName = name,
        level = lvl
    )
}

private fun HostDto.toHostDetails(id: Int): HostDetails {
    return HostDetails(
        hostId = id,
        text = text.orEmpty(),
        correct = correct ?: 0,
        city = city.orEmpty(),
        dist = dist ?: 0,
        direction = degree.orEmpty(),
        separateRoom = separate == 1,
        allowKids = kid == 1,
        gender = gender.toGender(),
        date = date.parseApiDateTime()
    )
}

private fun PhotoDto.toPhoto(): Photo {
    return Photo(id = -1, url = this)
}
