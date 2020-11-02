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
        id: String
    ): Garden? {
        val objectId = id.toObjectId() ?: return null
        val entity = repository.getGarden(objectId) ?: return null
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
        id: String,
        patch: GardenPatch
    ): Mono<Garden?> = mono {
        val objectId = id.toObjectId() ?: return@mono null
        val entity = repository.updateGarden(objectId, patch) ?: return@mono null
        auditService.auditGardenUpdate()
        return@mono entity.toGarden()
    }

    @Transactional // suspend should work in spring boot 2.4
    fun deleteGarden(
        id: String
    ): Mono<Unit> = mono {
        val objectId = id.toObjectId() ?: return@mono null
        repository.deleteGarden(objectId)
        auditService.auditGardenDelete()
    }
}

private fun String.toObjectId() = if (ObjectId.isValid(this)) ObjectId(this) else null
