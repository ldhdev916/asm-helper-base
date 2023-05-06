import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.10"
    id("com.github.gmazzo.buildconfig") version "3.1.0" apply false
    `maven-publish`
    signing
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

artifacts {
    archives(tasks.javadoc)
    archives(tasks.kotlinSourcesJar)
}

signing {
    sign(configurations.archives.get())
}


publishing {

    repositories {
        maven {
            url = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")

            credentials {
                username = findProperty("ossrhUsername") as String
                password = findProperty("ossrhPassword") as String
            }
        }
    }

    publications {
        create<MavenPublication>("maven") {
            from(components["java"])

            pom {
                name.set("asm-helper")
                packaging = "jar"

                description.set("Made to help doing asm")
                url.set("https://github.com/ldhdev916/asm-helper-base")

                scm {
                    connection.set("scm:svn:https://github.com/ldhdev916/asm-helper-base.git")
                    developerConnection.set("scm:svn:https://github.com/ldhdev916/asm-helper-base.git")
                    url.set("https://github.com/ldhdev916/asm-helper-base.git")
                }

                licenses {
                    license {
                        name.set("MIT license")
                        url.set("https://opensource.org/licenses/MIT")
                    }
                }

                developers {
                    developer {
                        id.set("ldhdev")
                        name.set("ldhdev")
                        email.set("dongheon0916@gmail.com")
                    }
                }
            }
        }
    }
}