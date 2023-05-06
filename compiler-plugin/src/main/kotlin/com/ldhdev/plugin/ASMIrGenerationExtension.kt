package com.ldhdev.plugin

import com.ldhdev.plugin.transformations.*
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.visitors.transformChildrenVoid


class ASMIrGenerationExtension(private val collector: MessageCollector) : IrGenerationExtension {
    override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {

        for (file in moduleFragment.files) {
            file.transformChildrenVoid(FieldElementTransformer(pluginContext, collector))
            file.transformChildrenVoid(MethodElementTransformer(pluginContext, collector))
            file.transformChildrenVoid(UtilElementTransformer(pluginContext))
            file.transformChildrenVoid(ConstructorElementTransformer(pluginContext))
            file.transformChildrenVoid(StringBuilderElementTransformer(pluginContext))
            file.transformChildrenVoid(ArrayElementTransformer(pluginContext))
            file.transformChildrenVoid(PrintElementTransformer(pluginContext))
            file.transformChildrenVoid(KotlinElementTransformer(pluginContext, collector))
        }
    }
}