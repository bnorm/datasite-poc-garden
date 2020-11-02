package com.datasite.poc.garden.audit.dto

import kotlinx.serialization.Serializable

@Serializable
sealed class AuditEvent {
    abstract val transactionId: String?
    abstract val userId: String

    @Serializable
    data class AllGardensAccess(
        override val userId: String,
    ) : AuditEvent() {
        override val transactionId: String? = null
    }

    @Serializable
    data class GardenAccess(
        override val userId: String,
        val gardenId: String,
    ) : AuditEvent() {
        override val transactionId: String? = null
    }

    @Serializable
    data class GardenCreate(
        override val transactionId: String,
        override val userId: String,
    ) : AuditEvent()

    @Serializable
    data class GardenUpdate(
        override val transactionId: String,
        override val userId: String,
    ) : AuditEvent()

    @Serializable
    data class GardenDelete(
        override val transactionId: String,
        override val userId: String,
    ) : AuditEvent()
}
