@file:Suppress("unused")

package com.ldhdev.asmhelper.compiler

import org.objectweb.asm.MethodVisitor

fun <T> MethodVisitor.getKObject(): Unit = error("Replaced by compiler plugin")

fun <T> MethodVisitor.getKCompanion(): Unit = error("Replaced by compiler plugin")