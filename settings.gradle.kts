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
import(":reporting:ui")

fun import(name: String) {
    val projectName = name.replace(":", "-").removePrefix("-")
    include(name)
    project(name).name = projectName
}
