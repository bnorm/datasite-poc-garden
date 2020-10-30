package com.datasite.poc.garden.ui

import com.bnorm.react.RFunction
import io.ktor.client.features.websocket.*
import io.ktor.client.request.*
import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.scan
import kotlinx.serialization.decodeFromString
import react.RBuilder
import react.dom.li
import react.dom.ul
import react.getValue
import react.setValue
import react.useState

@Suppress("FunctionName")
@RFunction
fun RBuilder.DummyReportTable() {
    var table by useState(emptyList<Dummy>())
    useAsync(emptyList()) {
        client.ws(port = 8080, path = "/api/v1/reports/events") {
            // Consume future events and update dummies
            val initial = client.get<List<Dummy>>(port = 8080, path = "/api/v1/reports/dummies")
                .associateBy { it.character }
            incoming.consumeAsFlow()
                .filterIsInstance<Frame.Text>()
                .map { json.decodeFromString<ReportEvent>(it.readText()) }
                .filterIsInstance<ReportEvent.DummyEvent>()
                .scan(initial) { acc, value -> acc + (value.character to Dummy.from(value)) }
                .map { events -> events.values.sortedBy { it.character } }
                .collect { table = it }
        }
    }

    ul {
        for (dummy in table) {
            li {
                +"${dummy.character}=${dummy.count}"
            }
        }
    }
}
