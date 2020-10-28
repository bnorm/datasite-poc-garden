package com.datasite.poc.garden

import com.datasite.poc.garden.entity.GARDEN_COLLECTION
import com.fasterxml.jackson.databind.JsonNode
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.connect.json.JsonDeserializer
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.handler.annotation.Header
import org.springframework.messaging.handler.annotation.Payload

@EnableKafka
@Configuration
class KafkaListenerConfig(
    @Value(value = "\${kafka.bootstrapAddress}")
    private val bootstrapAddress: String
) {
    private val log = LoggerFactory.getLogger(KafkaListenerConfig::class.java)

    @Bean
    fun consumerFactory(): ConsumerFactory<JsonNode, JsonNode> {
        return DefaultKafkaConsumerFactory(
            mapOf(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to bootstrapAddress,
                ConsumerConfig.GROUP_ID_CONFIG to "datasite-poc-garden",
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to JsonDeserializer::class.java,
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to JsonDeserializer::class.java,
            )
        )
    }

    @Bean
    fun kafkaListenerContainerFactory(
        consumerFactory: ConsumerFactory<JsonNode, JsonNode>
    ): ConcurrentKafkaListenerContainerFactory<JsonNode, JsonNode> {
        return ConcurrentKafkaListenerContainerFactory<JsonNode, JsonNode>().apply {
            this.consumerFactory = consumerFactory
        }
    }

    @KafkaListener(topics = ["mongo.\${spring.data.mongodb.database}.${GARDEN_COLLECTION}"])
    fun mongoChanges(
        @Payload(required = false) message: JsonNode?,
        @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY, required = false) key: JsonNode?,
    ) {
        log.info("Received message: $key=$message")
    }
}
