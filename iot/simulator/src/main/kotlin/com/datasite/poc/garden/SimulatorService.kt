package com.datasite.poc.garden

import com.datasite.poc.garden.iot.dto.SensorReading
import java.net.URI
import java.time.OffsetDateTime
import java.time.ZoneOffset
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

    val sensors = listOf(
            "5c1084a8-3372-4af0-9761-bf24453a6eb6",
            "057f1256-ed90-4e5d-bb9b-db68975dcc4d",
            "069d991b-2e0c-4fea-9c19-b5d4cdf08bd0",
            "df06a4c3-b660-4223-9560-9360971d47ed"
    )

    val webClient: WebClient = WebClient.builder()
            .clientConnector(ReactorClientHttpConnector(HttpClient.create())).build()

    @Scheduled(fixedRate = 30000)
    fun sendReading() {
        logger.info("sending readings....")
        sensors.forEach { sensor ->
            webClient
                    .post()
                    .uri(URI.create("http://localhost:9000/api/v1/iot"))
                    .bodyValue(
                            SensorReading(
                                    timestamp = OffsetDateTime.now(ZoneOffset.UTC).toEpochSecond(),
                                    sensorId = sensor,
                                    value = Random.nextLong(30, 90)
                            )
                    )
                    .retrieve()
                    .bodyToMono(String::class.java)
                    .block()
        }
    }
}