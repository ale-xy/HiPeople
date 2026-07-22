package me.alexy.hipipl.core.data.mapper

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private val apiDateFormatter: DateTimeFormatter =
    DateTimeFormatter.ofPattern("yyyy-MM-dd")

/**
 * Parses an API date string ("yyyy-MM-dd" — reviews have no time component).
 * Falls back to now() for null or unparseable input.
 */
internal fun String?.parseApiDateTime(): LocalDateTime {
    if (this == null) return LocalDateTime.now()
    return try {
        LocalDate.parse(this, apiDateFormatter).atStartOfDay()
    } catch (e: Exception) {
        LocalDateTime.now()
    }
}
