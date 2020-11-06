package com.datasite.poc.garden.report

import com.bnorm.react.RFunction
import com.datasite.poc.garden.report.components.Header
import com.datasite.poc.garden.report.components.Reports
import kotlinx.css.flexGrow
import kotlinx.css.paddingTop
import kotlinx.css.px
import kotlinx.css.zIndex
import materialui.styles.makeStyles
import react.RBuilder
import react.dom.div


private val useStyles = makeStyles<dynamic> {
    "root" {
    }
    "header" {
        zIndex = theme.zIndex.drawer.toInt() + 1
    }
    "content" {
        flexGrow = 1.0
        paddingTop = 100.px
    }
}

@Suppress("FunctionName")
@RFunction
fun RBuilder.App() {
    val classes = useStyles()
    div(classes = classes.root) {
        div(classes = classes.header) {
            Header()
        }
        div(classes = classes.content) {
            Reports()
        }
    }
}
