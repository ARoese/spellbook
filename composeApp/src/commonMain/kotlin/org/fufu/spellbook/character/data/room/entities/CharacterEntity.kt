package org.fufu.spellbook.character.data.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.fufu.spellbook.character.domain.Character
import org.fufu.spellbook.character.domain.SpellSlotLevel

@Entity
data class CharacterEntity (
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val characterClass: String,
    val subclass: String,
    val level: Int,
    val maxPreparedSpells: Int,
    val characterIcon: String,
)

fun CharacterEntity.fromEntity(
    spellSlots: Map<Int, SpellSlotLevel>,
    spells: Map<Int, Boolean>,
    spellPrepLists: Map<String, Set<Int>>
) : Character {
    return Character(
        id = id,
        name = name,
        spells = spells,
        characterClass = characterClass,
        subclass = subclass,
        level = level,
        maxPreparedSpells = maxPreparedSpells,
        spellSlots = spellSlots,
        characterIcon = characterIcon,
        preparedSpellLists = spellPrepLists
    )
}

fun Character.toEntity() : CharacterEntity {
    return CharacterEntity(
        id = id,
        name = name,
        characterClass = characterClass,
        subclass = subclass,
        level = level,
        maxPreparedSpells = maxPreparedSpells,
        characterIcon = characterIcon
    )
}