package org.fufu.spellbook

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import org.fufu.spellbook.data.room.DB_FILE_NAME
import org.fufu.spellbook.data.room.SpellBookDatabase

fun getDatabaseBuilder(ctx: Context): RoomDatabase.Builder<SpellBookDatabase> {
    val appContext = ctx.applicationContext
    val dbFile = appContext.getDatabasePath(DB_FILE_NAME)
    return Room.databaseBuilder<SpellBookDatabase>(
        context = appContext,
        name = dbFile.absolutePath
    )
}