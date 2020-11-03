package com.datasite.poc.garden.report

import com.datasite.poc.garden.report.dto.reportDtoSerializersModule
import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.features.websocket.*
import io.ktor.http.*
import kotlinx.browser.window
import kotlinx.serialization.json.Json

val json = Json {
    serializersModule = reportDtoSerializersModule
}

val locationUrl = Url(window.location.toString())
val client = HttpClient {
    defaultRequest {
        if (url.host == "localhost") {
            url.host = locationUrl.host
            url.port = locationUrl.port
        }
    }
    install(JsonFeature) {
        serializer = KotlinxSerializer(json)
    }
    install(WebSockets)
}
