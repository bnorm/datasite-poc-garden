package com.datasite.poc.garden.report.entity

import com.datasite.poc.garden.report.dto.MostPopularGardensReport
import io.r2dbc.spi.Row
import java.util.*

data class GardenViewCountSelectRow(
    val id: UUID,
    val name: String,
    val viewCount: Long
)

fun Row.toGardenViewCountSelectRow() = GardenViewCountSelectRow(
    get(GardenViewCountSelectRow::id),
    get(GardenViewCountSelectRow::name),
    get(GardenViewCountSelectRow::viewCount),
)

fun List<GardenViewCountSelectRow>.toReport() =
    MostPopularGardensReport(map { it.toMetric() })

fun GardenViewCountSelectRow.toMetric() =
    MostPopularGardensReport.Metric(com.datasite.poc.garden.report.dto.GardenEntity(id, name), viewCount)
