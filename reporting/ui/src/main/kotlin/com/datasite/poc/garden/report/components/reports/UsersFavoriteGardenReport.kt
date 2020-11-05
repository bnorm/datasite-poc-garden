package com.datasite.poc.garden.report.components.reports

import com.bnorm.react.RFunction
import com.datasite.poc.garden.report.client
import com.datasite.poc.garden.report.dto.Report
import com.datasite.poc.garden.report.dto.UsersFavoriteGardenReport
import com.datasite.poc.garden.report.json
import com.datasite.poc.garden.report.useAsync
import io.ktor.client.features.websocket.ws
import io.ktor.client.request.get
import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.readText
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.serialization.decodeFromString
import react.RBuilder
import react.dom.div
import react.dom.li
import react.dom.ul
import react.useState

@Suppress("FunctionName")
@RFunction
fun RBuilder.UsersFavoriteGardenReport() {
    var report by useState<UsersFavoriteGardenReport?>(null)
    useAsync(emptyList()) {
        client.ws(path = "/api/v1/reports?reports=UsersFavoriteGarden") {
            val initial =
                client.get<UsersFavoriteGardenReport>(path = "/api/v1/reports/favorite")
            incoming.consumeAsFlow()
                .filterIsInstance<Frame.Text>()
                .map { json.decodeFromString<Report>(it.readText()) }
                .filterIsInstance<UsersFavoriteGardenReport>()
                .onStart { emit(initial) }
                .onCompletion { it?.printStackTrace() }
                .collect { report = it }
        }
    }

    val localReport = report
    if (localReport != null) {
        ul {
            for (metric in localReport.metrics) {
                li {
                    +"${metric.user.name} has viewed ${metric.garden.name}, ${metric.viewCount} time(s)!"
                }
            }
        }
    } else {
        div {
            +"Loading report..."
        }
    }
}
