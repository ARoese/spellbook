import kotlinx.coroutines.runBlocking
import kson.KsonApi
import kson.models.Conditions
import org.fufu.spellbook.spell.data.srd5eapi.makeClient
import org.junit.Test

class SRD5eConditionProviderTest {
    @Test
    fun testGetConditions(){
        runBlocking {
            val api = KsonApi(makeClient())
            val conditions = api.query<Conditions>()
            println(conditions)
            println(api.fetch<Conditions>(conditions.results[1].index))
        }
    }
}