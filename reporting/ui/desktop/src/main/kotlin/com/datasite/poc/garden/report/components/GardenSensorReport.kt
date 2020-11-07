package com.datasite.poc.garden.report.components

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.datasite.poc.garden.report.client
import com.datasite.poc.garden.report.consumeAsFlow
import com.datasite.poc.garden.report.dto.Report
import com.datasite.poc.garden.report.dto.SensorReport
import io.ktor.client.features.websocket.*
import io.ktor.client.request.*
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart

@Composable
fun GardenSensorReport(
    modifier: Modifier = Modifier,
) = ReportCard(
    name = "Average Sensor Reading",
    reports = flow {
        client.ws(path = "/api/v1/reports?reports=Sensors") {
            val initial = client.get<SensorReport>(path = "/api/v1/reports/sensors")
            emitAll(incoming.consumeAsFlow<Report>()
                .filterIsInstance<SensorReport>()
                .onStart { emit(initial) })
        }
    },
    modifier,
) {
    for (metric in it.metrics) {
        Text("${metric.garden.name} : average sensor reading : ${metric.readingSum / metric.readingCount}")
    }
}
