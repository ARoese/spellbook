package org.fufu.spellbook.character.data.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import org.fufu.spellbook.spell.data.room.entities.SpellEntity

@Entity(foreignKeys = [
    ForeignKey(
        entity = CharacterEntity::class,
        parentColumns = ["id"],
        childColumns = ["characterId"],
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE
    ),
    ForeignKey(
        entity = SpellEntity::class,
        parentColumns = ["key"],
        childColumns = ["spellId"],
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE
    )
],
    primaryKeys = ["characterId", "spellId"]
)
data class CharacterSpellEntity(
    val characterId: Int,
    @ColumnInfo(index = true)
    val spellId: Int,
    val prepared: Boolean
)