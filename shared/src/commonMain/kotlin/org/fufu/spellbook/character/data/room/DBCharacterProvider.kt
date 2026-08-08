package org.fufu.spellbook.character.data.room

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import org.fufu.spellbook.character.data.room.entities.CharacterSpellEntity
import org.fufu.spellbook.character.data.room.entities.toEntity
import org.fufu.spellbook.character.domain.Character
import org.fufu.spellbook.character.domain.CharacterMutator
import org.fufu.spellbook.character.domain.CharacterProvider

open class DBCharacterProvider(
    protected val characterDao: CharacterDao
) : CharacterProvider {
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getCharacters(): Flow<List<Character>> {
        val characterIdsFlow = characterDao.getCharacters().map { it.map { it.id } }
        val charactersFlow = characterIdsFlow.map { characterIds ->
            characterIds.map { id ->
                getCharacter(id=id)
            }
        }
        return charactersFlow.flatMapLatest {
            combine(it){ characters ->
                characters.toList().filterNotNull()
            }
        }
    }

    override fun getCharacter(id: Int): Flow<Character?> {
        return characterDao.getCharacterWithSpells(id)
    }
}

class DBCharacterMutator(
    characterDao: CharacterDao
) : DBCharacterProvider(characterDao), CharacterMutator {
    override suspend fun setCharacter(character: Character) {
        require(character.id >= 0){"cannot create character here"}
        val spellEntities = character.spells.map {
            CharacterSpellEntity(character.id, it.key, it.value)
        }
        val spellSlotEntities = character.spellSlots.map {
            it.value.toEntity(character.id, it.key)
        }
        characterDao.setCharacter(
            character.toEntity(),
            spellSlotEntities,
            spellEntities,
            character.preparedSpellLists
        )
    }

    override suspend fun addCharacter(character: Character): Int {
        return characterDao.upsertCharacter(character.copy(id = 0).toEntity()).toInt()
    }

    override suspend fun deleteCharacter(character: Character) {
        characterDao.deleteCharacter(character.toEntity())
    }

}