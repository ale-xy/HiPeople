package me.alexy.hipipl.core.domain

interface ContactSearchRemoteDataSource {
    suspend fun findByQuery(
        query: String,
        userId: Int?,
    ): Result<ContactSearchResult, DataError.Network>
}

sealed interface ContactSearchResult {
    data class UserFound(
        val id: Int,
        val name: String,
        val age: Int,
        val photos: List<Photo>,
    ) : ContactSearchResult

    // Covers the "hosts"/"geo" success shapes and the "not found" shape -
    // none of which this search screen's Users tab needs to render.
    data object NotFoundOrOther : ContactSearchResult
}
