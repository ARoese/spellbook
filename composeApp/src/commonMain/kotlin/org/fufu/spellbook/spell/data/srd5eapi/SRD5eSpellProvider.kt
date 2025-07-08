package org.fufu.spellbook.spell.data.srd5eapi

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.engine.cio.endpoint
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNamingStrategy
import kson.KsonApi
import kson.models.Spells
import org.fufu.spellbook.CachedSuspend
import org.fufu.spellbook.spell.domain.Spell
import org.fufu.spellbook.spell.domain.SpellInfo
import org.fufu.spellbook.spell.domain.SpellProvider
import org.fufu.spellbook.spell.domain.normalized

@OptIn(kotlinx.serialization.ExperimentalSerializationApi::class)
fun makeClient(): HttpClient {
    return HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                //type keyword is used in DND API
                classDiscriminator = "#class"
                namingStrategy = JsonNamingStrategy.SnakeCase
                ignoreUnknownKeys = true
                //prettyPrint = true
                //isLenient = true
            })
        }
        engine {
            maxConnectionsCount = 10
            endpoint {
                maxConnectionsPerRoute = 10
                pipelineMaxSize = 1
                keepAliveTime = 10000
                connectTimeout = 10000
                connectAttempts = 5
            }
        }
    }
}

fun formatComponents(spell: Spells): String {
    val componentString = spell.components.mapNotNull {
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

    spell.material?.let { return "$componentString (${it})" }
    return componentString
}

class SRD5eSpellProvider(val api: KsonApi): SpellProvider {
    private val spellResponse = CachedSuspend{
        api.query<Spells>()
    }

    private val spellIdMap = CachedSuspend{
        spellResponse.get().results.mapIndexed{n, i ->
            n to i.index
        }.associate {it}
    }

    suspend fun numSpells(): Int{
        return spellResponse.get().results.size
    }

    private suspend fun getDomainSpellById(id: Int): Spell? {
        val index = spellIdMap.get()[id] ?: return null

        val res = api.fetch<Spells>(index)
        return Spell(
            key = id,
            info = SpellInfo(
                sources = listOf("D&D 5e SRD"),
                versions = listOf("5e"),
                classes = res.classes.map { it.name },
                components = formatComponents(res),
                duration = res.duration,
                guilds = emptyList(),
                level = res.level,
                name = res.name,
                optional = emptyList(),
                range = res.range,
                ritual = res.ritual,
                school = res.school.name,
                subclasses = res.subclasses.map { it.name },
                text = res.desc.joinToString("\n"),
                time = res.castingTime,
                tag = emptyList(),
                damages = res.damage?.damageType?.name?.let { listOf(it) } ?: emptyList(),
                saves = emptyList(), // TODO: extract this from description
                dragonmarks = emptyList()
            ).normalized()
        )
    }

    override fun getSpells(): Flow<List<Spell>> {
        return flow {
            emit(
                coroutineScope {
                    spellIdMap.get().keys.map{ id ->
                        async{getDomainSpellById(id)}
                    }.awaitAll()
                }.filterNotNull()
            )
        }
    }

    override fun getSpells(ids: Set<Int>): Flow<List<Spell>> {
        return flow {
            emit(
                coroutineScope {
                    spellIdMap.get().keys.intersect(ids).map{ id ->
                        async{getDomainSpellById(id)}
                    }.awaitAll()
                }.filterNotNull()
            )
        }
    }

    override fun getSpell(id: Int): Flow<Spell?> {
        return flow {
            emit(getDomainSpellById(id))
        }
    }
}