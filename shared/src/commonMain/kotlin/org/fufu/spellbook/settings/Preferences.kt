package org.fufu.spellbook.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import org.koin.compose.koinInject

data object PreferencesKeys {
    val IS_DARK_MODE = booleanPreferencesKey("dark_mode")
    val USE_SPELL_BADGES = booleanPreferencesKey("use_spell_badges")
}

enum class DarkModePreference{
    DARK,
    SYSTEM,
    LIGHT
}


fun getPreferencesIsDarkMode(datastore: DataStore<Preferences>): Flow<DarkModePreference> {
    return datastore.data.map {
        when(it[PreferencesKeys.IS_DARK_MODE]){
            true -> DarkModePreference.DARK
            false -> DarkModePreference.LIGHT
            null -> DarkModePreference.SYSTEM
        }
    }
}

fun setPreferencesIsDarkMode(datastore: DataStore<Preferences>, newState: DarkModePreference) {
    //TODO: make this properly async. This is fast enough for now, though
    runBlocking {
        datastore.edit {
            val darkModeKey = PreferencesKeys.IS_DARK_MODE
            when(newState){
                DarkModePreference.DARK -> it[darkModeKey] = true
                DarkModePreference.SYSTEM -> it.remove(darkModeKey)
                DarkModePreference.LIGHT -> it[darkModeKey] = false
            }
        }
    }
}

fun getPreferencesUseSpellBadge(datastore: DataStore<Preferences>): Flow<Boolean> {
    return datastore.data.map {
        it[PreferencesKeys.USE_SPELL_BADGES] ?: false
    }
}

fun setPreferencesUseSpellBadge(datastore: DataStore<Preferences>, newState: Boolean) {
    //TODO: make this properly async. This is fast enough for now, though
    runBlocking {
        datastore.edit {
            it[PreferencesKeys.USE_SPELL_BADGES] = newState
        }
    }
}
