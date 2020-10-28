package com.datasite.poc.garden.entity

import com.datasite.poc.garden.dto.GardenPrototype
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

const val GARDEN_COLLECTION = "gardens"

@Document(GARDEN_COLLECTION)
data class GardenEntity(
    @Id val id: ObjectId = ObjectId.get(),
    val name: String,
    val createdDate: Instant = Instant.now(),
    val modifiedDate: Instant = Instant.now()
) {
    companion object {
        fun from(prototype: GardenPrototype) = GardenEntity(
            name = prototype.name,
        )
    }
}
