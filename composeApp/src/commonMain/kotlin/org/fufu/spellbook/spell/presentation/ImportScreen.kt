package org.fufu.spellbook.spell.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.vinceglb.filekit.dialogs.FileKitMode
import io.github.vinceglb.filekit.dialogs.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.name
import org.fufu.spellbook.composables.DropdownSelector

@Composable
fun EditableJsonImportSource(
    source: ImportSource.JSON,
    onChangeSource: (ImportSource) -> Unit
){
    Box{
        val launcher = rememberFilePickerLauncher(
            mode = FileKitMode.Single
        ) { file ->
            onChangeSource(ImportSource.JSON(file))
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
fun EditableImportSource(
    source: ImportSource,
    onChangeSource: (ImportSource) -> Unit
){
    Column {
        fun present(src: ImportSource) : String {
            return when(src){
                is ImportSource.JSON -> "json"
                ImportSource.SELECT -> "SELECT"
                ImportSource.WIKIDOT -> "wikidot"
            }
        }
        DropdownSelector(
            options = listOf(
                ImportSource.JSON(null),
                ImportSource.WIKIDOT,
            ),
            selected = emptySet(),
            singleSelect = true,
            optionPresenter = { Text(present(it)) },
            buttonContent = { Text(present(source)) },
            onOptionPicked = { onChangeSource(it) }
        )
        //ImportSourceDropDown(source, onChangeSource)
        when(source){
            is ImportSource.SELECT -> {}
            is ImportSource.JSON -> EditableJsonImportSource(source, onChangeSource)
            is ImportSource.WIKIDOT -> {
                Text("This is not yet implemented")
            }
        }
    }
}

@Composable
fun ImportScreenRoot(
    viewModel: ImportScreenVM,
    navBar: @Composable () -> Unit
){
    val state by viewModel.state.collectAsStateWithLifecycle()

    ImportScreen(
        state = state,
        onChangeSource = {viewModel.onChangeSource(it)},
        onDoImport = {viewModel.doImport()},
        navBar = navBar
    )
}

@Composable
fun ImportScreen(
    state: ImportScreenState,
    onChangeSource: (ImportSource) -> Unit = {},
    onDoImport: () -> Unit = {},
    navBar: @Composable () -> Unit
){
    Scaffold(
        bottomBar = navBar
    ){ padding ->
        Box(modifier=Modifier.padding(padding)){
            Column {
                EditableImportSource(
                    state.importSource,
                    onChangeSource = onChangeSource
                )
                if(state.importSource is ImportSource.SELECT){
                    return@Box
                }

                if(state.loading){
                    CircularProgressIndicator()
                }else if(state.importing){
                    CircularProgressIndicator(
                        progress = { state.importProgress },
                    )
                    Text("Importing...")
                }else{
                    Text("There are ${state.availableSpells.size} spells available to import")
                    if(state.availableSpells.isNotEmpty()){
                        Button(onClick = onDoImport){
                            Text("Import!")
                        }
                    }
                }
            }
        }
    }
}