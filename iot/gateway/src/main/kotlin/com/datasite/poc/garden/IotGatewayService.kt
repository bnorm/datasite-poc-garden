package com.datasite.poc.garden

import com.datasite.poc.garden.dto.SensorReading
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

@Service
class IotGatewayService(
        val kafkaTemplate: KafkaTemplate<String, String>
) {

    val logger: Logger = LoggerFactory.getLogger(IotGatewayService::class.java)

    suspend fun process(sensorReading: SensorReading) {
        val sensorReadingAsJson: String = Json.encodeToString(sensorReading)
        logger.info("sending sensor reading to kafka : {}", sensorReadingAsJson)
        kafkaTemplate.send("iot.garden.reading", sensorReading.sensorId, sensorReadingAsJson).addCallback(
                { sendResult ->
                    logger.info("successfully sent : partition {},  offset : {}",
                            sendResult?.recordMetadata?.partition(),
                            sendResult?.recordMetadata?.offset()
                    )

                },
                { error ->
                    logger.error(error.message)
                }
        )
    }

}