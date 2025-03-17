package org.fufu.spellbook

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import org.fufu.spellbook.character.data.room.CharacterDao
import org.fufu.spellbook.character.data.room.CharacterEntity
import org.fufu.spellbook.character.data.room.SpellSlotLevelEntity
import org.fufu.spellbook.spell.data.room.SpellDao
import org.fufu.spellbook.spell.data.room.SpellEntity

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