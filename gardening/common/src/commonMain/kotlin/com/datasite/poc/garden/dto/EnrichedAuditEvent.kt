package com.datasite.poc.garden.dto

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
sealed class EnrichedAuditEvent {
    abstract val timestamp: Instant
    abstract val userId: Uuid

    @Serializable
    data class GardenAccess(
        @Serializable(with = InstantSerializer::class)
        override val timestamp: Instant,
        @Serializable(with = UuidSerializer::class)
        override val userId: Uuid,
        val garden: Garden,
    ) : EnrichedAuditEvent()
}
