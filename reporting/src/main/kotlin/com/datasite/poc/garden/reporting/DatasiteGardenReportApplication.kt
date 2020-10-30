package com.datasite.poc.garden.reporting

import kotlinx.serialization.json.Json
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean

@SpringBootApplication
class DatasiteGardenReportApplication {
    @get:Bean
    val json: Json = Json.Default
}

fun main(args: Array<String>) {
    runApplication<DatasiteGardenReportApplication>(*args)
}
