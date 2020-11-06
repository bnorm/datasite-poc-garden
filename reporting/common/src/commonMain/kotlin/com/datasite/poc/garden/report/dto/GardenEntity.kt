package com.datasite.poc.garden.report.dto

import com.datasite.poc.garden.dto.Uuid
import com.datasite.poc.garden.dto.UuidSerializer
import kotlinx.serialization.Serializable

@Serializable
data class GardenEntity(
    @Serializable(with = UuidSerializer::class)
    val id: Uuid,
    val name: String,
)
