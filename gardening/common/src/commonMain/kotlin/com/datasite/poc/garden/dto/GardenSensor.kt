package com.datasite.poc.garden.dto

import kotlinx.serialization.Serializable

@Serializable
data class GardenSensor(
    @Serializable(with = UuidSerializer::class)
    val id: Uuid,
    val name: String,
    val garden: Garden,
)

@Serializable
data class GardenSensorPrototype(
    val name: String,
    @Serializable(with = UuidSerializer::class)
    val gardenId: Uuid,
)

@Serializable
data class GardenSensorPatch(
    val name: String? = null,
    @Serializable(with = UuidSerializer::class)
    val gardenId: Uuid? = null,
)
