package com.datasite.poc.garden.report.components

import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun Reports() {
    ScrollableColumn(
        modifier = Modifier.fillMaxSize()
            .padding(vertical = 8.dp)
            .background(MaterialTheme.colors.background),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        UsersFavoriteGardenReport()
        MostPopularGardensReport()
        GardenSensorReport()
        SensorCountGardenReport()
    }
}
