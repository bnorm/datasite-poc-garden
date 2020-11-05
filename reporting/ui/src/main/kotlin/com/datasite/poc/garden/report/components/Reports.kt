package com.datasite.poc.garden.report.components

import com.bnorm.react.RFunction
import react.RBuilder
import react.dom.div
import react.dom.h1
import react.dom.li
import react.dom.ul
import react.router.dom.routeLink


@Suppress("FunctionName")
@RFunction
fun RBuilder.Reports() {

    div {
        h1 {
            +"Pick your report!"
        }
        ul {
            li {
                routeLink(to = "/reports/favorite") {
                    +"favorite"
                }
            }
            li {
                routeLink(to = "/reports/popular") {
                    +"popular"
                }
            }
        }

    }
}