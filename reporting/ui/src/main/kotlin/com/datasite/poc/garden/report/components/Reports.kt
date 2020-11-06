package com.datasite.poc.garden.report.components

import com.bnorm.react.RFunction
import com.datasite.poc.garden.report.components.reports.GardenSensorReport
import com.datasite.poc.garden.report.components.reports.MostPopularGardensReport
import com.datasite.poc.garden.report.components.reports.SensorCountGardenReport
import com.datasite.poc.garden.report.components.reports.UsersFavoriteGardenReport
import materialui.components.grid.grid
import react.RBuilder

@Suppress("FunctionName")
@RFunction
fun RBuilder.Reports() {
    grid {
        attrs.container = true
        attrs.spacing(4)
        grid {
            attrs.item = true
            ReportCard(name = "Favorite Gardens", report = { UsersFavoriteGardenReport() })
        }
        grid {
            attrs.item = true
            ReportCard(name = "Popular Gardens", report = { MostPopularGardensReport() })
        }
        grid {
            attrs.item = true
            ReportCard(name = "Average Sensor Reading", report = { GardenSensorReport() })
        }
        grid {
            attrs.item = true
            ReportCard(name = "Total Sensor Reading", report = { SensorCountGardenReport() })
        }
    }
}