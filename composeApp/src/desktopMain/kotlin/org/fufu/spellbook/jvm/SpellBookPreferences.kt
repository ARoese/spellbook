package org.fufu.spellbook.jvm

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.absolutePath
import io.github.vinceglb.filekit.filesDir
import org.fufu.spellbook.PREFERENCES_FILE_NAME
import java.io.File

fun createDataStore(): DataStore<Preferences> = org.fufu.spellbook.createDataStore(
    producePath = {
        val file = File(FileKit.filesDir.absolutePath(), PREFERENCES_FILE_NAME)
        file.absolutePath
    }
)