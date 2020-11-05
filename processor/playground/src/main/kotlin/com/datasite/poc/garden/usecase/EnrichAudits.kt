package com.datasite.poc.garden.usecase

import com.datasite.poc.garden.audit.dto.AuditEvent
import com.datasite.poc.garden.audit.dto.EnrichedAuditEvent
import com.datasite.poc.garden.dto.Garden
import com.datasite.poc.garden.event.MongoUuid
import com.datasite.poc.garden.event.KotlinxSerde
import com.datasite.poc.garden.event.MongoGarden
import com.datasite.poc.garden.event.filterIsInstance
import com.datasite.poc.garden.event.jsonFormat
import com.datasite.poc.garden.event.mongoOpLog
import com.datasite.poc.garden.event.toGarden
import com.datasite.poc.garden.event.toKTable
import kotlinx.coroutines.awaitCancellation
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.StreamsConfig
import org.apache.kafka.streams.kstream.Consumed
import java.util.*

suspend fun main() {
    val mongoCdcSource = "mongo.datasite-poc.gardens"
    val auditSource = "auditing.garden.events"

    val props = Properties()
    props[StreamsConfig.APPLICATION_ID_CONFIG] = "datasite-audit-reprocess"
    props[StreamsConfig.MAX_TASK_IDLE_MS_CONFIG] = "60000" // Important for doing reprocessing of KStream-KTable joins
    props[StreamsConfig.BOOTSTRAP_SERVERS_CONFIG] = "localhost:9092"
    props[StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG] = Serdes.String()::class.java
    props[StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG] = Serdes.String()::class.java

    val builder = StreamsBuilder()

    val audits = builder.stream(
        auditSource,
        Consumed.with(Serdes.String(), KotlinxSerde<AuditEvent>())
    )

    // Cannot consume KTable topic as it will compact and hide deleted entities
    val mongoTable = builder.mongoOpLog(mongoCdcSource).toKTable<Garden>(
        keyTransformer = { jsonFormat.decodeFromString<MongoUuid>(it.id).uuid.toString() },
        valueTransformer = { value -> value?.let { jsonFormat.decodeFromString<MongoGarden>(it).toGarden() } }
    )

    audits
        .filterIsInstance<String, AuditEvent.GardenAccess>()
        .selectKey { _, value -> jsonFormat.encodeToString(value.gardenId) }
        .join<Garden, EnrichedAuditEvent>(mongoTable) { audit, garden ->
            EnrichedAuditEvent.GardenAccess(audit.timestamp, audit.userId, garden)
        }
        .foreach { _, value -> println("enriched = $value") }

    val streams = KafkaStreams(builder.build(), props)
    streams.start()
    Runtime.getRuntime().addShutdownHook(Thread {
        streams.cleanUp()
    })

    awaitCancellation()
}
