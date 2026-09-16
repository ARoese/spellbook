package org.fufu.spellbook.settings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.FlowRowOverflow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.QuestionCircle
import org.fufu.spellbook.spell.presentation.MaterialCost
import org.fufu.spellbook.spell.presentation.ParsedComponents
import org.fufu.spellbook.spell.presentation.SpellBadge
import org.koin.compose.koinInject

@Composable
fun SettingsControls(){
    val datastore = koinInject<DataStore<Preferences>>()
    val darkMode by getPreferencesIsDarkMode(datastore)
        .collectAsState(DarkModePreference.SYSTEM)
    val useBadges by getPreferencesUseSpellBadge(datastore)
        .collectAsState(false)
    Column(
        Modifier.verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ){
        Text("Theme", style=MaterialTheme.typography.titleLarge)
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

        Text("Spell Badges", style=MaterialTheme.typography.titleLarge)
        Text(
            "Spell badges appear in spell lists to tell you what a spell's contextual requirements for casting are at a glance." +
                    " It displays whether the spell needs Verbal, Somatic, or Material components," +
                    " what the monetary cost of those components are, and whether the spell can be cast as a ritual.", style = MaterialTheme.typography.bodySmall)
        FlowRow(maxLines = 1) {
            SpellBadge(
                ParsedComponents(true, true, true, MaterialCost.NONE),
                true,
                Modifier.width(60.dp)
            )
            SpellBadge(
                ParsedComponents(true, true, true, MaterialCost.GOLD),
                false,
                Modifier.width(60.dp)
            )
            SpellBadge(
                ParsedComponents(true, false, true, MaterialCost.SILVER),
                true,
                Modifier.width(60.dp)
            )
            SpellBadge(
                ParsedComponents(false, true, true, MaterialCost.COPPER),
                false,
                Modifier.width(60.dp)
            )
            SpellBadge(
                ParsedComponents(false, true, false, MaterialCost.NONE),
                true,
                Modifier.width(60.dp)
            )
        }

        Switch(useBadges, onCheckedChange = { setPreferencesUseSpellBadge(datastore, it)})
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
                SettingsControls()
            }
        }
    }
}