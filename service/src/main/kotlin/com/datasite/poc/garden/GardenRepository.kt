package com.datasite.poc.garden

import com.datasite.poc.garden.dto.GardenPrototype
import com.datasite.poc.garden.entity.GARDEN_COLLECTION
import com.datasite.poc.garden.entity.GardenEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.reactive.awaitSingleOrNull
import kotlinx.coroutines.runBlocking
import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.ReactiveMongoOperations
import org.springframework.data.mongodb.core.findAll
import org.springframework.data.mongodb.core.findById
import org.springframework.stereotype.Repository
import javax.annotation.PostConstruct

@Repository
class GardenRepository(
    private val mongoOperation: ReactiveMongoOperations,
) {
    @PostConstruct
    fun createCollections(): Unit = runBlocking {
        if (mongoOperation.getCollection(GARDEN_COLLECTION).awaitSingleOrNull() == null) {
            mongoOperation.createCollection(GARDEN_COLLECTION).awaitSingle()
        }
    }

    fun getAllGardens(): Flow<GardenEntity> =
        mongoOperation.findAll<GardenEntity>().asFlow()

    suspend fun getGarden(
        id: String
    ): GardenEntity? =
        mongoOperation.findById<GardenEntity>(ObjectId(id)).awaitSingleOrNull()

    suspend fun createGarden(
        prototype: GardenPrototype
    ): GardenEntity =
        mongoOperation.save(GardenEntity.from(prototype)).awaitSingle()

    suspend fun deleteGarden(
        id: String
    ) {
        val garden = getGarden(id) ?: return
        mongoOperation.remove(garden).awaitSingle()
    }
}
