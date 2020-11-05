rootProject.name = "datasite-poc-garden"

import(":auditing:common")
import(":processor")
import(":reporting:common")
import(":reporting:service")
import(":reporting:ui")
import(":service")
import(":iot:gateway")
import(":iot:simulator")
import(":iot:common")

fun import(name: String) {
    val projectName = name.replace(":", "-").removePrefix("-")
    include(name)
    project(name).name = projectName
}
