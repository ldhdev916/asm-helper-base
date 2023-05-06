@file:Suppress("unused", "UNUSED_PARAMETER")

package com.ldhdev.asmhelper.compiler

import com.ldhdev.asmhelper.ArrayBuilder
import org.objectweb.asm.MethodVisitor

fun <T> MethodVisitor.newArray(size: Int, action: ArrayBuilder.() -> Unit = {}): Unit =
    error("Replaced by compiler plugin")