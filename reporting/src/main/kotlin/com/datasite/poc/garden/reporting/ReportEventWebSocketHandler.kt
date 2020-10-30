package com.datasite.poc.garden.reporting

import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.reactive.awaitSingleOrNull
import kotlinx.coroutines.reactor.asFlux
import kotlinx.coroutines.reactor.mono
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.springframework.stereotype.Component
import org.springframework.web.reactive.socket.WebSocketHandler
import org.springframework.web.reactive.socket.WebSocketMessage
import org.springframework.web.reactive.socket.WebSocketSession
import reactor.core.publisher.Mono

@Component
class ReportEventWebSocketHandler(
    private val events: SharedFlow<ReportEvent>,
    private val json: Json,
) : WebSocketHandler {
    override fun handle(session: WebSocketSession): Mono<Void> =
        events
            .map { json.encodeToString(it) }
            .map { session.textMessage(it) }
            .onEach { session.send(it) }
            .asFlux()
            .then()

    private suspend fun WebSocketSession.send(message: WebSocketMessage) {
        send(mono { message }).awaitSingleOrNull()
    }
}
