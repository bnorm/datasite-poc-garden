package com.datasite.poc.garden.report.entity

import org.springframework.data.relational.core.mapping.Table
import java.util.*

const val SENSOR_ACCUMULATION = "sensor_accumulations"

@Table(SENSOR_ACCUMULATION)
class SensorAccumulationPgEntity(
    val sensorId: UUID,
    val readingSum: Long,
    val readingCount: Long,
)
