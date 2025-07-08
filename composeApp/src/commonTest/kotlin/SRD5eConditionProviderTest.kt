import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kson.KsonApi
import kson.models.Conditions
import org.fufu.spellbook.spell.data.srd5eapi.SRD5eConditionProvider
import org.fufu.spellbook.spell.data.srd5eapi.makeClient
import org.junit.Test

class SRD5eConditionProviderTest {
    @Test
    fun testGetConditions(){
        runBlocking {
            val api = KsonApi(makeClient())
            val provider = SRD5eConditionProvider(api)
            val conditions = provider.getConditions().first()
            val condition = provider
                .getFullCondition(conditions.first())
                .first()
            println(conditions)
            println(condition)
        }
    }
}