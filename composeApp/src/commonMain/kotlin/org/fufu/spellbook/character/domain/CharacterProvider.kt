package org.fufu.spellbook.character.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.fufu.spellbook.PreviewCharacters

interface CharacterProvider {
    fun getCharacters() : Flow<List<Character>>
    fun getCharacter(id: Int) : Flow<Character?>

}

interface CharacterMutator : CharacterProvider {
    suspend fun setCharacter(character: Character)
    suspend fun addCharacter(character: Character): Int
    suspend fun deleteCharacter(character: Character)
}

class MockCharacterProvider : CharacterProvider {
    private val mockCharacters : List<Character> = PreviewCharacters
    override fun getCharacters(): Flow<List<Character>> {
        return flowOf(mockCharacters)
    }

    override fun getCharacter(id: Int): Flow<Character?> {
        return flowOf( mockCharacters.find{it.id == id} )
    }
}