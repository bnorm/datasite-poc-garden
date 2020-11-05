package com.datasite.poc.garden.event

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MongoId(
    @SerialName("\$binary")
    val binary: String,
    @SerialName("\$type")
    val type: String
)
