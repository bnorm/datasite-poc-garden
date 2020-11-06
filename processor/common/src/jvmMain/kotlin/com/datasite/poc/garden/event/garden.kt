package com.datasite.poc.garden.event

import com.datasite.poc.garden.dto.Uuid
import com.datasite.poc.garden.report.dto.GardenEntity
import com.datasite.poc.garden.report.dto.GardenSensorEntity
import com.datasite.poc.garden.report.dto.UserEntity
import org.bson.BsonBinarySubType
import org.bson.UuidRepresentation
import org.bson.internal.UuidHelper
import java.util.*

fun MongoUser.toUserEntity(): UserEntity {
    return UserEntity(id.toUuid(), name, gardenIds.map { it.toUuid() })
}

fun MongoGarden.toGardenEntity(): GardenEntity {
    return GardenEntity(id.toUuid(), name)
}

fun MongoGardenSensor.toGardenSensorEntity(): GardenSensorEntity {
    return GardenSensorEntity(id.toUuid(), name, gardenId.toUuid())
}

fun MongoId.toUuid(): Uuid {
    val bytes = Base64.getDecoder().decode(binary)
    // TODO is there a better way to decode a BSON UUID represented as binary?
    return UuidHelper.decodeBinaryToUuid(bytes, BsonBinarySubType.UUID_LEGACY.value, UuidRepresentation.JAVA_LEGACY)
}
