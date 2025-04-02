package org.fufu.spellbook.character.data.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.SerializationException
import org.fufu.spellbook.character.domain.Character
import org.fufu.spellbook.character.domain.SpellSlotLevel

@Dao
interface CharacterDao {
    @Query("SELECT * FROM CharacterEntity")
    fun getCharacters() : Flow<List<CharacterEntity>>

    @Query("SELECT * FROM CharacterEntity WHERE `id` = :id")
    fun getCharacter(id : Int) : Flow<CharacterEntity?>

    @Query("SELECT * FROM SpellSlotLevelEntity WHERE characterId = :characterId")
    fun getCharacterLevels(characterId: Int) : Flow<List<SpellSlotLevelEntity>>

    @Upsert
    suspend fun setCharacter(character: CharacterEntity)

    @Delete
    suspend fun deleteCharacter(character: CharacterEntity)

    @Upsert
    suspend fun setCharacterLevel(level: SpellSlotLevelEntity)

    @Delete
    suspend fun deleteCharacterLevel(level: SpellSlotLevelEntity)
}

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

class Converters{
    @TypeConverter
    fun fromMap(m: Map<Int, Boolean>) : String {
        return m
            .map{ (id, prepared) ->
                "($id, $prepared)"
            }
            .joinToString(separator = "|")
    }

    @TypeConverter
    fun toMap(s: String) : Map<Int, Boolean>{
        if(s.isEmpty()){
            return mapOf()
        }
        val pairs = s
            .splitToSequence('|')
            .map{ pair ->
                val subElement = pair
                    .filter{ it != '(' && it != ')'}
                    .split(',')
                    .take(2)

                if(subElement.size != 2){
                    throw SerializationException("Invalid map: $s")
                }
                val (id, prepared) = subElement

                (id.toInt() to prepared.toBoolean())
            }

        return pairs.toMap()
    }
}

@Entity
@TypeConverters(Converters::class)
data class CharacterEntity (
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    // spell id -> preparedness
    val spells: Map<Int, Boolean>,
    val characterClass: String,
    val subclass: String,
    val level: Int,
    val maxPreparedSpells: Int,
    val characterIcon: String
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

fun CharacterEntity.fromEntity(spellSlots: List<SpellSlotLevel>) : Character {
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
        spells = spells,
        characterClass = characterClass,
        subclass = subclass,
        level = level,
        maxPreparedSpells = maxPreparedSpells,
        characterIcon = characterIcon
    )
}
