package com.datasite.poc.garden.event

import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import org.apache.kafka.common.serialization.Serializer
import org.apache.kafka.common.serialization.Deserializer
import org.apache.kafka.common.serialization.Serdes

val jsonFormat = Json {
    ignoreUnknownKeys = true
}

class KotlinxJsonSerializer<T>(
    private val serializer: KSerializer<T>
) : Serializer<T>, Deserializer<T> {
    override fun serialize(topic: String?, data: T?): ByteArray? {
        data ?: return null
        return jsonFormat.encodeToString(serializer, data).toByteArray()
    }

    override fun deserialize(topic: String?, data: ByteArray?): T? {
        data ?: return null
        val json = data.toString(Charsets.UTF_8)
        return jsonFormat.decodeFromString(serializer, json)
    }

    override fun configure(configs: MutableMap<String, *>?, isKey: Boolean) = Unit
    override fun close() = Unit
}

inline fun <reified T> KotlinxJsonSerializer() = KotlinxJsonSerializer(serializer<T>())

class KotlinxSerde<T>(
    kotlinxJsonSerializer: KotlinxJsonSerializer<T>
) : Serdes.WrapperSerde<T>(
    kotlinxJsonSerializer, kotlinxJsonSerializer
)

inline fun <reified T> KotlinxSerde() = KotlinxSerde(KotlinxJsonSerializer<T>())
