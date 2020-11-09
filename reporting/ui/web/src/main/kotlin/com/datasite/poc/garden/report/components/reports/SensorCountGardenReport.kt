package com.datasite.poc.garden.report.components.reports

import com.bnorm.react.RFunction
import com.datasite.poc.garden.report.client
import com.datasite.poc.garden.report.dto.GardenSensorReport
import com.datasite.poc.garden.report.dto.Report
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
import materialui.components.list.list
import materialui.components.listitem.listItem
import materialui.components.listitemtext.listItemText
import react.RBuilder
import react.dom.div
import react.useState

@Suppress("FunctionName")
@RFunction
fun RBuilder.SensorCountGardenReport() {
    var report by useState<GardenSensorReport?>(null)
    useAsync(emptyList()) {
        client.ws(path = "/api/v1/reports?reports=GardenSensors") {
            val initial =
                    client.get<GardenSensorReport>(path = "/api/v1/reports/garden_sensors")
            incoming.consumeAsFlow()
                    .filterIsInstance<Frame.Text>()
                    .map { json.decodeFromString<Report>(it.readText()) }
                    .filterIsInstance<GardenSensorReport>()
                    .onStart { emit(initial) }
                    .onCompletion { it?.printStackTrace() }
                    .collect { report = it }
        }
    }

    val localReport = report
    if (localReport != null) {
        list {
            for (metric in localReport.metrics) {
                listItem {
                    listItemText {
                        +"${metric.garden.name} : sensor reports : ${metric.readingCount}"
                    }
                }
            }
        }
    } else {
        div {
            +"Loading report..."
        }
    }
}
