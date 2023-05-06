@file:Suppress("unused", "UNUSED_PARAMETER")

package com.ldhdev.asmhelper.compiler

import com.ldhdev.asmhelper.VisitorAction
import org.objectweb.asm.MethodVisitor

fun <T> MethodVisitor.print(action: VisitorAction): Unit = error("Replaced by compiler plugin")