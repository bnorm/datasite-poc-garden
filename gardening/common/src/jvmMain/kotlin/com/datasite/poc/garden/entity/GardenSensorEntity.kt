package com.datasite.poc.garden.entity

import com.datasite.poc.garden.dto.Garden
import com.datasite.poc.garden.dto.GardenSensor
import com.datasite.poc.garden.dto.GardenSensorPrototype
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

const val GARDEN_SENSOR_COLLECTION = "garden_sensors"

@Document(GARDEN_SENSOR_COLLECTION)
data class GardenSensorEntity(
    @Id val id: UUID = UUID.randomUUID(),
    val name: String,
    val gardenId: UUID,
)

fun GardenSensorEntity.toGardenSensor(garden: Garden) = GardenSensor(
    id = id,
    name = name,
    garden = garden,
)

fun GardenSensorPrototype.toEntity() = GardenSensorEntity(
    name = name,
    gardenId = gardenId,
)
