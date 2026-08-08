package org.fufu.spellbook.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.room3.Room
import androidx.room3.RoomDatabase
import org.fufu.spellbook.DB_FILE_NAME
import org.fufu.spellbook.PREFERENCES_FILE_NAME
import org.fufu.spellbook.SpellBookDatabase
import org.fufu.spellbook.createDataStore
import org.koin.android.ext.koin.androidApplication
import org.koin.core.module.Module
import org.koin.dsl.module

fun getDatabaseBuilder(ctx: Context): RoomDatabase.Builder<SpellBookDatabase> {
    val appContext = ctx.applicationContext
    val dbFile = appContext.getDatabasePath(DB_FILE_NAME)
    return Room.databaseBuilder<SpellBookDatabase>(
        context = appContext,
        name = dbFile.absolutePath
    )
}

fun createDataStore(context: Context): DataStore<Preferences> = createDataStore(
    producePath = { context.filesDir.resolve(PREFERENCES_FILE_NAME).absolutePath }
)

val databaseModule: Module = module {
    single { getDatabaseBuilder(androidApplication()) }
    single { createDataStore(androidApplication()) }
}