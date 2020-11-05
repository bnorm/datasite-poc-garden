package com.datasite.poc.garden

import com.datasite.poc.garden.dto.UserPatch
import com.datasite.poc.garden.dto.UserPrototype
import com.datasite.poc.garden.entity.USER_COLLECTION
import com.datasite.poc.garden.entity.UserEntity
import com.datasite.poc.garden.entity.toEntity
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import javax.annotation.PostConstruct
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.reactive.awaitSingleOrNull
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
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
class UserRepository(
    private val mongoOperation: ReactiveMongoOperations,
) {
    private val log = LoggerFactory.getLogger(this::class.java)

    @PostConstruct
    fun createCollections(): Unit = runBlocking {
        if (!mongoOperation.collectionExists(USER_COLLECTION).awaitSingle()) {
            mongoOperation.createCollection(USER_COLLECTION).awaitSingle()

            GlobalScope.launch {
                // TODO not sure why, but this needs to be performance asynchronously?
                // The save call never returns
                delay(5_000)
                val steven = mongoOperation.save(
                    UserEntity(
                        id = UUID.fromString("4bb5b46f-c8f9-43db-b1cd-137f12948935"),
                        name = "Steven McAdams",
                        gardenIds = emptyList()
                    )
                ).awaitSingle()
                log.info("Created user {}", steven)

                val brian = mongoOperation.save(
                    UserEntity(
                        id = UUID.fromString("014bfade-7f82-4b2c-94ba-ef3a17d37cc6"),
                        name = "Brian Norman",
                        gardenIds = emptyList()
                    )
                ).awaitSingle()
                log.info("Created user {}", brian)
            }
        }
    }

    fun getAllUsers(): Flow<UserEntity> =
        mongoOperation.findAll<UserEntity>().asFlow()

    suspend fun getUser(
        id: UUID
    ): UserEntity? =
        mongoOperation.findById<UserEntity>(id).awaitSingleOrNull()

    suspend fun createUser(
        prototype: UserPrototype
    ): UserEntity =
        mongoOperation.save(prototype.toEntity()).awaitSingle()

    suspend fun updateUser(
        id: UUID,
        patch: UserPatch
    ): UserEntity? {
        val query = query(Criteria.where("_id").isEqualTo(id))
        val update = patch.update()
        if (null != update) {
            mongoOperation.updateFirst<UserEntity>(query, update).awaitSingle()
        }
        return getUser(id)
    }

    suspend fun deleteUser(
        id: UUID
    ) {
        val query = query(Criteria.where("_id").isEqualTo(id))
        mongoOperation.remove<UserEntity>(query).awaitSingle()
    }

    private fun UserPatch.update(): Update? {
        return Update().apply {
            var modified = false
            if (name != null) {
                set("name", name)
                modified = true
            }
            if (gardenIds != null) {
                set("gardenIds", gardenIds)
                modified = true
            }
            if (!modified) return null
        }
    }
}
