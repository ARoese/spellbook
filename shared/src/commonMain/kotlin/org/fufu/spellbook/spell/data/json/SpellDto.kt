package org.fufu.spellbook.spell.data.json

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.fufu.spellbook.spell.domain.Spell
import org.fufu.spellbook.spell.domain.SpellInfo

// TODO: use this in database text field
@Serializable
data class MultiLingualStringDto (
    @SerialName("enUS") val enUS: String? = null,
    @SerialName("pt")    val pt: String? = null,
    @SerialName("fr")    val fr: String? = null,
    @SerialName("es")    val es: String? = null,
    @SerialName("it")    val it: String? = null,
    @SerialName("de")    val de: String? = null,
)

@Serializable
data class SpellDto(
    @SerialName("sources")      val sources: Array<String>,
    @SerialName("classes")      val classes: Array<String>,
    @SerialName("components")   val components: MultiLingualStringDto,
    @SerialName("duration")     val duration: String,
    @SerialName("guilds")       val guilds: Array<String>,
    @SerialName("key")          val key: Int,
    @SerialName("level")        val level: Int,
    @SerialName("name")         val name: MultiLingualStringDto,
    @SerialName("optional")     val optional: Array<String>,
    @SerialName("range")        val range: String,
    @SerialName("ritual")       val ritual: Boolean,
    @SerialName("school")       val school: String,
    @SerialName("subclasses")   val subclasses: Array<String>,
    @SerialName("text")         val text: MultiLingualStringDto,
    @SerialName("time")         val time: String,
    @SerialName("tag")          val tag: Array<String>,
    @SerialName("damage")       val damage: Array<String>,
    @SerialName("ts")           val ts: Array<String>,
    @SerialName("dragonmarks")  val dragonmarks: Array<String>,
    @SerialName("versions")     val versions: Array<String>
){
    fun toDomain() : Spell {
        return Spell(
            key,
            // TODO: configurably pull the correct language from multilinguals
            SpellInfo(
                sources = sources.toList(),
                versions = versions.toList(),
                classes = classes.toList(),
                components = components.enUS ?: "",
                duration = duration,
                guilds = guilds.toList(),
                level = level,
                name = name.enUS ?: "",
                optional = optional.toList(),
                range = range,
                ritual = ritual,
                school = school,
                subclasses = subclasses.toList(),
                text = text.enUS ?: "",
                time = time,
                tag = tag.toList(),
                damages = damage.toList(),
                saves = ts.toList(),
                dragonmarks = dragonmarks.toList()
            )
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SpellDto

        if (!sources.contentEquals(other.sources)) return false
        if (!classes.contentEquals(other.classes)) return false
        if (components != other.components) return false
        if (duration != other.duration) return false
        if (!guilds.contentEquals(other.guilds)) return false
        if (key != other.key) return false
        if (level != other.level) return false
        if (name != other.name) return false
        if (!optional.contentEquals(other.optional)) return false
        if (range != other.range) return false
        if (ritual != other.ritual) return false
        if (school != other.school) return false
        if (!subclasses.contentEquals(other.subclasses)) return false
        if (text != other.text) return false
        if (time != other.time) return false
        if (!tag.contentEquals(other.tag)) return false
        if (!damage.contentEquals(other.damage)) return false
        if (!ts.contentEquals(other.ts)) return false
        if (!dragonmarks.contentEquals(other.dragonmarks)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = sources.contentHashCode()
        result = 31 * result + classes.contentHashCode()
        result = 31 * result + components.hashCode()
        result = 31 * result + duration.hashCode()
        result = 31 * result + guilds.contentHashCode()
        result = 31 * result + key
        result = 31 * result + level
        result = 31 * result + name.hashCode()
        result = 31 * result + optional.contentHashCode()
        result = 31 * result + range.hashCode()
        result = 31 * result + ritual.hashCode()
        result = 31 * result + school.hashCode()
        result = 31 * result + subclasses.contentHashCode()
        result = 31 * result + text.hashCode()
        result = 31 * result + time.hashCode()
        result = 31 * result + tag.contentHashCode()
        result = 31 * result + damage.contentHashCode()
        result = 31 * result + ts.contentHashCode()
        result = 31 * result + dragonmarks.contentHashCode()
        return result
    }

    companion object {
        fun fromDomain(spell: Spell) : SpellDto {
            return SpellDto(
                sources = spell.info.sources.toTypedArray(),
                classes = spell.info.classes.toTypedArray(),
                components = MultiLingualStringDto(spell.info.components),
                duration = spell.info.duration,
                guilds = spell.info.guilds.toTypedArray(),
                key = spell.key,
                level = spell.info.level,
                name = MultiLingualStringDto(spell.info.name),
                optional = spell.info.optional.toTypedArray(),
                range = spell.info.range,
                ritual = spell.info.ritual,
                school = spell.info.school,
                subclasses = spell.info.subclasses.toTypedArray(),
                text = MultiLingualStringDto(spell.info.text),
                time = spell.info.time,
                tag = spell.info.tag.toTypedArray(),
                damage = spell.info.damages.toTypedArray(),
                ts = spell.info.saves.toTypedArray(),
                dragonmarks = spell.info.dragonmarks.toTypedArray(),
                versions = spell.info.versions.toTypedArray()
            )
        }
    }
}