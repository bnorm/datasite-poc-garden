package com.datasite.poc.garden.entity

import com.datasite.poc.garden.dto.Garden
import com.datasite.poc.garden.dto.User
import com.datasite.poc.garden.dto.UserPrototype
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

const val USER_COLLECTION = "users"

@Document(USER_COLLECTION)
data class UserEntity(
    @Id val id: UUID = UUID.randomUUID(),
    val name: String,
    val gardenIds: List<UUID>,
)

fun UserEntity.toUser(gardens: List<Garden>) = User(
    id = id,
    name = name,
    gardens = gardens,
)

fun UserPrototype.toEntity() = UserEntity(
    name = name,
    gardenIds = gardenIds,
)
