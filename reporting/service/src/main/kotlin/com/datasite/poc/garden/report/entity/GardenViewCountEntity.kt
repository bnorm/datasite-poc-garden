package com.datasite.poc.garden.report.entity

import com.datasite.poc.garden.dto.toUuid
import com.datasite.poc.garden.report.dto.MostPopularGardensReport

data class GardenViewCountEntity(
    val id: String,
    val name: String,
    val viewCount: Long
)

fun List<GardenViewCountEntity>.toReport() =
    MostPopularGardensReport(map { it.toMetric() })

fun GardenViewCountEntity.toMetric() =
    MostPopularGardensReport.Metric(com.datasite.poc.garden.report.dto.GardenEntity(id.toUuid(), name), viewCount)
