package org.fufu.spellbook.domain

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
    val spellSlots: List<SpellSlotLevel>
)

fun Character.hasPreparedSpell(id: Int) : Boolean{
    return spells.get(id) ?: false
}