package org.fufu.spellbook

import kotlinx.serialization.SerializationException

inline fun <reified T : Enum<T>>toStringUsingName(enum: T) : String{
    return enum.name
}

inline fun <reified T : Enum<T>>toEnumUsingName(enum: String, default: T? = null) : T {
    val map = enumValues<T>().associate { (it.name to it) }
    return map[enum] ?: default ?: throw SerializationException("illegal enum: $enum")
}