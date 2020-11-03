package com.datasite.poc.garden.usecase

import com.datasite.poc.garden.KotlinxSerde
import com.datasite.poc.garden.audit.dto.MongoGarden
import com.datasite.poc.garden.mongoOpLog
import com.datasite.poc.garden.toKTable
import kotlinx.coroutines.awaitCancellation
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
        .toKTable<MongoGarden>(
            materializedAs<String, String?, KeyValueStore<Bytes, ByteArray>>("mongo.garden.table")
                .withCachingDisabled()
        )
        .toStream()
        .to("mongo.garden.table", Produced.valueSerde(KotlinxSerde()))

    val streams = KafkaStreams(builder.build(), props)
    streams.start()

    awaitCancellation()
}
