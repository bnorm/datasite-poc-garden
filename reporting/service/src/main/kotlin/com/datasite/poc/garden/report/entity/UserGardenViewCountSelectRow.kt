package com.datasite.poc.garden.report.entity

import com.datasite.poc.garden.report.dto.GardenEntity
import com.datasite.poc.garden.report.dto.UserEntity
import com.datasite.poc.garden.report.dto.UsersFavoriteGardenReport
import java.util.*

data class UserGardenViewCountSelectRow(
    val userId: UUID,
    val userName: String,
    val gardenId: UUID,
    val gardenName: String,
    val viewCount: Long
)

fun List<UserGardenViewCountSelectRow>.toReport() =
    UsersFavoriteGardenReport(map { it.toMetric() })

fun UserGardenViewCountSelectRow.toMetric() =
    UsersFavoriteGardenReport.Metric(
        UserEntity(userId, userName, emptyList()),
        GardenEntity(gardenId, gardenName),
        viewCount
    )
