package com.datasite.poc.garden.event

import com.datasite.poc.garden.audit.dto.AuditEvent
import com.datasite.poc.garden.audit.dto.EnrichedAuditEvent
import com.datasite.poc.garden.audit.dto.MongoGarden
import org.apache.kafka.streams.kstream.KStream
import org.springframework.cloud.stream.annotation.EnableBinding
import org.springframework.cloud.stream.annotation.Input
import org.springframework.cloud.stream.annotation.Output
import org.springframework.cloud.stream.annotation.StreamListener
import org.springframework.stereotype.Service

@Service
@EnableBinding(GardenMongoProcessor.KStreamProcessor::class)
class GardenMongoProcessor {
    @StreamListener
    @Output("mongoGardenTable")
    fun processGardenMongoOpLog(
        @Input("mongoGardenOpLog") opLog: KStream<MongoOpLogKey, MongoOpLogValue?>
    ): KStream<String, MongoGarden?> {
        TODO()
    }

    @StreamListener
    @Output("gardenEnhancedAuditLog")
    fun process(
        @Input("gardenAuditLog") audits: KStream<String, AuditEvent>,
        @Input("mongoGardenOpLog") opLog: KStream<MongoOpLogKey, MongoOpLogValue?>
    ): KStream<String, EnrichedAuditEvent> {
        TODO()
    }

    internal interface KStreamProcessor {
        @Input("mongoGardenOpLog")
        fun mongoGardenOpLog(): KStream<MongoOpLogKey, MongoOpLogValue?>

        @Input("gardenAuditLog")
        fun gardenAuditLog(): KStream<String, AuditEvent>

        @Output("mongoGardenTable")
        fun mongoGardenTable(): KStream<String, MongoGarden?>

        @Output("gardenEnhancedAuditLog")
        fun gardenEnhancedAuditLog(): KStream<String, EnrichedAuditEvent>
    }
}
