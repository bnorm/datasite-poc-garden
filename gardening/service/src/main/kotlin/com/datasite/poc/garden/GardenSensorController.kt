package com.datasite.poc.garden

import com.datasite.poc.garden.dto.GardenSensor
import com.datasite.poc.garden.dto.GardenSensorPatch
import com.datasite.poc.garden.dto.GardenSensorPrototype
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import java.util.*

@RestController
@RequestMapping("/api/v1/sensors")
class GardenSensorController(
    private val service: GardenSensorService,
) {
    @GetMapping
    suspend fun getAllGardenSensors(): List<GardenSensor> = service.getAllGardenSensors()

    @GetMapping("/{id}")
    suspend fun getGardenSensor(
        @PathVariable id: UUID
    ): GardenSensor = service.getGardenSensor(id)
        ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Unknown GardenSensor with id=$id")

    @PostMapping
    suspend fun createGardenSensor(
        @RequestBody prototype: GardenSensorPrototype
    ): GardenSensor = service.createGardenSensor(prototype)

    @PutMapping("/{id}")
    suspend fun createGardenSensor(
        @PathVariable id: UUID,
        @RequestBody patch: GardenSensorPatch
    ): GardenSensor = service.updateGardenSensor(id, patch)
        ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Unknown GardenSensor with id=$id")

    @DeleteMapping("/{id}")
    suspend fun deleteGardenSensor(
        @PathVariable id: UUID
    ): Unit = service.deleteGardenSensor(id)
}
