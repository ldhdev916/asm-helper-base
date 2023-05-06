package com.ldhdev.plugin.transformations

import com.ldhdev.plugin.getType
import org.jetbrains.kotlin.backend.common.IrElementTransformerVoidWithContext
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.builders.irString
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.types.getClass
import org.jetbrains.kotlin.ir.util.companionObject
import org.jetbrains.kotlin.ir.util.isObject
import org.jetbrains.kotlin.ir.util.kotlinFqName
import org.jetbrains.kotlin.name.FqName

class KotlinElementTransformer(private val pluginContext: IrPluginContext, private val collector: MessageCollector) :
    IrElementTransformerVoidWithContext() {

    private fun getFunction(name: String) =
        pluginContext.referenceFunctions(FqName("com.ldhdev.asmhelper.$name")).single()

    private val getKObjectFunction by lazy { getFunction("getKObject") }
    private val getKCompanionFunction by lazy { getFunction("getKCompanion") }

    override fun visitCall(expression: IrCall): IrExpression {

        val (functionSymbol, check) = when (expression.symbol.owner.kotlinFqName) {
            getKObjectFqName -> getKObjectFunction to { it: IrClass ->
                if (!it.isObject) collector.report(
                    CompilerMessageSeverity.ERROR,
                    "Class ${it.kotlinFqName} is not Object"
                )
            }

            getKCompanionFqName -> getKCompanionFunction to { it: IrClass ->
                if (it.companionObject() == null) collector.report(
                    CompilerMessageSeverity.ERROR,
                    "Class ${it.kotlinFqName} does not have Companion Object"
                )
            }

            else -> null to null
        }

        if (functionSymbol != null && check != null) {

            val type = expression.getTypeArgument(0)!!
            check(type.getClass()!!)
            val owner = type.getType()

            return with(DeclarationIrBuilder(pluginContext, expression.symbol)) {
                irCall(functionSymbol).apply {
                    extensionReceiver = expression.extensionReceiver

                    putValueArgument(0, irString(owner))
                }
            }
        }

        return super.visitCall(expression)
    }

    companion object {
        private val getKObjectFqName = FqName("com.ldhdev.asmhelper.compiler.getKObject")
        private val getKCompanionFqName = FqName("com.ldhdev.asmhelper.compiler.getKCompanion")
    }
}