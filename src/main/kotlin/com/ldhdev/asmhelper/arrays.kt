package com.ldhdev.asmhelper

import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes.*

class ArrayBuilder(private val visitor: MethodVisitor) {

    private var index = 0

    fun aadd(code: VisitorAction) = add(AASTORE, code)

    fun badd(code: VisitorAction) = add(BASTORE, code)

    fun sadd(code: VisitorAction) = add(SASTORE, code)

    fun iadd(code: VisitorAction) = add(IASTORE, code)

    fun ladd(code: VisitorAction) = add(LASTORE, code)

    fun fadd(code: VisitorAction) = add(FASTORE, code)

    fun dadd(code: VisitorAction) = add(DASTORE, code)

    fun cadd(code: VisitorAction) = add(CASTORE, code)


    private fun add(opcode: Int, code: VisitorAction): Unit = with(visitor) {
        insn(DUP)
        int(index++)
        code()
        insn(opcode)
    }
}

inline fun MethodVisitor.newArray(type: String, size: Int, action: ArrayBuilder.() -> Unit = {}) {
    int(size)
    visitTypeInsn(ANEWARRAY, type)

    ArrayBuilder(this).action()
}

inline fun MethodVisitor.newByteArray(size: Int, action: ArrayBuilder.() -> Unit = {}) {
    int(size)
    visitIntInsn(NEWARRAY, T_BYTE)

    ArrayBuilder(this).action()
}

inline fun MethodVisitor.newShortArray(size: Int, action: ArrayBuilder.() -> Unit = {}) {
    int(size)
    visitIntInsn(NEWARRAY, T_SHORT)

    ArrayBuilder(this).action()
}

inline fun MethodVisitor.newIntArray(size: Int, action: ArrayBuilder.() -> Unit = {}) {
    int(size)
    visitIntInsn(NEWARRAY, T_INT)

    ArrayBuilder(this).action()
}

inline fun MethodVisitor.newLongArray(size: Int, action: ArrayBuilder.() -> Unit = {}) {
    int(size)
    visitIntInsn(NEWARRAY, T_LONG)

    ArrayBuilder(this).action()
}

inline fun MethodVisitor.newFloatArray(size: Int, action: ArrayBuilder.() -> Unit = {}) {
    int(size)
    visitIntInsn(NEWARRAY, T_FLOAT)

    ArrayBuilder(this).action()
}

inline fun MethodVisitor.newDoubleArray(size: Int, action: ArrayBuilder.() -> Unit = {}) {
    int(size)
    visitIntInsn(NEWARRAY, T_DOUBLE)

    ArrayBuilder(this).action()
}

inline fun MethodVisitor.newCharArray(size: Int, action: ArrayBuilder.() -> Unit = {}) {
    int(size)
    visitIntInsn(NEWARRAY, T_CHAR)

    ArrayBuilder(this).action()
}

inline fun MethodVisitor.newBooleanArray(size: Int, action: ArrayBuilder.() -> Unit = {}) {
    int(size)
    visitIntInsn(NEWARRAY, T_BOOLEAN)

    ArrayBuilder(this).action()
}