package com.datasite.poc.garden.report.entity

import io.r2dbc.spi.Row
import org.springframework.data.relational.core.mapping.Table
import java.util.*

const val USER_GARDEN_VIEW_TABLE = "user_garden_views"

@Table(USER_GARDEN_VIEW_TABLE)
class UserGardenViewPgEntity(
    val userId: UUID,
    val gardenId: UUID,
    val viewCount: Long
)

fun Row.toUserGardenViewPgEntity() = UserGardenViewPgEntity(
    get(UserGardenViewPgEntity::userId),
    get(UserGardenViewPgEntity::gardenId),
    get(UserGardenViewPgEntity::viewCount),
)
