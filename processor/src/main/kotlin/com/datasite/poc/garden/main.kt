package com.datasite.poc.garden

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import kotlinx.coroutines.awaitCancellation
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.common.utils.Bytes
import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.StreamsConfig
import org.apache.kafka.streams.state.KeyValueStore
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.time.Instant
import java.util.*
import org.apache.kafka.streams.kstream.Materialized.`as` as materializedAs

suspend fun main() {
    val source = "mongo.datasite-poc.gardens"
    val kafka = "localhost:9092"

    val props = Properties()
    props[StreamsConfig.APPLICATION_ID_CONFIG] = "datasite-poc-processor"
    props[StreamsConfig.BOOTSTRAP_SERVERS_CONFIG] = kafka
    props[StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG] = Serdes.Bytes()::class.java
    props[StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG] = Serdes.Bytes()::class.java

    val builder = StreamsBuilder()
    val cdc = builder.stream<Bytes, Bytes>(source)

    val gardenTable = cdc.groupByKey()
        .aggregate(
            { null },
            { _, value, agg -> merge(agg, value) },
            materializedAs<Bytes, Bytes?, KeyValueStore<Bytes, ByteArray>>("$source.table")
                .withCachingDisabled()
        )

    gardenTable.toStream().foreach { _, value ->
        if (value != null) {
            println("foreach = ${String(value.get(), StandardCharsets.UTF_8)}")
        }
    }

    val streams = KafkaStreams(builder.build(), props)
    streams.start()

    awaitCancellation()
}

fun merge(oldValue: Bytes?, newValue: Bytes?): Bytes? {
    if (newValue == null) {
        println("received = null")
        return null
    }

    val jsonString = String(newValue.get(), StandardCharsets.UTF_8)
    println("received = $jsonString")

    return try {
        val mapper = ObjectMapper()
        val payload: JsonNode = mapper.readTree(jsonString)
        println("op = ${payload.get("op").asText()}")
        val data: String? = when (payload.get("op").asText()) {
            "c", "r" -> payload.get("after").asText()
            "u" -> update(
                String(oldValue!!.get(), StandardCharsets.UTF_8),
                payload.get("patch").asText()
            )
            "d" -> null
            else -> null
        }
        println("sending = ${data}")
        if (data != null) {
            Bytes(data.toByteArray(StandardCharsets.UTF_8))
        } else {
            null
        }
    } catch (e: IOException) {
        e.printStackTrace()
        null
    }
}

fun update(oldjson: String, patch: String): String {
    val mapper = ObjectMapper()
    val after: ObjectNode = mapper.readTree(oldjson) as ObjectNode
    val json = mapper.readTree(patch)
    if (json.has("\$set")) {
        val set = json["\$set"]
        val it = set.fieldNames()
        while (it.hasNext()) {
            val key = it.next()
            after.put(key, tidyValue(set[key]))
        }
    }
    if (json.has("\$unset")) {
        val unset = json["\$unset"]
        val it = unset.fieldNames()
        while (it.hasNext()) {
            val key = it.next()
            after.remove(key)
        }
    }
    return after.toString()
}

fun tidyValue(value: JsonNode): String = if (value.has("\$date")) {
    Instant.ofEpochMilli(value["\$date"].asText().toLong()).toString()
} else value.asText()
