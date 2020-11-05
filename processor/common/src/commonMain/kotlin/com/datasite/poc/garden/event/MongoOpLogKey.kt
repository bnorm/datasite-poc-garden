package com.datasite.poc.garden.event

import kotlinx.serialization.Serializable

@Serializable
data class MongoOpLogKey(
    val id: String
)
