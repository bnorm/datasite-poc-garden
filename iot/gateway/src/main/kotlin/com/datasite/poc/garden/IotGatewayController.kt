package com.datasite.poc.garden

import com.datasite.poc.garden.dto.SensorReading
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/iot")
class IotGatewayController(
        private val service: IotGatewayService,
) {
    @PostMapping
    suspend fun submitReading(@RequestBody sensorReading: SensorReading) = service.process(sensorReading)
}
