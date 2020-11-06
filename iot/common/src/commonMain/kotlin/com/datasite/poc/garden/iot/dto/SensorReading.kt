package com.datasite.poc.garden.iot.dto

import com.datasite.poc.garden.dto.Uuid
import com.datasite.poc.garden.dto.UuidSerializer
import kotlinx.datetime.Clock
import kotlinx.serialization.Serializable

@Serializable
data class SensorReading(
        // TODO This could be Instant if Jackson knew how to serialize it
        val timestamp: Long = Clock.System.now().toEpochMilliseconds(),
        @Serializable(with = UuidSerializer::class)
        val sensorId: Uuid,
        val value: Long
)
