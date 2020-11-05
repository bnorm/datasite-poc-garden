package com.datasite.poc.garden.iot.dto

import kotlinx.serialization.Serializable

@Serializable
data class SensorReading(
        val timestamp: Long,
        val sensorId: String,
        val value: Long
)
