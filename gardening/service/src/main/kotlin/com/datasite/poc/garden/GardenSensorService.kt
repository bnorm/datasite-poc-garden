package com.datasite.poc.garden

import com.datasite.poc.garden.dto.GardenSensor
import com.datasite.poc.garden.dto.GardenSensorPatch
import com.datasite.poc.garden.dto.GardenSensorPrototype
import com.datasite.poc.garden.entity.GardenSensorEntity
import com.datasite.poc.garden.entity.toGarden
import com.datasite.poc.garden.entity.toGardenSensor
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactor.mono
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono
import java.util.*

@Service
class GardenSensorService(
    private val sensorRepository: GardenSensorRepository,
    private val gardenRepository: GardenRepository,
    private val auditService: AuditService,
) {
    suspend fun getAllGardenSensors(): List<GardenSensor> {
        val sensors = sensorRepository.getAllGardenSensors().map { it.toGardenSensor() }.toList()
        auditService.auditAllGardenSensorsAccess()
        return sensors
    }

    suspend fun getGardenSensor(
        id: UUID
    ): GardenSensor? {
        val entity = sensorRepository.getGardenSensor(id) ?: return null
        auditService.auditGardenSensorAccess(id)
        return entity.toGardenSensor()
    }

    @Transactional // suspend should work in spring boot 2.4
    fun createGardenSensor(
        prototype: GardenSensorPrototype
    ): Mono<GardenSensor> = mono {
        val entity = sensorRepository.createGardenSensor(prototype)
        auditService.auditGardenSensorCreate()
        return@mono entity.toGardenSensor()
    }

    @Transactional // suspend should work in spring boot 2.4
    fun updateGardenSensor(
        id: UUID,
        patch: GardenSensorPatch
    ): Mono<GardenSensor?> = mono {
        val entity = sensorRepository.updateGardenSensor(id, patch) ?: return@mono null
        auditService.auditGardenSensorUpdate()
        return@mono entity.toGardenSensor()
    }

    @Transactional // suspend should work in spring boot 2.4
    fun deleteGardenSensor(
        id: UUID
    ): Mono<Unit> = mono {
        sensorRepository.deleteGardenSensor(id)
        auditService.auditGardenSensorDelete()
    }

    private suspend fun GardenSensorEntity.toGardenSensor(): GardenSensor {
        val garden = gardenRepository.getGarden(gardenId)?.toGarden() ?: error("unknown garden id=$gardenId")
        return toGardenSensor(garden)
    }
}
