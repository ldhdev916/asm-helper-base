plugins {
    kotlin("jvm")
    kotlin("kapt")
    id("com.github.gmazzo.buildconfig")
    `maven-publish`
}

dependencies {
    kapt("com.google.auto.service:auto-service:1.0.1")
    implementation("com.google.auto.service:auto-service:1.0.1")

    compileOnly("org.jetbrains.kotlin:kotlin-compiler-embeddable")

    testImplementation(kotlin("test"))
    testImplementation("org.jetbrains.kotlin:kotlin-compiler-embeddable")
    testImplementation("com.github.tschuchortdev:kotlin-compile-testing:1.4.9")
    testImplementation(rootProject)
}

tasks {
    test {
        useJUnitPlatform()
    }
}

buildConfig {
    packageName(group.toString())
    buildConfigField("String", "KOTLIN_PLUGIN_ID", "\"${rootProject.property("kotlin.plugin.id")}\"")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
            artifact(tasks.kotlinSourcesJar)
        }
    }
}