package com.hippl.network.model

import kotlinx.serialization.Serializable


@Serializable
data class NetworkContacts(
    val vk: Int? = null,
    val tg: String? = null,
    val tel: String? = null,
    val fb: String? = null,
    val extra: String? = null
)