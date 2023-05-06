package com.ldhdev.plugin

import org.jetbrains.kotlin.ir.types.*
import org.jetbrains.kotlin.name.FqName

private fun IrType.convertPrimitiveWrapper() = "java/lang/" + with(makeNotNull()) {
    when {
        isByte() -> "Byte"
        isShort() -> "Short"
        isInt() -> "Integer"
        isLong() -> "Long"
        isFloat() -> "Float"
        isDouble() -> "Double"
        isChar() -> "Character"
        isBoolean() -> "Boolean"
        else -> error("Unknown primitive type $this")
    }
}

fun IrType.getDescriptor(): String = when {
    isUnit() -> "V"
    isByte() -> "B"
    isShort() -> "S"
    isInt() -> "I"
    isLong() -> "L"
    isFloat() -> "F"
    isDouble() -> "D"
    isBoolean() -> "Z"
    isChar() -> "C"
    makeNotNull().isByteArray() -> "[B"
    makeNotNull().isShortArray() -> "[S"
    makeNotNull().isIntArray() -> "[I"
    makeNotNull().isLongArray() -> "[J"
    makeNotNull().isFloatArray() -> "[F"
    makeNotNull().isDoubleArray() -> "[D"
    makeNotNull().isBooleanArray() -> "[Z"
    makeNotNull().isCharArray() -> "[C"
    isArray() || isNullableArray() -> "[" + (this as IrSimpleType).arguments[0].typeOrNull!!.makeNullable()
        .getDescriptor()

    else -> "L${getType()};"
}

fun IrType.getType() = when {
    isNullablePrimitiveType() || isPrimitiveType() -> convertPrimitiveWrapper()
    isAny() || isNullableAny() -> "java/lang/Object"
    isStringClassType() -> "java/lang/String"
    isCharSequence() -> "java/lang/CharSequence"
    else -> classFqName!!.slash()
}

fun FqName.slash() = asString().slash()

fun String.slash() = replace(".", "/")