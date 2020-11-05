package com.datasite.poc.garden.report.entity

import com.datasite.poc.garden.report.dto.Garden
import com.datasite.poc.garden.report.dto.User
import com.datasite.poc.garden.report.dto.UsersFavoriteGardenReport

data class UserGardenViewCountEntity(
    val userId: String,
    val gardenId: String,
    val gardenName: String,
    val viewCount: Long
)

fun List<UserGardenViewCountEntity>.toReport() =
    UsersFavoriteGardenReport(map { it.toMetric() })

fun UserGardenViewCountEntity.toMetric() =
    UsersFavoriteGardenReport.Metric(
        User(userId, userId),
        Garden(gardenId, gardenName),
        viewCount
    )
