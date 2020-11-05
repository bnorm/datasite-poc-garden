rootProject.name = "datasite-poc-garden"

import(":auditing:common")
import(":iot:common")
import(":iot:gateway")
import(":iot:simulator")
import(":processor")
import(":reporting:common")
import(":reporting:service")
import(":reporting:ui")
import(":service")

fun import(name: String) {
    val projectName = name.replace(":", "-").removePrefix("-")
    include(name)
    project(name).name = projectName
}
