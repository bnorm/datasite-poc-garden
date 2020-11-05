package com.datasite.poc.garden.audit.dto

import com.datasite.poc.garden.dto.Uuid
import com.datasite.poc.garden.dto.UuidSerializer
import kotlinx.serialization.Serializable

@Serializable
sealed class EnrichedAuditEvent {
    abstract val userId: Uuid

    @Serializable
    data class GardenAccess(
        @Serializable(with = UuidSerializer::class)
        override val userId: Uuid,
        val garden: MongoGarden,
    ) : EnrichedAuditEvent()
}
