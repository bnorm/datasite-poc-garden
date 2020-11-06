package com.datasite.poc.garden.event

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class MongoGardenSensor(
    @SerialName("_id")
    val id: MongoId,
    val name: String,
    val gardenId: MongoId,
)
