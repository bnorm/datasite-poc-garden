package com.datasite.poc.garden.dto

actual typealias Uuid = java.util.UUID
actual fun String.toUuid(): Uuid = Uuid.fromString(this)
