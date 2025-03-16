package org.fufu.spellbook.domain

interface CharacterProvider {
    suspend fun GetCharacters() : List<Character>
    suspend fun GetCharacter(id: Int) : Character?
}

class MockCharacterProvider : CharacterProvider {
    private val MockCharacters : List<Character> = PreviewCharacters
    override suspend fun GetCharacters(): List<Character> {
        return MockCharacters
    }

    override suspend fun GetCharacter(id: Int): Character? {
        return MockCharacters.find{it.id == id}
    }
}