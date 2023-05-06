plugins {
    kotlin("jvm")
    `java-gradle-plugin`
    id("com.github.gmazzo.buildconfig")
    `maven-publish`
    id("com.gradle.plugin-publish") version "1.1.0"
}

dependencies {
    implementation(kotlin("gradle-plugin-api"))
}

buildConfig {
    val project = project(":compiler-plugin")

    packageName(project.group.toString())
    buildConfigField("String", "KOTLIN_PLUGIN_ID", "\"${rootProject.property("kotlin.plugin.id")}\"")
    buildConfigField("String", "KOTLIN_PLUGIN_GROUP", "\"${project.group}\"")
    buildConfigField("String", "KOTLIN_PLUGIN_NAME", "\"${project.name}\"")
    buildConfigField("String", "KOTLIN_PLUGIN_VERSION", "\"${project.version}\"")

    buildConfigField("String", "HELPER_LIBRARY_GROUP", "\"${rootProject.group}\"")
    buildConfigField("String", "HELPER_LIBRARY_NAME", "\"${rootProject.name}\"")
    buildConfigField("String", "HELPER_LIBRARY_VERSION", "\"${rootProject.version}\"")
}

gradlePlugin {
    plugins {
        create("asmKotlinIrPlugin") {
            id = rootProject.property("kotlin.plugin.id") as String
            displayName = "ASM Kotlin Ir Plugin"
            description = "ASM Kotlin Ir Plugin"
            implementationClass = "com.ldhdev.gradle.ASMGradlePlugin"
        }
    }
}