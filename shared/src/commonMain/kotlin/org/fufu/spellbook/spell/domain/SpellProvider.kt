package org.fufu.spellbook.spell.domain

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.lastOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.replay
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.flow.stateIn
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

fun importTagFor(i: Int): String{
    return "Import-$i"
}

fun importNumFromSource(source: String): Int? {
    return source
        .split("-")
        .takeIf { it.size == 2 } // if is form (.*-\d*)
        ?.let {
            it[1].toIntOrNull()
        }
}

fun SpellInfo.withoutImportTags(): SpellInfo {
    return this.copy(
        sources = this.sources.filter { importNumFromSource(it) == null }
    )
}

fun getMaxImportNum(spell: Spell): Int? {
    return spell.info.sources
        .mapNotNull { importNumFromSource(it) }
        .maxOfOrNull { it }
}

fun Iterable<Spell>.getMaxImportNum(): Int {
    return this.maxOfOrNull {
        getMaxImportNum(it) ?: 0
    } ?: 0
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