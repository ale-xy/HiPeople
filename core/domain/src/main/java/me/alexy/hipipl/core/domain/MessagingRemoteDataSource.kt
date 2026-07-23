package me.alexy.hipipl.core.domain

import java.time.LocalDateTime

interface MessagingRemoteDataSource {
    suspend fun getContacts(
        userId: Int,
        targetUserId: Int,
    ): Result<HostContacts, DataError.Network>

    suspend fun sendMessage(
        targetUserId: Int,
        listingId: Int,
        text: String,
    ): Result<SendMessageResult, DataError.Network>
}

sealed interface HostContacts {
    data class Available(val contacts: Map<ContactType, List<String>>) : HostContacts
    data object ProfileIncomplete : HostContacts
    data class UnansweredReview(val review: PendingReview) : HostContacts
    data object PrivacyHidden : HostContacts
}

data class PendingReview(
    val whoId: Int,
    val whoName: String,
    val text: String,
    val date: LocalDateTime,
    val type: String,
    val photo: String?,
)

data class SendMessageResult(
    val chatId: Int,
    val canSend: Boolean,
    val pendingWithoutReply: Int,
)
