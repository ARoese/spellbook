package org.fufu.spellbook

import androidx.room.AutoMigration
import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import org.fufu.spellbook.character.data.room.CharacterDao
import org.fufu.spellbook.character.data.room.entities.CharacterEntity
import org.fufu.spellbook.character.data.room.entities.CharacterPrepItemEntity
import org.fufu.spellbook.character.data.room.entities.CharacterPrepListEntity
import org.fufu.spellbook.character.data.room.entities.CharacterSpellEntity
import org.fufu.spellbook.character.data.room.entities.SpellSlotLevelEntity
import org.fufu.spellbook.spell.data.room.SpellDao
import org.fufu.spellbook.spell.data.room.entities.SpellEntity

const val DB_FILE_NAME = "SpellBook.db"

@Database(
    entities = [
        CharacterEntity::class,
        CharacterSpellEntity::class,
        SpellEntity::class,
        SpellSlotLevelEntity::class,
        CharacterPrepListEntity::class,
        CharacterPrepItemEntity::class
    ],
    version = 2,
    autoMigrations = [
        AutoMigration(from = 1, to = 2)
    ],
    exportSchema = true
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