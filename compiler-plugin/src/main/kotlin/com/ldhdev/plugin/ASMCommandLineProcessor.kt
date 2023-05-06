package com.ldhdev.plugin

import com.google.auto.service.AutoService
import com.ldhdev.BuildConfig
import org.jetbrains.kotlin.compiler.plugin.AbstractCliOption
import org.jetbrains.kotlin.compiler.plugin.CommandLineProcessor

@AutoService(CommandLineProcessor::class)
class ASMCommandLineProcessor : CommandLineProcessor {
    override val pluginId = BuildConfig.KOTLIN_PLUGIN_ID
    override val pluginOptions: Collection<AbstractCliOption> = emptyList()
}