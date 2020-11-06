package com.datasite.poc.garden.report.entity

import com.datasite.poc.garden.dto.toUuid
import com.datasite.poc.garden.report.dto.UserEntity
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
        UserEntity(userId.toUuid(), userId, emptyList()),
        com.datasite.poc.garden.report.dto.GardenEntity(gardenId.toUuid(), gardenName),
        viewCount
    )
