package org.fufu.spellbook.character.data.room.entities

import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.Index
import androidx.room3.PrimaryKey
import org.fufu.spellbook.spell.data.room.entities.SpellEntity


@Entity(foreignKeys = [
    ForeignKey(
        entity = CharacterEntity::class,
        parentColumns = ["id"],
        childColumns = ["characterId"],
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE
    )
],
    indices = [Index("characterId")]
)
data class CharacterPrepListEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val characterId: Int,
    val name: String
)

@Entity(foreignKeys = [
    ForeignKey(
        entity = CharacterPrepListEntity::class,
        parentColumns = ["id"],
        childColumns = ["prepListId"],
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
    primaryKeys = ["prepListId", "spellId"],
    indices = [Index("spellId")]
)
data class CharacterPrepItemEntity(
    val prepListId: Int,
    val spellId: Int
)