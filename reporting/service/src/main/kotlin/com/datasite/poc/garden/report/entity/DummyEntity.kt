package com.datasite.poc.garden.report.entity

import com.datasite.poc.garden.report.dto.Dummy
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

const val DUMMY_TABLE = "dummies"

@Table(DUMMY_TABLE)
class DummyEntity(
    @Id val character: Char,
    val count: Long
)

fun DummyEntity.toDummy() = Dummy(
    character,
    count,
)
