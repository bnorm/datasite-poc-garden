package com.datasite.poc.garden.reporting

import com.datasite.poc.garden.reporting.dto.Dummy
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
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
    @GetMapping("/dummies")
    fun getDummyEvents(): Flow<Dummy> =
        service.getDummyEvents()


    @Configuration
    class WebSocketConfiguration(
        private val webSocketHandler: ReportEventWebSocketHandler
    ) {
        @Bean
        fun webSocketHandlerMapping(): HandlerMapping {
            return SimpleUrlHandlerMapping().apply {
                order = 1
                urlMap = mapOf(
                    "/api/v1/reports/events" to webSocketHandler,
                )
            }
        }

        @Bean
        fun handlerAdapter(): WebSocketHandlerAdapter {
            return WebSocketHandlerAdapter()
        }
    }
}
