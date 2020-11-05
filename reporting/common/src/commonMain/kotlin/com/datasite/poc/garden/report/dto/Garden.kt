package com.datasite.poc.garden.report.dto

import kotlinx.serialization.Serializable

@Serializable
data class Garden(
    val id: String,
    val name: String,
)
