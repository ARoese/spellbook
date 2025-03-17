package org.fufu.spellbook

import androidx.room.Room
import androidx.room.RoomDatabase
import java.io.File

fun getDatabaseBuilder(): RoomDatabase.Builder<SpellBookDatabase> {
    val dbFile = File(System.getProperty("java.io.tmpdir"), DB_FILE_NAME)
    return Room.databaseBuilder<SpellBookDatabase>(
        name = dbFile.absolutePath,
    )
}