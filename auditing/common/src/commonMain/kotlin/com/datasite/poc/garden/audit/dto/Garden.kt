package com.datasite.poc.garden.audit.dto

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class Garden(
    @SerialName("_id")
    val id: ObjectId,
    val name: String
)

@Serializable
data class ObjectId(
    @SerialName("\$oid")
    val oid: String
)
