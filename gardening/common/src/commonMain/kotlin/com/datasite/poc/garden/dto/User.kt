package com.datasite.poc.garden.dto

import kotlinx.serialization.Serializable

@Serializable
data class User(
    @Serializable(with = UuidSerializer::class)
    val id: Uuid,
    val name: String,
    val gardens: List<Garden>
)

@Serializable
data class UserPrototype(
    val name: String,
    val gardenIds: List<@Serializable(with = UuidSerializer::class) Uuid> = emptyList(),
)

@Serializable
data class UserPatch(
    val name: String? = null,
    val gardenIds: List<@Serializable(with = UuidSerializer::class) Uuid>? = null,
)
