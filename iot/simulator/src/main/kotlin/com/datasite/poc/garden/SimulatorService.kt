package com.datasite.poc.garden

import com.datasite.poc.garden.dto.Garden
import com.datasite.poc.garden.dto.GardenPatch
import com.datasite.poc.garden.dto.GardenPrototype
import com.datasite.poc.garden.dto.GardenSensor
import com.datasite.poc.garden.dto.GardenSensorPatch
import com.datasite.poc.garden.dto.GardenSensorPrototype
import com.datasite.poc.garden.dto.Uuid
import com.datasite.poc.garden.iot.dto.SensorReading
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import org.springframework.web.reactive.function.client.awaitExchange
import reactor.netty.http.client.HttpClient
import java.net.URI
import javax.annotation.PostConstruct
import kotlin.random.Random

@Service
class SimulatorService(
    @Value(value = "\${iot.gateway.url}") private val iotGatewayUrl: String,
) {
    companion object {
        private const val steveId = "4bb5b46f-c8f9-43db-b1cd-137f12948935"
        private const val brianId = "014bfade-7f82-4b2c-94ba-ef3a17d37cc6"

        private const val MIN_GARDENS = 5
        private const val MIN_SENSORS = 2
    }

    val logger: Logger = LoggerFactory.getLogger(SimulatorService::class.java)

    val webClient: WebClient = WebClient.builder()
        .clientConnector(ReactorClientHttpConnector(HttpClient.create())).build()

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default + CoroutineName("Simulator"))

    private val users = listOf(steveId, brianId)
    private val actions = SimulatorAction.values().flatMap { action -> List(action.weight) { action } }.shuffled()

    private var gardenCount = 0
    private fun nextGardenName() = "Garden Number ${(gardenCount++).toString().padStart(3, '0')}"

    private var sensorCount = 0
    private fun nextSensorName() = "Sensor Number ${(sensorCount++).toString().padStart(3, '0')}"

    @PostConstruct
    fun schedule() {
        scope.launch {
            init()
            while (true) {
                delay(250)
                try {
                    loop()
                } catch (t: Throwable) {
                    logger.warn("Simulator loop failed", t)
                }
            }
        }
    }

    suspend fun init() {
        var ping = false
        while (!ping) {
            ping = runCatching { getAllGardens(users.random()) }.isSuccess
        }

        val gardenCount = getAllGardens(users.random()).size
        repeat(MIN_GARDENS - gardenCount) {
            createGarden(users.random(), GardenPrototype(nextGardenName()))
        }

        val gardens = getAllGardens(users.random())
        val sensorCount = getAllGardenSensors(users.random()).size
        repeat(MIN_SENSORS - sensorCount) {
            createGardenSensor(users.random(), GardenSensorPrototype(nextSensorName(), gardens.random().id))
        }
    }

    suspend fun loop() {
        val user = users.random()
        when (actions.random()) {
            SimulatorAction.SensorReadings -> {
                val sensor = getAllGardenSensors(user).random()
                logger.info("Sensor reading: sensor={}", sensor)
                submitSensorReading(sensor, Random.nextLong(1, 11))
            }
            SimulatorAction.UserAccess -> {
                val garden = getAllGardens(user).random()
                logger.info("User access: garden={}", garden)
                getGarden(user, garden.id)
            }
            SimulatorAction.RenameGarden -> {
                val garden = getAllGardens(user).random()
                val name = nextGardenName()
                logger.info("Renaming garden: name={} garden={}", name, garden)
                updateGarden(user, garden.id, GardenPatch(name = name))
            }
            SimulatorAction.MoveSensor -> {
                val sensor = getAllGardenSensors(user).random()
                val garden = getAllGardens(user).filter { it.id != sensor.garden.id }.random()
                logger.info("Moving sensor: sensor={} garden={}", sensor, garden)
                updateGardenSensor(user, sensor.id, GardenSensorPatch(gardenId = garden.id))
            }
        }
    }


    private suspend fun submitSensorReading(sensor: GardenSensor, reading: Long) {
        webClient.post()
            .uri(URI.create(iotGatewayUrl))
            .bodyValue(SensorReading(sensorId = sensor.id, value = reading))
            .awaitExchange()
    }

    private suspend fun getAllGardens(user: String) = webClient.get()
        .uri(URI.create("http://localhost:9000/api/v1/gardens"))
        .header("X-USER", user)
        .retrieve().awaitBody<List<Garden>>()

    private suspend fun getGarden(user: String, id: Uuid) = webClient.get()
        .uri(URI.create("http://localhost:9000/api/v1/gardens/$id"))
        .header("X-USER", user)
        .retrieve().awaitBody<Garden>()

    private suspend fun createGarden(user: String, prototype: GardenPrototype) = webClient.post()
        .uri(URI.create("http://localhost:9000/api/v1/gardens"))
        .header("X-USER", user)
        .bodyValue(prototype)
        .retrieve().awaitBody<Garden>()

    private suspend fun updateGarden(user: String, id: Uuid, patch: GardenPatch) = webClient.put()
        .uri(URI.create("http://localhost:9000/api/v1/gardens/$id"))
        .header("X-USER", user)
        .bodyValue(patch)
        .retrieve().awaitBody<Garden>()

    private suspend fun getAllGardenSensors(user: String) = webClient.get()
        .uri(URI.create("http://localhost:9000/api/v1/sensors"))
        .header("X-USER", user)
        .retrieve().awaitBody<List<GardenSensor>>()

    private suspend fun createGardenSensor(user: String, prototype: GardenSensorPrototype) = webClient.post()
        .uri(URI.create("http://localhost:9000/api/v1/sensors"))
        .header("X-USER", user)
        .bodyValue(prototype)
        .retrieve().awaitBody<GardenSensor>()

    private suspend fun updateGardenSensor(user: String, id: Uuid, patch: GardenSensorPatch) = webClient.put()
        .uri(URI.create("http://localhost:9000/api/v1/sensors/$id"))
        .header("X-USER", user)
        .bodyValue(patch)
        .retrieve().awaitBody<GardenSensor>()
}
