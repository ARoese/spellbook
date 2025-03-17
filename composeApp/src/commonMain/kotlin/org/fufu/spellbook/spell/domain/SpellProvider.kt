package org.fufu.spellbook.spell.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.fufu.spellbook.PreviewSpells

interface SpellProvider {
    fun getSpells() : Flow<List<Spell>>
    fun getSpell(id: Int) : Flow<Spell?>
    fun getSpells(ids: Set<Int>): Flow<List<Spell>>
}

interface SpellMutator : SpellProvider {
    suspend fun setSpell(spell: Spell)
    suspend fun deleteSpell(spell: Spell)
    suspend fun addSpell(spell: SpellInfo)
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