package org.fufu.spellbook.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

interface CharacterProvider {
    fun GetCharacters() : Flow<List<Character>>
    fun GetCharacter(id: Int) : Flow<Character?>
}

interface CharacterMutator : CharacterProvider {
    suspend fun SetCharacter(character: Character)
    suspend fun AddCharacter(character: Character)
    suspend fun DeleteCharacter(character: Character)
}

class MockCharacterProvider : CharacterProvider {
    private val MockCharacters : List<Character> = PreviewCharacters
    override fun GetCharacters(): Flow<List<Character>> {
        return flowOf(MockCharacters)
    }

    override fun GetCharacter(id: Int): Flow<Character?> {
        return flowOf( MockCharacters.find{it.id == id} )
    }
}