package com.datasite.poc.garden.report

import com.datasite.poc.garden.report.dto.GardenSensorReport
import com.datasite.poc.garden.report.dto.MostPopularGardensReport
import com.datasite.poc.garden.report.dto.SensorReport
import com.datasite.poc.garden.report.dto.UsersFavoriteGardenReport
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.HandlerMapping
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter

@RestController
@RequestMapping("/api/v1/reports")
class ReportController(
    private val service: ReportService
) {
    @GetMapping("/popular")
    suspend fun getMostPopularGardensReport(): MostPopularGardensReport =
        service.getMostPopularGardensReport()

    @GetMapping("/favorite")
    suspend fun getUsersFavoriteGardenReport(): UsersFavoriteGardenReport =
        service.getUsersFavoriteGardenReport()

    @GetMapping("/garden_sensors")
    suspend fun getGardenSensorTotalReport(): GardenSensorReport =
        service.getGardenSensorTotalReport()

    @GetMapping("/sensors")
    suspend fun getSensorTotalReport(): SensorReport = TODO()

    @Configuration
    class WebSocketConfiguration(
        private val reportWebSocketHandler: ReportWebSocketHandler,
    ) {
        @Bean
        fun webSocketHandlerMapping(): HandlerMapping {
            return SimpleUrlHandlerMapping().apply {
                order = 1
                urlMap = mapOf(
                    "/api/v1/reports" to reportWebSocketHandler,
                )
            }
        }

        @Bean
        fun handlerAdapter(): WebSocketHandlerAdapter {
            return WebSocketHandlerAdapter()
        }
    }
}
