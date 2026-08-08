package org.fufu.spellbook.spell.data.srd5eapi

import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toLowerCase
import androidx.datastore.preferences.protobuf.LazyStringArrayList.emptyList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.fufu.spellbook.CachedSuspend
import org.fufu.spellbook.spell.domain.Spell
import org.fufu.spellbook.spell.domain.SpellInfo
import org.fufu.spellbook.spell.domain.SpellProvider
import org.fufu.spellbook.spell.domain.normalized
import spellbook.shared.generated.resources.Res

fun formatComponents(components: List<String>, material: String?): String {
    val componentString = components.mapNotNull {
            when(it){ // only V S M
                "V" -> Pair(0, "V")
                "S" -> Pair(1, "S")
                "M" -> Pair(2, "M")
                else -> null
            }
        }
        // always in order V -> S -> M
        .sortedBy { it.first }
        .map { it.second }
        .joinToString(", ")

    material?.let { return "$componentString (${it})" }
    return componentString
}

/**
 * Assume that this JsonElement is a jsonObject, and that it has a given field.
 * return the value of that field, or throw
 *
 * @throws IllegalArgumentException if assumptions are untrue
 */
fun JsonElement.expectField(field: String): JsonElement {
    return this.jsonObject[field]
        ?: throw IllegalArgumentException("Expected field \"$field\" in $this")
}

class SRD5eSpellProvider: SpellProvider {
    /**
     * maps subclass index to a proper name for that subclass
     * in the form `class: subclass`
     */
    private val subclassFullNames = CachedSuspend {
        val bytes = Res.readBytes("files/srd5e/2014/en/5e-SRD-Subclasses.json").decodeToString()
        val json = Json.parseToJsonElement(bytes)

        json.jsonArray.associate { subclass ->
            val index = subclass.expectField("index").jsonPrimitive.content
            val name = subclass.expectField("name").jsonPrimitive.content
            val className = subclass.expectField("class").expectField("name").jsonPrimitive.content

            index to "$className: $name"
        }
    }

    private fun extractSaves(desc: String): List<String> {
        return "(\\w+) saving throw"
            .toRegex(RegexOption.IGNORE_CASE)
            .findAll(desc)
            .mapNotNull { match ->
                match.groups[1]!!.value.takeIf {
                    it.toLowerCase(Locale.current) in arrayOf(
                        "dexterity",
                        "wisdom",
                        "charisma",
                        "constitution",
                        "intelligence",
                        "strength"
                    )
                }
            }.toList()
    }

    private val spells = CachedSuspend {
        val bytes = Res.readBytes("files/srd5e/2014/en/5e-SRD-Spells.json").decodeToString()
        val json = Json.parseToJsonElement(bytes)


        try {
            json.jsonArray.withIndex().associate { (i, spell) ->
                val desc = spell.expectField("desc").jsonArray.map {
                    it.jsonPrimitive.content
                }.joinToString("\n")

                val classes = spell.expectField("classes").jsonArray.map { cl ->
                    cl.expectField("name").jsonPrimitive.content
                }

                val components = spell.expectField("components").jsonArray.map {
                    it.jsonPrimitive.content
                }
                val material = spell.jsonObject["material"]?.jsonPrimitive?.content

                val subclasses = spell.expectField("subclasses").jsonArray
                    .mapNotNull { subclass ->
                        val subclassIndex = subclass.expectField("index").jsonPrimitive.content
                        subclassFullNames.get()[subclassIndex]
                    }

                val newSpell = Spell(
                    key = i,
                    info = SpellInfo(
                        sources = listOf("D&D 5e 2014 SRD"),
                        versions = listOf("5e"),
                        classes = classes,
                        components = formatComponents(components, material),
                        duration = spell.expectField("duration").jsonPrimitive.content,
                        guilds = emptyList(),
                        level = spell.expectField("level").jsonPrimitive.int,
                        name = spell.expectField("name").jsonPrimitive.content,
                        optional = emptyList(), // TODO: this
                        range = spell.expectField("range").jsonPrimitive.content,
                        ritual = spell.expectField("ritual").jsonPrimitive.boolean,
                        school = spell.expectField("school").expectField("name").jsonPrimitive.content,
                        subclasses = subclasses,
                        text = desc,
                        time = spell.expectField("casting_time").jsonPrimitive.content,
                        tag = emptyList(),
                        damages = emptyList(), // TODO: extract this from description
                        saves = extractSaves(desc),
                        dragonmarks = emptyList()
                    ).normalized()
                )
                /*
                Logger.getLogger("SRD5eSpellProvider")
                    .log(Level.INFO, "Read spell: $spell")
                 */
                i to newSpell
            }
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException("Conditions json resource is malformed", e)
        }
    }

    suspend fun numSpells(): Int{
        return spells.get().size
    }

    override fun getSpells(): Flow<List<Spell>> {
        return flow {
            emit(spells.get().values.toList())
        }
    }

    override fun getSpells(ids: Set<Int>): Flow<List<Spell>> {
        return flow {
            emit(spells.get().filter { it.key in ids }.values.toList())
        }
    }

    override fun getSpell(id: Int): Flow<Spell?> {
        return flow {
            emit(spells.get()[id])
        }
    }
}