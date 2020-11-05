package com.datasite.poc.garden

import com.datasite.poc.garden.audit.dto.AuditEvent
import com.datasite.poc.garden.user.currentUser
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.springframework.data.mongodb.currentClientSession
import org.springframework.data.mongodb.transactionId
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

@Service
class AuditService(
    private val kafkaTemplate: KafkaTemplate<String, String>,
    private val json: Json,
) {
    suspend fun auditAllGardensAccess() {
        val user = currentUser()
        audit(user.id, AuditEvent.AllGardensAccess(user.id))
    }

    suspend fun auditGardenAccess(gardenId: String) {
        val user = currentUser()
        audit(user.id, AuditEvent.GardenAccess(user.id, gardenId))
    }

    suspend fun auditGardenCreate() {
        val user = currentUser()
        val transactionId = currentTransactionId()
        audit(user.id, AuditEvent.GardenCreate(transactionId, user.id))
    }

    suspend fun auditGardenUpdate() {
        val user = currentUser()
        val transactionId = currentTransactionId()
        audit(user.id, AuditEvent.GardenUpdate(transactionId, user.id))
    }

    suspend fun auditGardenDelete() {
        val user = currentUser()
        val transactionId = currentTransactionId()
        audit(user.id, AuditEvent.GardenDelete(transactionId, user.id))
    }

    private fun audit(key: String, audit: AuditEvent) {
        kafkaTemplate.send("auditing.garden.events", key, json.encodeToString(audit))
    }

    private suspend fun currentTransactionId(): String {
        return currentClientSession()?.transactionId ?: error("not in transaction")
    }
}
