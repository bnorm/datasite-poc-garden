package com.datasite.poc.garden.dto

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
sealed class AuditEvent {
    abstract val timestamp: Instant
    abstract val transactionId: String?
    abstract val userId: Uuid

    @Serializable
    data class AllGardensAccess(
        @Serializable(with = InstantSerializer::class)
        override val timestamp: Instant = Clock.System.now(),
        @Serializable(with = UuidSerializer::class)
        override val userId: Uuid,
    ) : AuditEvent() {
        override val transactionId: String? = null
    }

    @Serializable
    data class GardenAccess(
        @Serializable(with = InstantSerializer::class)
        override val timestamp: Instant = Clock.System.now(),
        @Serializable(with = UuidSerializer::class)
        override val userId: Uuid,
        @Serializable(with = UuidSerializer::class)
        val gardenId: Uuid,
    ) : AuditEvent() {
        override val transactionId: String? = null
    }

    @Serializable
    data class GardenCreate(
        @Serializable(with = InstantSerializer::class)
        override val timestamp: Instant = Clock.System.now(),
        override val transactionId: String,
        @Serializable(with = UuidSerializer::class)
        override val userId: Uuid,
    ) : AuditEvent()

    @Serializable
    data class GardenUpdate(
        @Serializable(with = InstantSerializer::class)
        override val timestamp: Instant = Clock.System.now(),
        override val transactionId: String,
        @Serializable(with = UuidSerializer::class)
        override val userId: Uuid,
    ) : AuditEvent()

    @Serializable
    data class GardenDelete(
        @Serializable(with = InstantSerializer::class)
        override val timestamp: Instant = Clock.System.now(),
        override val transactionId: String,
        @Serializable(with = UuidSerializer::class)
        override val userId: Uuid,
    ) : AuditEvent()

    @Serializable
    data class AllGardenSensorsAccess(
        @Serializable(with = InstantSerializer::class)
        override val timestamp: Instant = Clock.System.now(),
        @Serializable(with = UuidSerializer::class)
        override val userId: Uuid,
    ) : AuditEvent() {
        override val transactionId: String? = null
    }

    @Serializable
    data class GardenSensorAccess(
        @Serializable(with = InstantSerializer::class)
        override val timestamp: Instant = Clock.System.now(),
        @Serializable(with = UuidSerializer::class)
        override val userId: Uuid,
        @Serializable(with = UuidSerializer::class)
        val sensorId: Uuid,
    ) : AuditEvent() {
        override val transactionId: String? = null
    }

    @Serializable
    data class GardenSensorCreate(
        @Serializable(with = InstantSerializer::class)
        override val timestamp: Instant = Clock.System.now(),
        override val transactionId: String,
        @Serializable(with = UuidSerializer::class)
        override val userId: Uuid,
    ) : AuditEvent()

    @Serializable
    data class GardenSensorUpdate(
        @Serializable(with = InstantSerializer::class)
        override val timestamp: Instant = Clock.System.now(),
        override val transactionId: String,
        @Serializable(with = UuidSerializer::class)
        override val userId: Uuid,
    ) : AuditEvent()

    @Serializable
    data class GardenSensorDelete(
        @Serializable(with = InstantSerializer::class)
        override val timestamp: Instant = Clock.System.now(),
        override val transactionId: String,
        @Serializable(with = UuidSerializer::class)
        override val userId: Uuid,
    ) : AuditEvent()
}
