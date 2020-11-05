package com.datasite.poc.garden.audit.dto

import kotlinx.serialization.Serializable

@Serializable
sealed class EnrichedAuditEvent {
    abstract val userId: String

    @Serializable
    data class GardenAccess(
        override val userId: String,
        val garden: Garden,
    ) : EnrichedAuditEvent()
}
