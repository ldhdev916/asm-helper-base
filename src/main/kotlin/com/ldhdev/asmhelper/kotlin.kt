package com.ldhdev.asmhelper

import org.objectweb.asm.MethodVisitor

fun MethodVisitor.getKObject(owner: String) = getStatic(owner, "INSTANCE", "L$owner;")

fun MethodVisitor.getKCompanion(owner: String) = getStatic(owner, "Companion", "L$owner\$Companion;")