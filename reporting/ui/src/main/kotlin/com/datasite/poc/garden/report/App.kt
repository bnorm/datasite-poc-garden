package com.datasite.poc.garden.report

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
