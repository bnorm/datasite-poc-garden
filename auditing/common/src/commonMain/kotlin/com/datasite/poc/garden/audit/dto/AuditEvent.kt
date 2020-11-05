package com.datasite.poc.garden.audit.dto

import kotlinx.datetime.Clock.System
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.Serializable

@Serializable
sealed class AuditEvent {
    abstract val timestamp: Long
    abstract val transactionId: String?
    abstract val userId: String

    @Serializable
    data class AllGardensAccess(
            override val userId: String,
            override val timestamp: Long = System.now().toLocalDateTime(TimeZone.UTC).toInstant(TimeZone.UTC).toEpochMilliseconds()
    ) : AuditEvent() {
        override val transactionId: String? = null
    }

    @Serializable
    data class GardenAccess(
            override val userId: String,
            val gardenId: String,
            override val timestamp: Long = System.now().toLocalDateTime(TimeZone.UTC).toInstant(TimeZone.UTC).toEpochMilliseconds()
    ) : AuditEvent() {
        override val transactionId: String? = null
    }

    @Serializable
    data class GardenCreate(
            override val transactionId: String,
            override val userId: String,
            override val timestamp: Long = System.now().toLocalDateTime(TimeZone.UTC).toInstant(TimeZone.UTC).toEpochMilliseconds()
    ) : AuditEvent()

    @Serializable
    data class GardenUpdate(
            override val transactionId: String,
            override val userId: String,
            override val timestamp: Long = System.now().toLocalDateTime(TimeZone.UTC).toInstant(TimeZone.UTC).toEpochMilliseconds()
    ) : AuditEvent()

    @Serializable
    data class GardenDelete(
            override val transactionId: String,
            override val userId: String,
            override val timestamp: Long = System.now().toLocalDateTime(TimeZone.UTC).toInstant(TimeZone.UTC).toEpochMilliseconds()
    ) : AuditEvent()
}
