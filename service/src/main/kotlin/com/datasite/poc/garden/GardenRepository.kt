package com.datasite.poc.garden

import com.datasite.poc.garden.dto.GardenPatch
import com.datasite.poc.garden.dto.GardenPrototype
import com.datasite.poc.garden.entity.GARDEN_COLLECTION
import com.datasite.poc.garden.entity.GardenEntity
import javax.annotation.PostConstruct
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.reactive.awaitSingleOrNull
import kotlinx.coroutines.runBlocking
import org.bson.types.ObjectId
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

@Repository
class GardenRepository(
    private val mongoOperation: ReactiveMongoOperations,
) {
    @PostConstruct
    fun createCollections(): Unit = runBlocking {
        if (!mongoOperation.collectionExists(GARDEN_COLLECTION).awaitSingle()) {
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

    suspend fun updateGarden(
        id: String,
        patch: GardenPatch
    ): GardenEntity? {
        val query = query(Criteria.where("_id").isEqualTo(ObjectId(id)))
        val update = patch.update()
        if (null != update) {
            mongoOperation.updateFirst<GardenEntity>(query, update).awaitSingle()
        }
        return getGarden(id)
    }

    suspend fun deleteGarden(
        id: String
    ) {
        val query = query(Criteria.where("_id").isEqualTo(ObjectId(id)))
        mongoOperation.remove<GardenEntity>(query).awaitSingle()
    }

    private fun GardenPatch.update(): Update? {
        return Update().apply {
            var modified = false
            if (name != null) {
                set("name", name)
                modified = true
            }
            if (!modified) return null
        }
    }
}
