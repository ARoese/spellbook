package org.fufu.spellbook.character.data.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.fufu.spellbook.character.domain.SpellSlotLevel
import org.fufu.spellbook.character.domain.Character

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

fun CharacterEntity.fromEntity(spellSlots: Map<Int, SpellSlotLevel>, spells: Map<Int, Boolean>) : Character {
    return Character(
        id = id,
        name = name,
        spells = spells,
        characterClass = characterClass,
        subclass = subclass,
        level = level,
        maxPreparedSpells = maxPreparedSpells,
        spellSlots = spellSlots,
        characterIcon = characterIcon
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