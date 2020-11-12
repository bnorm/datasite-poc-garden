package com.datasite.poc.garden

import com.datasite.poc.garden.dto.User
import com.datasite.poc.garden.dto.UserPatch
import com.datasite.poc.garden.dto.UserPrototype
import com.datasite.poc.garden.entity.UserEntity
import com.datasite.poc.garden.entity.toGarden
import com.datasite.poc.garden.entity.toUser
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
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

    @Transactional
    suspend fun createUser(
        prototype: UserPrototype
    ): User {
        val entity = userRepository.createUser(prototype)
        return entity.toUser()
    }

    @Transactional
    suspend fun updateUser(
        id: UUID,
        patch: UserPatch
    ): User? {
        val entity = userRepository.updateUser(id, patch) ?: return null
        return entity.toUser()
    }

    @Transactional
    suspend fun deleteUser(
        id: UUID
    ) {
        userRepository.deleteUser(id)
    }

    private suspend fun UserEntity.toUser(): User {
        // TODO this could be better and ask for all gardens at once
        val gardens = gardenIds.map { gardenRepository.getGarden(it)?.toGarden() ?: error("unknown garden id=$it") }
        return toUser(gardens)
    }
}
