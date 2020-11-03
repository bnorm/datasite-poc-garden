package com.datasite.poc.garden.report.dto

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String,
    val name: String,
)
