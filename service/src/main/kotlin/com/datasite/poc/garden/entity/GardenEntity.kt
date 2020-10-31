package com.datasite.poc.garden.entity

import com.datasite.poc.garden.dto.Garden
import com.datasite.poc.garden.dto.GardenPrototype
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

const val GARDEN_COLLECTION = "gardens"

@Document(GARDEN_COLLECTION)
data class GardenEntity(
    @Id val id: ObjectId = ObjectId.get(),
    val name: String
)

fun GardenEntity.toGarden() = Garden(
    id = id.toHexString(),
    name = name,
)

fun GardenPrototype.toEntity() = GardenEntity(
    name = name,
)
