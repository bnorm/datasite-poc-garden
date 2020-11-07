package com.datasite.poc.garden.report

import com.datasite.poc.garden.report.dto.reportDtoSerializersModule
import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.features.websocket.*
import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

val json = Json {
    serializersModule = reportDtoSerializersModule
}

val client = HttpClient {
    defaultRequest {
        if (url.host == "localhost") {
            url.host = "localhost"
            url.port = 9030
        }
    }
    install(JsonFeature) {
        serializer = KotlinxSerializer(json)
    }
    install(WebSockets)
}

inline fun <reified T> ReceiveChannel<Frame>.consumeAsFlow() =
    consumeAsFlow()
        .filterIsInstance<Frame.Text>()
        .map { json.decodeFromString<T>(it.readText()) }
