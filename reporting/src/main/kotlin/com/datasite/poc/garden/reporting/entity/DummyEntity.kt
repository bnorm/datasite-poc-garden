package com.datasite.poc.garden.reporting.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

const val DUMMY_TABLE = "dummies"

@Table(DUMMY_TABLE)
class DummyEntity(
    @Id val character: Char,
    val count: Long
)
