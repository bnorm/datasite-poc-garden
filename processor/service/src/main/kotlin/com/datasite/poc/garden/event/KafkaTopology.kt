package com.datasite.poc.garden.event

import com.datasite.poc.garden.iot.dto.SensorReading
import com.datasite.poc.garden.report.dto.GardenEntity
import com.datasite.poc.garden.report.dto.GardenSensorEntity
import com.datasite.poc.garden.report.dto.GardenSensorReading
import com.datasite.poc.garden.report.dto.UserEntity
import kotlinx.datetime.Instant
import kotlinx.serialization.decodeFromString
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.common.utils.Bytes
import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.StreamsConfig
import org.apache.kafka.streams.kstream.Consumed
import org.apache.kafka.streams.kstream.KStream
import org.apache.kafka.streams.kstream.KTable
import org.apache.kafka.streams.kstream.Materialized
import org.apache.kafka.streams.kstream.Produced
import org.apache.kafka.streams.state.KeyValueStore
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.stereotype.Component
import java.util.*
import javax.annotation.PostConstruct

@Component
class KafkaTopology(
    private val kafkaProperties: KafkaProperties
) {
    @PostConstruct
    fun init() {
        val props = Properties()
        props[StreamsConfig.APPLICATION_ID_CONFIG] = "datasite-event-processor"
        props[StreamsConfig.MAX_TASK_IDLE_MS_CONFIG] = "5000" // Important for doing KStream-KTable joins
        props[StreamsConfig.BOOTSTRAP_SERVERS_CONFIG] = kafkaProperties.bootstrapServers.joinToString()
        props[StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG] = Serdes.String()::class.java
        props[StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG] = Serdes.String()::class.java

        val builder = StreamsBuilder()

        val sensorReadings = builder.stream(
            "iot.sensor.reading",
            Consumed.with(Serdes.String(), KotlinxSerde<SensorReading>())
        )

//        val audits = builder.stream(
//            "audit.garden.event",
//            Consumed.with(Serdes.String(), KotlinxSerde<AuditEvent>())
//        )

        val gardenTable: KTable<String, GardenEntity?> =
            builder.mongoOpLog("mongo.datasite-poc.gardens")
                .toKTable(
                    Materialized.`as`<String, String?, KeyValueStore<Bytes, ByteArray>>("mongo.garden.table")
                        .withCachingDisabled(),
                    keyTransformer = { jsonFormat.decodeFromString<MongoUuid>(it.id).uuid.toString() },
                    valueTransformer = { value ->
                        value?.let { jsonFormat.decodeFromString<MongoGarden>(it).toGardenEntity() }
                    },
                )

        val userTable: KTable<String, UserEntity?> =
            builder.mongoOpLog("mongo.datasite-poc.users")
                .toKTable(
                    Materialized.`as`<String, String?, KeyValueStore<Bytes, ByteArray>>("mongo.user.table")
                        .withCachingDisabled(),
                    keyTransformer = { jsonFormat.decodeFromString<MongoUuid>(it.id).uuid.toString() },
                    valueTransformer = { value ->
                        value?.let { jsonFormat.decodeFromString<MongoUser>(it).toUserEntity() }
                    },
                )

        val sensorTable: KTable<String, GardenSensorEntity?> =
            builder.mongoOpLog("mongo.datasite-poc.garden_sensors")
                .toKTable(
                    Materialized.`as`<String, String?, KeyValueStore<Bytes, ByteArray>>("mongo.garden_sensor.table")
                        .withCachingDisabled(),
                    keyTransformer = { jsonFormat.decodeFromString<MongoUuid>(it.id).uuid.toString() },
                    valueTransformer = { value ->
                        value?.let { jsonFormat.decodeFromString<MongoGardenSensor>(it).toGardenSensorEntity() }
                    },
                )

        val gardenReadings: KStream<String, GardenSensorReading> =
            sensorReadings.join<GardenSensorEntity, GardenSensorReading>(sensorTable) { reading, sensor ->
                GardenSensorReading(
                    Instant.fromEpochMilliseconds(reading.timestamp),
                    sensor.gardenId,
                    reading.sensorId,
                    reading.value
                )
            }

        userTable
            .toStream()
            .to("mongo.user.table", Produced.valueSerde(KotlinxSerde()))

        gardenTable
            .toStream()
            .to("mongo.garden.table", Produced.valueSerde(KotlinxSerde()))

        sensorTable
            .toStream()
            .to("mongo.garden_sensor.table", Produced.valueSerde(KotlinxSerde()))

        gardenReadings
            .to("iot.garden_sensor.reading", Produced.valueSerde(KotlinxSerde()))

//        val sensorTable = mongoSensorTable
//            .join<GardenSensorEntity?, String, Garden>(
//                gardenTable,
//                { it?.gardenId?.toString() },
//                { sensor: GardenSensorEntity?, garden: Garden ->
//                    sensor?.toGardenSensor(garden)
//                },
//                Materialized.`as`<String, GardenSensor?, KeyValueStore<Bytes, ByteArray>>("mongo.sensor.table.joined")
//                    .withCachingDisabled(),
//            )

//        audits
//            .filterIsInstance<String, AuditEvent.GardenAccess>()
//            .selectKey { _, value -> jsonFormat.encodeToString(value.gardenId) }
//            .join<Garden, EnrichedAuditEvent>(gardenTable) { audit, garden ->
//                EnrichedAuditEvent.GardenAccess(audit.timestamp, audit.userId, garden)
//            }
//            .foreach { _, value -> println("enriched = $value") }


        val streams = KafkaStreams(builder.build(), props)
        streams.start()
    }
}
