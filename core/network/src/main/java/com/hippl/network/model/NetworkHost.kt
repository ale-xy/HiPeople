package com.hippl.network.model

import kotlinx.serialization.Serializable


@Serializable
data class NetworkHost(
    val text: String? = null,
    val correct: Int? = null,
    val degree: String? = null,
    val dist: Int? = null,
    val date: String? = null,
    val city: String? = null,
    val separate: Int? = null,
    val kid: Int? = null,
    val gender: Int? = null
)