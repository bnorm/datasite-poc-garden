package com.datasite.poc.garden

import com.datasite.poc.garden.dto.Garden
import com.datasite.poc.garden.dto.GardenPrototype
import kotlinx.coroutines.flow.Flow
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/api/v1/gardens")
class GardenController(
    private val service: GardenService,
) {
    @GetMapping
    fun getAllGardens(): Flow<Garden> = service.getAllGardens()

    @GetMapping("/{id}")
    suspend fun getGarden(
        @PathVariable id: String
    ): Garden = service.getGarden(id)
        ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Unknown Garden with id=$id")

    @PostMapping
    suspend fun createGarden(
        @RequestBody prototype: GardenPrototype
    ): Garden = service.createGarden(prototype)

    @DeleteMapping("/{id}")
    suspend fun deleteGarden(
        @PathVariable id: String
    ): Unit = service.deleteGarden(id)
}
