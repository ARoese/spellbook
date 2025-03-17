package org.fufu.spellbook.data.room

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import org.fufu.spellbook.data.room.character.CharacterDao
import org.fufu.spellbook.data.room.character.CharacterEntity
import org.fufu.spellbook.data.room.character.SpellSlotLevelEntity
import org.fufu.spellbook.data.room.spell.SpellDao
import org.fufu.spellbook.data.room.spell.SpellEntity

const val DB_FILE_NAME = "SpellBook.db"

@Database(
    entities = [
        CharacterEntity::class,
        SpellEntity::class,
        SpellSlotLevelEntity::class
    ],
    version = 1
)
@ConstructedBy(SpellBookDatabaseConstructor::class)
abstract class SpellBookDatabase : RoomDatabase() {
    abstract fun getSpellDao(): SpellDao
    abstract fun getCharacterDao(): CharacterDao
}

@Suppress("NO_ACTUAL_FOR_EXPECT") // compiler-provided
expect object SpellBookDatabaseConstructor : RoomDatabaseConstructor<SpellBookDatabase> {
    override fun initialize(): SpellBookDatabase
}