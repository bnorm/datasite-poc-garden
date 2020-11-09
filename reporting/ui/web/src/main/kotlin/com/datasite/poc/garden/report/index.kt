package com.datasite.poc.garden.report

import kotlinx.browser.document
import react.dom.render

fun main() {
    document.getElementById("root")?.let {
        render(it) { App() }
    }
}
