@file:Suppress("unused")

package com.ldhdev.asmhelper.compiler

import org.objectweb.asm.MethodVisitor

fun <T> MethodVisitor.callEquals(): Unit = error("Replaced by compiler plugin")

fun <T> MethodVisitor.callToString(): Unit = error("Replaced by compiler plugin")

fun <T> MethodVisitor.`class`(): Unit = error("Replaced by compiler plugin")