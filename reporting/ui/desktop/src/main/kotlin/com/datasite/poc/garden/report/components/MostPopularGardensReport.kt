package com.datasite.poc.garden.report.components

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import com.datasite.poc.garden.report.client
import com.datasite.poc.garden.report.dto.MostPopularGardensReport
import com.datasite.poc.garden.report.dto.Report
import com.datasite.poc.garden.report.json
import io.ktor.client.features.websocket.*
import io.ktor.client.request.*
import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.serialization.decodeFromString

@Composable
fun MostPopularGardensReport() = ReportCard(
    name = "Most Popular Gardens",
    reports = flow {
        client.ws(path = "/api/v1/reports?reports=MostPopularGardens") {
            val initial = client.get<MostPopularGardensReport>(path = "/api/v1/reports/popular")
            emitAll(incoming.consumeAsFlow()
                .filterIsInstance<Frame.Text>()
                .map { json.decodeFromString<Report>(it.readText()) }
                .filterIsInstance<MostPopularGardensReport>()
                .onStart { emit(initial) }
                .onCompletion { it?.printStackTrace() })
        }
    }
) {
    for (metric in it.metrics) {
        Text("${metric.garden.name} has been viewed ${metric.viewCount} time(s)!")
    }
}
