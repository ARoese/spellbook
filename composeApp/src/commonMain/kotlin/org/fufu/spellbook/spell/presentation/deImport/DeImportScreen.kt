package org.fufu.spellbook.spell.presentation.deImport

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.fufu.spellbook.composables.DropdownSelector
import org.fufu.spellbook.spell.domain.importNumFromSource

sealed interface Intent{
    data object DeImport : Intent
    data class SetSelectedImportKeys(val keys: Set<String>) : Intent
    data object Back : Intent
}

@Composable
fun DeImportScreenRoot(
    viewModel: DeImportScreenVM,
    onBack: () -> Unit
){
    val state by viewModel.state.collectAsStateWithLifecycle()

    DeImportScreen(state){
        when(it){
            Intent.Back -> onBack()
            Intent.DeImport -> viewModel.doDeImport()
            is Intent.SetSelectedImportKeys -> viewModel.setSelectedImportKeys(it.keys)
        }
    }
}

@Composable
fun DeImportScreen(
    state: DeImportScreenState,
    intend: (Intent) -> Unit
){
    Scaffold(
        topBar = {
            Box(modifier = Modifier.fillMaxWidth()){
                Row(modifier = Modifier.align(Alignment.CenterEnd)){
                    IconButton(
                        onClick = { intend(Intent.Back) },
                    ) {
                        Icon(Icons.Filled.Close, "Close")
                    }
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)){
            DeImportProcess(state, intend)
        }
    }
}

@Composable
fun DeImportProcess(
    state: DeImportScreenState,
    intend: (Intent) -> Unit
){
    withFullScreenLoading(state.spells){ spells ->
        Column {
            val selectorOptions = spells.flatMap { spell->
                spell.info.sources.filter { importNumFromSource(it) != null }
            }.toSet()
            DropdownSelector(
                options = selectorOptions.toList(),
                selected = state.selectedImportKeys,
                optionPresenter = { Text(it) },
                onOptionPicked = {
                    val newKeys = state.selectedImportKeys.plus(it)
                    intend(Intent.SetSelectedImportKeys(newKeys))
                },
                singleSelect = false,
            ){
                Text("Import to delete")
            }

            Button(
                onClick = {intend(Intent.DeImport)},
                enabled = state.selectedImportKeys.isNotEmpty() && !state.deImporting
            ){
                Text("De-Import")
            }
        }
    }
}