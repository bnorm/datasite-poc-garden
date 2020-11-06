package com.datasite.poc.garden.report.components

import com.bnorm.react.RFunction
import kotlinx.css.Display
import kotlinx.css.display
import kotlinx.css.flexGrow
import kotlinx.css.padding
import kotlinx.css.zIndex
import materialui.components.appbar.appBar
import materialui.components.appbar.enums.AppBarPosition
import materialui.components.toolbar.toolbar
import materialui.components.typography.enums.TypographyVariant
import materialui.components.typography.typography
import materialui.styles.makeStyles
import react.RBuilder


private val useStyles = makeStyles<dynamic> {
    "root" {
        display = Display.flex
    }
    "appBar" {
        zIndex = theme.zIndex.drawer.toInt() + 1
    }
    "content" {
        flexGrow = 1.0
        padding = theme.spacing(3).toString()
    }
}


@Suppress("FunctionName")
@RFunction
fun RBuilder.Header() {

    val classes = useStyles()

    appBar {
        attrs {
            className = classes.appBar
            position = AppBarPosition.fixed
        }
        toolbar {
            typography {
                attrs {
                    TypographyVariant.h6
                }
                +"AutoAudit™"
            }
        }
    }
}