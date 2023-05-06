package com.ldhdev.asmhelper

import org.objectweb.asm.Label
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

class IfClauseBuilder(private val visitor: MethodVisitor) {

    private val label = Label()
    private var code: VisitorAction? = null

    fun condition(opcode: Int, action: VisitorAction) = apply {
        visitor.action()
        visitor.jump(opcode, label)
    }

    fun code(action: VisitorAction) = apply { this.code = action }

    fun build() {
        code?.invoke(visitor)
        visitor.visitLabel(label)
    }
}

class IfElseClauseBuilder(private val visitor: MethodVisitor) {

    private val ifConditions = mutableListOf<Pair<Int, VisitorAction>>()
    private var code: VisitorAction? = null
    private var elseCode: VisitorAction? = null

    fun `if`(opcode: Int, action: VisitorAction) = apply { ifConditions.add(opcode to action) }

    fun code(action: VisitorAction) = apply { this.code = action }

    fun `else`(action: VisitorAction) = apply { this.elseCode = action }

    fun build(): Unit = with(visitor) {
        val ifLabel = Label()
        val elseLabel = Label()

        ifConditions.forEach { (opcode, action) ->
            action()
            jump(opcode, ifLabel)
        }

        code?.invoke(this)
        jump(Opcodes.GOTO, elseLabel)
        visitLabel(ifLabel)

        elseCode?.invoke(this)

        visitLabel(elseLabel)
    }
}

inline fun MethodVisitor.ifClause(action: IfClauseBuilder.() -> Unit) = IfClauseBuilder(this).apply(action).build()

inline fun MethodVisitor.ifElseClause(action: IfElseClauseBuilder.() -> Unit) =
    IfElseClauseBuilder(this).apply(action).build()