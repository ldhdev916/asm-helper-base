package com.ldhdev.plugin.transformations

import com.ldhdev.plugin.getDescriptor
import com.ldhdev.plugin.getType
import org.jetbrains.kotlin.backend.common.IrElementTransformerVoidWithContext
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.builders.irString
import org.jetbrains.kotlin.ir.declarations.IrField
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrConst
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrPropertyReference
import org.jetbrains.kotlin.ir.types.getClass
import org.jetbrains.kotlin.ir.types.typeWith
import org.jetbrains.kotlin.ir.util.dumpKotlinLike
import org.jetbrains.kotlin.ir.util.kotlinFqName
import org.jetbrains.kotlin.ir.util.parentAsClass
import org.jetbrains.kotlin.ir.util.properties
import org.jetbrains.kotlin.ir.visitors.IrElementVisitorVoid
import org.jetbrains.kotlin.ir.visitors.acceptChildrenVoid
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name

class FieldElementTransformer(private val pluginContext: IrPluginContext, private val collector: MessageCollector) :
    IrElementTransformerVoidWithContext() {

    private fun getFunction(name: String) =
        pluginContext.referenceFunctions(FqName("com.ldhdev.asmhelper.$name")).single()

    private val getFieldFunction by lazy { getFunction("getField") }
    private val getStaticFunction by lazy { getFunction("getStatic") }
    private val putFieldFunction by lazy { getFunction("putField") }
    private val putStaticFunction by lazy { getFunction("putStatic") }

    override fun visitCall(expression: IrCall): IrExpression {

        val function = expression.symbol.owner

        val isGetter = when (function.kotlinFqName) {
            getFieldFqName -> true
            putFieldFqName -> false
            else -> null
        }

        if (isGetter != null) {
            val field: IrField?
            if (expression.typeArgumentsCount == 0) {
                val propertyReference = expression.getValueArgument(0) as IrPropertyReference
                val fieldSymbol = propertyReference.field

                if (fieldSymbol == null) {
                    collector.report(
                        CompilerMessageSeverity.ERROR,
                        "Converting kotlin property into asm call is not supported\n${propertyReference.symbol.owner.dumpKotlinLike()}"
                    )
                }

                field = fieldSymbol?.owner
            } else {
                val ownerClass = expression.getTypeArgument(0)?.getClass()!!
                val fieldName = Name.identifier((expression.getValueArgument(0)!! as IrConst<*>).value as String)

                val property =
                    pluginContext.referenceProperties(ownerClass.kotlinFqName.child(fieldName)).singleOrNull()?.owner
                        ?: ownerClass.properties.singleOrNull { it.name == fieldName }

                var foundField: IrField? = null
                property?.acceptChildrenVoid(object : IrElementVisitorVoid {
                    override fun visitElement(element: IrElement) {
                        element.acceptChildrenVoid(this)
                        if (element is IrField) foundField = element
                    }
                })

                field = foundField.also {
                    if (it == null) {
                        collector.report(
                            CompilerMessageSeverity.ERROR,
                            "Can't find field $fieldName in class ${ownerClass.kotlinFqName}"
                        )
                    }
                }
            }
            if (field != null) {
                val owner = field.parentAsClass.typeWith().getType()
                val name = field.name.asString()
                val isStatic = field.isStatic
                val type = field.type.getDescriptor()

                val functionSymbol = when {
                    isStatic -> if (isGetter) getStaticFunction else putStaticFunction
                    else -> if (isGetter) getFieldFunction else putFieldFunction
                }

                return with(DeclarationIrBuilder(pluginContext, expression.symbol)) {
                    irCall(functionSymbol).apply {

                        extensionReceiver = expression.extensionReceiver
                        putValueArgument(0, irString(owner))
                        putValueArgument(1, irString(name))
                        putValueArgument(2, irString(type))
                    }
                }
            }
        }

        return super.visitCall(expression)
    }

    companion object {
        private val getFieldFqName = FqName("com.ldhdev.asmhelper.compiler.getField")
        private val putFieldFqName = FqName("com.ldhdev.asmhelper.compiler.putField")
    }
}
