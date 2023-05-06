package com.ldhdev.plugin.transformations

import com.ldhdev.plugin.getDescriptor
import com.ldhdev.plugin.getType
import org.jetbrains.kotlin.backend.common.IrElementTransformerVoidWithContext
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.ir.isStatic
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.backend.jvm.fullValueParameterList
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.descriptors.DescriptorVisibilities
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.builders.irString
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrConst
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrFunctionReference
import org.jetbrains.kotlin.ir.types.getClass
import org.jetbrains.kotlin.ir.types.typeWith
import org.jetbrains.kotlin.ir.util.*
import org.jetbrains.kotlin.library.metadata.KlibMetadataProtoBuf.className
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.org.objectweb.asm.Opcodes.*

class MethodElementTransformer(private val pluginContext: IrPluginContext, private val collector: MessageCollector) :
    IrElementTransformerVoidWithContext() {

    private fun getFunction(name: String) =
        pluginContext.referenceFunctions(FqName("com.ldhdev.asmhelper.$name")).single()

    private val invokeVirtualFunction by lazy { getFunction("invokeVirtual") }
    private val invokeStaticFunction by lazy { getFunction("invokeStatic") }
    private val invokeSpecialFunction by lazy { getFunction("invokeSpecial") }
    private val invokeInterfaceFunction by lazy { getFunction("invokeInterface") }

    private fun getFunctionByOpcode(opcode: Int) = when (opcode) {
        INVOKEVIRTUAL -> invokeVirtualFunction
        INVOKESTATIC -> invokeStaticFunction
        INVOKESPECIAL -> invokeSpecialFunction
        INVOKEINTERFACE -> invokeInterfaceFunction
        else -> error("Unknown opcode $opcode")
    }

    override fun visitCall(expression: IrCall): IrExpression {

        fun IrFunction.asCall(): IrCall? {

            val parentClass = parentClassOrNull ?: run {
                collector.report(
                    CompilerMessageSeverity.ERROR,
                    "Converting top-level function into asm call is not supported\n${dumpKotlinLike()}",
                )
                return null
            }

            val opcode = when {
                isStatic -> INVOKESTATIC
                parentClass.isInterface -> INVOKEINTERFACE
                visibility == DescriptorVisibilities.PRIVATE -> INVOKESPECIAL
                else -> INVOKEVIRTUAL
            }

            val owner = when {
                parentClass.isCompanion -> "${parentClass.parentAsClass.typeWith().getType()}\$Companion"
                else -> parentClass.typeWith().getType()
            }
            val name = name.asString()

            val returnType = returnType.getDescriptor()
            val parameters = fullValueParameterList.joinToString("") {
                it.type.getDescriptor()
            }
            val descriptor = "($parameters)$returnType"

            return with(DeclarationIrBuilder(pluginContext, expression.symbol)) {
                irCall(getFunctionByOpcode(opcode)).apply {

                    extensionReceiver = expression.extensionReceiver

                    putValueArgument(0, irString(owner))
                    putValueArgument(1, irString(name))
                    putValueArgument(2, irString(descriptor))
                }
            }
        }

        val function = expression.symbol.owner

        val referencedFunction = when (function.kotlinFqName) {
            invokeFqName -> {
                val reference = expression.getValueArgument(0) as IrFunctionReference
                reference.symbol.owner
            }

            methodFqName -> {
                val name = Name.identifier((expression.getValueArgument(0) as IrConst<*>).value as String)

                val ownerClass = expression.getTypeArgument(0)?.getClass()!!

                val providedParameterTypes =
                    List(expression.typeArgumentsCount - 1) { expression.getTypeArgument(it + 1)!!.getDescriptor() }


                (pluginContext.referenceFunctions(ownerClass.kotlinFqName.child(name)).singleOrNull()?.owner
                    ?: ownerClass.functions.singleOrNull {
                        val classFunction = it.symbol.owner
                        val parameters = classFunction.valueParameters.map { param -> param.type.getDescriptor() }

                        classFunction.name == name && providedParameterTypes == parameters
                    }).also {
                    if (it == null) {
                        collector.report(
                            CompilerMessageSeverity.ERROR,
                            "Can't find function $name with parameters $providedParameterTypes in class $className"
                        )
                    }
                }
            }

            else -> null
        }

        referencedFunction?.asCall()?.let { return it }

        return super.visitCall(expression)
    }

    companion object {
        private val invokeFqName = FqName("com.ldhdev.asmhelper.compiler.invoke")

        private val methodFqName = FqName("com.ldhdev.asmhelper.compiler.method")
    }
}