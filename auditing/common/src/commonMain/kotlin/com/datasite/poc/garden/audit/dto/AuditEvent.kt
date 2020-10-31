package com.datasite.poc.garden.audit.dto

import kotlinx.serialization.Serializable

@Serializable
sealed class AuditEvent {
    abstract val transactionId: String?
    abstract val userId: String

    @Serializable
    class AllGardensAccess(
        override val userId: String,
    ) : AuditEvent() {
        override val transactionId: String? = null
    }

    @Serializable
    class GardenAccess(
        override val userId: String,
        val gardenId: String,
    ) : AuditEvent() {
        override val transactionId: String? = null
    }

    @Serializable
    class GardenCreate(
        override val transactionId: String,
        override val userId: String,
    ) : AuditEvent()

    @Serializable
    class GardenUpdate(
        override val transactionId: String,
        override val userId: String,
    ) : AuditEvent()

    @Serializable
    class GardenDelete(
        override val transactionId: String,
        override val userId: String,
    ) : AuditEvent()
}
