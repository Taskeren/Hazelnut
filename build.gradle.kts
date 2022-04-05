import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.10"
    application
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "cn.taskeren.code"
version = "1.0.0"

repositories {
    mavenCentral()
    maven("https://jitpack.io")
    maven("https://libraries.minecraft.net")
}

dependencies {

    // Github: KaiKt/KaiKt
    implementation("com.github.KaiKt:KaiKt:70eac573a8")

    // Github: Taskeren/DestinyDatabase
    implementation("com.github.Taskeren:DestinyDatabase:16d6d887d2")

    // Mojang: Brigadier
    implementation("com.mojang:brigadier:1.0.18")

    // Github: Taskeren/BrigadierX
    implementation("com.github.Taskeren:brigadierX:1.2")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "16"

    kotlinOptions.freeCompilerArgs += "-Xopt-in=kotlin.RequiresOptIn"
}

application {
    mainClass.set("cn.taskeren.hazelnut.HazelnutKt")
    applicationName = "Huzelnut"
}

tasks.shadowJar {
    manifest {
        attributes["Main-Class"] = "cn.taskeren.hazelnut.HazelnutKt"
        attributes["Multi-Release"] = "true"
    }
}