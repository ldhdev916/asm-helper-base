import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.10"
    id("com.github.gmazzo.buildconfig") version "3.1.0" apply false
    `maven-publish`
}

allprojects {
    group = "com.ldhdev"
    version = "1.0.0"

    repositories {
        mavenCentral()
    }
}

dependencies {
    api("org.ow2.asm:asm:9.3")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
            artifact(tasks.kotlinSourcesJar)
        }
    }
}