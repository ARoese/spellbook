package org.fufu.spellbook.settings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Regular
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.QuestionCircle
import org.fufu.spellbook.navigation.Route
import org.fufu.spellbook.spell.presentation.importScreen.Intent
import org.koin.compose.koinInject

@Composable
fun DarkModeSettingControl(){
    val datastore = koinInject<DataStore<Preferences>>()
    val darkMode by getPreferencesIsDarkMode(datastore)
        .collectAsState(DarkModePreference.SYSTEM)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Text("Theme")

        SingleChoiceSegmentedButtonRow {
            val choices = DarkModePreference.entries
            val n = choices.size
            choices.forEachIndexed { i, p ->
                SegmentedButton(
                    darkMode == p,
                    onClick = { setPreferencesIsDarkMode(datastore, p)},
                    shape = SegmentedButtonDefaults.itemShape(i, n),
                    label = {
                        Text(
                            p.name
                                .lowercase()
                                .replaceFirstChar { it.uppercase() }
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun SettingsScreen(
    onAbout: () -> Unit,
    navBar: @Composable () -> Unit
){
    Scaffold(
        topBar = {
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                IconButton(onClick = onAbout) {
                    Icon(FontAwesomeIcons.Solid.QuestionCircle, "Question Circle")
                }
            }
        },
        bottomBar = navBar
    ){ padding ->
        Box(
            modifier = Modifier.padding(padding)
        ){
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                DarkModeSettingControl()
            }
        }
    }
}