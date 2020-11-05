package com.datasite.poc.garden

import com.datasite.poc.garden.dto.Garden
import com.datasite.poc.garden.dto.GardenPatch
import com.datasite.poc.garden.dto.GardenPrototype
import com.datasite.poc.garden.entity.toGarden
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactor.mono
import org.bson.types.ObjectId
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono
import java.util.*

@Service
class GardenService(
    private val repository: GardenRepository,
    private val auditService: AuditService,
) {
    suspend fun getAllGardens(): List<Garden> {
        val gardens = repository.getAllGardens().map { it.toGarden() }.toList()
        auditService.auditAllGardensAccess()
        return gardens
    }

    suspend fun getGarden(
        id: UUID
    ): Garden? {
        val entity = repository.getGarden(id) ?: return null
        auditService.auditGardenAccess(id)
        return entity.toGarden()
    }

    @Transactional // suspend should work in spring boot 2.4
    fun createGarden(
        prototype: GardenPrototype
    ): Mono<Garden> = mono {
        val entity = repository.createGarden(prototype)
        auditService.auditGardenCreate()
        return@mono entity.toGarden()
    }

    @Transactional // suspend should work in spring boot 2.4
    fun updateGarden(
        id: UUID,
        patch: GardenPatch
    ): Mono<Garden?> = mono {
        val entity = repository.updateGarden(id, patch) ?: return@mono null
        auditService.auditGardenUpdate()
        return@mono entity.toGarden()
    }

    @Transactional // suspend should work in spring boot 2.4
    fun deleteGarden(
        id: UUID
    ): Mono<Unit> = mono {
        repository.deleteGarden(id)
        auditService.auditGardenDelete()
    }
}

fun String.toObjectId() = if (ObjectId.isValid(this)) ObjectId(this) else null
