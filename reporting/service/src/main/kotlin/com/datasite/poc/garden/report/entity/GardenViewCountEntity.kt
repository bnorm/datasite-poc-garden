package com.datasite.poc.garden.report.entity

import com.datasite.poc.garden.report.dto.Garden
import com.datasite.poc.garden.report.dto.MostPopularGardensReport

data class GardenViewCountEntity(
    val id: String,
    val name: String,
    val viewCount: Long
)

fun List<GardenViewCountEntity>.toReport() =
    MostPopularGardensReport(map { it.toMetric() })

fun GardenViewCountEntity.toMetric() =
    MostPopularGardensReport.Metric(Garden(id, name), viewCount)
