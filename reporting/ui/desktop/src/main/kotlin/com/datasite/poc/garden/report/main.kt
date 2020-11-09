package com.datasite.poc.garden.report

import androidx.compose.desktop.Window
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.ui.unit.IntSize
import com.datasite.poc.garden.report.components.Reports

fun main() = Window(
    title = "Compose for Desktop",
    size = IntSize(600, 600)
) {
    MaterialTheme {
        Scaffold(
            topBar = {
                TopAppBar(title = {
                    Text(text = "AutoAudit™")
                })
            },
            bodyContent = {
                Reports()
            }
        )
    }
}
