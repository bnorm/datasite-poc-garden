package com.datasite.poc.garden.entity

import com.datasite.poc.garden.dto.Garden
import com.datasite.poc.garden.dto.GardenPrototype
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

const val GARDEN_COLLECTION = "gardens"

@Document(GARDEN_COLLECTION)
data class GardenEntity(
    @Id val id: UUID = UUID.randomUUID(),
    val name: String
)

fun GardenEntity.toGarden() = Garden(
    id = id,
    name = name,
)

fun GardenPrototype.toEntity() = GardenEntity(
    name = name,
)
