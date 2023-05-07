package com.ldhdev.plugin.transformations

import com.ldhdev.plugin.getType
import org.jetbrains.kotlin.backend.common.IrElementTransformerVoidWithContext
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.builders.irString
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.util.kotlinFqName
import org.jetbrains.kotlin.name.FqName

class ArrayElementTransformer(private val pluginContext: IrPluginContext) : IrElementTransformerVoidWithContext() {

    private val newArrayFunction by lazy {
        pluginContext.referenceFunctions(FqName("com.ldhdev.asmhelper.newArray")).single()
    }

    override fun visitCall(expression: IrCall): IrExpression {

        if (expression.symbol.owner.kotlinFqName == newArrayFqName) {
            val type = expression.getTypeArgument(0)!!.getType()

            return with(DeclarationIrBuilder(pluginContext, expression.symbol)) {
                irCall(newArrayFunction).apply {
                    extensionReceiver = expression.extensionReceiver

                    putValueArgument(0, irString(type))
                    putValueArgument(1, expression.getValueArgument(0))
                    putValueArgument(2, expression.getValueArgument(1))
                }
            }
        }

        return super.visitCall(expression)
    }

    companion object {
        private val newArrayFqName = FqName("com.ldhdev.asmhelper.compiler.newArray")
    }
}