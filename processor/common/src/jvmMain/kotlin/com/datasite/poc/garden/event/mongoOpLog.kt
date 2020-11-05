package com.datasite.poc.garden.event

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject
import org.apache.kafka.common.utils.Bytes
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.kstream.Consumed
import org.apache.kafka.streams.kstream.KStream
import org.apache.kafka.streams.kstream.KTable
import org.apache.kafka.streams.kstream.Materialized
import org.apache.kafka.streams.state.KeyValueStore

@Serializable
data class MongoOpLogValue(
    val op: String,
    val after: String? = null,
    val patch: String? = null,
    val transaction: Transaction? = null,
) {
    @Serializable
    data class Transaction(
        val id: String,
        val total_order: Long,
        val data_collection_order: Long,
    )
}

@Serializable
data class MongoOpLogKey(
    val id: String
)

fun StreamsBuilder.mongoOpLog(topic: String): KStream<MongoOpLogKey, MongoOpLogValue?> =
    stream(topic, Consumed.with(KotlinxSerde(), KotlinxSerde()))

inline fun <reified V> KStream<MongoOpLogKey, MongoOpLogValue?>.toKTable(
    materialized: Materialized<String, String, KeyValueStore<Bytes, ByteArray>>? = null
): KTable<String, V?> =
    selectKey { key, _ -> jsonFormat.parseToJsonElement(key.id).toString() }
        .groupByKey()
        .let {
            if (materialized != null) {
                it.aggregate(
                    { null },
                    { _, value, agg -> merge(agg, value) },
                    materialized
                )
            } else {
                it.aggregate<String>(
                    { null },
                    { _, value, agg -> merge(agg, value) }
                )
            }
        }
        .mapValues { _, value -> value?.let { jsonFormat.decodeFromString<V>(it) } }

fun merge(
    oldValue: String?,
    change: MongoOpLogValue?,
): String? {
    return when (change?.op) {
        "c", "r" -> change.after!!
        "u" -> update(oldValue!!, change.patch!!)
        "d" -> null
        else -> null
    }
}

fun update(oldString: String, patchString: String): String {
    val oldValue = jsonFormat.parseToJsonElement(oldString).jsonObject
    val patch = jsonFormat.parseToJsonElement(patchString).jsonObject
    return buildJsonObject {
        val removedKeys = patch["\$unset"]?.jsonObject?.keys ?: emptySet()
        for ((key, value) in oldValue) {
            if (key !in removedKeys) {
                put(key, value)
            }
        }

        val set = patch["\$set"]?.jsonObject
        if (set != null) {
            for ((key, value) in set.entries) {
                put(key, value)
            }
        }

    }.toString()
}
