package com.datasite.poc.garden.ui

import com.bnorm.react.RFunction
import react.RBuilder
import react.dom.div

@Suppress("FunctionName")
@RFunction
fun RBuilder.App() {
    +"Hello, World!"

    div {
        DummyReportTable()
    }
}
