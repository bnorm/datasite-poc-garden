package com.datasite.poc.garden.user

import com.datasite.poc.garden.UserRepository
import com.datasite.poc.garden.dto.User
import com.datasite.poc.garden.entity.toUser
import kotlinx.coroutines.reactive.awaitSingleOrNull
import kotlinx.coroutines.reactor.ReactorContext
import kotlinx.coroutines.reactor.mono
import org.springframework.http.HttpStatus
import org.springframework.web.server.WebFilter
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilterChain
import java.lang.Void
import org.springframework.stereotype.Component
import org.springframework.web.server.ResponseStatusException
import reactor.core.publisher.Mono
import java.util.*
import kotlin.coroutines.coroutineContext

@Component
class UserHeaderFilter(
    private val userRepository: UserRepository
) : WebFilter {
    companion object {
        private const val USER_HEADER = "X-User"
    }

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val userId = UUID.fromString(
            exchange.request.headers[USER_HEADER]?.singleOrNull()
                ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "no user id specified")
        )

        return mono {
            val user = userRepository.getUser(userId)
                ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "unknown user")

            chain.filter(exchange)
                .subscriberContext { ctx -> ctx.put(User::class, user.toUser(emptyList())) }
                .awaitSingleOrNull()
        }
    }
}

suspend fun currentUser(): User {
    val reactorContext = coroutineContext[ReactorContext]?.context ?: error("no reactor context")
    return reactorContext.getOrDefault<User>(User::class, null) ?: error("no user")
}
