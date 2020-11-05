package com.datasite.poc.garden.audit.dto

import com.datasite.poc.garden.dto.Uuid
import com.datasite.poc.garden.dto.UuidSerializer
import kotlinx.serialization.Serializable

@Serializable
sealed class AuditEvent {
    abstract val transactionId: String?
    abstract val userId: Uuid

    @Serializable
    data class AllGardensAccess(
        @Serializable(with = UuidSerializer::class)
        override val userId: Uuid,
    ) : AuditEvent() {
        override val transactionId: String? = null
    }

    @Serializable
    data class GardenAccess(
        @Serializable(with = UuidSerializer::class)
        override val userId: Uuid,
        @Serializable(with = UuidSerializer::class)
        val gardenId: Uuid,
    ) : AuditEvent() {
        override val transactionId: String? = null
    }

    @Serializable
    data class GardenCreate(
        override val transactionId: String,
        @Serializable(with = UuidSerializer::class)
        override val userId: Uuid,
    ) : AuditEvent()

    @Serializable
    data class GardenUpdate(
        override val transactionId: String,
        @Serializable(with = UuidSerializer::class)
        override val userId: Uuid,
    ) : AuditEvent()

    @Serializable
    data class GardenDelete(
        override val transactionId: String,
        @Serializable(with = UuidSerializer::class)
        override val userId: Uuid,
    ) : AuditEvent()

    @Serializable
    data class AllGardenSensorsAccess(
        @Serializable(with = UuidSerializer::class)
        override val userId: Uuid,
    ) : AuditEvent() {
        override val transactionId: String? = null
    }

    @Serializable
    data class GardenSensorAccess(
        @Serializable(with = UuidSerializer::class)
        override val userId: Uuid,
        @Serializable(with = UuidSerializer::class)
        val sensorId: Uuid,
    ) : AuditEvent() {
        override val transactionId: String? = null
    }

    @Serializable
    data class GardenSensorCreate(
        override val transactionId: String,
        @Serializable(with = UuidSerializer::class)
        override val userId: Uuid,
    ) : AuditEvent()

    @Serializable
    data class GardenSensorUpdate(
        override val transactionId: String,
        @Serializable(with = UuidSerializer::class)
        override val userId: Uuid,
    ) : AuditEvent()

    @Serializable
    data class GardenSensorDelete(
        override val transactionId: String,
        @Serializable(with = UuidSerializer::class)
        override val userId: Uuid,
    ) : AuditEvent()
}
