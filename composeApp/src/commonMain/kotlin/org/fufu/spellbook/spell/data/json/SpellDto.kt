package org.fufu.spellbook.spell.data.json

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.fufu.spellbook.spell.domain.Spell
import org.fufu.spellbook.spell.domain.SpellInfo

@Serializable
enum class BookDto {
    @SerialName("strings.wildemountname")   wildemount,
    @SerialName("strings.lostlabname")      lostlab,
    @SerialName("strings.scagname")         scag,
    @SerialName("strings.eename")           elementalevil,
    @SerialName("strings.xanatharname")     xanathar,
    @SerialName("strings.strixname")        strix,
    @SerialName("strings.fizbanname")       fizban,
    @SerialName("strings.phbname")          phb,
    @SerialName("strings.rimename")         rime,
    @SerialName("strings.acqincname")       acqinc,
    @SerialName("strings.tashaname")        tasha,
    @SerialName("strings.ravnicaname")      ravnica,
    @SerialName("strings.otherbook")        otherBook;

    fun toName(): String {
        return this.name
    }

    companion object {
        fun fromDomain(src: String): BookDto {
            return entries.find {it.name == src} ?: otherBook
        }
    }
}

@Serializable
enum class DragonMarkDto(val mark: String){
    @SerialName("strings.markOfHealing")     HEALING("HEALING"),
    @SerialName("strings.markOfShadow")      SHADOW("SHADOW"),
    @SerialName("strings.markOfScribing")    SCRIBING("SCRIBING"),
    @SerialName("strings.markOfFinding")     FINDING("FINDING"),
    @SerialName("strings.markOfSentinel")    SENTINEL("SENTINEL"),
    @SerialName("strings.markOfMaking")      MAKING("MAKING"),
    @SerialName("strings.markOfHospitality") HOSPITALITY("HOSPITALITY"),
    @SerialName("strings.markOfStorm")       STORM("STORM"),
    @SerialName("strings.markOfDetection")   DETECTION("DETECTION"),
    @SerialName("strings.markOfHandling")    HANDLING("HANDLING"),
    @SerialName("strings.markOfPassage")     PASSAGE("PASSAGE"),
    @SerialName("strings.markOfWarding")     WARDING("WARDING");

    companion object {
        fun fromDomain(mark: String): DragonMarkDto {
            return entries.find { it.mark == mark } ?: HEALING
        }
    }
}

@Serializable
enum class DamageTypeDto(val damageType: String){
    @SerialName("strings.dmgRadiant")       RADIANT("RADIANT"),
    @SerialName("strings.dmgPoison")        POISON("POISON"),
    @SerialName("strings.dmgNecro")         NECROTIC("NECROTIC"),
    @SerialName("strings.dmgThunder")       THUNDER("THUNDER"),
    @SerialName("strings.dmgPiercing")      PIERCING("PIERCING"),
    @SerialName("strings.dmgPsychic")       PSYCHIC("PSYCHIC"),
    @SerialName("strings.dmgCold")          COLD("COLD"),
    @SerialName("strings.dmgBlud")          BLUDGEONING("BLUDGEONING"),
    @SerialName("strings.dmgSlashing")      SLASHING("SLASHING"),
    @SerialName("strings.dmgFire")          FIRE("FIRE"),
    @SerialName("strings.dmgLightning")     LIGHTNING("LIGHTNING"),
    @SerialName("strings.dmgForce")         FORCE("FORCE"),
    @SerialName("strings.dmgAcid")          ACID("ACID");

    companion object {
        fun fromDomain(damageType: String): DamageTypeDto {
            return entries.find {it.damageType == damageType} ?: RADIANT
        }
    }
}

@Serializable
enum class MagicSchoolDto(val school: String) {
    @SerialName("Divination")       DIVINATION("DIVINATION"),
    @SerialName("Abjuration")       ABJURATION("ABJURATION"),
    @SerialName("Enchantment")      ENCHANTMENT("ENCHANTMENT"),
    @SerialName("Illusion")         ILLUSION("ILLUSION"),
    @SerialName("Evocation")        EVOCATION("EVOCATION"),
    @SerialName("Conjuration")      CONJURATION("CONJURATION"),
    @SerialName("Necromancy")       NECROMANCY("NECROMANCY"),
    @SerialName("Transmutation")    TRANSMUTATION("TRANSMUTATION");

    companion object {
        fun fromDomain(school: String): MagicSchoolDto {
            return entries.find {it.school == school} ?: DIVINATION
        }
    }
}

@Serializable
enum class SaveTypeDto(val saveType: String) {
    @SerialName("strings.tsCos")    CONSTITUTION("CONSTITUTION"),
    @SerialName("strings.tsInt")    INTELLIGENCE("INTELLIGENCE"),
    @SerialName("strings.tsWis")    WISDOM("WISDOM"),
    @SerialName("strings.tsForza")  STRENGTH("STRENGTH"),
    @SerialName("strings.tsCha")    CHARISMA("CHARISMA"),
    @SerialName("strings.tsDex")    DEXTERITY("DEXTERITY");

    companion object {
        fun fromDomain(saveType: String): SaveTypeDto {
            return entries.find {it.saveType == saveType} ?: CONSTITUTION
        }
    }
}

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
    @SerialName("book")         val book: Array<BookDto>,
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
    @SerialName("school")       val school: MagicSchoolDto,
    @SerialName("subclasses")   val subclasses: Array<String>,
    @SerialName("text")         val text: MultiLingualStringDto,
    @SerialName("time")         val time: String,
    @SerialName("tag")          val tag: Array<String>,
    @SerialName("damage")       val damage: Array<DamageTypeDto>,
    @SerialName("ts")           val ts: Array<SaveTypeDto>,
    @SerialName("dragonmarks")  val dragonmarks: Array<DragonMarkDto>,
){
    fun toDomain() : Spell {
        return Spell(
            key,
            // TODO: configurably pull the correct language from multilinguals
            SpellInfo(
                sources = book.map{it.toName()},
                // TODO: do not assume 5e. Add something to the json spec that gives this
                versions = listOf("5e"),
                classes = classes.toList(),
                components = components.enUS ?: "",
                duration = duration,
                guilds = guilds.toList(),
                level = level,
                name = name.enUS ?: "",
                optional = optional.toList(),
                range = range,
                ritual = ritual,
                school = school.school,
                subclasses = subclasses.toList(),
                text = text.enUS ?: "",
                time = time,
                tag = tag.toList(),
                damages = damage.map{ it.damageType },
                saves = ts.map{ it.saveType },
                dragonmarks = dragonmarks.map { it.mark }
            )
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SpellDto

        if (!book.contentEquals(other.book)) return false
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
        var result = book.contentHashCode()
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
                book = spell.info.sources.map { BookDto.fromDomain(it) }.toTypedArray(),
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
                school = MagicSchoolDto.fromDomain(spell.info.school),
                subclasses = spell.info.subclasses.toTypedArray(),
                text = MultiLingualStringDto(spell.info.text),
                time = spell.info.time,
                tag = spell.info.tag.toTypedArray(),
                damage = spell.info.damages.map{ DamageTypeDto.fromDomain(it) }.toTypedArray(),
                ts = spell.info.saves.map { SaveTypeDto.fromDomain(it) }.toTypedArray(),
                dragonmarks = spell.info.dragonmarks.map{ DragonMarkDto.fromDomain(it) }.toTypedArray()
            )
        }
    }
}