package org.fufu.spellbook.spell.data.json

import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.readString
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonArray
import org.fufu.spellbook.spell.domain.Spell
import org.fufu.spellbook.spell.domain.SpellProvider

class JsonSpellProvider(val file: PlatformFile) : SpellProvider {
    private var spells: List<Spell>? = null
    private val spellsLock: Mutex = Mutex()

    private val deserializer = Json{ignoreUnknownKeys=true}

    private suspend fun loadSpells() : List<Spell> {
        // TODO: log errors here
        val elements = try {
            deserializer.parseToJsonElement(file.readString()).jsonArray
        }catch (_:SerializationException){
            return emptyList()
        }catch (_:IllegalArgumentException){
            return emptyList()
        }

        val validSpells = elements.mapNotNull { element ->
            try{
                deserializer.decodeFromJsonElement<SpellDto>(element)
            } catch (e: SerializationException) {
                null
            }
        }

        return validSpells.map { it.toDomain() }
    }

    private suspend fun loadSpellsSynchronized() : List<Spell> {
        spellsLock.withLock {
            val currentSpells = spells
            if(currentSpells != null){
                return currentSpells
            }

            val newSpells = loadSpells()
            spells = newSpells
            return newSpells
        }
    }

    private val spellFlow = flow {
        emit(loadSpellsSynchronized())
    }

    override fun getSpells(): Flow<List<Spell>> {
        return spellFlow
    }

    override fun getSpells(ids: Set<Int>): Flow<List<Spell>> {
        return spellFlow.map { spells -> spells.filter { it.key in ids } }
    }

    override fun getSpell(id: Int): Flow<Spell?> {
        return spellFlow.map { spells -> spells.find { it.key == id } }
    }
}