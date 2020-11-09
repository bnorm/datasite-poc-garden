package com.datasite.poc.garden.report.components

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.datasite.poc.garden.report.client
import com.datasite.poc.garden.report.consumeAsFlow
import com.datasite.poc.garden.report.dto.Report
import com.datasite.poc.garden.report.dto.UsersFavoriteGardenReport
import io.ktor.client.features.websocket.*
import io.ktor.client.request.*
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart

@Composable
fun UsersFavoriteGardenReport(
    modifier: Modifier = Modifier,
) = ReportCard(
    name = "Favorite Gardens By User",
    reports = flow {
        client.ws(path = "/api/v1/reports?reports=UsersFavoriteGarden") {
            val initial = client.get<UsersFavoriteGardenReport>(path = "/api/v1/reports/favorite")
            emitAll(incoming.consumeAsFlow<Report>()
                .filterIsInstance<UsersFavoriteGardenReport>()
                .onStart { emit(initial) })
        }
    },
    modifier,
) {
    for (metric in it.metrics) {
        Text("${metric.user.name} has viewed ${metric.garden.name}, ${metric.viewCount} time(s)!")
    }
}
