package me.alexy.hipipl.core.data

import io.ktor.client.HttpClient
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import me.alexy.hipipl.core.data.dto.SupportRequestDto
import me.alexy.hipipl.core.data.dto.SupportResponseDto
import me.alexy.hipipl.core.domain.DataError
import me.alexy.hipipl.core.domain.EmptyResult
import me.alexy.hipipl.core.domain.SupportRemoteDataSource
import me.alexy.hipipl.core.domain.asEmptyResult

class KtorSupportDataSource(
    private val httpClient: HttpClient
) : SupportRemoteDataSource {

    override suspend fun submitComplaint(
        message: String,
        userId: Int?,
        additionalData: Map<String, String>,
    ): EmptyResult<DataError.Network> {
        val additionalDataJson = buildJsonObject {
            additionalData.forEach { (key, value) -> put(key, JsonPrimitive(value)) }
        }
        return httpClient.postV1<SupportRequestDto, SupportResponseDto>(
            route = "api/v1/support",
            body = SupportRequestDto(
                type = "user_complaint",
                message = message,
                userId = userId,
                additionalData = additionalDataJson.toString(),
            )
        ).asEmptyResult()
    }
}
