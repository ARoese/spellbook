package org.fufu.spellbook.android

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import org.fufu.spellbook.DB_FILE_NAME
import org.fufu.spellbook.SpellBookDatabase

fun getDatabaseBuilder(ctx: Context): RoomDatabase.Builder<SpellBookDatabase> {
    val appContext = ctx.applicationContext
    val dbFile = appContext.getDatabasePath(DB_FILE_NAME)
    return Room.databaseBuilder<SpellBookDatabase>(
        context = appContext,
        name = dbFile.absolutePath
    )
}