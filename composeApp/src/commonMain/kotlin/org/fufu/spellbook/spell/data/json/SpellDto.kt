package org.fufu.spellbook.spell.data.json

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.fufu.spellbook.spell.domain.DamageType
import org.fufu.spellbook.spell.domain.DragonMark
import org.fufu.spellbook.spell.domain.MagicSchool
import org.fufu.spellbook.spell.domain.SaveType
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
enum class DragonMarkDto(val mark: DragonMark){
    @SerialName("strings.markOfHealing")     markOfHealing(DragonMark.HEALING),
    @SerialName("strings.markOfShadow")      markOfShadow(DragonMark.SHADOW),
    @SerialName("strings.markOfScribing")    markOfScribing(DragonMark.SCRIBING),
    @SerialName("strings.markOfFinding")     markOfFinding(DragonMark.FINDING),
    @SerialName("strings.markOfSentinel")    markOfSentinel(DragonMark.SENTINEL),
    @SerialName("strings.markOfMaking")      markOfMaking(DragonMark.MAKING),
    @SerialName("strings.markOfHospitality") markOfHospitality(DragonMark.HOSPITALITY),
    @SerialName("strings.markOfStorm")       markOfStorm(DragonMark.STORM),
    @SerialName("strings.markOfDetection")   markOfDetection(DragonMark.DETECTION),
    @SerialName("strings.markOfHandling")    markOfHandling(DragonMark.HANDLING),
    @SerialName("strings.markOfPassage")     markOfPassage(DragonMark.PASSAGE),
    @SerialName("strings.markOfWarding")     markOfWarding(DragonMark.WARDING);

    fun toDomain(): DragonMark {
        return this.mark
    }

    companion object {
        fun fromDomain(mark: DragonMark): DragonMarkDto {
            return entries.find {it.mark == mark} ?: markOfHealing
        }
    }
}

@Serializable
enum class DamageTypeDto(val damageType: DamageType){
    @SerialName("strings.dmgRadiant")       Radiant(DamageType.RADIANT),
    @SerialName("strings.dmgPoison")        Poison(DamageType.POISON),
    @SerialName("strings.dmgNecro")         Necro(DamageType.NECROTIC),
    @SerialName("strings.dmgThunder")       Thunder(DamageType.THUNDER),
    @SerialName("strings.dmgPiercing")      Piercing(DamageType.PIERCING),
    @SerialName("strings.dmgPsychic")       Psychic(DamageType.PSYCHIC),
    @SerialName("strings.dmgCold")          Cold(DamageType.COLD),
    @SerialName("strings.dmgBlud")          Blud(DamageType.BLUDGEONING),
    @SerialName("strings.dmgSlashing")      Slashing(DamageType.SLASHING),
    @SerialName("strings.dmgFire")          Fire(DamageType.FIRE),
    @SerialName("strings.dmgLightning")     Lightning(DamageType.LIGHTNING),
    @SerialName("strings.dmgForce")         Force(DamageType.FORCE),
    @SerialName("strings.dmgAcid")          Acid(DamageType.ACID);

    fun toDomain(): DamageType {
        return this.damageType
    }

    companion object {
        fun fromDomain(damageType: DamageType): DamageTypeDto {
            return entries.find {it.damageType == damageType} ?: Radiant
        }
    }
}

@Serializable
enum class MagicSchoolDto(val school: MagicSchool) {
    @SerialName("Divination")       divination(MagicSchool.DIVINATION),
    @SerialName("Abjuration")       abjuration(MagicSchool.ABJURATION),
    @SerialName("Enchantment")      enchantment(MagicSchool.ENCHANTMENT),
    @SerialName("Illusion")         illusion(MagicSchool.ILLUSION),
    @SerialName("Evocation")        evocation(MagicSchool.EVOCATION),
    @SerialName("Conjuration")      conjuration(MagicSchool.CONJURATION),
    @SerialName("Necromancy")       necromancy(MagicSchool.NECROMANCY),
    @SerialName("Transmutation")    transmutation(MagicSchool.TRANSMUTATION);

    fun toDomain(): MagicSchool {
        return this.school
    }

    companion object {
        fun fromDomain(school: MagicSchool): MagicSchoolDto {
            return entries.find {it.school == school} ?: divination
        }
    }
}

@Serializable
enum class SaveTypeDto(val saveType: SaveType) {
    @SerialName("strings.tsCos")    Con(SaveType.CON),
    @SerialName("strings.tsInt")    Int(SaveType.INT),
    @SerialName("strings.tsWis")    Wis(SaveType.WIS),
    @SerialName("strings.tsForza")  Str(SaveType.STR),
    @SerialName("strings.tsCha")    Cha(SaveType.CHA),
    @SerialName("strings.tsDex")    Dex(SaveType.DEX);

    fun toDomain(): SaveType {
        return this.saveType
    }

    companion object {
        fun fromDomain(saveType: SaveType): SaveTypeDto {
            return entries.find {it.saveType == saveType} ?: Con
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
                school = school.toDomain(),
                subclasses = subclasses.toList(),
                text = text.enUS ?: "",
                time = time,
                tag = tag.toList(),
                damages = damage.map{ it.toDomain() },
                saves = ts.map{ it.toDomain() },
                dragonmarks = dragonmarks.map { it.toDomain() }
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