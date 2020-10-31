package com.datasite.poc.garden.user

import org.springframework.http.HttpStatus
import org.springframework.web.server.WebFilter
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilterChain
import java.lang.Void
import org.springframework.stereotype.Component
import org.springframework.web.server.ResponseStatusException
import reactor.core.publisher.Mono

@Component
class UserHeaderFilter : WebFilter {
    companion object {
        private const val USER_HEADER = "X-User"
    }

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val userId = exchange.request.headers[USER_HEADER]?.singleOrNull()
            ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "no user id specified")

        return chain.filter(exchange)
            .subscriberContext { ctx -> ctx.put(User::class, User(userId)) }
    }
}
