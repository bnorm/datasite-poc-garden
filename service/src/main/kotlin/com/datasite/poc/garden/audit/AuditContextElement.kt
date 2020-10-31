package com.datasite.poc.garden.audit

import kotlin.coroutines.CoroutineContext

class AuditContextElement(
    val auditType: String
) : CoroutineContext.Element {
    override val key: CoroutineContext.Key<*> get() = AuditContextElement

    companion object : CoroutineContext.Key<AuditContextElement>
}
