package com.datasite.poc.garden

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.ReactiveMongoDatabaseFactory
import org.springframework.data.mongodb.ReactiveMongoTransactionManager

@Configuration
class MongoConfig {
    @Bean
    fun reactiveMongoTransactionManager(
        databaseFactory: ReactiveMongoDatabaseFactory,
    ): ReactiveMongoTransactionManager {
        return ReactiveMongoTransactionManager(databaseFactory)
    }
}
