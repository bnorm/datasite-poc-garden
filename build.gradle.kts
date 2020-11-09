plugins {
    kotlin("jvm") version "1.4.10" apply false
    kotlin("js") version "1.4.10" apply false
    kotlin("multiplatform") version "1.4.10" apply false
    kotlin("plugin.spring") version "1.4.10" apply false
    kotlin("plugin.serialization") version "1.4.10" apply false
    id("org.jetbrains.compose") version "0.1.0-m1-build62" apply false
    id("org.springframework.boot") version "2.3.4.RELEASE" apply false
    id("io.spring.dependency-management") version "1.0.10.RELEASE" apply false
    id("com.bnorm.react.kotlin-react-function") version "0.2.1" apply false
}

allprojects {
    group = "com.datasite.poc.garden"
    version = "0.0.1-SNAPSHOT"

    repositories {
        mavenCentral()
        jcenter()
        maven { setUrl("https://kotlin.bintray.com/kotlinx") }
        maven { setUrl("https://dl.bintray.com/kotlin/kotlin-js-wrappers") }
    }
}
