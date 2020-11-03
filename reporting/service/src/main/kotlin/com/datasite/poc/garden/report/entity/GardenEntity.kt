package com.datasite.poc.garden.report.entity

import org.springframework.data.relational.core.mapping.Table

const val GARDEN_TABLE = "gardens"

@Table(GARDEN_TABLE)
class GardenEntity(
    val id: String,
    val name: String,
)
