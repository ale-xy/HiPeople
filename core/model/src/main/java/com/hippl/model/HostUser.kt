package com.hippl.model

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
    val photos: List<String>,
    val donate: Int,
    val userLanguages: List<UserLanguage>,
    val host: HostDetails,
)

data class UserLanguage(
    val langCode: String,
    val level: Int,
)

data class HostDetails(
    val hostId: Int,
    val text: String,
    val correct: Int,
    val city: String,
    val dist: Int,
    val date: LocalDateTime,
    val separateRoom: Boolean,
    val allowKids: Boolean,
    val gender: Gender
)

enum class ContactType {
    VK,
    PHONE,
    TELEGRAM,
    FACEBOOK,
    OTHER
}

enum class Gender {
    MALE,
    FEMALE,
    UNKNOWN
}