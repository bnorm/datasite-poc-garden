package com.datasite.poc.garden.event

import com.datasite.poc.garden.dto.Uuid
import com.datasite.poc.garden.dto.UuidSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MongoUuid(
    @SerialName("\$uuid")
    @Serializable(with = UuidSerializer::class)
    val uuid: Uuid,
)
