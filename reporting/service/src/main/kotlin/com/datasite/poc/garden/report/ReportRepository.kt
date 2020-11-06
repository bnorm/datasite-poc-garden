package com.datasite.poc.garden.report

import com.datasite.poc.garden.report.entity.GARDEN_SENSOR_ACCUMULATION
import com.datasite.poc.garden.report.entity.GARDEN_SENSOR_TABLE
import com.datasite.poc.garden.report.entity.GARDEN_TABLE
import com.datasite.poc.garden.report.entity.GardenPgEntity
import com.datasite.poc.garden.report.entity.GardenSensorAccumulationPgEntity
import com.datasite.poc.garden.report.entity.GardenSensorPgEntity
import com.datasite.poc.garden.report.entity.GardenSensorTotalSelectRow
import com.datasite.poc.garden.report.entity.GardenViewCountSelectRow
import com.datasite.poc.garden.report.entity.SENSOR_ACCUMULATION
import com.datasite.poc.garden.report.entity.SensorAccumulationPgEntity
import com.datasite.poc.garden.report.entity.SensorTotalSelectRow
import com.datasite.poc.garden.report.entity.USER_GARDEN_VIEW_TABLE
import com.datasite.poc.garden.report.entity.USER_TABLE
import com.datasite.poc.garden.report.entity.UserGardenViewCountSelectRow
import com.datasite.poc.garden.report.entity.UserGardenViewPgEntity
import com.datasite.poc.garden.report.entity.UserPgEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.runBlocking
import org.springframework.data.r2dbc.core.DatabaseClient
import org.springframework.data.r2dbc.core.asType
import org.springframework.data.r2dbc.core.await
import org.springframework.data.r2dbc.core.awaitOne
import org.springframework.data.r2dbc.core.flow
import org.springframework.stereotype.Repository
import java.util.*
import javax.annotation.PostConstruct

