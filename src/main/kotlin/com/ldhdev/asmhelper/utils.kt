package com.ldhdev.asmhelper

import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Opcodes.DUP
import org.objectweb.asm.Type

typealias VisitorAction = MethodVisitor.() -> Unit

inline fun MethodVisitor.newInstance(type: String, descriptor: String, parameters: VisitorAction = {}) {
    new(type)
    insn(DUP)

    parameters()

    init(type, descriptor)
}

fun MethodVisitor.`this`() = aload(0)

fun MethodVisitor.callEquals(owner: String) = invokeVirtual(owner, "equals", "(Ljava/lang/Object;)Z")

fun MethodVisitor.callToString(owner: String) = invokeVirtual(owner, "toString", "()Ljava/lang/String;")

fun MethodVisitor.init(owner: String, descriptor: String) = invokeSpecial(owner, "<init>", descriptor)

fun MethodVisitor.long(number: Long) = when (number) {
    0L -> visitInsn(Opcodes.LCONST_0)
    1L -> visitInsn(Opcodes.LCONST_1)
    else -> visitLdcInsn(number)
}

fun MethodVisitor.double(number: Double) = when (number) {
    0.0 -> visitInsn(Opcodes.DCONST_0)
    1.0 -> visitInsn(Opcodes.DCONST_1)
    else -> visitLdcInsn(number)
}

fun MethodVisitor.float(number: Float) = when (number) {
    0f -> visitInsn(Opcodes.FCONST_0)
    1f -> visitInsn(Opcodes.FCONST_1)
    2f -> visitInsn(Opcodes.FCONST_2)
    else -> visitLdcInsn(number)
}

fun MethodVisitor.boolean(boolean: Boolean) = if (boolean) int(1) else int(0)

fun MethodVisitor.string(string: String) = visitLdcInsn(string)

fun MethodVisitor.`class`(name: String) = visitLdcInsn(Type.getType(name))