package com.datasite.poc.garden.dto

import com.datasite.poc.garden.entity.GardenEntity

data class Garden(
    val id: String,
    val name: String,
) {
    companion object {
        fun from(entity: GardenEntity) = Garden(
            id = entity.id.toHexString(),
            name = entity.name,
        )
    }
}

data class GardenPrototype(
    val name: String,
)
