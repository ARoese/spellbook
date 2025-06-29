package org.fufu.spellbook.spell.domain

import kotlinx.coroutines.flow.Flow

data class Condition(
    val name: String,
    val desc: String
)

interface ConditionProvider{
    fun getConditions(): Flow<Set<String>>
    fun getFullCondition(name: String): Flow<Condition?>
}