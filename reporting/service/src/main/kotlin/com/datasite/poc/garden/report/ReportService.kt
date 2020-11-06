package com.datasite.poc.garden.report

import com.datasite.poc.garden.dto.AuditEvent
import com.datasite.poc.garden.dto.toUuid
import com.datasite.poc.garden.report.dto.GardenEntity
import com.datasite.poc.garden.report.dto.GardenSensorEntity
import com.datasite.poc.garden.report.dto.GardenSensorReading
import com.datasite.poc.garden.report.dto.GardenSensorReport
import com.datasite.poc.garden.report.dto.MostPopularGardensReport
import com.datasite.poc.garden.report.dto.Report
import com.datasite.poc.garden.report.dto.SensorReport
import com.datasite.poc.garden.report.dto.UserEntity
import com.datasite.poc.garden.report.dto.UsersFavoriteGardenReport
import com.datasite.poc.garden.report.entity.GardenPgEntity
import com.datasite.poc.garden.report.entity.GardenSensorPgEntity
import com.datasite.poc.garden.report.entity.UserPgEntity
import com.datasite.poc.garden.report.entity.toReport
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.handler.annotation.Header
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Service

@Service
class ReportService(
    private val repository: ReportRepository,
    private val json: Json,
) {
    private val log = LoggerFactory.getLogger(this::class.java)

    private val _reportFlow = MutableSharedFlow<Report>()
    val reportFlow = _reportFlow.asSharedFlow()

    suspend fun getMostPopularGardensReport(): MostPopularGardensReport =
        repository.getGardenViewCounts().toList().toReport()

    suspend fun getUsersFavoriteGardenReport(): UsersFavoriteGardenReport =
        repository.getUserTopGarden().toList().toReport()

    suspend fun getGardenSensorTotalReport(): GardenSensorReport =
        repository.getGardenSensorTotal().toList().toReport()

    suspend fun getSensorTotalReport(): SensorReport =
        repository.getSensorTotal().toList().toReport()

    @KafkaListener(topics = ["mongo.garden.table"])
    fun mongoGardenTable(
        @Payload message: String?,
        @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) key: String?,
    ) = runBlocking {
        log.info("Processing Mongo gardens table change {}", message)
        if (message != null) {
            val garden = json.decodeFromString<GardenEntity>(message)
            repository.upsertGarden(GardenPgEntity(garden.id, garden.name))
        } else if (key != null) {
            repository.deleteGarden(key.toUuid())
        }
        _reportFlow.emit(getUsersFavoriteGardenReport())
        _reportFlow.emit(getMostPopularGardensReport())
        _reportFlow.emit(getGardenSensorTotalReport())
    }

    @KafkaListener(topics = ["mongo.garden_sensor.table"])
    fun mongoGardenSensorTable(
        @Payload message: String?,
        @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) key: String?,
    ) = runBlocking {
        log.info("Processing Mongo garden sensors table change {}", message)
        if (message != null) {
            val sensor = json.decodeFromString<GardenSensorEntity>(message)
            repository.upsertGardenSensor(GardenSensorPgEntity(sensor.id, sensor.name, sensor.gardenId))
        } else if (key != null) {
            repository.deleteGardenSensor(key.toUuid())
        }
        _reportFlow.emit(getSensorTotalReport())
    }

    @KafkaListener(topics = ["mongo.user.table"])
    fun mongoUserTable(
        @Payload message: String?,
        @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) key: String?,
    ) = runBlocking {
        log.info("Processing Mongo users table change {}", message)
        if (message != null) {
            val user = json.decodeFromString<UserEntity>(message)
            repository.upsertUser(UserPgEntity(user.id, user.name))
        } else if (key != null) {
            repository.deleteUser(key.toUuid())
        }
        _reportFlow.emit(getUsersFavoriteGardenReport())
        _reportFlow.emit(getMostPopularGardensReport())
    }

    @KafkaListener(topics = ["audit.garden.event"])
    fun auditGardenEvent(
        @Payload message: String
    ) = runBlocking {
        log.info("Processing audit event {}", message)
        val event = json.decodeFromString<AuditEvent>(message)
        if (event is AuditEvent.GardenAccess) {
            repository.incrementUserGardenViewCount(event.userId, event.gardenId)
            _reportFlow.emit(getUsersFavoriteGardenReport())
            _reportFlow.emit(getMostPopularGardensReport())
        }
    }

    @KafkaListener(topics = ["iot.garden_sensor.reading"])
    fun iotGardenSensorReading(
        @Payload message: String
    ) = runBlocking {
        log.info("Processing Garden sensor reading {}", message)
        val reading = json.decodeFromString<GardenSensorReading>(message)
        repository.accumulateGardenSensorReading(reading.gardenId, reading.value)
        repository.accumulateSensorReading(reading.sensorId, reading.value)
        _reportFlow.emit(getGardenSensorTotalReport())
        _reportFlow.emit(getSensorTotalReport())
    }
}
