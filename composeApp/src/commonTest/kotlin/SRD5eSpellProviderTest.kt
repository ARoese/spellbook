import kotlinx.coroutines.runBlocking
import kson.KsonApi
import org.fufu.spellbook.spell.data.srd5eapi.SRD5eSpellProvider
import org.fufu.spellbook.spell.data.srd5eapi.makeClient
import kotlin.test.Test

class SRD5eSpellProviderTest {
    @Test
    fun getSpells(){
        runBlocking {
            val provider = SRD5eSpellProvider(KsonApi(makeClient()))
            val numSpells = provider.numSpells()
            println("numSpells: $numSpells")
            provider.getSpell(1).collect{
                println(it)
            }
        }
    }
}