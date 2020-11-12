package com.datasite.poc.garden.report.entity

import io.r2dbc.spi.Row
import org.springframework.data.relational.core.mapping.Table
import java.util.*

const val GARDEN_SENSOR_TABLE = "garden_sensors"

@Table(GARDEN_SENSOR_TABLE)
class GardenSensorPgEntity(
    val id: UUID,
    val name: String,
    val gardenId: UUID,
)

fun Row.toGardenSensorPgEntity() = GardenSensorPgEntity(
    get(GardenSensorPgEntity::id),
    get(GardenSensorPgEntity::name),
    get(GardenSensorPgEntity::gardenId),
)
