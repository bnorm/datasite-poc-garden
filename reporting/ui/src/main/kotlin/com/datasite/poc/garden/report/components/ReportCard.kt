package com.datasite.poc.garden.report.components

import com.bnorm.react.RFunction
import kotlinx.css.Display
import kotlinx.css.FlexWrap
import kotlinx.css.color
import kotlinx.css.display
import kotlinx.css.flexWrap
import kotlinx.css.padding
import materialui.components.card.card
import materialui.components.cardcontent.cardContent
import materialui.components.typography.enums.TypographyVariant
import materialui.components.typography.typography
import materialui.styles.makeStyles
import materialui.styles.palette.secondary
import react.RBuilder

private val useStyles = makeStyles<dynamic> {
    "report" {
        display = Display.flex
        flexWrap = FlexWrap.wrap
        padding = theme.spacing(3).toString()
        color = theme.palette.text.secondary
    }
}

@Suppress("FunctionName")
@RFunction
fun RBuilder.ReportCard(name: String, report: RBuilder.() -> Unit = {}) {
    val styles = useStyles()
    card {
        attrs {
            classes("${styles.report}")
        }

        cardContent {
            typography {
                attrs {
                    TypographyVariant.h1
                }
                +name
            }
            report()
        }
    }
}