@Repository
class ReportRepository(
    private val client: DatabaseClient,
) {
    @PostConstruct
    fun init() = runBlocking {
        client.execute(
            """--
CREATE TABLE IF NOT EXISTS $USER_TABLE
(
    id   UUID PRIMARY KEY,
    name TEXT
)
"""
        ).await()

        client.execute(
            """--
CREATE TABLE IF NOT EXISTS $GARDEN_TABLE
(
    id   UUID PRIMARY KEY,
    name TEXT
)
"""
        ).await()

        client.execute(
            """--
CREATE TABLE IF NOT EXISTS $GARDEN_SENSOR_TABLE
(
    id        UUID PRIMARY KEY,
    name      TEXT,
    garden_id UUID -- foreign key?
)
"""
        ).await()

        client.execute(
            """--
CREATE TABLE IF NOT EXISTS $USER_GARDEN_VIEW_TABLE
(
    user_id    UUID, -- foreign key?
    garden_id  UUID, -- foreign key?
    view_count BIGINT,

    primary key (user_id, garden_id)
)
"""
        ).await()

        client.execute(
            """--
CREATE TABLE IF NOT EXISTS $GARDEN_SENSOR_ACCUMULATION
(
    garden_id     UUID PRIMARY KEY, -- foreign key?
    reading_sum   BIGINT,
    reading_count BIGINT
)
"""
        ).await()

        client.execute(
            """--
CREATE TABLE IF NOT EXISTS $SENSOR_ACCUMULATION
(
    sensor_id     UUID PRIMARY KEY, -- foreign key?
    reading_sum   BIGINT,
    reading_count BIGINT
)
"""
        ).await()
    }

    suspend fun upsertGarden(entity: GardenPgEntity): GardenPgEntity {
        client.execute(
            """--
INSERT INTO $GARDEN_TABLE (id, name)
VALUES ($1, $2)
ON CONFLICT (id) DO UPDATE SET name = $2
"""
        ).bind(0, entity.id).bind(1, entity.name).await()
        return entity
    }

    suspend fun deleteGarden(gardenId: UUID) {
        client.execute("""DELETE FROM $GARDEN_TABLE WHERE id = $1""")
            .bind(0, gardenId).await()
    }

    suspend fun upsertGardenSensor(entity: GardenSensorPgEntity): GardenSensorPgEntity {
        client.execute(
            """--
INSERT INTO $GARDEN_SENSOR_TABLE (id, name, garden_id)
VALUES ($1, $2, $3)
ON CONFLICT (id) DO UPDATE SET name = $2, garden_id = $3
"""
        ).bind(0, entity.id).bind(1, entity.name).bind(2, entity.gardenId).await()
        return entity
    }

    suspend fun deleteGardenSensor(sensorId: UUID) {
        client.execute("""DELETE FROM $GARDEN_SENSOR_TABLE WHERE id = $1""")
            .bind(0, sensorId).await()
    }

    suspend fun upsertUser(entity: UserPgEntity): UserPgEntity {
        client.execute(
            """--
INSERT INTO $USER_TABLE (id, name)
VALUES ($1, $2)
ON CONFLICT (id) DO UPDATE SET name = $2
"""
        ).bind(0, entity.id).bind(1, entity.name).await()
        return entity
    }

    suspend fun deleteUser(userId: UUID) {
        client.execute("""DELETE FROM $USER_TABLE WHERE id = $1""")
            .bind(0, userId).await()
    }

    suspend fun incrementUserGardenViewCount(userId: UUID, gardenId: UUID): UserGardenViewPgEntity {
        return client.execute(
            """--
INSERT INTO $USER_GARDEN_VIEW_TABLE (user_id, garden_id, view_count)
VALUES ($1, $2, 1)
ON CONFLICT (user_id, garden_id) DO UPDATE SET view_count = $USER_GARDEN_VIEW_TABLE.view_count + 1
RETURNING *
"""
        ).bind(0, userId).bind(1, gardenId)
            .asType<UserGardenViewPgEntity>().fetch().awaitOne()
    }

    suspend fun accumulateGardenSensorReading(gardenId: UUID, value: Long): GardenSensorAccumulationPgEntity {
        return client.execute(
            """--
INSERT INTO $GARDEN_SENSOR_ACCUMULATION (garden_id, reading_sum, reading_count)
VALUES ($1, $2, 1)
ON CONFLICT (garden_id) DO UPDATE SET reading_sum   = $GARDEN_SENSOR_ACCUMULATION.reading_sum + $2,
                                      reading_count = $GARDEN_SENSOR_ACCUMULATION.reading_count + 1
RETURNING *
"""
        ).bind(0, gardenId).bind(1, value)
            .asType<GardenSensorAccumulationPgEntity>().fetch().awaitOne()
    }

    suspend fun accumulateSensorReading(sensorId: UUID, value: Long): SensorAccumulationPgEntity {
        return client.execute(
            """--
INSERT INTO $SENSOR_ACCUMULATION (sensor_id, reading_sum, reading_count)
VALUES ($1, $2, 1)
ON CONFLICT (sensor_id) DO UPDATE SET reading_sum   = $SENSOR_ACCUMULATION.reading_sum + $2,
                                      reading_count = $SENSOR_ACCUMULATION.reading_count + 1
RETURNING *
"""
        ).bind(0, sensorId).bind(1, value)
            .asType<SensorAccumulationPgEntity>().fetch().awaitOne()
    }

    fun getGardenViewCounts(limit: Int = 5): Flow<GardenViewCountSelectRow> {
        return client.execute(
            """--
SELECT garden.id             AS id,
       garden.name           AS name,
       sum(views.view_count) AS view_count
FROM $USER_GARDEN_VIEW_TABLE views
         INNER JOIN $GARDEN_TABLE garden ON views.garden_id = garden.id
GROUP BY garden.id, garden.name
ORDER BY 3 DESC
LIMIT $1
"""
        ).bind(0, limit).asType<GardenViewCountSelectRow>().fetch().flow()
    }

    fun getUserTopGarden(): Flow<UserGardenViewCountSelectRow> {
        return client.execute(
            """--
SELECT u.id              AS user_id,
       u.name            AS user_name,
       g.id              AS garden_id,
       g.name            AS garden_name,
       ranked.view_count AS view_count
FROM (
         SELECT *, rank() OVER (PARTITION BY user_id ORDER BY view_count DESC) AS rank
         FROM $USER_GARDEN_VIEW_TABLE
     ) AS ranked
         INNER JOIN $GARDEN_TABLE AS g ON ranked.garden_id = g.id
         INNER JOIN $USER_TABLE AS u ON ranked.user_id = u.id
WHERE ranked.rank = 1
ORDER BY ranked.view_count DESC
"""
        ).asType<UserGardenViewCountSelectRow>().fetch().flow()
    }

    fun getGardenSensorTotal(): Flow<GardenSensorTotalSelectRow> {
        return client.execute(
            """--
SELECT g.id              AS garden_id,
       g.name            AS garden_name,
       acc.reading_sum   AS reading_sum,
       acc.reading_count AS reading_count
FROM $GARDEN_SENSOR_ACCUMULATION AS acc
         INNER JOIN $GARDEN_TABLE AS g ON acc.garden_id = g.id
ORDER BY acc.reading_sum DESC
"""
        ).asType<GardenSensorTotalSelectRow>().fetch().flow()
    }

    fun getSensorTotal(): Flow<SensorTotalSelectRow> {
        return client.execute(
            """--
SELECT s.id              AS sensor_id,
       s.name            AS sensor_name,
       s.garden_id       AS garden_id,
       acc.reading_sum   AS reading_sum,
       acc.reading_count AS reading_count
FROM $SENSOR_ACCUMULATION AS acc
         INNER JOIN $GARDEN_SENSOR_TABLE AS s ON acc.sensor_id = s.id
ORDER BY acc.reading_sum DESC
"""
        ).asType<SensorTotalSelectRow>().fetch().flow()
    }
}
