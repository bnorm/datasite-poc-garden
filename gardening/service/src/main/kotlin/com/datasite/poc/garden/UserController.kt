package com.datasite.poc.garden

import com.datasite.poc.garden.dto.User
import com.datasite.poc.garden.dto.UserPatch
import com.datasite.poc.garden.dto.UserPrototype
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.reactive.awaitSingleOrNull
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
@RequestMapping("/api/v1/users")
class UserController(
    private val service: UserService,
) {
    @GetMapping
    suspend fun getAllUsers(): List<User> = service.getAllUsers()

    @GetMapping("/{id}")
    suspend fun getUser(
        @PathVariable id: UUID
    ): User = service.getUser(id)
        ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Unknown User with id=$id")

    @PostMapping
    suspend fun createUser(
        @RequestBody prototype: UserPrototype
    ): User = service.createUser(prototype)

    @PutMapping("/{id}")
    suspend fun createUser(
        @PathVariable id: UUID,
        @RequestBody patch: UserPatch
    ): User = service.updateUser(id, patch)
        ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Unknown User with id=$id")

    @DeleteMapping("/{id}")
    suspend fun deleteUser(
        @PathVariable id: UUID
    ): Unit = service.deleteUser(id)
}
