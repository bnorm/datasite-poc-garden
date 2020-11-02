package com.datasite.poc.garden.user

import kotlinx.coroutines.reactor.ReactorContext
import kotlin.coroutines.coroutineContext

data class User(
    val id: String,
    val name: String = id
)

suspend fun currentUser(): User {
    val reactorContext = coroutineContext[ReactorContext]?.context ?: error("no reactor context")
    return reactorContext.getOrDefault<User>(User::class, null) ?: error("no user")
}
