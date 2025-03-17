package org.fufu.spellbook.data.room.character

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.map
import org.fufu.spellbook.domain.Character
import org.fufu.spellbook.domain.CharacterMutator
import org.fufu.spellbook.domain.CharacterProvider

open class DBCharacterProvider(
    protected val characterDao: CharacterDao
) : CharacterProvider {
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun GetCharacters(): Flow<List<Character>> {
        return characterDao.getCharacters()
            .flatMapConcat { characters ->
                val characterFlows = characters.map { character ->
                    characterDao
                        .getCharacterLevels(character.id)
                        .map{ levels ->
                            levels
                                .sortedBy { it.level }
                                .map { it.fromEntity() }
                        }
                        .map{ levels ->
                            character.fromEntity(levels)
                        }
                }
                combine(characterFlows){it.toList()}
            }
    }

    override fun GetCharacter(id: Int): Flow<Character?> {
        val characterFlow = characterDao.getCharacter(id)
        val spellSlotLevelFlow = characterDao.getCharacterLevels(id)
        return characterFlow.combine(spellSlotLevelFlow){ character, levels ->
            val sortedLevels = levels
                .sortedBy { it.level }
                .map { it.fromEntity() }
            character?.fromEntity(sortedLevels)
        }
    }
}

class DBCharacterMutator(
    characterDao: CharacterDao
) : DBCharacterProvider(characterDao), CharacterMutator{
    override suspend fun SetCharacter(character: Character) {
        characterDao.setCharacter(character.toEntity())
    }

    override suspend fun AddCharacter(character: Character) {
        characterDao.setCharacter(character.copy(id = 0).toEntity())
    }

    override suspend fun DeleteCharacter(character: Character) {
        characterDao.deleteCharacter(character.toEntity())
    }

}