package com.datasite.poc.garden.report.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.util.*

const val USER_TABLE = "users"

@Table(USER_TABLE)
class UserPgEntity(
    @Id val id: UUID,
    val name: String,
)
