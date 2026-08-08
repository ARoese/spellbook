import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.fufu.spellbook.CachedSuspend
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class CachedSuspendTest {
    // this is a cyclical dependency, and thus should fail
    @Test
    fun testAntiDeadlock(){
        runBlocking {
            var dep: CachedSuspend<String>? = null
            val makeStringWithDep: suspend () -> String = {
                val innerDep = dep
                innerDep?.let { innerDep.get() }
                    ?: throw UnsupportedOperationException("this test would not deadlock")
            }
            dep = CachedSuspend{
                makeStringWithDep()
            }
            try{
                withTimeout(4000){
                    assertFailsWith(IllegalStateException::class){
                        val resultingString = dep.get()
                        println(resultingString)
                    }
                }
            } catch(e: TimeoutCancellationException){
                throw IllegalStateException("likely deadlock", e)
            }
        }
    }

    // this is not a cyclical dependency, and thus should pass
    @Test
    fun testChainedCall(){
        val str = "string1"
        val cache1 = CachedSuspend{
            str
        }
        val cache2 = CachedSuspend{
            cache1.get()
        }

        val cacheResult = runBlocking { cache2.get() }
        assertEquals(cacheResult, str)
    }
}