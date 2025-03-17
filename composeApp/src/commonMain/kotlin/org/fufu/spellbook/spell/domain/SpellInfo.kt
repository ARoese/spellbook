package org.fufu.spellbook.spell.domain

import kotlin.math.absoluteValue

enum class Book {
    WILDEMOUNT,
    LOSTLAB,
    SCAG,
    ELEMENTALEVIL,
    XANATHAR,
    STRIX,
    FIZBAN,
    PHB,
    RIME,
    ACQINC,
    TASHA,
    RAVNICA,
    OTHER
}

enum class DamageType {
    RADIANT,
    POISON,
    NECROTIC,
    THUNDER,
    PIERCING,
    PSYCHIC,
    COLD,
    BLUDGEONING,
    SLASHING,
    FIRE,
    LIGHTNING,
    FORCE,
    ACID
}

enum class DragonMark {
    HEALING,
    SHADOW,
    SCRIBING,
    FINDING,
    SENTINEL,
    MAKING,
    HOSPITALITY,
    STORM,
    DETECTION,
    HANDLING,
    PASSAGE,
    WARDING,
    OTHER
}

enum class MagicSchool {
    DIVINATION,
    ABJURATION,
    ENCHANTMENT,
    ILLUSION,
    EVOCATION,
    CONJURATION,
    NECROMANCY,
    TRANSFORMATION,
    OTHER
}

enum class SaveType {
    CON,
    INT,
    WIS,
    STR,
    CHA,
    DEX
}

data class Spell(
    val key: Int,
    val info: SpellInfo
)

data class SpellInfo(
    val book: List<Book>,
    val classes: List<String>,
    val components: String,
    val duration: String,
    val guilds: List<String>,
    val level: Int,
    val name: String,
    val optional: List<String>,
    val range: String,
    val ritual: Boolean,
    val school: MagicSchool,
    val subclasses: List<String>,
    val text: String,
    val time: String,
    val tag: List<String>,
    val damages: List<DamageType>,
    val saves: List<SaveType>,
    val dragonmarks: List<DragonMark>
)

fun SpellInfo.formatAsOrdinalSchool() : String {
    // 7th-level Transformation
    // Conjuration cantrip
    fun SpellInfo.formatAsOrdinalSchoolInternal() : String{
        val schoolName = school.name
            .lowercase()
            .replaceFirstChar{
                if(it.isLowerCase())
                    it.titlecase()
                else it.toString()
            }
        if(level == 0){
            return "$schoolName cantrip"
        }
        return "${level.asOrdinal()}-level $schoolName"
    }

    // tack (ritual) onto the end if it's a ritual
    val formatted = this.formatAsOrdinalSchoolInternal()
    return if(ritual) "$formatted (ritual)" else formatted
}

///https://stackoverflow.com/a/41774548
fun Int.asOrdinal() : String {
    val iAbs = this.absoluteValue // if you want negative ordinals, or just use i
    return "$this" + if (iAbs % 100 in 11..13) "th" else when (iAbs % 10) {
        1 -> "st"
        2 -> "nd"
        3 -> "rd"
        else -> "th"
    }
}
