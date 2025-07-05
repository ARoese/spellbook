package org.fufu.spellbook.spell.data.srd5eapi

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kson.KsonApi
import kson.models.Conditions
import org.fufu.spellbook.CachedSuspend
import org.fufu.spellbook.spell.domain.Condition
import org.fufu.spellbook.spell.domain.ConditionProvider

fun Conditions.toDomain(): Condition{
    return Condition(
        name = name,
        desc = desc.joinToString("\n")
    )
}

class SRD5eConditionProvider(
    private val api: KsonApi
): ConditionProvider {
    private val conditions = CachedSuspend{
        api.query<Conditions>().results
    }

    private val fullConditions = CachedSuspend{
        conditions.get().associate {
            it.index to CachedSuspend{
                api.fetch<Conditions>(it.index).toDomain()
            }
        }
    }

    override fun getConditions(): Flow<Set<String>> {
        return flow {
            emit(conditions.get().map { it.name }.toSet())
        }
    }

    override fun getFullCondition(name: String): Flow<Condition?> {
        return flow {
            val conditionIndex = conditions.get().find {
                it.name.lowercase() == name.lowercase()
            }?.index

            val res = conditionIndex?.let {
                fullConditions.get()[it]?.get()
            }

            emit(res)
        }
    }
}