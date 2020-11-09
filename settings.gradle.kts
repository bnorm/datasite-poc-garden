pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

rootProject.name = "datasite-poc-garden"

import(":gardening:common")
import(":gardening:service")
import(":iot:common")
import(":iot:gateway")
import(":iot:simulator")
import(":processor:common")
import(":processor:service")
import(":reporting:common")
import(":reporting:service")
include(":reporting:ui:web")
include(":reporting:ui:desktop")

fun import(name: String) {
    val projectName = name.replace(":", "-").removePrefix("-")
    include(name)
    project(name).name = projectName
}
