package me.alexy.hipipl.core.data.mapper

import me.alexy.hipipl.core.data.dto.ContactsResponseDto
import me.alexy.hipipl.core.data.dto.PendingReviewDto
import me.alexy.hipipl.core.domain.ContactType
import me.alexy.hipipl.core.domain.HostContacts
import me.alexy.hipipl.core.domain.PendingReview

// Response error codes: 1 = profile incomplete, 2 = unanswered review, 3 = privacy hides contacts.
fun ContactsResponseDto.toHostContacts(): HostContacts {
    return when (error?.code) {
        1 -> HostContacts.ProfileIncomplete
        2 -> HostContacts.UnansweredReview(review?.toPendingReview() ?: return HostContacts.ProfileIncomplete)
        3 -> HostContacts.PrivacyHidden
        else -> HostContacts.Available(data.toContactTypeMap())
    }
}

private fun List<Map<String, String>>.toContactTypeMap(): Map<ContactType, List<String>> {
    val result = mutableMapOf<ContactType, MutableList<String>>()
    forEach { entry ->
        entry.forEach { (key, value) ->
            val type = key.toContactType() ?: return@forEach
            result.getOrPut(type) { mutableListOf() }.add(value)
        }
    }
    return result
}

private fun String.toContactType(): ContactType? {
    return when (this) {
        "vk" -> ContactType.VK
        "tg" -> ContactType.TELEGRAM
        "fb", "fb_psid" -> ContactType.FACEBOOK
        "wa" -> ContactType.WHATSAPP
        "phone" -> ContactType.PHONE
        "extra" -> ContactType.OTHER
        else -> null // "cs" (Couchsurfing) not modeled yet
    }
}

private fun PendingReviewDto.toPendingReview(): PendingReview? {
    val id = whoId ?: return null
    val reviewerName = whoName ?: return null
    val reviewText = text ?: return null
    val reviewType = type ?: return null
    return PendingReview(
        whoId = id,
        whoName = reviewerName,
        text = reviewText,
        date = date.parseApiDateTime(),
        type = reviewType,
        photo = photo,
    )
}
