package org.fufu.spellbook.character.data.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.MapColumn
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import org.fufu.spellbook.character.data.room.entities.CharacterEntity
import org.fufu.spellbook.character.data.room.entities.CharacterPrepItemEntity
import org.fufu.spellbook.character.data.room.entities.CharacterPrepListEntity
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

    @Query("DELETE FROM CharacterPrepListEntity WHERE `characterId` = :id")
    suspend fun clearCharacterPrepLists(id: Int)

    @Insert
    suspend fun addCharacterPrepList(prepList: CharacterPrepListEntity): Long

    @Insert
    suspend fun addCharacterPrepListItem(item: CharacterPrepItemEntity)

    @Transaction
    suspend fun setCharacterPrepSets(id: Int, lists: Map<String, Set<Int>>){
        clearCharacterPrepLists(id)
        lists.forEach{
            val newId = addCharacterPrepList(
                CharacterPrepListEntity(0, id, it.key)
            ).toInt()
            it.value.forEach { spellId ->
                addCharacterPrepListItem(
                    CharacterPrepItemEntity(newId, spellId)
                )
            }
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
        spells: List<CharacterSpellEntity>,
        characterPrepSets: Map<String, Set<Int>>
    ){
        upsertCharacter(character)
        setCharacterSpellSlots(character.id, spellSlotEntities)
        setCharacterSpells(character.id, spells)
        setCharacterPrepSets(character.id, characterPrepSets)
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
        val characterPrepLists = getCharacterPrepListMap(characterId)
        return combine(
            characterFlow,
            characterSpells,
            characterSpellSlots,
            characterPrepLists,
        ) { character, spells, spellSlots, prepLists ->
            character?.let {
                val spellsMap = spells.associate { it.spellId to it.prepared }
                val spellSlotsMap = spellSlots.associate{ it.level to it.fromEntity() }
                character.fromEntity(spellSlotsMap, spellsMap, spellPrepLists = prepLists)
            }
        }
    }

    @Query("SELECT name, spellId FROM CharacterPrepListEntity " +
            "JOIN CharacterPrepItemEntity " +
            "ON CharacterPrepListEntity.id = CharacterPrepItemEntity.prepListId " +
            "WHERE characterId = :characterID")
    fun getCharacterPrepListMap(characterID: Int):
            Flow<Map<
                @MapColumn(columnName="name") String,
                Set<@MapColumn(columnName="spellId") Int>
            >>

    @Upsert
    suspend fun upsertCharacter(character: CharacterEntity): Long

    @Delete
    suspend fun deleteCharacter(character: CharacterEntity)

    @Upsert
    suspend fun setCharacterLevel(level: SpellSlotLevelEntity)

    @Delete
    suspend fun deleteCharacterLevel(level: SpellSlotLevelEntity)
}