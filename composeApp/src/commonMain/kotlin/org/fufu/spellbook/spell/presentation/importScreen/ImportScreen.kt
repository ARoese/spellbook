package org.fufu.spellbook.spell.presentation.importScreen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.vinceglb.filekit.dialogs.FileKitMode
import io.github.vinceglb.filekit.dialogs.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.name
import org.fufu.spellbook.composables.DropdownSelector
import org.fufu.spellbook.navigation.Route
import org.fufu.spellbook.spell.domain.ImportPolicy

@Composable
fun ImportScreenRoot(
    viewModel: ImportScreenVM,
    navigateTo: (Route) -> Unit,
    navBar: @Composable () -> Unit
){
    val state by viewModel.state.collectAsStateWithLifecycle()

    ImportScreen(
        state = state,
        navBar = navBar
    ){
        when(it){
            is Intent.ChangeSource -> viewModel.onChangeSource(it.newSource)
            is Intent.DoImport -> viewModel.doImport(it.ids)
            is Intent.NavigateTo -> navigateTo(it.route)
        }
    }
}

sealed interface Intent{
    data class DoImport(val ids: Set<Int>?): Intent
    data class NavigateTo(val route: Route): Intent
    data class ChangeSource(val newSource: ImportSource): Intent
}

@Composable
fun ImportScreen(
    state: ImportScreenState,
    navBar: @Composable () -> Unit,
    intend: (Intent) -> Unit
){
    Scaffold(
        topBar = {
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd){
                ImportOptionsDropDown(intend)
            }
        },
        bottomBar = navBar
    ){ padding ->
        Box(modifier=Modifier.padding(padding)){
            Column {
                EditableImportSource(
                    state.importSource, intend
                )
                if(state.importSource is ImportSource.SELECT){
                    return@Box
                }
                if(state.importing){
                    CircularProgressIndicator(
                        progress = { state.importProgress },
                    )
                    Text("Importing...")
                    return@Box
                }

                state.currentSpells.combine(state.availableSpells).map(
                    ifNotLoaded = {CircularProgressIndicator()}
                ){ (currentSpells, availableSpells) ->
                    Text("There are ${availableSpells.size} spells available to import")
                    val uniqueSpells = ImportPolicy(matchByName = true)
                        .filterShouldImport(currentSpells, availableSpells)
                        .map { it.key }
                    Text("Only the ${uniqueSpells.size} unique spells will be imported")
                    if(uniqueSpells.isNotEmpty()){
                        Button(onClick = {intend(Intent.DoImport(uniqueSpells.toSet()))}){
                            Text("Import!")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EditableJsonImportSource(
    source: ImportSource.JSON,
    intend: (Intent) -> Unit
){
    Box{
        val launcher = rememberFilePickerLauncher(
            mode = FileKitMode.Single
        ) { file ->
            intend(Intent.ChangeSource(ImportSource.JSON(file)))
        }
        Row{
            Button(
                onClick = { launcher.launch() },
                modifier = Modifier
            ){
                Text(source.file?.name ?: "Choose a file")
            }
        }
    }
}

@Composable
fun SRD5eImportSource(){
    Text("Import from the 5e SRD")
}

@Composable
fun EditableImportSource(
    source: ImportSource,
    intend: (Intent) -> Unit
){
    Column {
        fun present(src: ImportSource) : String {
            return when(src){
                is ImportSource.JSON -> "json"
                ImportSource.SELECT -> "Select import source"
                ImportSource.WIKIDOT -> "wikidot"
                ImportSource.SRD5E -> "5e 2014 SRD"
            }
        }
        DropdownSelector(
            options = listOf(
                ImportSource.JSON(null),
                ImportSource.WIKIDOT,
                ImportSource.SRD5E
            ),
            selected = emptySet(),
            singleSelect = true,
            optionPresenter = { Text(present(it)) },
            buttonContent = { Text(present(source)) },
            onOptionPicked = { intend(Intent.ChangeSource(it)) }
        )
        //ImportSourceDropDown(source, onChangeSource)
        when(source){
            is ImportSource.SELECT -> {}
            is ImportSource.JSON -> EditableJsonImportSource(source, intend)
            is ImportSource.WIKIDOT -> {
                Text("This is not yet implemented")
            }
            is ImportSource.SRD5E -> SRD5eImportSource()
        }
    }
}

@Composable
fun ImportOptionsDropDown(
    intend: (Intent) -> Unit
){
    var expanded by remember { mutableStateOf(false) }
    Box(
        modifier = Modifier
            .padding(4.dp)
    ) {
        IconButton(onClick = { expanded = !expanded }) {
            Icon(Icons.Default.MoreVert, contentDescription = "More options")
        }
        DropdownMenu(
            expanded,
            onDismissRequest = { expanded = false }
        ){
            DropdownMenuItem(
                text = { Text("De-Import") },
                onClick = {
                    expanded = false
                    intend(Intent.NavigateTo(Route.DeImportScreen))
                }
            )
        }
    }
}