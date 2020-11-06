package com.datasite.poc.garden.report

import kotlinx.serialization.json.Json
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean

@SpringBootApplication
class ReportApplication {
    @get:Bean
    val json: Json = Json.Default
}

fun main(args: Array<String>) {
    runApplication<ReportApplication>(*args)
}
