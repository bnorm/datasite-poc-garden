package com.datasite.poc.garden.report

import io.ktor.client.features.websocket.*
import io.ktor.client.request.*
import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import react.RDependenciesList
import react.useEffectWithCleanup

// React hook to run an async process during the component lifecycle
fun useAsync(dependencies: RDependenciesList? = null, block: suspend () -> Unit) {
    useEffectWithCleanup(dependencies) {
        val job = GlobalScope.launch {
            block()
        }
        return@useEffectWithCleanup { job.cancel() }
    }
}
