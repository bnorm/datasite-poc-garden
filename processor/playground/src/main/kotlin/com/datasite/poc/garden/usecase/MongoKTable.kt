package com.datasite.poc.garden.usecase

import com.datasite.poc.garden.event.KotlinxSerde
import com.datasite.poc.garden.event.MongoGarden
import com.datasite.poc.garden.event.MongoUuid
import com.datasite.poc.garden.event.jsonFormat
import com.datasite.poc.garden.event.mongoOpLog
import com.datasite.poc.garden.event.toGarden
import com.datasite.poc.garden.event.toKTable
import kotlinx.coroutines.awaitCancellation
import kotlinx.serialization.decodeFromString
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.common.utils.Bytes
import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.StreamsConfig
import org.apache.kafka.streams.kstream.Produced
import org.apache.kafka.streams.state.KeyValueStore
import java.util.*
import org.apache.kafka.streams.kstream.Materialized.`as` as materializedAs

suspend fun main() {
    val props = Properties()
    props[StreamsConfig.APPLICATION_ID_CONFIG] = "datasite-mongo-processor"
    props[StreamsConfig.BOOTSTRAP_SERVERS_CONFIG] = "localhost:9092"
    props[StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG] = Serdes.String()::class.java
    props[StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG] = Serdes.String()::class.java

    val builder = StreamsBuilder()

    builder.mongoOpLog("mongo.datasite-poc.gardens")
        .toKTable(
            materializedAs<String, String?, KeyValueStore<Bytes, ByteArray>>("mongo.garden.table")
                .withCachingDisabled(),
            keyTransformer = { jsonFormat.decodeFromString<MongoUuid>(it.id).uuid.toString() },
            valueTransformer = { value -> value?.let { jsonFormat.decodeFromString<MongoGarden>(it).toGarden() } },
        )
        .toStream()
        .to("mongo.garden.table", Produced.valueSerde(KotlinxSerde()))

    val streams = KafkaStreams(builder.build(), props)
    streams.start()

    awaitCancellation()
}
