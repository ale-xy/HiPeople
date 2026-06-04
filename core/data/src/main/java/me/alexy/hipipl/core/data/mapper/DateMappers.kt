package me.alexy.hipipl.core.data.mapper

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private val apiDateTimeFormatter: DateTimeFormatter =
    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

/**
 * Parses an API date-time string ("yyyy-MM-dd HH:mm:ss").
 * Falls back to now() for null or unparseable input.
 */
internal fun String?.parseApiDateTime(): LocalDateTime {
    if (this == null) return LocalDateTime.now()
    return try {
        LocalDateTime.parse(this, apiDateTimeFormatter)
    } catch (e: Exception) {
        LocalDateTime.now()
    }
}
