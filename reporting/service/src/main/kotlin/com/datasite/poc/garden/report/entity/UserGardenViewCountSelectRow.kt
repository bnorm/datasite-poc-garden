package com.datasite.poc.garden.report.entity

import com.datasite.poc.garden.report.dto.GardenEntity
import com.datasite.poc.garden.report.dto.UserEntity
import com.datasite.poc.garden.report.dto.UsersFavoriteGardenReport
import io.r2dbc.spi.Row
import java.util.*

data class UserGardenViewCountSelectRow(
    val userId: UUID,
    val userName: String,
    val gardenId: UUID,
    val gardenName: String,
    val viewCount: Long
)

fun Row.toUserGardenViewCountSelectRow() = UserGardenViewCountSelectRow(
    get(UserGardenViewCountSelectRow::userId),
    get(UserGardenViewCountSelectRow::userName),
    get(UserGardenViewCountSelectRow::gardenId),
    get(UserGardenViewCountSelectRow::gardenName),
    get(UserGardenViewCountSelectRow::viewCount),
)

fun List<UserGardenViewCountSelectRow>.toReport() =
    UsersFavoriteGardenReport(map { it.toMetric() })

fun UserGardenViewCountSelectRow.toMetric() =
    UsersFavoriteGardenReport.Metric(
        UserEntity(userId, userName, emptyList()),
        GardenEntity(gardenId, gardenName),
        viewCount
    )
