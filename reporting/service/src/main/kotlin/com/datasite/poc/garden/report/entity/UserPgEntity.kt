package com.datasite.poc.garden.report.entity

import io.r2dbc.spi.Row
import org.springframework.data.relational.core.mapping.Table
import java.util.*

const val USER_TABLE = "users"

@Table(USER_TABLE)
class UserPgEntity(
    val id: UUID,
    val name: String,
)

fun Row.toUserPgEntity() = UserPgEntity(
    get(UserPgEntity::id),
    get(UserPgEntity::name),
)
