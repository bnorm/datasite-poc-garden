package com.datasite.poc.garden.report.entity

import org.springframework.data.relational.core.mapping.Table
import java.util.*

const val GARDEN_SENSOR_ACCUMULATION = "garden_sensor_accumulations"

@Table(GARDEN_SENSOR_ACCUMULATION)
class GardenSensorAccumulationPgEntity(
    val gardenId: UUID,
    val readingSum: Long,
    val readingCount: Long,
)
