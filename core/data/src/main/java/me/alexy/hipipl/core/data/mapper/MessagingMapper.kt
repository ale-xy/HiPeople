package me.alexy.hipipl.core.data.mapper

import me.alexy.hipipl.core.data.dto.SendMessageResponseDto
import me.alexy.hipipl.core.domain.SendMessageResult

fun SendMessageResponseDto.toSendMessageResult(): SendMessageResult? {
    val id = chatId ?: return null
    return SendMessageResult(
        chatId = id,
        canSend = canSend ?: true,
        pendingWithoutReply = pendingWithoutReply ?: 0,
    )
}
