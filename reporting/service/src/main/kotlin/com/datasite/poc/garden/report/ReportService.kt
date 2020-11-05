package com.datasite.poc.garden.report

import com.datasite.poc.garden.audit.dto.AuditEvent
import com.datasite.poc.garden.dto.Garden
import com.datasite.poc.garden.report.dto.MostPopularGardensReport
import com.datasite.poc.garden.report.dto.Report
import com.datasite.poc.garden.report.dto.UsersFavoriteGardenReport
import com.datasite.poc.garden.report.entity.GardenEntity
import com.datasite.poc.garden.report.entity.toReport
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
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

    @KafkaListener(topics = ["mongo.garden.table"])
    fun gardenTableChanges(@Payload message: String?) = runBlocking {
        log.info("Processing Mongo gardens table change {}", message)
        if (message != null) {
            val garden = json.decodeFromString<Garden>(message)
            repository.upsertGarden(GardenEntity(garden.id.toString(), garden.name))
        } else {
            // TODO get key and delete garden
        }
        _reportFlow.emit(getUsersFavoriteGardenReport())
        _reportFlow.emit(getMostPopularGardensReport())
    }

    @KafkaListener(topics = ["auditing.garden.events"])
    fun gardenAudits(@Payload message: String) = runBlocking {
        log.info("Processing audit event {}", message)
        val event = json.decodeFromString<AuditEvent>(message)
        if (event is AuditEvent.GardenAccess) {
            repository.incrementUserGardenViewCount(event.userId, event.gardenId)
            _reportFlow.emit(getUsersFavoriteGardenReport())
            _reportFlow.emit(getMostPopularGardensReport())
        }
    }
}
