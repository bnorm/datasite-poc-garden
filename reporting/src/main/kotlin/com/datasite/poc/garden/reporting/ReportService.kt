package com.datasite.poc.garden.reporting

import com.datasite.poc.garden.reporting.dto.Dummy
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct

@Service
class ReportService(
    private val repository: ReportRepository,
    private val events: SharedFlow<ReportEvent>,
) {
    private val scope = CoroutineScope(Dispatchers.Default + CoroutineName("ReportService"))

    @PostConstruct
    fun init() {
        scope.launch {
            events.collect { event ->
                when (event) {
                    is ReportEvent.DummyEvent -> repository.insertDummyEvent(event)
                }
            }
        }
    }

    fun getDummyEvents(): Flow<Dummy> =
        repository.getDummyEvents().map { Dummy.from(it) }
}
