package me.alexy.hipipl.core.domain

import java.time.LocalDateTime

data class HostUser(
    val userId: Int,
    val name: String,
    val gender: Gender,
    val age: Int,
    val description: String,
    val totalReviews: Int,
    val contacts: Map<ContactType, String>,
    val averageRating: Float,
    val photos: List<Photo>,
    val donate: Int,
    val userLanguages: List<UserLanguage>,
    val host: HostDetails,
    val lastActivity: ActivityStatus? = null,
    val vibes: List<HostVibe> = emptyList(),
)

enum class ActivityStatus { TODAY, RECENTLY, LONG_AGO }

data class HostVibe(val label: String, val code: String)

data class UserLanguage(
    val langCode: String,
    val langName: String,
    val level: Int,
)

data class HostDetails(
    val hostId: Int,
    val text: String,
    val correct: Int,
    val city: String,
    val dist: Float,
    val direction: String,
    val date: LocalDateTime,
    val separateRoom: Boolean,
    val allowKids: Boolean,
    val petsAtHome: Boolean,
    val gender: Gender
)

data class Photo(
    val id: Int,
    val url: String
)

enum class ContactType {
    VK,
    TELEGRAM,
    FACEBOOK,
    WHATSAPP,
    PHONE,
    OTHER
}

enum class Gender {
    MALE,
    FEMALE,
    PEOPLE,
    UNKNOWN
}
