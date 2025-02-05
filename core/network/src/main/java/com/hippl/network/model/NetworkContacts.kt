package com.hippl.network.model

import kotlinx.serialization.Serializable


@Serializable
data class NetworkContacts(
    val vk: Long? = null,
    val tg: String? = null,
    val tel: String? = null,
    val fb: Long? = null,
    val extra: String? = null
)