package org.fufu.spellbook

import org.fufu.spellbook.character.domain.Character
import org.fufu.spellbook.character.domain.CharacterIcon
import org.fufu.spellbook.character.domain.SpellSlotLevel
import org.fufu.spellbook.spell.domain.Spell
import org.fufu.spellbook.spell.domain.SpellInfo
import kotlin.random.Random

val seededRandom = Random(42)

val tagChoices = listOf("damage", "utility", "debuff", "buff", "storage", "transportation", "teleportation")
fun randomTags() : List<String> {
    val count = seededRandom.nextInt(0,5)
    return tagChoices.shuffled(seededRandom).subList(0, count)
}

fun randomSchool() : String {
    return setOf(
        "abjuration",
        "conjuration",
        "divination",
        "evocation"
    ).random(seededRandom)
}

val PreviewBaseSpellInfo = SpellInfo(
    sources = listOf("PreviewSource1", "PreviewSource2"),
    classes = listOf("Wizard", "Sorcerer"),
    versions = listOf("5e"),
    components = "V, S",
    duration = "instantaneous",
    guilds = emptyList(),
    level = 0,
    name = "Preview Spell",
    optional = emptyList(),
    range = "example range",
    ritual = false,
    school = "ILLUSION",
    subclasses = listOf("strings.Wizard", "strings.Sorcerer"),
    text = "Description for spell.",
    time = "5 minutes",
    tag = listOf("tag1", "tag2", "tag3"),
    damages = listOf("ACID", "COLD"),
    saves = listOf("STR", "CHA"),
    dragonmarks = emptyList()
)

val PreviewSpells = (1..40).map {
    Spell(
        it,
        PreviewBaseSpellInfo.copy(
            text="Spell text for spell $it",
            name="Spell $it",
            tag= randomTags(),
            school= randomSchool(),
            ritual= seededRandom.nextBoolean(),
            level= seededRandom.nextInt(0, 10)
        )
    )
}

val PreviewCharacters = (1..21)
    .map{
        val numSpells = PreviewSpells.size
        val min = PreviewSpells.minOf{it.key}
        val max = PreviewSpells.maxOf{it.key}
        val spellIds = (min..max)
            .shuffled(seededRandom)
            .subList(0, seededRandom.nextInt(from=0, until=10))
        Character(
            id = it,
            name = "Preview Character $it",
            spells = spellIds.associateWith { seededRandom.nextBoolean() },
            characterClass = "Class $it",
            subclass = "Subclass $it",
            level = it,
            maxPreparedSpells = 20,
            spellSlots = (1..9)
                .associateWith { seededRandom.nextInt(until=6)+1 }
                .mapValues { SpellSlotLevel(it.value, seededRandom.nextInt(it.value)) },
            characterIcon = CharacterIcon.options().random(seededRandom)
        )
    }