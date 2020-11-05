package com.datasite.poc.garden

import com.datasite.poc.garden.dto.User
import com.datasite.poc.garden.dto.UserPatch
import com.datasite.poc.garden.dto.UserPrototype
import com.datasite.poc.garden.entity.UserEntity
import com.datasite.poc.garden.entity.toGarden
import com.datasite.poc.garden.entity.toUser
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactor.mono
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono
import java.util.*

@Service
class UserService(
    private val userRepository: UserRepository,
    private val gardenRepository: GardenRepository,
) {
    suspend fun getAllUsers(): List<User> {
        return userRepository.getAllUsers().map { it.toUser() }.toList()
    }

    suspend fun getUser(
        id: UUID
    ): User? {
        val entity = userRepository.getUser(id) ?: return null
        return entity.toUser()
    }

    @Transactional // suspend should work in spring boot 2.4
    fun createUser(
        prototype: UserPrototype
    ): Mono<User> = mono {
        val entity = userRepository.createUser(prototype)
        return@mono entity.toUser()
    }

    @Transactional // suspend should work in spring boot 2.4
    fun updateUser(
        id: UUID,
        patch: UserPatch
    ): Mono<User?> = mono {
        val entity = userRepository.updateUser(id, patch) ?: return@mono null
        return@mono entity.toUser()
    }

    @Transactional // suspend should work in spring boot 2.4
    fun deleteUser(
        id: UUID
    ): Mono<Unit> = mono {
        userRepository.deleteUser(id)
    }

    private suspend fun UserEntity.toUser(): User {
        // TODO this could be better and ask for all gardens at once
        val gardens = gardenIds.map { gardenRepository.getGarden(it)?.toGarden() ?: error("unknown garden id=$it") }
        return toUser(gardens)
    }
}
