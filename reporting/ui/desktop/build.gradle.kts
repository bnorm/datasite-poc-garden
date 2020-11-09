import org.jetbrains.compose.compose

plugins {
    kotlin("jvm")
    id("org.jetbrains.compose")
}

repositories {
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

dependencies {
    implementation(compose.desktop.currentOs)

    implementation(project(":reporting:reporting-common"))

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.0")

    implementation("io.ktor:ktor-client-json:1.4.1")
    implementation("io.ktor:ktor-client-serialization:1.4.1")
    implementation("io.ktor:ktor-client-okhttp:1.4.1")
}

compose.desktop {
    application {
        mainClass = "com.datasite.poc.garden.report.MainKt"
    }
}
