package com.datasite.poc.garden.audit.dto

import com.datasite.poc.garden.dto.Uuid
import com.datasite.poc.garden.dto.UuidSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class MongoGarden(
    @SerialName("_id")
    val id: MongoId,
    val name: String
)

@Serializable
data class MongoId(
    @SerialName("\$binary")
    val binary: String,
    @SerialName("\$type")
    val type: String
)

@Serializable
data class MongoUuid(
    @SerialName("\$uuid")
    @Serializable(with = UuidSerializer::class)
    val uuid: Uuid,
)
