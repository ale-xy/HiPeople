package me.alexy.hipipl.core.domain

enum class TriStateFilter { YES, NO, UNSPECIFIED }

data class HostSearchFilters(
    val separateRoom: TriStateFilter = TriStateFilter.UNSPECIFIED,
    val kidsAllowed: TriStateFilter = TriStateFilter.UNSPECIFIED,
    val petsAtHome: TriStateFilter = TriStateFilter.UNSPECIFIED,
)

data class HostSearchResult(
    val hosts: List<HostUser>,
    val totalFound: Int,
    val hasMore: Boolean,
    val searchCacheId: String?,
)
