pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        mavenLocal()
    }
}

rootProject.name = "asm-helper-base"
include("asm-compiler-plugin")
include("asm-gradle-plugin")
