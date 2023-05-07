package com.ldhdev.plugin.transformations

import com.ldhdev.plugin.getDescriptor
import org.jetbrains.kotlin.backend.common.IrElementTransformerVoidWithContext
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.builders.irString
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.util.kotlinFqName
import org.jetbrains.kotlin.name.FqName

class PrintElementTransformer(private val pluginContext: IrPluginContext) : IrElementTransformerVoidWithContext() {

    private val printFunction by lazy {
        pluginContext.referenceFunctions(FqName("com.ldhdev.asmhelper.print")).single()
    }

    override fun visitCall(expression: IrCall): IrExpression {

        if (expression.symbol.owner.kotlinFqName == printFqName) {
            val type = expression.getTypeArgument(0)!!.getDescriptor()

            return with(DeclarationIrBuilder(pluginContext, expression.symbol)) {
                irCall(printFunction).apply {
                    extensionReceiver = expression.extensionReceiver

                    putValueArgument(0, irString(type))
                    putValueArgument(1, expression.getValueArgument(0))
                }
            }
        }

        return super.visitCall(expression)
    }

    companion object {
        private val printFqName = FqName("com.ldhdev.asmhelper.compiler.print")
    }
}