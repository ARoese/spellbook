package org.fufu.spellbook.settings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun DarkModeSettingControl(){
    val isDarkModePref = getPreferencesIsDarkMode()
    var darkMode by remember { mutableStateOf(isDarkModePref) }
    darkMode?.let { setPreferencesIsDarkMode(it) }
    isDarkModePref?.let { darkMode = isDarkModePref }

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
                    onClick = {darkMode = p},
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
    navBar: @Composable () -> Unit
){
    Scaffold(
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