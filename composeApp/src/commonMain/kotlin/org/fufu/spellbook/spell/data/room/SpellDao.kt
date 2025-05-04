package org.fufu.spellbook.spell.data.room

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import org.fufu.spellbook.spell.data.room.entities.SpellEntity

@Dao
interface SpellDao{
    @Upsert
    suspend fun upsertSpell(item: SpellEntity) : Long

    @Query("SELECT * FROM SpellEntity")
    fun getAllSpells(): Flow<List<SpellEntity>>

    @Query("SELECT * FROM SpellEntity where `key` in (:keys)")
    fun getAllSpells(keys: Set<Int>) : Flow<List<SpellEntity>>

    @Query("SELECT * FROM SpellEntity where `key`=:key")
    fun getSpell(key: Int) : Flow<SpellEntity?>
    
    @Query("DELETE FROM SpellEntity where `key`=:key")
    suspend fun deleteSpell(key: Int)
}