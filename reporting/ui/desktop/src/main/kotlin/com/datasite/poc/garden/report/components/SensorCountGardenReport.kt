package com.datasite.poc.garden.report.components

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.datasite.poc.garden.report.client
import com.datasite.poc.garden.report.consumeAsFlow
import com.datasite.poc.garden.report.dto.GardenSensorReport
import com.datasite.poc.garden.report.dto.Report
import io.ktor.client.features.websocket.*
import io.ktor.client.request.*
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart

@Composable
fun SensorCountGardenReport(
    modifier: Modifier = Modifier,
) = ReportCard(
    name = "Total Sensor Readings by Garden",
    reports = flow {
        client.ws(path = "/api/v1/reports?reports=GardenSensors") {
            val initial = client.get<GardenSensorReport>(path = "/api/v1/reports/garden_sensors")
            emitAll(incoming.consumeAsFlow<Report>()
                .filterIsInstance<GardenSensorReport>()
                .onStart { emit(initial) })
        }
    },
    modifier,
) {
    for (metric in it.metrics) {
        Text("${metric.garden.name} : sensor reports : ${metric.readingCount}")
    }
}
