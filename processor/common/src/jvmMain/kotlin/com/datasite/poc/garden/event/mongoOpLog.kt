package com.datasite.poc.garden.event

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

fun StreamsBuilder.mongoOpLog(topic: String): KStream<MongoOpLogKey, MongoOpLogValue?> =
    stream(topic, Consumed.with(KotlinxSerde(), KotlinxSerde()))

inline fun <reified V> KStream<MongoOpLogKey, MongoOpLogValue?>.toKTable(
    materialized: Materialized<String, String, KeyValueStore<Bytes, ByteArray>>? = null,
    crossinline keyTransformer: (key: MongoOpLogKey) -> String = { jsonFormat.parseToJsonElement(it.id).toString() },
    crossinline valueTransformer: (value: String?) -> V? = { it?.let { jsonFormat.decodeFromString<V>(it) } }
): KTable<String, V?> =
    selectKey { key, _ -> keyTransformer(key) }
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
        .mapValues { _, value -> valueTransformer(value) }

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
