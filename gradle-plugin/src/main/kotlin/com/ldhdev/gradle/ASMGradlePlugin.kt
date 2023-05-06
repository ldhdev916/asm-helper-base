package com.ldhdev.gradle

import com.ldhdev.BuildConfig
import org.gradle.api.provider.Provider
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilerPluginSupportPlugin
import org.jetbrains.kotlin.gradle.plugin.SubpluginArtifact
import org.jetbrains.kotlin.gradle.plugin.SubpluginOption

class ASMGradlePlugin : KotlinCompilerPluginSupportPlugin {

    override fun applyToCompilation(kotlinCompilation: KotlinCompilation<*>): Provider<List<SubpluginOption>> {
        kotlinCompilation.dependencies {
            implementation("${BuildConfig.HELPER_LIBRARY_GROUP}:${BuildConfig.HELPER_LIBRARY_NAME}:${BuildConfig.HELPER_LIBRARY_VERSION}")
        }
        val project = kotlinCompilation.target.project
        project.repositories.mavenLocal()

        return project.provider {
            emptyList()
        }
    }

    override fun getCompilerPluginId() = BuildConfig.KOTLIN_PLUGIN_ID

    override fun getPluginArtifact() = SubpluginArtifact(
        BuildConfig.KOTLIN_PLUGIN_GROUP,
        BuildConfig.KOTLIN_PLUGIN_NAME,
        BuildConfig.KOTLIN_PLUGIN_VERSION
    )

    override fun isApplicable(kotlinCompilation: KotlinCompilation<*>) = true
}