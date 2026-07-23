package me.alexy.hipipl.core.domain

data class Account(
    val userId: Int,
    val name: String,
    val gender: Gender,
    val age: Int,
    val description: String,
    val totalReviews: Int,
    val averageRating: Float,
    val photos: List<Photo>,
    val donate: Int,
    val userLanguages: List<UserLanguage>,
)

sealed interface AuthState {
    data object Unauthenticated : AuthState
    data class Authenticated(val userId: Int, val account: Account?) : AuthState
}
