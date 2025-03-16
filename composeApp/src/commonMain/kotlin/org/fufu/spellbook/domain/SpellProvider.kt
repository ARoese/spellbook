package org.fufu.spellbook.domain

interface SpellProvider {
    suspend fun getSpells() : List<Spell>
    suspend fun getSpell(id: Int) : Spell?
}

class MockSpellProvider : SpellProvider {
    private val mockSpells = PreviewSpells
    override suspend fun getSpells() : List<Spell> {
        return mockSpells.toList()
    }

    override suspend fun getSpell(id: Int) : Spell? {
        return mockSpells.find{
            it.key == id
        }
    }
}