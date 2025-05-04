package org.fufu.spellbook.character.data.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import org.fufu.spellbook.character.data.room.entities.CharacterEntity
import org.fufu.spellbook.character.data.room.entities.CharacterSpellEntity
import org.fufu.spellbook.character.data.room.entities.SpellSlotLevelEntity
import org.fufu.spellbook.character.data.room.entities.fromEntity
import org.fufu.spellbook.character.domain.Character

@Dao
interface CharacterDao {
    @Query("SELECT * FROM CharacterEntity")
    fun getCharacters() : Flow<List<CharacterEntity>>

    @Query("SELECT * FROM CharacterEntity WHERE `id` = :id")
    fun getCharacter(id : Int) : Flow<CharacterEntity?>

    @Query("SELECT * FROM CharacterSpellEntity WHERE `characterId` = :id")
    fun getCharacterSpells(id: Int) : Flow<List<CharacterSpellEntity>>

    @Query("DELETE FROM CharacterSpellEntity WHERE `characterId` = :id")
    suspend fun clearCharacterSpells(id: Int)

    @Upsert
    suspend fun addCharacterSpell(characterSpell: CharacterSpellEntity)

    @Transaction
    suspend fun setCharacterSpells(id: Int, spells: List<CharacterSpellEntity>){
        clearCharacterSpells(id)
        spells.forEach{
            require(id == it.characterId){
                "character id != entity character id ($id != ${it.characterId})"
            }
            addCharacterSpell(it)
        }
    }

    @Query("DELETE FROM SpellSlotLEvelEntity WHERE characterId = :characterId")
    suspend fun clearCharacterSpellSlots(characterId: Int)

    @Insert
    suspend fun addCharacterSpellSlot(spellSlotLevel: SpellSlotLevelEntity)

    @Transaction
    suspend fun setCharacter(
        character: CharacterEntity,
        spellSlotEntities: List<SpellSlotLevelEntity>,
        spells: List<CharacterSpellEntity>
    ){
        upsertCharacter(character)
        setCharacterSpellSlots(character.id, spellSlotEntities)
        setCharacterSpells(character.id, spells)
    }

    @Transaction
    suspend fun setCharacterSpellSlots(characterId: Int, spellSlots: List<SpellSlotLevelEntity>){
        clearCharacterSpellSlots(characterId)
        spellSlots.forEach{
            require(characterId == it.characterId){
                "character id != entity character id ($characterId != ${it.characterId})"
            }
            addCharacterSpellSlot(it)
        }
    }

    @Query("SELECT * FROM SpellSlotLevelEntity WHERE characterId = :characterId")
    fun getCharacterSpellSlots(characterId: Int) : Flow<List<SpellSlotLevelEntity>>

    fun getCharacterWithSpells(characterId: Int): Flow<Character?> {
        val characterFlow = getCharacter(characterId)
        val characterSpells = getCharacterSpells(characterId)
        val characterSpellSlots = getCharacterSpellSlots(characterId)
        return combine(
            characterFlow,
            characterSpells,
            characterSpellSlots
        ) { character, spells, spellSlots ->
            character?.let {
                val spellsMap = spells.associate { it.spellId to it.prepared }
                val spellSlotsMap = spellSlots.associate{ it.level to it.fromEntity() }
                character.fromEntity(spellSlotsMap, spellsMap)
            }
        }
    }

    @Upsert
    suspend fun upsertCharacter(character: CharacterEntity): Long

    @Delete
    suspend fun deleteCharacter(character: CharacterEntity)

    @Upsert
    suspend fun setCharacterLevel(level: SpellSlotLevelEntity)

    @Delete
    suspend fun deleteCharacterLevel(level: SpellSlotLevelEntity)
}