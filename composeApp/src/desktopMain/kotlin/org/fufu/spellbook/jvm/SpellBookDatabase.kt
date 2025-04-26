package org.fufu.spellbook.jvm

import androidx.room.Room
import androidx.room.RoomDatabase
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.absolutePath
import io.github.vinceglb.filekit.databasesDir
import org.fufu.spellbook.DB_FILE_NAME
import org.fufu.spellbook.SpellBookDatabase
import java.io.File

fun getDatabaseBuilder(): RoomDatabase.Builder<SpellBookDatabase> {
    val dbFile = File(FileKit.databasesDir.absolutePath(), DB_FILE_NAME)
    return Room.databaseBuilder<SpellBookDatabase>(
        name = dbFile.absolutePath,
    )
}