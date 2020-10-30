package com.datasite.poc.garden.reporting

import kotlinx.serialization.Polymorphic
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class ReportEvent {
    @Serializable
    @SerialName("dummy")
    class DummyEvent(val character: Char, val count: Long) : ReportEvent()

    // TODO build out report event types
}
