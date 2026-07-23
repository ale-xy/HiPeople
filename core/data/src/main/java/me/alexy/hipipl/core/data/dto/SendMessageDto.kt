package me.alexy.hipipl.core.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SendMessageRequestDto(
    val text: String,
    @SerialName("target_user_id") val targetUserId: Int,
    @SerialName("chat_type") val chatType: String,
    @SerialName("listing_id") val listingId: Int? = null,
)

@Serializable
data class SendMessageResponseDto(
    @SerialName("chat_id") val chatId: Int? = null,
    @SerialName("chat_type") val chatType: String? = null,
    @SerialName("message_id") val messageId: Int? = null,
    @SerialName("can_send") val canSend: Boolean? = null,
    @SerialName("pending_without_reply") val pendingWithoutReply: Int? = null,
)
