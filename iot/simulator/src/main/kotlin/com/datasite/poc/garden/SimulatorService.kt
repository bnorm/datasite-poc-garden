package com.datasite.poc.garden

import com.datasite.poc.garden.dto.GardenSensor
import com.datasite.poc.garden.iot.dto.SensorReading
import kotlinx.coroutines.runBlocking
import java.net.URI
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import org.springframework.web.reactive.function.client.awaitExchange
import reactor.netty.http.client.HttpClient
import kotlin.random.Random

@Service
class SimulatorService(
        @Value(value = "\${iot.gateway.url}") private val iotGatewayUrl: String,
) {
    val logger: Logger = LoggerFactory.getLogger(SimulatorService::class.java)

    val webClient: WebClient = WebClient.builder()
            .clientConnector(ReactorClientHttpConnector(HttpClient.create())).build()

    @Scheduled(fixedRate = 30000)
    fun sendReading() = runBlocking {
        logger.info("sending readings....")

        val sensors = webClient
            .get()
            .uri(URI.create("http://localhost:9000/api/v1/sensors"))
            .header("X-USER", "014bfade-7f82-4b2c-94ba-ef3a17d37cc6")
            .retrieve()
            .awaitBody<List<GardenSensor>>()

        sensors.forEach { sensor ->
            webClient
                    .post()
                    .uri(URI.create(iotGatewayUrl))
                    .bodyValue(
                            SensorReading(
                                    sensorId = sensor.id,
                                    value = Random.nextLong(30, 90)
                            )
                    )
                    .awaitExchange()
        }
    }
}
