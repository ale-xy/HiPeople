package me.alexy.hipipl.core.data

import io.ktor.client.HttpClient
import me.alexy.hipipl.core.data.dto.ContactsResponseDto
import me.alexy.hipipl.core.data.dto.SendMessageRequestDto
import me.alexy.hipipl.core.data.dto.SendMessageResponseDto
import me.alexy.hipipl.core.data.mapper.toHostContacts
import me.alexy.hipipl.core.data.mapper.toSendMessageResult
import me.alexy.hipipl.core.domain.DataError
import me.alexy.hipipl.core.domain.HostContacts
import me.alexy.hipipl.core.domain.MessagingRemoteDataSource
import me.alexy.hipipl.core.domain.Result
import me.alexy.hipipl.core.domain.SendMessageResult
import me.alexy.hipipl.core.domain.map

class KtorMessagingDataSource(
    private val httpClient: HttpClient
) : MessagingRemoteDataSource {

    override suspend fun getContacts(
        listingId: Int,
    ): Result<HostContacts, DataError.Network> {
        // Bespoke envelope (data is a list, error.code is numeric) - doesn't fit getV1's
        // ApiResponse<T>/unwrap(), so this uses the raw `get` and maps success/error itself.
        return httpClient.get<ContactsResponseDto>(
            route = "api/v1/contacts",
            queryParameters = mapOf("type" to "host", "id" to listingId)
        ).map { it.toHostContacts() }
    }

    override suspend fun sendMessage(
        targetUserId: Int,
        listingId: Int,
        text: String,
    ): Result<SendMessageResult, DataError.Network> {
        val result = httpClient.postV1<SendMessageRequestDto, SendMessageResponseDto>(
            route = "api/v1/messages",
            body = SendMessageRequestDto(
                text = text,
                targetUserId = targetUserId,
                chatType = "host",
                listingId = listingId,
            )
        )
        return when (result) {
            is Result.Success -> result.data.toSendMessageResult()
                ?.let { Result.Success(it) }
                ?: Result.Error(DataError.Network.SERIALIZATION)

            is Result.Error -> result
        }
    }
}
