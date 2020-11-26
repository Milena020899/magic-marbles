plugins {
    kotlin("jvm")
}

group = "magic-marbles"
version = "1.0-SNAPSHOT"

tasks {
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "1.8"
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(project(":core"))
    implementation("com.michael-bull.kotlin-result:kotlin-result:1.1.9")
}
