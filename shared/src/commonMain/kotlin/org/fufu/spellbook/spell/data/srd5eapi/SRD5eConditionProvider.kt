package org.fufu.spellbook.spell.data.srd5eapi

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.fufu.spellbook.CachedSuspend
import org.fufu.spellbook.spell.domain.Condition
import org.fufu.spellbook.spell.domain.ConditionProvider
import spellbook.shared.generated.resources.Res

class SRD5eConditionProvider: ConditionProvider {
    private val conditions = CachedSuspend{
        val bytes = Res.readBytes("files/srd5e/2014/en/5e-SRD-Conditions.json").decodeToString()
        val json = Json.parseToJsonElement(bytes)
        try {
            json.jsonArray.associate { condition ->
                val index = condition.jsonObject["index"]?.jsonPrimitive?.content
                    ?: throw IllegalArgumentException("index nonexistent: $condition")
                val name = condition.jsonObject["name"]?.jsonPrimitive?.content
                    ?: throw IllegalArgumentException("name nonexistent: $condition")
                val desc = condition.jsonObject["desc"]?.jsonArray?.map {
                    it.jsonPrimitive.content
                } ?: throw IllegalArgumentException("desc nonexistent: $condition")

                val condition = Condition(
                    name,
                    desc.joinToString("\n")
                )
                /*
                Logger.getLogger("SRD5eConditionProvider")
                    .log(Level.INFO, "Read condition: $condition")
                 */
                index to condition
            }
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException("Conditions json resource is malformed", e)
        }
    }

    override fun getConditions(): Flow<Set<String>> {
        return flow {
            emit(conditions.get().keys)
        }
    }

    override fun getFullCondition(name: String): Flow<Condition?> {
        return flow {
            emit(conditions.get()[name])
        }
    }
}