package com.datasite.poc.garden.report.components

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.datasite.poc.garden.report.client
import com.datasite.poc.garden.report.consumeAsFlow
import com.datasite.poc.garden.report.dto.MostPopularGardensReport
import com.datasite.poc.garden.report.dto.Report
import io.ktor.client.features.websocket.*
import io.ktor.client.request.*
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart

@Composable
fun MostPopularGardensReport(
    modifier: Modifier = Modifier,
) = ReportCard(
    name = "Most Popular Gardens",
    reports = flow {
        client.ws(path = "/api/v1/reports?reports=MostPopularGardens") {
            val initial = client.get<MostPopularGardensReport>(path = "/api/v1/reports/popular")
            emitAll(incoming.consumeAsFlow<Report>()
                .filterIsInstance<MostPopularGardensReport>()
                .onStart { emit(initial) })
        }
    },
    modifier,
) {
    for (metric in it.metrics) {
        Text("${metric.garden.name} has been viewed ${metric.viewCount} time(s)!")
    }
}
