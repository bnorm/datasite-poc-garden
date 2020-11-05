package com.datasite.poc.garden

import com.datasite.poc.garden.audit.dto.AuditEvent
import com.datasite.poc.garden.dto.Uuid
import com.datasite.poc.garden.user.currentUser
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.springframework.data.mongodb.currentClientSession
import org.springframework.data.mongodb.transactionId
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service
import java.util.*

@Service
class AuditService(
    private val kafkaTemplate: KafkaTemplate<String, String>,
    private val json: Json,
) {
    suspend fun auditAllGardensAccess() {
        val user = currentUser()
        audit(user.id, AuditEvent.AllGardensAccess(userId = user.id))
    }

    suspend fun auditGardenAccess(gardenId: UUID) {
        val user = currentUser()
        audit(user.id, AuditEvent.GardenAccess(userId = user.id, gardenId = gardenId))
    }

    suspend fun auditGardenCreate() {
        val user = currentUser()
        val transactionId = currentTransactionId()
        audit(user.id, AuditEvent.GardenCreate(transactionId = transactionId, userId = user.id))
    }

    suspend fun auditGardenUpdate() {
        val user = currentUser()
        val transactionId = currentTransactionId()
        audit(user.id, AuditEvent.GardenUpdate(transactionId = transactionId, userId = user.id))
    }

    suspend fun auditGardenDelete() {
        val user = currentUser()
        val transactionId = currentTransactionId()
        audit(user.id, AuditEvent.GardenDelete(transactionId = transactionId, userId = user.id))
    }

    suspend fun auditAllGardenSensorsAccess() {
        val user = currentUser()
        audit(user.id, AuditEvent.AllGardenSensorsAccess(userId = user.id))
    }

    suspend fun auditGardenSensorAccess(sensorId: UUID) {
        val user = currentUser()
        audit(user.id, AuditEvent.GardenSensorAccess(userId = user.id, sensorId = sensorId))
    }

    suspend fun auditGardenSensorCreate() {
        val user = currentUser()
        val transactionId = currentTransactionId()
        audit(user.id, AuditEvent.GardenSensorCreate(transactionId = transactionId, userId = user.id))
    }

    suspend fun auditGardenSensorUpdate() {
        val user = currentUser()
        val transactionId = currentTransactionId()
        audit(user.id, AuditEvent.GardenSensorUpdate(transactionId = transactionId, userId = user.id))
    }

    suspend fun auditGardenSensorDelete() {
        val user = currentUser()
        val transactionId = currentTransactionId()
        audit(user.id, AuditEvent.GardenSensorDelete(transactionId = transactionId, userId = user.id))
    }

    private fun audit(key: Uuid, audit: AuditEvent) {
        kafkaTemplate.send("audit.garden.events", key.toString(), json.encodeToString(audit))
    }

    private suspend fun currentTransactionId(): String {
        return currentClientSession()?.transactionId ?: error("not in transaction")
    }
}
