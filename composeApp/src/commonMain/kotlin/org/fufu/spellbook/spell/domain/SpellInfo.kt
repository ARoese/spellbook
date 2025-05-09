package org.fufu.spellbook.spell.domain

import kotlin.math.absoluteValue

data class Spell(
    val key: Int,
    val info: SpellInfo
)

data class SpellInfo(
    val sources: List<String>,
    val versions: List<String>,
    val classes: List<String>,
    val components: String,
    val duration: String,
    val guilds: List<String>,
    val level: Int,
    val name: String,
    val optional: List<String>,
    val range: String,
    val ritual: Boolean,
    val school: String,
    val subclasses: List<String>,
    val text: String,
    val time: String,
    val tag: List<String>,
    val damages: List<String>,
    val saves: List<String>,
    val dragonmarks: List<String>
)

fun SpellInfo.normalized() : SpellInfo {
    return this.copy(
        classes=classes.normalized(),
        guilds=guilds.normalized(),
        school=school.uppercase(),
        subclasses=subclasses.normalized(),
        damages=damages.normalized(),
        saves=saves.normalized(),
        dragonmarks=dragonmarks.normalized()
    )
}

fun List<String>.normalized() : List<String> {
    return this.map {
        it.lowercase()
    }
}

fun DefaultSpellInfo() : SpellInfo {
    return SpellInfo(
        sources = listOf("Custom"),
        versions = listOf("5e"),
        classes = emptyList(),
        components = "",
        duration = "",
        guilds = emptyList(),
        level = 0,
        name = "New Spell",
        optional = emptyList(),
        range = "",
        ritual = false,
        school = "ILLUSION",
        subclasses = emptyList(),
        text = "Spell text",
        time = "",
        tag = emptyList(),
        damages = emptyList(),
        saves = emptyList(),
        dragonmarks = emptyList()
    )
}

fun SpellInfo.formatAsOrdinalSchool() : String {
    // 7th-level Transformation
    // Conjuration cantrip
    fun SpellInfo.formatAsOrdinalSchoolInternal() : String{
        val schoolName = school
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
