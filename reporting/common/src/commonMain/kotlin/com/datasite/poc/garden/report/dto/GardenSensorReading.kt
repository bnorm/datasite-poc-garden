package com.datasite.poc.garden.report.dto

import com.datasite.poc.garden.dto.InstantSerializer
import com.datasite.poc.garden.dto.Uuid
import com.datasite.poc.garden.dto.UuidSerializer
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class GardenSensorReading(
    @Serializable(with = InstantSerializer::class)
    val timestamp: Instant,
    @Serializable(with = UuidSerializer::class)
    val gardenId: Uuid,
    @Serializable(with = UuidSerializer::class)
    val sensorId: Uuid,
    val value: Long,
)
