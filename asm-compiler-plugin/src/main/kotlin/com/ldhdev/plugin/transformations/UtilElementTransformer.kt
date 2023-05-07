package com.ldhdev.plugin.transformations

import com.ldhdev.plugin.getDescriptor
import com.ldhdev.plugin.getType
import org.jetbrains.kotlin.backend.common.IrElementTransformerVoidWithContext
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.builders.irString
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.types.makeNullable
import org.jetbrains.kotlin.ir.util.fqNameWhenAvailable
import org.jetbrains.kotlin.name.FqName

class UtilElementTransformer(private val pluginContext: IrPluginContext) : IrElementTransformerVoidWithContext() {

    private val equalsFunction by lazy {
        pluginContext.referenceFunctions(FqName("com.ldhdev.asmhelper.callEquals")).single()
    }

    private val toStringFunction by lazy {
        pluginContext.referenceFunctions(FqName("com.ldhdev.asmhelper.callToString")).single()
    }

    private val classFunction by lazy {
        pluginContext.referenceFunctions(FqName("com.ldhdev.asmhelper.class")).single()
    }

    override fun visitCall(expression: IrCall): IrExpression {

        val function = expression.symbol.owner

        val (functionSymbol, type) = when (function.fqNameWhenAvailable) {
            equalsFqName -> equalsFunction to true
            toStringFqName -> toStringFunction to true
            classFqName -> classFunction to false
            else -> null to null
        }

        if (functionSymbol != null && type != null) {
            val typeArgument = expression.getTypeArgument(0)!!
            val arg = if (type) typeArgument.getType() else typeArgument.makeNullable().getDescriptor()

            return with(DeclarationIrBuilder(pluginContext, expression.symbol)) {
                irCall(functionSymbol).apply {
                    extensionReceiver = expression.extensionReceiver
                    putValueArgument(0, irString(arg))
                }
            }
        }

        return super.visitCall(expression)
    }

    companion object {
        private val equalsFqName = FqName("com.ldhdev.asmhelper.compiler.UtilsKt.callEquals")
        private val toStringFqName = FqName("com.ldhdev.asmhelper.compiler.UtilsKt.callToString")
        private val classFqName = FqName("com.ldhdev.asmhelper.compiler.UtilsKt.class")
    }
}