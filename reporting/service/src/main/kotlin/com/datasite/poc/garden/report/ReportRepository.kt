package com.datasite.poc.garden.report

import com.datasite.poc.garden.report.entity.GARDEN_TABLE
import com.datasite.poc.garden.report.entity.GardenEntity
import com.datasite.poc.garden.report.entity.GardenViewCountEntity
import com.datasite.poc.garden.report.entity.USER_GARDEN_VIEW_TABLE
import com.datasite.poc.garden.report.entity.UserGardenViewCountEntity
import com.datasite.poc.garden.report.entity.UserGardenViewEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.runBlocking
import org.springframework.data.r2dbc.core.DatabaseClient
import org.springframework.data.r2dbc.core.asType
import org.springframework.data.r2dbc.core.await
import org.springframework.data.r2dbc.core.awaitOne
import org.springframework.data.r2dbc.core.flow
import org.springframework.stereotype.Repository
import javax.annotation.PostConstruct

@Repository
class ReportRepository(
    private val client: DatabaseClient,
) {
    @PostConstruct
    fun init() = runBlocking {
        client.execute(
            """--
CREATE TABLE IF NOT EXISTS $GARDEN_TABLE
(
    id   TEXT PRIMARY KEY,
    name TEXT
)
"""
        ).await()

        client.execute(
            """--
CREATE TABLE IF NOT EXISTS $USER_GARDEN_VIEW_TABLE
(
    user_id    TEXT,
    garden_id  TEXT,
    view_count BIGINT,

    primary key (user_id, garden_id)
)
"""
        ).await()
    }

    suspend fun upsertGarden(entity: GardenEntity): GardenEntity {
        client.execute(
            """--
INSERT INTO $GARDEN_TABLE (id, name)
VALUES ($1, $2)
ON CONFLICT (id) DO UPDATE SET name = $2
"""
        ).bind(0, entity.id).bind(1, entity.name).await()
        return entity
    }

    suspend fun deleteGarden(gardenId: String) {
        client.execute("""DELETE FROM $GARDEN_TABLE WHERE id = $1""")
            .bind(0, gardenId).await()
    }

    suspend fun incrementUserGardenViewCount(userId: String, gardenId: String): UserGardenViewEntity {
        return client.execute(
            """--
INSERT INTO $USER_GARDEN_VIEW_TABLE (user_id, garden_id, view_count)
VALUES ($1, $2, 1)
ON CONFLICT (user_id, garden_id) DO UPDATE SET view_count = $USER_GARDEN_VIEW_TABLE.view_count + 1
RETURNING *
"""
        ).bind(0, userId).bind(1, gardenId)
            .asType<UserGardenViewEntity>().fetch().awaitOne()
    }

    fun getGardenViewCounts(limit: Int = 5): Flow<GardenViewCountEntity> {
        return client.execute(
            """--
SELECT garden.id             AS id,
       garden.name           AS name,
       sum(views.view_count) AS view_count
FROM $USER_GARDEN_VIEW_TABLE views
         INNER JOIN $GARDEN_TABLE garden ON views.garden_id = garden.id
GROUP BY garden.id, garden.name
ORDER BY 2 DESC
LIMIT $1
"""
        ).bind(0, limit).asType<GardenViewCountEntity>().fetch().flow()
    }

    fun getUserTopGarden(): Flow<UserGardenViewCountEntity> {
        return client.execute(
            """--
SELECT ranked.user_id    AS user_id,
       g.id              AS garden_id,
       g.name            AS garden_name,
       ranked.view_count AS view_count
FROM (
         SELECT *, rank() OVER (PARTITION BY user_id ORDER BY view_count DESC) AS rank
         FROM $USER_GARDEN_VIEW_TABLE
     ) AS ranked
         INNER JOIN $GARDEN_TABLE g ON ranked.garden_id = g.id
WHERE ranked.rank = 1
ORDER BY ranked.view_count DESC
"""
        ).asType<UserGardenViewCountEntity>().fetch().flow()
    }
}
