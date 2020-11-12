package com.datasite.poc.garden

import com.datasite.poc.garden.dto.Garden
import com.datasite.poc.garden.dto.GardenPatch
import com.datasite.poc.garden.dto.GardenPrototype
import com.datasite.poc.garden.entity.toGarden
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
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

    @Transactional
    suspend fun createGarden(
        prototype: GardenPrototype
    ): Garden {
        val entity = repository.createGarden(prototype)
        auditService.auditGardenCreate()
        return entity.toGarden()
    }

    @Transactional
    suspend fun updateGarden(
        id: UUID,
        patch: GardenPatch
    ): Garden? {
        val entity = repository.updateGarden(id, patch) ?: return null
        auditService.auditGardenUpdate()
        return entity.toGarden()
    }

    @Transactional
    suspend fun deleteGarden(
        id: UUID
    ) {
        repository.deleteGarden(id)
        auditService.auditGardenDelete()
    }
}
