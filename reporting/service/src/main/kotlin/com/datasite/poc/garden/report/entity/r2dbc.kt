package com.datasite.poc.garden.report.entity

import io.r2dbc.spi.Row
import kotlin.reflect.KProperty

inline fun <reified T> Row.get(property: KProperty<T>): T =
    get(property.name.camelToSnakeCase(), T::class.java)!!

private val camelRegex = "(?<=[a-zA-Z])[A-Z]".toRegex()
fun String.camelToSnakeCase(): String {
    return camelRegex.replace(this) {
        "_${it.value}"
    }.toLowerCase()
}
