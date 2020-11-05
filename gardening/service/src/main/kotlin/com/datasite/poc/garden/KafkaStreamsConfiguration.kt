package com.datasite.poc.garden

import com.datasite.poc.garden.entity.GARDEN_COLLECTION
import java.util.*
import javax.annotation.PostConstruct
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.StreamsConfig
import org.apache.kafka.streams.Topology
import org.apache.kafka.streams.kstream.Consumed
import org.apache.kafka.streams.kstream.Predicate
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component


@Component
class KafkaStreamsConfiguration(
        @Value("\${spring.data.mongodb.database}") val databaseName: String
) {

    val logger: Logger = LoggerFactory.getLogger(KafkaStreamsConfiguration::class.java)

    @PostConstruct
    fun init() {
        val properties = Properties()
        properties[StreamsConfig.APPLICATION_ID_CONFIG] = "datasite-poc-garden-streams"
        properties[StreamsConfig.BOOTSTRAP_SERVERS_CONFIG] = "localhost:9092"

        val topology = buildTopology()
        val streams = KafkaStreams(topology, properties)

        streams.setUncaughtExceptionHandler { thread: Thread, throwable: Throwable ->
            logger.error("====================================")
            logger.error("===== KAFKA STREAMS EXCEPTION ======")
            logger.error("====================================")
            throwable.printStackTrace()
            streams.close()
            streams.cleanUp()
        }

        Runtime.getRuntime().addShutdownHook(Thread(streams::close))
        streams.start()
    }

    private fun buildTopology(): Topology {
        val builder = StreamsBuilder()
        val stringSerde = Serdes.String()
        val stream = builder.stream(
                "mongo.$databaseName.$GARDEN_COLLECTION",
                Consumed.with(stringSerde, stringSerde)
        )

        val (steveStream, brianStream, otherStream) = stream.branch(
                Predicate { _, value -> value?.contains("steve", true) == true },
                Predicate { _, value -> value?.contains("brian", true) == true },
                Predicate { _, _ -> true }
        )

        steveStream.foreach { key, value ->
            logger.info("STEVE STREAM : {} : {}", key, value)
        }

        brianStream.foreach { key, value ->
            logger.info("BRIAN STREAM : {} : {}", key, value)
        }

        otherStream.foreach { key, value ->
            logger.info("OTHER STREAM : {} : {}", key, value)
        }

        return builder.build()
    }


}
