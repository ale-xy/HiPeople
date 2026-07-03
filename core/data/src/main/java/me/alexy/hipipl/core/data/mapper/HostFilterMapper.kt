package me.alexy.hipipl.core.data.mapper

import me.alexy.hipipl.core.domain.HostSearchFilters
import me.alexy.hipipl.core.domain.TriStateFilter

fun HostSearchFilters.toQueryParams(): Map<String, String> = buildMap {
    separateRoom.toQueryValue()?.let { put("separate", it) }
    kidsAllowed.toQueryValue()?.let { put("kids", it) }
    petsAtHome.toQueryValue()?.let { put("pets", it) }
}

private fun TriStateFilter.toQueryValue(): String? = when (this) {
    TriStateFilter.YES -> "y"
    TriStateFilter.NO -> "n"
    TriStateFilter.UNSPECIFIED -> null
}
