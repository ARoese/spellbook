package org.fufu.spellbook.character.data.room.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import org.fufu.spellbook.character.domain.SpellSlotLevel

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = CharacterEntity::class,
            parentColumns = ["id"],
            childColumns = ["characterId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ],
    primaryKeys = ["characterId", "level"]
)
data class SpellSlotLevelEntity(
    val characterId: Int,
    val level : Int,
    val maxSlots: Int,
    val slots: Int
)

fun SpellSlotLevelEntity.fromEntity() : SpellSlotLevel {
    return SpellSlotLevel(
        maxSlots = maxSlots,
        slots = slots
    )
}

fun SpellSlotLevel.toEntity(characterId: Int, level: Int) : SpellSlotLevelEntity {
    return SpellSlotLevelEntity(
        characterId = characterId,
        level = level,
        maxSlots = maxSlots,
        slots = slots
    )
}
