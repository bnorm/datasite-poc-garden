package com.datasite.poc.garden.dto

import kotlinx.serialization.Serializable

@Serializable
data class Garden(
    val id: String,
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
