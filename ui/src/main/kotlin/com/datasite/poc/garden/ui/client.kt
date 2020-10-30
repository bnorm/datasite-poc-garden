package com.datasite.poc.garden.ui

import io.ktor.client.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.features.websocket.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual
import kotlinx.serialization.modules.serializersModuleOf

val json = Json {
    serializersModule = SerializersModule {
        contextual(Dummy.serializer())
        contextual(ReportEvent.serializer())
    }
}

val client = HttpClient {
    install(JsonFeature) {
        serializer = KotlinxSerializer(json)
    }
    install(WebSockets)
}
