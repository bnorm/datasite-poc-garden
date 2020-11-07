package com.datasite.poc.garden.report.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect

@Composable
fun <T> ReportCard(
    name: String,
    reports: Flow<T>,
    display: @Composable (T) -> Unit,
) {
    var report by remember { mutableStateOf<T?>(null) }
    LaunchedEffect(null) {
        reports.collect { report = it }
    }

    Card(
        elevation = 4.dp,
        modifier = Modifier.padding(horizontal = 8.dp)
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.h5
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
