package com.datasite.poc.garden.report.components

import com.bnorm.react.RFunction
import com.datasite.poc.garden.report.components.reports.MostPopularGardensReport
import com.datasite.poc.garden.report.components.reports.UsersFavoriteGardenReport
import react.RBuilder
import react.dom.div
import react.router.dom.browserRouter
import react.router.dom.redirect
import react.router.dom.route
import react.router.dom.switch

@RFunction
fun RBuilder.Routes() {
    browserRouter {
        switch {
            route(path = "/reports", exact = true) {
                div {
                    Reports()
                }
            }
            route(path = "/reports/favorite", exact = true) {
                div {
                    UsersFavoriteGardenReport()
                }
            }
            route(path = "/reports/popular", exact = true) {
                div {
                    MostPopularGardensReport()
                }
            }
        }
        redirect(from = "/", to = "/reports", exact = true)
    }
}
