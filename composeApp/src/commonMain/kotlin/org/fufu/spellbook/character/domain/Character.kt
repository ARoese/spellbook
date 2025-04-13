package org.fufu.spellbook.character.domain

// TODO: add level to this. Sort order is not enough
data class SpellSlotLevel(
    val maxSlots: Int,
    val slots: Int
)

data class Character (
    val id: Int,
    val name: String,
    // spell id -> preparedness
    val spells: Map<Int, Boolean>,
    val characterClass: String,
    val subclass: String,
    val level: Int,
    val maxPreparedSpells: Int,
    val spellSlots: List<SpellSlotLevel>,
    val characterIcon: String
)

val defaultCharacter: Character = Character(
    id = 0,
    name = "",
    spells = emptyMap(),
    characterClass = "",
    subclass = "",
    level = 0,
    maxPreparedSpells = 0,
    spellSlots = emptyList(),
    characterIcon = "Icon1"
)

fun Character.setPreparedness(preparednesses: Map<Int, Boolean>) : Character {
    return this.copy(
        spells = spells.plus(preparednesses)
    )
}

fun Character.learnSpells(newSpells: Set<Int>) : Character {
    return this.copy(
        spells = spells.plus(newSpells.subtract(spells.keys).associateWith{false})
    )
}

fun Character.hasPreparedSpell(id: Int) : Boolean {
    return spells[id] ?: false
}

fun Character.knowsSpell(id: Int) : Boolean {
    return spells.containsKey(id)
}