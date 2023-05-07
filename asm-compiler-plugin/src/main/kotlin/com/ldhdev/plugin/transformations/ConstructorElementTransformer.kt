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
import org.jetbrains.kotlin.ir.util.kotlinFqName
import org.jetbrains.kotlin.name.FqName

class ConstructorElementTransformer(private val pluginContext: IrPluginContext) :
    IrElementTransformerVoidWithContext() {

    private fun getFunction(name: String) =
        pluginContext.referenceFunctions(FqName("com.ldhdev.asmhelper.$name")).single()

    private val newInstanceFunction by lazy { getFunction("newInstance") }
    private val initFunction by lazy { getFunction("init") }

    override fun visitCall(expression: IrCall): IrExpression {

        val function = expression.symbol.owner

        val functionSymbol = when (function.kotlinFqName) {
            newInstanceFqName -> newInstanceFunction
            initFqName -> initFunction
            else -> null
        }

        if (functionSymbol != null) {
            val owner = expression.getTypeArgument(0)!!.getType()
            val parameters =
                List(expression.typeArgumentsCount - 1) {
                    expression.getTypeArgument(it + 1)!!.getDescriptor()
                }.joinToString("")
            val descriptor = "($parameters)V"

            return with(DeclarationIrBuilder(pluginContext, expression.symbol)) {
                irCall(functionSymbol).apply {
                    extensionReceiver = expression.extensionReceiver
                    putValueArgument(0, irString(owner))
                    putValueArgument(1, irString(descriptor))

                    if (functionSymbol.owner.valueParameters.size == 3) {
                        putValueArgument(2, expression.getValueArgument(expression.valueArgumentsCount - 1))
                    }
                }
            }
        }

        return super.visitCall(expression)
    }

    companion object {
        private val newInstanceFqName = FqName("com.ldhdev.asmhelper.compiler.newInstance")

        private val initFqName = FqName("com.ldhdev.asmhelper.compiler.init")
    }
}