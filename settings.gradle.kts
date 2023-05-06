pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        mavenLocal()
    }
}

rootProject.name = "asm-helper-base"
include("compiler-plugin")
include("gradle-plugin")
