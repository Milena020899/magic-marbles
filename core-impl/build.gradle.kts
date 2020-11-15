plugins {
    kotlin("jvm") version "1.4.10"
}

group = "magic-marbles"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(project("core"))
}
