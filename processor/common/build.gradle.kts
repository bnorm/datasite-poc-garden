import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
}

kotlin {
    jvm()
    js(IR) {
        browser()
    }

    sourceSets {
        named("commonMain") {
            dependencies {
                api("org.jetbrains.kotlinx:kotlinx-serialization-json:1.0.1")
            }
        }
        named("jvmMain") {
            dependencies {
                api("org.apache.kafka:kafka-streams:2.6.0")
            }
        }
    }
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = "11"
        freeCompilerArgs = listOf("-Xopt-in=kotlin.RequiresOptIn", "-XXLanguage:+InlineClasses")
//    useIR = true
    }
}
