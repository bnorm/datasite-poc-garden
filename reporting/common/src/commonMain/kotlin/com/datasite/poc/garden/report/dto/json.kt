package com.datasite.poc.garden.report.dto

import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

val reportDtoSerializersModule = SerializersModule {
    contextual(UserEntity.serializer())
    contextual(GardenEntity.serializer())
    contextual(Report.serializer())
    polymorphic(Report::class) {
        subclass(MostPopularGardensReport::class)
        subclass(UsersFavoriteGardenReport::class)
    }
}
