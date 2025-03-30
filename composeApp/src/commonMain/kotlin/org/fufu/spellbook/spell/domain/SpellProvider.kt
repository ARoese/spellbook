package org.fufu.spellbook.spell.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.lastOrNull
import org.fufu.spellbook.PreviewSpells

interface SpellProvider {
    fun getSpells() : Flow<List<Spell>>
    fun getSpell(id: Int) : Flow<Spell?>
    fun getSpells(ids: Set<Int>): Flow<List<Spell>>
}

interface SpellMutator : SpellProvider {
    suspend fun setSpell(spell: Spell)
    suspend fun deleteSpell(key: Int)
    suspend fun addSpell(spell: SpellInfo): Int
}

suspend fun SpellMutator.importFrom(
    provider: SpellProvider,
    ids : Set<Int>? = null,
    onProgress : (Float) -> Unit = {}
) : List<Int> {
    val spellsFlow = if(ids == null) provider.getSpells() else provider.getSpells(ids)

    onProgress(0.5f)

    val lastSpells = spellsFlow.lastOrNull()
    if(lastSpells.isNullOrEmpty()){
        onProgress(1f)
        return emptyList()
    }

    val numSpells = lastSpells.size // not 0
    return lastSpells.map{ (i, spell) ->
        val res = addSpell(spell)
        onProgress(0.5f + ((i+1)/numSpells)*0.5f)
        res
    }.toList()
}

class MockSpellProvider : SpellProvider {
    private val mockSpells = PreviewSpells
    override fun getSpells() : Flow<List<Spell>> {
        return flowOf(mockSpells.toList())
    }

    override fun getSpells(ids: Set<Int>) : Flow<List<Spell>> {
        return flowOf(mockSpells.filter{
            ids.contains(it.key)
        })
    }

    override fun getSpell(id: Int) : Flow<Spell?> {
        return flowOf(mockSpells.find{
            it.key == id
        })
    }
}