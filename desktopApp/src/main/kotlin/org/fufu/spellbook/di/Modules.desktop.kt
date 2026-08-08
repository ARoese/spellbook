package org.fufu.spellbook.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.room3.Room
import androidx.room3.RoomDatabase
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.absolutePath
import io.github.vinceglb.filekit.databasesDir
import io.github.vinceglb.filekit.filesDir
import org.fufu.spellbook.DB_FILE_NAME
import org.fufu.spellbook.PREFERENCES_FILE_NAME
import org.fufu.spellbook.SpellBookDatabase
import org.fufu.spellbook.createDataStore
import org.koin.core.module.Module
import org.koin.dsl.module
import java.io.File

fun getDatabaseBuilder(): RoomDatabase.Builder<SpellBookDatabase> {
    val dbFile = File(FileKit.databasesDir.absolutePath(), DB_FILE_NAME)
    return Room.databaseBuilder<SpellBookDatabase>(
        name = dbFile.absolutePath,
    )
}

fun createDataStore(): DataStore<Preferences> = createDataStore(
    producePath = {
        val file = File(FileKit.filesDir.absolutePath(), PREFERENCES_FILE_NAME)
        file.absolutePath
    }
)

val databaseModule: Module = module {
    single { getDatabaseBuilder() }
    single { createDataStore() }
}