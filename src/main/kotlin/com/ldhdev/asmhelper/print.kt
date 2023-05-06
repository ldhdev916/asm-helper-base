package com.ldhdev.asmhelper

import org.objectweb.asm.MethodVisitor

inline fun MethodVisitor.print(type: String, action: VisitorAction) {

    val printStream = "java/io/PrintStream"
    getStatic("java/lang/System", "out", "L$printStream;")
    action()
    invokeVirtual(printStream, "println", "($type)V")
}