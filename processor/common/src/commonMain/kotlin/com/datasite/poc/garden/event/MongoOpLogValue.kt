package com.datasite.poc.garden.event

import kotlinx.serialization.Serializable

@Serializable
data class MongoOpLogValue(
    val op: String,
    val after: String? = null,
    val patch: String? = null,
    val transaction: Transaction? = null,
) {
    @Serializable
    data class Transaction(
        val id: String,
        val total_order: Long,
        val data_collection_order: Long,
    )
}
