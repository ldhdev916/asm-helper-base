@file:Suppress("unused", "UNUSED_PARAMETER")

package com.ldhdev.asmhelper.compiler

import com.ldhdev.asmhelper.StringBuilderBuilder
import com.ldhdev.asmhelper.VisitorAction

fun <T> StringBuilderBuilder.append(value: VisitorAction): Unit = error("Replaced by compiler plugin")