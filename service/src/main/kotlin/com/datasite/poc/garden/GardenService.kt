package com.datasite.poc.garden

import com.datasite.poc.garden.dto.Garden
import com.datasite.poc.garden.dto.GardenPrototype
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.springframework.stereotype.Service
import org.springframework.transaction.ReactiveTransactionManager
import org.springframework.transaction.reactive.TransactionalOperator
import org.springframework.transaction.reactive.executeAndAwait

@Service
class GardenService(
    private val repository: GardenRepository,
    transactionManager: ReactiveTransactionManager,
) {
    private val transactionalOperator = TransactionalOperator.create(transactionManager)

    fun getAllGardens(): Flow<Garden> = repository.getAllGardens().map { Garden.from(it) }

    suspend fun getGarden(
        id: String
    ): Garden? = repository.getGarden(id)?.let { Garden.from(it) }

    suspend fun createGarden(
        prototype: GardenPrototype
    ): Garden {
        return transactionalOperator.executeAndAwait {
            Garden.from(repository.createGarden(prototype))
        }!!
    }

    suspend fun deleteGarden(
        id: String
    ) {
        transactionalOperator.executeAndAwait {
            repository.deleteGarden(id)
        }
    }
}
