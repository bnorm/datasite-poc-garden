package com.datasite.poc.garden.dto

data class Garden(
    val id: String,
    val name: String,
)

data class GardenPrototype(
    val name: String,
)

data class GardenPatch(
    val name: String? = null,
)
