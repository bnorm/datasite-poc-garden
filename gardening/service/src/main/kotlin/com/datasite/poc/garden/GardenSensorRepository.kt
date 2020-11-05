package com.datasite.poc.garden

import com.datasite.poc.garden.dto.GardenSensorPatch
import com.datasite.poc.garden.dto.GardenSensorPrototype
import com.datasite.poc.garden.entity.GARDEN_SENSOR_COLLECTION
import com.datasite.poc.garden.entity.GardenSensorEntity
import com.datasite.poc.garden.entity.toEntity
import javax.annotation.PostConstruct
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.reactive.awaitSingleOrNull
import kotlinx.coroutines.runBlocking
import org.springframework.data.mongodb.core.ReactiveMongoOperations
import org.springframework.data.mongodb.core.findAll
import org.springframework.data.mongodb.core.findById
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query.query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.data.mongodb.core.query.isEqualTo
import org.springframework.data.mongodb.core.remove
import org.springframework.data.mongodb.core.updateFirst
import org.springframework.stereotype.Repository
import java.util.*

@Repository
class GardenSensorRepository(
    private val mongoOperation: ReactiveMongoOperations,
) {
    @PostConstruct
    fun createCollections(): Unit = runBlocking {
        if (!mongoOperation.collectionExists(GARDEN_SENSOR_COLLECTION).awaitSingle()) {
            mongoOperation.createCollection(GARDEN_SENSOR_COLLECTION).awaitSingle()
        }
    }

    fun getAllGardenSensors(): Flow<GardenSensorEntity> =
        mongoOperation.findAll<GardenSensorEntity>().asFlow()

    suspend fun getGardenSensor(
        id: UUID
    ): GardenSensorEntity? =
        mongoOperation.findById<GardenSensorEntity>(id).awaitSingleOrNull()

    suspend fun createGardenSensor(
        prototype: GardenSensorPrototype
    ): GardenSensorEntity =
        mongoOperation.save(prototype.toEntity()).awaitSingle()

    suspend fun updateGardenSensor(
        id: UUID,
        patch: GardenSensorPatch
    ): GardenSensorEntity? {
        val query = query(Criteria.where("_id").isEqualTo(id))
        val update = patch.update()
        if (null != update) {
            mongoOperation.updateFirst<GardenSensorEntity>(query, update).awaitSingle()
        }
        return getGardenSensor(id)
    }

    suspend fun deleteGardenSensor(
        id: UUID
    ) {
        val query = query(Criteria.where("_id").isEqualTo(id))
        mongoOperation.remove<GardenSensorEntity>(query).awaitSingle()
    }

    private fun GardenSensorPatch.update(): Update? {
        return Update().apply {
            var modified = false
            if (name != null) {
                set("name", name)
                modified = true
            }
            if (gardenId != null) {
                set("gardenId", gardenId)
                modified = true
            }
            if (!modified) return null
        }
    }
}
