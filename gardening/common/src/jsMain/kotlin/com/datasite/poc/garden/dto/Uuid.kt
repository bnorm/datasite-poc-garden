package com.datasite.poc.garden.dto

actual typealias Uuid = String
actual fun String.toUuid(): Uuid = this
