package com.datasite.poc.garden.report

import io.r2dbc.spi.ConnectionFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.r2dbc.connectionfactory.R2dbcTransactionManager

@Configuration
class PostgresConfig {
    @Bean
    fun transactionManager(
        connectionFactory: ConnectionFactory,
    ): R2dbcTransactionManager {
        return R2dbcTransactionManager(connectionFactory)
    }
}
