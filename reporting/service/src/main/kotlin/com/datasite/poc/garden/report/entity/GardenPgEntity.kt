package com.datasite.poc.garden.report.entity

import io.r2dbc.spi.Row
import org.springframework.data.relational.core.mapping.Table
import java.util.*

const val GARDEN_TABLE = "gardens"

@Table(GARDEN_TABLE)
class GardenPgEntity(
    val id: UUID,
    val name: String,
)

fun Row.toGardenPgEntity() = GardenPgEntity(
    get(GardenPgEntity::id),
    get(GardenPgEntity::name),
)
