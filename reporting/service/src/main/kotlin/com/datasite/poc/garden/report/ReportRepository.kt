package com.datasite.poc.garden.report

import com.datasite.poc.garden.report.dto.ReportEvent
import com.datasite.poc.garden.report.entity.DUMMY_TABLE
import com.datasite.poc.garden.report.entity.DummyEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.runBlocking
import org.springframework.data.r2dbc.core.DatabaseClient
import org.springframework.data.r2dbc.core.await
import org.springframework.data.r2dbc.core.awaitOneOrNull
import org.springframework.data.r2dbc.core.flow
import org.springframework.data.r2dbc.core.from
import org.springframework.data.r2dbc.core.into
import org.springframework.data.r2dbc.core.table
import org.springframework.data.relational.core.query.Criteria
import org.springframework.stereotype.Repository
import javax.annotation.PostConstruct

@Repository
class ReportRepository(
    private val client: DatabaseClient,
) {
    @PostConstruct
    fun init() = runBlocking {
        client.execute("""CREATE TABLE IF NOT EXISTS $DUMMY_TABLE
(
    character CHARACTER PRIMARY KEY,
    count     BIGINT
)
""").await()
    }

    fun getDummyEvents(): Flow<DummyEntity> {
        return client.select()
            .from<DummyEntity>()
            .`as`(DummyEntity::class.java)
            .flow()
    }

    suspend fun insertDummyEvent(event: ReportEvent.DummyEvent): DummyEntity {
        val entity = DummyEntity(event.character, event.count)

        val existing = client.select()
            .from<DummyEntity>()
            .matching(Criteria.where("character").`is`(event.character))
            .`as`(DummyEntity::class.java)
            .awaitOneOrNull()

        if (existing == null) {
            client.insert()
                .into<DummyEntity>()
                .using(entity)
                .await()
        } else {
            client.update()
                .table<DummyEntity>()
                .using(entity)
                .matching(Criteria.where("character").`is`(event.character))
                .then().awaitFirstOrNull()
        }

        return entity
    }
}
