package com.datasite.poc.garden.event

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class MongoUser(
    @SerialName("_id")
    val id: MongoId,
    val name: String,
    val gardenIds: List<MongoId>,
)
