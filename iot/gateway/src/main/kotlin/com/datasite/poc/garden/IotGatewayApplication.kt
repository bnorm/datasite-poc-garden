package com.datasite.poc.garden

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class IotGatewayApplication

fun main(args: Array<String>) {
    runApplication<IotGatewayApplication>(*args)
}
