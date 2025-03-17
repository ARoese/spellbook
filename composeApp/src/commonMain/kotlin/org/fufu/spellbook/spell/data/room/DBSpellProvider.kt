package org.fufu.spellbook.spell.data.room

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.fufu.spellbook.spell.domain.Spell
import org.fufu.spellbook.spell.domain.SpellInfo
import org.fufu.spellbook.spell.domain.SpellMutator
import org.fufu.spellbook.spell.domain.SpellProvider

open class DBSpellProvider(protected val spellDao: SpellDao) : SpellProvider {
    override fun getSpells(): Flow<List<Spell>> {
        return spellDao.getAllSpells()
            .map{spells ->
                spells.map{ it.toSpell() }
            }
    }

    override fun getSpells(ids: Set<Int>): Flow<List<Spell>> {
        return spellDao.getAllSpells(ids)
            .map{spells ->
                spells.map{ it.toSpell() }
            }
    }

    override fun getSpell(id: Int): Flow<Spell?> {
        return spellDao.getSpell(id).map{it?.toSpell()}
    }
}

class DBSpellMutator(spellDao: SpellDao) : DBSpellProvider(spellDao), SpellMutator {
    override suspend fun setSpell(spell: Spell) {
        spellDao.upsertSpell(spell.toEntity())
    }

    override suspend fun addSpell(spell: SpellInfo) : Int {
        return spellDao.upsertSpell(Spell(key = 0, info = spell).toEntity()).toInt()
    }

    override suspend fun deleteSpell(spell: Spell) {
        spellDao.deleteSpell(spell.toEntity())
    }

}