import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
    kotlin("js")
    id("com.bnorm.react.kotlin-react-function")
}

repositories {
    maven { setUrl("https://dl.bintray.com/subroh0508/maven") }
}

kotlin {
    js(IR) {
        browser {
            useCommonJs()
            runTask {
                // TODO: use dsl after KT-32016 will be fixed
                devServer = KotlinWebpackConfig.DevServer(
                        port = 9004,
                        proxy = mapOf(
                                "/api/v1/**" to mapOf("target" to "ws://localhost:9005", "ws" to true)
                        ),
                        contentBase = listOf("$projectDir/src/main/resources")
                )
                outputFileName = "web.js"
            }
        }
        binaries.executable()
    }
}

dependencies {
    implementation(project(":reporting:reporting-common"))

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-js:1.4.0")

    implementation("io.ktor:ktor-client-json-js:1.4.1")
    implementation("io.ktor:ktor-client-serialization-js:1.4.1")

    implementation("com.bnorm.react:kotlin-react-function:0.2.1")
    implementation("org.jetbrains:kotlin-extensions:1.0.1-pre.129-kotlin-1.4.10")
    implementation("org.jetbrains:kotlin-react:17.0.0-pre.129-kotlin-1.4.10")
    implementation("org.jetbrains:kotlin-react-dom:17.0.0-pre.129-kotlin-1.4.10")
    implementation("org.jetbrains:kotlin-react-router-dom:5.2.0-pre.129-kotlin-1.4.10")

    implementation("net.subroh0508.kotlinmaterialui:core:0.5.3")
}

tasks.register<Sync>("jsBundle") {
    dependsOn(tasks.named("browserDevelopmentWebpack"))
    from(tasks.named("browserDistributeResources"))
    into("$buildDir/bundle")
}
