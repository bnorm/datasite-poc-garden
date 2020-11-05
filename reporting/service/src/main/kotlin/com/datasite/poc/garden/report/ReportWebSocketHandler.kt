package com.datasite.poc.garden.report

import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.reactive.awaitSingleOrNull
import kotlinx.coroutines.reactor.asFlux
import kotlinx.coroutines.reactor.mono
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.springframework.stereotype.Component
import org.springframework.web.reactive.socket.WebSocketHandler
import org.springframework.web.reactive.socket.WebSocketMessage
import org.springframework.web.reactive.socket.WebSocketSession
import org.springframework.web.util.UriComponentsBuilder
import reactor.core.publisher.Mono

@Component
class ReportWebSocketHandler(
    private val reportService: ReportService,
    private val json: Json,
) : WebSocketHandler {
    override fun handle(session: WebSocketSession): Mono<Void> {
        val params = UriComponentsBuilder.fromUri(session.handshakeInfo.uri).build().queryParams
        val reports = params["reports"]?.toSet() ?: emptySet()
        return reportService.reportFlow
            .filter { reports.isEmpty() || it.name in reports }
            .map { json.encodeToString(it) }
            .map { session.textMessage(it) }
            .onEach { session.send(it) }
            .asFlux()
            .then()
    }

    private suspend fun WebSocketSession.send(message: WebSocketMessage) {
        send(mono { message }).awaitSingleOrNull()
    }
}
