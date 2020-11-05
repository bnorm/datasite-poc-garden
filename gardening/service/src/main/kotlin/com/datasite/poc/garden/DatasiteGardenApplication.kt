package com.datasite.poc.garden

import kotlinx.serialization.json.Json
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean

@SpringBootApplication
class DatasiteGardenApplication {
	@get:Bean
	val json: Json = Json.Default
}

fun main(args: Array<String>) {
	runApplication<DatasiteGardenApplication>(*args)
}
