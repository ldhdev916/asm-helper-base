@file:Suppress("unused", "UNUSED_PARAMETER")

package com.ldhdev.asmhelper.compiler

import org.objectweb.asm.MethodVisitor
import kotlin.reflect.KFunction
import kotlin.reflect.KProperty

fun <T> MethodVisitor.getField(name: String): Unit = error("Replaced by compiler plugin")

fun <T> MethodVisitor.putField(name: String): Unit = error("Replaced by compiler plugin")

fun MethodVisitor.getField(property: KProperty<*>): Unit = error("Replaced by compiler plugin")

fun MethodVisitor.putField(property: KProperty<*>): Unit = error("Replaced by compiler plugin")

fun MethodVisitor.invoke(function: KFunction<*>): Unit = error("Replaced by compiler plugin")