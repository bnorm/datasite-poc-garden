package com.datasite.poc.garden

import com.datasite.poc.garden.event.MongoGarden
import com.datasite.poc.garden.event.mongoOpLog
import com.datasite.poc.garden.event.toKTable
import kotlinx.coroutines.awaitCancellation
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.common.utils.Bytes
import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.StreamsConfig
import org.apache.kafka.streams.state.KeyValueStore
import java.util.*
import org.apache.kafka.streams.kstream.Materialized.`as` as materializedAs

suspend fun main() {
    val source = "mongo.datasite-poc.gardens"
    val kafka = "localhost:9092"

    val props = Properties()
    props[StreamsConfig.APPLICATION_ID_CONFIG] = "datasite-poc-playground"
    props[StreamsConfig.BOOTSTRAP_SERVERS_CONFIG] = kafka
    props[StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG] = Serdes.Bytes()::class.java
    props[StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG] = Serdes.Bytes()::class.java

    val builder = StreamsBuilder()
    val cdc = builder.mongoOpLog(source)

    val gardenTable = cdc.toKTable<MongoGarden>(
        materializedAs<String, String?, KeyValueStore<Bytes, ByteArray>>("$source.table")
            .withCachingDisabled()
    )

    gardenTable.toStream().foreach { _, value ->
        println("foreach = $value")
    }

    val streams = KafkaStreams(builder.build(), props)
    streams.start()

    awaitCancellation()
}
