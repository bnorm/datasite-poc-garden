package com.datasite.poc.garden.report.entity

import com.datasite.poc.garden.report.dto.GardenSensorEntity
import com.datasite.poc.garden.report.dto.SensorReport
import io.r2dbc.spi.Row
import java.util.*

data class SensorTotalSelectRow(
    val sensorId: UUID,
    val sensorName: String,
    val gardenId: UUID,
    val readingSum: Long,
    val readingCount: Long,
)

fun Row.toSensorTotalSelectRow() = SensorTotalSelectRow(
    get(SensorTotalSelectRow::sensorId),
    get(SensorTotalSelectRow::sensorName),
    get(SensorTotalSelectRow::gardenId),
    get(SensorTotalSelectRow::readingSum),
    get(SensorTotalSelectRow::readingCount),
)

fun List<SensorTotalSelectRow>.toReport() =
    SensorReport(map { it.toMetric() })

fun SensorTotalSelectRow.toMetric() =
    SensorReport.Metric(GardenSensorEntity(sensorId, sensorName, gardenId), readingSum, readingCount)
