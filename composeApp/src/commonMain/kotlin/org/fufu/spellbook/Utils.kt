package org.fufu.spellbook

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
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

/** Represents a lazily-initialized value which will suspend on its first usage,
 *  then immediately return the computed value thereafter
 */
class CachedSuspend<T>(val initializer: (suspend () -> T)){
    private var cached: T? = null
    private val initializerMutex = Mutex()
    private val mutexOwner = Any()

    /**
     *  Get the cached value, or compute the value and return it.
     *
     *  @throws IllegalStateException if initializer calls itself
     */
    suspend fun get(): T {
        // skip lock acquisition if cached value is already non-null
        cached?.let { return it }

        try{
            initializerMutex.withLock(mutexOwner){
                // tasks waiting on the lock should check if the value
                // was computed in the mean-time
                cached?.let { return it }

                return initializer().also { cached = it }
            }
        }catch(e: IllegalStateException){
            throw IllegalStateException(
                "Cyclical dependency or double-initialize in cached suspend detected",
                e
            )
        }

    }
}