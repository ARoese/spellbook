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

private fun importTagFor(i: Int): String{
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

private fun getMaxImportNum(spell: Spell): Int? {
    return spell.info.sources
        .mapNotNull { importNumFromSource(it) }
        .maxOfOrNull { it }
}

suspend fun SpellMutator.importFrom(
    provider: SpellProvider,
    ids : Set<Int>? = null,
    scope: CoroutineScope,
    onProgress : (Float) -> Unit = {},
) : List<Int> {
    val spellsFlow = if(ids == null) provider.getSpells() else provider.getSpells(ids)
    val nextImportNum: Int = this.getSpells().map {
        it.maxOfOrNull {
            getMaxImportNum(it) ?: 0
        } ?: 0
    }.stateIn(scope = scope).value
    val lastSpells = spellsFlow.stateIn(scope = scope).value
    onProgress(0.5f)
    if(lastSpells.isEmpty()){
        onProgress(1f)
        return emptyList()
    }

    val numSpells = lastSpells.size // not 0 because of above check
    val importNum = nextImportNum+1
    return lastSpells.map{ (i, spell) ->
        addSpell(
            spell.copy(sources = spell.sources.plus(importTagFor(importNum)))
                .normalized()
        ).also {
            onProgress(0.5f + ((i+1)/numSpells)*0.5f)
        }
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