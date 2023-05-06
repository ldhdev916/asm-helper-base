package com.ldhdev.asmhelper

import org.objectweb.asm.MethodVisitor

class StringBuilderBuilder(val visitor: MethodVisitor) {

    inline fun append(type: String, value: VisitorAction) = apply {
        visitor.value()
        visitor.invokeVirtual("java/lang/StringBuilder", "append", "($type)Ljava/lang/StringBuilder;")
    }

    fun newLine() = append("java/lang/String") { string("\n") }

}

inline fun MethodVisitor.stringBuilder(action: StringBuilderBuilder.() -> Unit) {
    newInstance("java/lang/StringBuilder", "()V")
    StringBuilderBuilder(this).apply(action)
    callToString("java/lang/StringBuilder")
}