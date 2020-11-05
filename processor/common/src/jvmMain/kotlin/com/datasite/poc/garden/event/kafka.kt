/**
 * Collection of extension functions for Kafka KStream and KTable instances.
 */
package com.datasite.poc.garden.event

import org.apache.kafka.streams.kstream.KStream
import org.apache.kafka.streams.kstream.KTable

//
// KStream
//

@JvmName("filterNotNull")
fun <K : Any, V : Any> KStream<K?, V?>.filterNotNull(): KStream<K, V> {
    @Suppress("UNCHECKED_CAST")
    return filter { key, value -> key != null && value != null } as KStream<K, V>
}

@JvmName("filterValueNotNull")
fun <K : Any, V : Any> KStream<K, V?>.filterNotNull(): KStream<K, V> {
    @Suppress("UNCHECKED_CAST")
    return filter { _, value -> value != null } as KStream<K, V>
}

@JvmName("filterKeyNotNull")
fun <K : Any, V : Any> KStream<K?, V>.filterNotNull(): KStream<K, V> {
    @Suppress("UNCHECKED_CAST")
    return filter { key, _ -> key != null } as KStream<K, V>
}

inline fun <reified K, reified V> KStream<*, *>.filterIsInstance(): KStream<K, V> {
    @Suppress("UNCHECKED_CAST")
    return filter { key, value -> key is K && value is V } as KStream<K, V>
}

fun <K, V> KStream<*, *>.unsafeCast(): KStream<K, V> {
    @Suppress("UNCHECKED_CAST")
    return this as KStream<K, V>
}

//
// KTable
//

@JvmName("filterNotNull")
fun <K : Any, V : Any> KTable<K?, V?>.filterNotNull(): KTable<K, V> {
    @Suppress("UNCHECKED_CAST")
    return filter { key, value -> key != null && value != null } as KTable<K, V>
}

@JvmName("filterValueNotNull")
fun <K : Any, V : Any> KTable<K, V?>.filterNotNull(): KTable<K, V> {
    @Suppress("UNCHECKED_CAST")
    return filter { _, value -> value != null } as KTable<K, V>
}

@JvmName("filterKeyNotNull")
fun <K : Any, V : Any> KTable<K?, V>.filterNotNull(): KTable<K, V> {
    @Suppress("UNCHECKED_CAST")
    return filter { key, _ -> key != null } as KTable<K, V>
}
