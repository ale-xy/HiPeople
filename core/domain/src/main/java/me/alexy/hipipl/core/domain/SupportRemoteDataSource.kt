package me.alexy.hipipl.core.domain

interface SupportRemoteDataSource {
    suspend fun submitComplaint(
        message: String,
        userId: Int?,
        additionalData: Map<String, String>,
    ): EmptyResult<DataError.Network>
}
