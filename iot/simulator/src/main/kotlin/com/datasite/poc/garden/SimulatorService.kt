package com.datasite.poc.garden

import com.datasite.poc.garden.iot.dto.SensorReading
import java.net.URI
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.http.client.HttpClient
import kotlin.random.Random

@Service
class SimulatorService {
    val logger: Logger = LoggerFactory.getLogger(SimulatorService::class.java)

    val id = UUID.randomUUID().toString()

    val webClient: WebClient = WebClient.builder()
            .clientConnector(ReactorClientHttpConnector(HttpClient.create())).build()

    @Scheduled(fixedRate = 3000)
    fun sendReading() {
        logger.info("sending reading....")

        webClient
                .post()
                .uri(URI.create("http://localhost:9000/api/v1/iot"))
                .bodyValue(
                        SensorReading(
                                timestamp = OffsetDateTime.now(ZoneOffset.UTC).toEpochSecond(),
                                sensorId = id,
                                value = Random.nextLong(30, 90)
                        )
                )
                .retrieve()
                .bodyToMono(String::class.java)
                .block()
    }
}