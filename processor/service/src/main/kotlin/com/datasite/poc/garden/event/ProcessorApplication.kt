package com.datasite.poc.garden.event

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ProcessorApplication

fun main(args: Array<String>) {
    runApplication<ProcessorApplication>(*args)
}
