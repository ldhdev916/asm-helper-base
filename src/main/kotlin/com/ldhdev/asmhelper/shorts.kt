package com.ldhdev.asmhelper

import org.objectweb.asm.Label
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes.*

fun MethodVisitor.getField(owner: String, name: String, type: String) = visitFieldInsn(GETFIELD, owner, name, type)

fun MethodVisitor.getStatic(owner: String, name: String, type: String) = visitFieldInsn(GETSTATIC, owner, name, type)

fun MethodVisitor.putField(owner: String, name: String, type: String) = visitFieldInsn(PUTFIELD, owner, name, type)

fun MethodVisitor.putStatic(owner: String, name: String, type: String) = visitFieldInsn(PUTSTATIC, owner, name, type)

fun MethodVisitor.invokeVirtual(owner: String, name: String, descriptor: String) =
    visitMethodInsn(INVOKEVIRTUAL, owner, name, descriptor, false)

fun MethodVisitor.invokeStatic(owner: String, name: String, descriptor: String) =
    visitMethodInsn(INVOKESTATIC, owner, name, descriptor, false)

fun MethodVisitor.invokeSpecial(owner: String, name: String, descriptor: String) =
    visitMethodInsn(INVOKESPECIAL, owner, name, descriptor, false)

fun MethodVisitor.invokeInterface(owner: String, name: String, descriptor: String) =
    visitMethodInsn(INVOKEINTERFACE, owner, name, descriptor, true)


fun MethodVisitor.insn(opcode: Int) = visitInsn(opcode)

fun MethodVisitor.int(number: Int) = when (number) {
    -1 -> visitInsn(ICONST_M1)
    0 -> visitInsn(ICONST_0)
    1 -> visitInsn(ICONST_1)
    2 -> visitInsn(ICONST_2)
    3 -> visitInsn(ICONST_3)
    4 -> visitInsn(ICONST_4)
    5 -> visitInsn(ICONST_5)
    in -128 until 128 -> visitIntInsn(BIPUSH, number)
    in -32768 until 32768 -> visitIntInsn(SIPUSH, number)
    else -> visitLdcInsn(number)
}

fun MethodVisitor.iload(index: Int) = visitVarInsn(ILOAD, index)

fun MethodVisitor.lload(index: Int) = visitVarInsn(LLOAD, index)

fun MethodVisitor.fload(index: Int) = visitVarInsn(FLOAD, index)

fun MethodVisitor.dload(index: Int) = visitVarInsn(DLOAD, index)

fun MethodVisitor.aload(index: Int) = visitVarInsn(ALOAD, index)

fun MethodVisitor.istore(index: Int) = visitVarInsn(ISTORE, index)

fun MethodVisitor.lstore(index: Int) = visitVarInsn(LSTORE, index)

fun MethodVisitor.fstore(index: Int) = visitVarInsn(FSTORE, index)

fun MethodVisitor.dstore(index: Int) = visitVarInsn(DSTORE, index)

fun MethodVisitor.astore(index: Int) = visitVarInsn(ASTORE, index)

fun MethodVisitor.new(type: String) = visitTypeInsn(NEW, type)

fun MethodVisitor.jump(opcode: Int, label: Label) = visitJumpInsn(opcode, label)

fun MethodVisitor.`return`() = insn(RETURN)

fun MethodVisitor.ireturn() = insn(IRETURN)

fun MethodVisitor.freturn() = insn(FRETURN)

fun MethodVisitor.dreturn() = insn(DRETURN)

fun MethodVisitor.areturn() = insn(ARETURN)

fun MethodVisitor.lreturn() = insn(LRETURN)