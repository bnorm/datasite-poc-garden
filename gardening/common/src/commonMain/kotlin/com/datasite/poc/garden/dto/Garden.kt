package com.datasite.poc.garden.dto

import kotlinx.serialization.Serializable

@Serializable
data class Garden(
    @Serializable(with = UuidSerializer::class)
    val id: Uuid,
    val name: String,
)

@Serializable
data class GardenPrototype(
    val name: String,
)

@Serializable
data class GardenPatch(
    val name: String? = null,
)
