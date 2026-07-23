package me.alexy.hipipl.core.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class ContactsDto(
    val vk: Long? = null,
    val tg: String? = null,
    val tel: String? = null,
    val fb: Long? = null,
    val wa: String? = null,
    val extra: String? = null
)
