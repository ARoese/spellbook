package org.fufu.spellbook

import kotlinx.serialization.SerializationException

inline fun <reified T : Enum<T>>toStringUsingName(enum: T) : String{
    return enum.name
}

inline fun <reified T : Enum<T>>toEnumUsingName(enum: String, default: T? = null) : T {
    val map = enumValues<T>().associate { (it.name to it) }
    return map[enum] ?: default ?: throw SerializationException("illegal enum: $enum")
}

fun <T> Set<T>.xor(other: Set<T>): Set<T> {
    return this.union(other) - this.intersect(other)
}

fun <T> nullingXor(self: Set<T>?, other: Set<T>): Set<T>? {
    return (self?.xor(other) ?: other).ifEmpty { null }
}

fun <T> nullingXor(self: Set<T>?, other: T): Set<T>? {
    val oneSet = setOf(other)
    return (self?.xor(oneSet) ?: oneSet).ifEmpty { null }
}