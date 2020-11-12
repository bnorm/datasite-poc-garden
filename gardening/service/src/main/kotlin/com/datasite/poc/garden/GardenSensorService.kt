package com.datasite.poc.garden

import com.datasite.poc.garden.dto.GardenSensor
import com.datasite.poc.garden.dto.GardenSensorPatch
import com.datasite.poc.garden.dto.GardenSensorPrototype
import com.datasite.poc.garden.entity.GardenSensorEntity
import com.datasite.poc.garden.entity.toGarden
import com.datasite.poc.garden.entity.toGardenSensor
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
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

    @Transactional
    suspend fun createGardenSensor(
        prototype: GardenSensorPrototype
    ): GardenSensor {
        val entity = sensorRepository.createGardenSensor(prototype)
        auditService.auditGardenSensorCreate()
        return entity.toGardenSensor()
    }

    @Transactional
    suspend fun updateGardenSensor(
        id: UUID,
        patch: GardenSensorPatch
    ): GardenSensor? {
        val entity = sensorRepository.updateGardenSensor(id, patch) ?: return null
        auditService.auditGardenSensorUpdate()
        return entity.toGardenSensor()
    }

    @Transactional
    suspend fun deleteGardenSensor(
        id: UUID
    ) {
        sensorRepository.deleteGardenSensor(id)
        auditService.auditGardenSensorDelete()
    }

    private suspend fun GardenSensorEntity.toGardenSensor(): GardenSensor {
        val garden = gardenRepository.getGarden(gardenId)?.toGarden() ?: error("unknown garden id=$gardenId")
        return toGardenSensor(garden)
    }
}
