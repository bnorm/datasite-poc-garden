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
                api("org.jetbrains.kotlinx:kotlinx-datetime:0.1.0")
            }
        }
        named("jvmMain") {
            dependencies {
                api("org.springframework.data:spring-data-mongodb:3.0.4.RELEASE")
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
