package com.datasite.poc.garden

import com.datasite.poc.garden.dto.Garden
import com.datasite.poc.garden.dto.GardenPatch
import com.datasite.poc.garden.dto.GardenPrototype
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.reactor.mono
import org.slf4j.LoggerFactory
import org.springframework.data.mongodb.currentClientSession
import org.springframework.data.mongodb.transactionId
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono

@Service
class GardenService(
    private val repository: GardenRepository,
) {
    private val log = LoggerFactory.getLogger(this::class.java)

    fun getAllGardens(): Flow<Garden> = repository.getAllGardens().map { Garden.from(it) }

    suspend fun getGarden(
        id: String
    ): Garden? = repository.getGarden(id)?.let { Garden.from(it) }

    @Transactional // suspend should work in spring boot 2.4
    fun createGarden(
        prototype: GardenPrototype
    ): Mono<Garden> = mono {
        log.info("processing transaction with id: {}", currentClientSession()?.transactionId)
        Garden.from(repository.createGarden(prototype))
    }

    @Transactional // suspend should work in spring boot 2.4
    fun updateGarden(
        id: String,
        patch: GardenPatch
    ): Mono<Garden?> = mono {
        log.info("processing transaction with id: {}", currentClientSession()?.transactionId)
        repository.updateGarden(id, patch)?.let { Garden.from(it) }
    }

    @Transactional // suspend should work in spring boot 2.4
    fun deleteGarden(
        id: String
    ): Mono<Unit> = mono {
        log.info("processing transaction with id: {}", currentClientSession()?.transactionId)
        repository.deleteGarden(id)
    }
}
