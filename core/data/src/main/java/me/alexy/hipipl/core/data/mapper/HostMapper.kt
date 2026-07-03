package me.alexy.hipipl.core.data.mapper

import me.alexy.hipipl.core.data.dto.ContactsDto
import me.alexy.hipipl.core.data.dto.HostDto
import me.alexy.hipipl.core.data.dto.HostListItemDto
import me.alexy.hipipl.core.data.dto.HostUserDto
import me.alexy.hipipl.core.data.dto.PhotoDto
import me.alexy.hipipl.core.data.dto.UserLangDto
import me.alexy.hipipl.core.data.dto.HostsPageDto
import me.alexy.hipipl.core.domain.ContactType
import me.alexy.hipipl.core.domain.Gender
import me.alexy.hipipl.core.domain.HostDetails
import me.alexy.hipipl.core.domain.HostSearchResult
import me.alexy.hipipl.core.domain.HostUser
import me.alexy.hipipl.core.domain.Photo
import me.alexy.hipipl.core.domain.UserLanguage

// Map HostUserDto (details endpoint) to HostUser
fun HostUserDto.toHostUser(): HostUser? {
    val userIdValue = id ?: return null
    val hostData = host ?: return null
    val hostIdValue = hostData.id ?: return null

    return HostUser(
        userId = userIdValue,
        name = name.orEmpty(),
        gender = sex.toGenderFromString(),
        age = age ?: 0,
        description = about.orEmpty(),
        totalReviews = totalReviews ?: 0,
        contacts = contacts?.toContactMap() ?: mapOf(),
        averageRating = rating ?: 0.0f,
        photos = photos?.mapNotNull { it.toPhoto() } ?: listOf(),
        donate = donate ?: 0,
        userLanguages = userLangs?.mapNotNull { it.toUserLanguage() } ?: listOf(),
        host = hostData.toHostDetails(hostIdValue),
    )
}

// Map HostListItemDto (list endpoint) to HostUser
fun HostListItemDto.toHostUser(): HostUser? {
    val userIdValue = id ?: return null

    return HostUser(
        userId = userIdValue,
        name = name.orEmpty(),
        gender = (sex ?: gender).toGenderFromString(),
        age = age ?: 0,
        description = "",
        totalReviews = totalReviews ?: 0,
        contacts = emptyMap(),
        averageRating = rating?.toFloatOrNull() ?: 0.0f,
        photos = photos?.mapNotNull { it.toPhoto() } ?: listOf(),
        donate = 0,
        userLanguages = emptyList(),
        host = HostDetails(
            hostId = userIdValue,
            text = "",
            correct = 0,
            city = city.orEmpty(),
            dist = distance ?: 0f,
            direction = direction.orEmpty(),
            separateRoom = separate == "y",
            allowKids = kids == "y",
            petsAtHome = pets == "y",
            gender = (sex ?: gender).toGenderFromString(),
            date = null.parseApiDateTime()
        ),
    )
}

// Map HostsPageDto (list endpoint envelope) to HostSearchResult, preserving pagination metadata.
fun HostsPageDto.toHostSearchResult(): HostSearchResult {
    return HostSearchResult(
        hosts = hosts.mapNotNull { it.toHostUser() },
        totalFound = totalFound,
        hasMore = hasMore,
        searchCacheId = searchCacheId,
    )
}

// Parse string gender "m"/"f"/"ppl" to Gender enum
fun String?.toGenderFromString(): Gender {
    return when (this?.lowercase()) {
        "m" -> Gender.MALE
        "f" -> Gender.FEMALE
        "ppl" -> Gender.PEOPLE
        else -> Gender.UNKNOWN
    }
}

// Parse "y"/"n" to Boolean
fun String?.toYesNo(): Boolean {
    return this == "y"
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

private fun UserLangDto.toUserLanguage(): UserLanguage? {
    val langName = name ?: return null
    val langLevel = lvl ?: return null
    
    return UserLanguage(
        langCode = code.orEmpty(),
        langName = langName,
        level = langLevel
    )
}

private fun HostDto.toHostDetails(id: Int): HostDetails {
    return HostDetails(
        hostId = id,
        text = text.orEmpty(),
        correct = if (accurate == true) 1 else 0,
        city = city.orEmpty(),
        dist = dist?.toFloat() ?: 0f,
        direction = degree.orEmpty(),
        separateRoom = separateRoom.toYesNo(),
        allowKids = kidsAllowed.toYesNo(),
        petsAtHome = petsPresent.toYesNo(),
        gender = gender.toGenderFromString(),
        date = date.parseApiDateTime()
    )
}

internal fun PhotoDto.toPhoto(): Photo? {
    val photoUrl = url ?: return null
    return Photo(id = id ?: -1, url = photoUrl)
}
