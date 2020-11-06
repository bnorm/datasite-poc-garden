package com.datasite.poc.garden.report.entity

import com.datasite.poc.garden.report.dto.GardenEntity
import com.datasite.poc.garden.report.dto.GardenSensorReport
import java.util.*

data class GardenSensorTotalSelectRow(
    val gardenId: UUID,
    val gardenName: String,
    val readingSum: Long,
    val readingCount: Long,
)

fun List<GardenSensorTotalSelectRow>.toReport() =
    GardenSensorReport(map { it.toMetric() })

fun GardenSensorTotalSelectRow.toMetric() =
    GardenSensorReport.Metric(GardenEntity(gardenId, gardenName), readingSum, readingCount)
