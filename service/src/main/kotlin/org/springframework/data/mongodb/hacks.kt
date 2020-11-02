package org.springframework.data.mongodb

import com.mongodb.reactivestreams.client.ClientSession
import kotlinx.coroutines.reactive.awaitSingleOrNull
import org.bson.BsonBinary
import org.springframework.transaction.reactive.TransactionContextManager

suspend fun currentClientSession(): ClientSession? {
    // key = SimpleReactiveMongoDatabaseFactory instance
    val context = TransactionContextManager.currentContext().awaitSingleOrNull() ?: return null
    return context.resources.values
        .filterIsInstance<ReactiveMongoResourceHolder>()
        .firstOrNull()
        ?.session
}

val ClientSession.transactionId: String?
    get() {
        val sessionUuid = (serverSession.identifier["id"] as? BsonBinary)?.asUuid() ?: return null
        val transactionNumber = serverSession.transactionNumber
        return "$sessionUuid:$transactionNumber"
    }
