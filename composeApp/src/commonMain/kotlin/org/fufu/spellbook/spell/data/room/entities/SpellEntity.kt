package org.fufu.spellbook.spell.data.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import org.fufu.spellbook.spell.domain.Spell
import org.fufu.spellbook.spell.domain.SpellInfo

@Entity
@TypeConverters(Converters::class)
data class SpellEntity(
    @PrimaryKey(autoGenerate = true) val key: Int = 0,
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

fun SpellEntity.toSpell(): Spell {
    return Spell(
        key = key,
        info = SpellInfo(
            sources = sources,
            versions = versions,
            classes = classes,
            components = components,
            duration = duration,
            guilds = guilds,
            level = level,
            name = name,
            optional = optional,
            range = range,
            ritual = ritual,
            school = school,
            subclasses = subclasses,
            text = text,
            time = time,
            tag = tag,
            damages = damages,
            saves = saves,
            dragonmarks = dragonmarks
        )
    )
}

fun Spell.toEntity() : SpellEntity {
    return SpellEntity(
        key = key,
        sources = info.sources,
        versions = info.versions,
        classes = info.classes,
        components = info.components,
        duration = info.duration,
        guilds = info.guilds,
        level = info.level,
        name = info.name,
        optional = info.optional,
        range = info.range,
        ritual = info.ritual,
        school = info.school,
        subclasses = info.subclasses,
        text = info.text,
        time = info.time,
        tag = info.tag,
        damages = info.damages,
        saves = info.saves,
        dragonmarks = info.dragonmarks
    )
}