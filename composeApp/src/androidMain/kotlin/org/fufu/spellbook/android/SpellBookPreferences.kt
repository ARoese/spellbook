package org.fufu.spellbook.android

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import org.fufu.spellbook.PREFERENCES_FILE_NAME

fun createDataStore(context: Context): DataStore<Preferences> = org.fufu.spellbook.createDataStore(
    producePath = { context.filesDir.resolve(PREFERENCES_FILE_NAME).absolutePath }
)