package me.alexy.hipipl.core.data.mapper

import me.alexy.hipipl.core.data.dto.ContactsDto
import me.alexy.hipipl.core.data.dto.HostDto
import me.alexy.hipipl.core.data.dto.HostListItemDto
import me.alexy.hipipl.core.data.dto.HostUserDto
import me.alexy.hipipl.core.data.dto.HostsPageDto
import me.alexy.hipipl.core.data.dto.PhotoDto
import me.alexy.hipipl.core.data.dto.UserLangDto
import me.alexy.hipipl.core.data.dto.UserVibeDto
import me.alexy.hipipl.core.domain.ActivityStatus
import me.alexy.hipipl.core.domain.ContactType
import me.alexy.hipipl.core.domain.Gender
import me.alexy.hipipl.core.domain.HostDetails
import me.alexy.hipipl.core.domain.HostSearchResult
import me.alexy.hipipl.core.domain.HostUser
import me.alexy.hipipl.core.domain.HostVibe
import me.alexy.hipipl.core.domain.Photo
import me.alexy.hipipl.core.domain.UserLanguage
import me.alexy.hipipl.core.domain.VibeCode

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
        lastActivity = lastActivity.toActivityStatus(),
        vibes = userVibes?.mapNotNull { it.toHostVibe() } ?: listOf(),
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
    wa?.let { contactMap[ContactType.WHATSAPP] = it }
    extra?.let { contactMap[ContactType.OTHER] = it }
    return contactMap.toMap()
}

private fun String?.toActivityStatus(): ActivityStatus? {
    return when (this) {
        "today" -> ActivityStatus.TODAY
        "recently" -> ActivityStatus.RECENTLY
        "long ago" -> ActivityStatus.LONG_AGO
        else -> null
    }
}

private fun UserVibeDto.toHostVibe(): HostVibe? {
    val vibeLabel = label ?: return null
    val vibeCode = code ?: return null
    return HostVibe(label = vibeLabel, code = vibeCode.toVibeCode())
}

private fun String.toVibeCode(): VibeCode = when (this) {
    "active_sport" -> VibeCode.ACTIVE_SPORT
    "against_alcohol" -> VibeCode.AGAINST_ALCOHOL
    "ambivert" -> VibeCode.AMBIVERT
    "business" -> VibeCode.BUSINESS
    "creativity" -> VibeCode.CREATIVITY
    "dont_care" -> VibeCode.DONT_CARE
    "escapism" -> VibeCode.ESCAPISM
    "esoterics" -> VibeCode.ESOTERICS
    "extrovert" -> VibeCode.EXTROVERT
    "healthy_lifestyle" -> VibeCode.HEALTHY_LIFESTYLE
    "introvert" -> VibeCode.INTROVERT
    "luxury_fashion" -> VibeCode.LUXURY_FASHION
    "melancholy" -> VibeCode.MELANCHOLY
    "neutral_alcohol" -> VibeCode.NEUTRAL_ALCOHOL
    "parties" -> VibeCode.PARTIES
    "pro_alcohol" -> VibeCode.PRO_ALCOHOL
    "science" -> VibeCode.SCIENCE
    "silence" -> VibeCode.SILENCE
    "soulful_evenings" -> VibeCode.SOULFUL_EVENINGS
    else -> VibeCode.UNKNOWN
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
