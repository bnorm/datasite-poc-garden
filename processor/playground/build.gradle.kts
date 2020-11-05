import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	kotlin("jvm")
	kotlin("plugin.serialization")
}

java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
	mavenCentral()
}

dependencies {
	implementation(project(":processor:processor-common"))
	implementation(project(":auditing:auditing-common"))

	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:1.4.0")

	implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.0.1")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.11.3")

	implementation("org.apache.kafka:kafka-streams:2.6.0")

	runtimeOnly("org.apache.logging.log4j:log4j-core:2.13.3")
	runtimeOnly("org.apache.logging.log4j:log4j-slf4j-impl:2.13.3")
}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "11"
	}
}
