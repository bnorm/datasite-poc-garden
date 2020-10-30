package com.datasite.poc.garden.report

import com.datasite.poc.garden.report.dto.ReportEvent
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.messaging.handler.annotation.Payload
import javax.annotation.PostConstruct

@EnableKafka
@Configuration
class ReportEventListener(
    @Value(value = "\${kafka.bootstrapAddress}")
    private val bootstrapAddress: String,
    private val json: Json,
) {
    private val sharedFlow = MutableSharedFlow<ReportEvent>(extraBufferCapacity = 64)

    @get:Bean("reportEventsSharedFlow")
    val events = sharedFlow.asSharedFlow()

    @PostConstruct
    fun init() {
        // TODO produce dummy events
        GlobalScope.launch {
            val characters = 'a'..'z'
            var count = 0L
            while (true) {
                sharedFlow.emit(ReportEvent.DummyEvent(characters.random(), count++))
                delay(1_000)
            }
        }
    }

    @Bean
    fun consumerFactory(): ConsumerFactory<String, String> {
        return DefaultKafkaConsumerFactory(
            mapOf(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to bootstrapAddress,
                ConsumerConfig.GROUP_ID_CONFIG to "datasite-poc-garden-report",
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
            )
        )
    }

    @Bean
    fun kafkaListenerContainerFactory(
        consumerFactory: ConsumerFactory<String, String>
    ): ConcurrentKafkaListenerContainerFactory<String, String> {
        return ConcurrentKafkaListenerContainerFactory<String, String>().apply {
            this.consumerFactory = consumerFactory
        }
    }

    @KafkaListener(topics = ["postgres.report.events"])
    fun mongoChanges(@Payload message: String) = runBlocking {
        sharedFlow.emit(json.decodeFromString(message))
    }
}
