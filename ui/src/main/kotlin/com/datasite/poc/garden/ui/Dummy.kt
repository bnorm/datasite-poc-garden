package com.datasite.poc.garden.ui

import kotlinx.serialization.Serializable

@Serializable
data class Dummy(
    val character: Char,
    val count: Long
) {
    companion object {
        fun from(event: ReportEvent.DummyEvent) = Dummy(
            event.character,
            event.count,
        )
    }
}
