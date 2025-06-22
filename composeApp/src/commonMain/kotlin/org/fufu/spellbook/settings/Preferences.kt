package org.fufu.spellbook.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import org.koin.compose.koinInject

data object PreferencesKeys {
    val IS_DARK_MODE = booleanPreferencesKey("dark_mode")
}

enum class DarkModePreference{
    DARK,
    SYSTEM,
    LIGHT
}

@Composable
fun getPreferencesIsDarkMode(): DarkModePreference? {
    val dataStore = koinInject<DataStore<Preferences>>()
    return dataStore.data.map {
        when(it[PreferencesKeys.IS_DARK_MODE]){
            true -> DarkModePreference.DARK
            false -> DarkModePreference.LIGHT
            null -> DarkModePreference.SYSTEM
        }
    }.collectAsState(null).value
}

@Composable
fun setPreferencesIsDarkMode(newState: DarkModePreference) {
    val dataStore = koinInject<DataStore<Preferences>>()
    //TODO: make this properly async. This is fast enough for now, though
    runBlocking {
        dataStore.edit {
            val darkModeKey = PreferencesKeys.IS_DARK_MODE
            when(newState){
                DarkModePreference.DARK -> it[darkModeKey] = true
                DarkModePreference.SYSTEM -> it.remove(darkModeKey)
                DarkModePreference.LIGHT -> it[darkModeKey] = false
            }
        }
    }
}

data object PreferencesDefaults {
    val IS_DARK_MODE: Boolean? = null
}