package com.datasite.poc.garden.dto

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String,
    val name: String,
)
