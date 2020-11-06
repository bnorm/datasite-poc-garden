package com.datasite.poc.garden.report.dto

import kotlinx.serialization.Serializable

@Serializable
sealed class Report {
    abstract val name: String
}

@Serializable
data class MostPopularGardensReport(
    val metrics: List<Metric>,
) : Report() {
    override val name: String = "MostPopularGardens"

    @Serializable
    data class Metric(
        val garden: GardenEntity,
        val viewCount: Long,
    )
}

@Serializable
data class UsersFavoriteGardenReport(
    val metrics: List<Metric>
) : Report() {
    override val name: String = "UsersFavoriteGarden"

    @Serializable
    data class Metric(
        val user: UserEntity,
        val garden: GardenEntity,
        val viewCount: Long
    )
}

@Serializable
data class GardenSensorReport(
    val metrics: List<Metric>
) : Report() {
    override val name: String = "GardenSensors"

    @Serializable
    data class Metric(
        val garden: GardenEntity,
        val readingSum: Long,
        val readingCount: Long,
    )
}
