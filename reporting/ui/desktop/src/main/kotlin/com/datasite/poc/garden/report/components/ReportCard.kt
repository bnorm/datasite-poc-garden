package com.datasite.poc.garden.report.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.retry

@Composable
fun <T> ReportCard(
    name: String,
    reports: Flow<T>,
    modifier: Modifier = Modifier,
    display: @Composable (T) -> Unit,
) {
    var report by remember { mutableStateOf<T?>(null) }
    LaunchedEffect(null) {
        reports.onCompletion { it?.printStackTrace() }
            .retry { delay(5_000); true }
            .collect { report = it }
    }

    Card(
        elevation = 4.dp,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.h5
            )

            Divider(
                modifier.padding(top = 4.dp, bottom = 16.dp)
            )

            val localReport = report
            if (localReport != null) {
                display(localReport)
            } else {
                Text("Loading report...")
            }
        }
    }
}
