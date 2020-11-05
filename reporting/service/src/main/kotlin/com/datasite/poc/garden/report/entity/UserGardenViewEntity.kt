package com.datasite.poc.garden.report.entity

import org.springframework.data.relational.core.mapping.Table

const val USER_GARDEN_VIEW_TABLE = "user_garden_views"

@Table(USER_GARDEN_VIEW_TABLE)
class UserGardenViewEntity(
    val userId: String,
    val gardenId: String,
    val viewCount: Long
)
