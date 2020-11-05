package com.datasite.poc.garden.event

import com.datasite.poc.garden.dto.Garden
import com.datasite.poc.garden.dto.toUuid
import java.util.*

fun MongoGarden.toGarden(): Garden {
    return Garden(id.toUuid(), name)
}

fun MongoId.toUuid() =
    Base64.getDecoder().decode(binary).toString().toUuid()
