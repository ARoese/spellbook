package org.fufu.spellbook

import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import kotlin.coroutines.CoroutineContext

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


    private class ReentrancyMarker(
        val localReentrancyKey: CoroutineContext.Key<ReentrancyMarker>
    ) : CoroutineContext.Element {
        override val key: CoroutineContext.Key<*>
            get() = localReentrancyKey

    }

    // it's important that this object is not singleton. Otherwise, another
    // CachedSuspend sharing that singleton key will falsely think it's
    // cyclical.
    private val reentrancyKey = object : CoroutineContext.Key<ReentrancyMarker>{}
    private val reentrancyMarker = ReentrancyMarker(reentrancyKey)

    /**
     *  Get the cached value, or compute the value and return it.
     *
     *  @throws IllegalStateException if initializer calls itself
     */
    suspend fun get(): T {
        // skip lock acquisition if cached value is already non-null
        cached?.let { return it }

        val context = currentCoroutineContext()
        if (context[reentrancyKey] != null) {
            throw IllegalStateException("Cyclical dependency or recursive initializer call detected")
        }

        initializerMutex.withLock{
            // tasks waiting on the lock should check if the value
            // was computed in the mean-time
            cached?.let { return it }

            val ctx = context + reentrancyMarker
            return withContext(ctx){
                initializer().also { cached = it }
            }
        }
    }
}