package com.datasite.poc.garden.reporting.dto

import com.datasite.poc.garden.reporting.entity.DummyEntity

class Dummy(
    val character: Char,
    val count: Long
) {
    companion object {
        fun from(entity: DummyEntity) = Dummy(
            entity.character,
            entity.count,
        )
    }
}
