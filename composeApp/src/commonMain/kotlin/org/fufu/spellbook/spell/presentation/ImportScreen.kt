package org.fufu.spellbook.spell.presentation

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.darkrockstudios.libraries.mpfilepicker.FilePicker

@Composable
fun ImportSourceDropDown(
    selected: ImportSource,
    onChange: (ImportSource) -> Unit
){
    Box(modifier = Modifier.fillMaxWidth()){
        var expanded by remember { mutableStateOf(false) }
        IconButton(
            onClick = {expanded = true},
            modifier = Modifier.border(
                Dp.Hairline,
                Color.Black,
                RoundedCornerShape(6.dp)
            ).width(150.dp)
        ){
            val text = when(selected){
                is ImportSource.JSON -> "Json"
                ImportSource.SELECT -> "SELECT"
                ImportSource.WIKIDOT -> "Wikidot"
            }
            Text(text)
        }
        DropdownMenu(expanded = expanded, onDismissRequest = {expanded = false}) {
            DropdownMenuItem(
                text={Text("Json")},
                onClick= {
                    onChange(ImportSource.JSON(null))
                    expanded = false
                }
            )
            DropdownMenuItem(
                text={Text("Wikidot")},
                onClick= {
                    onChange(ImportSource.WIKIDOT)
                    expanded = false
                }
            )
        }
    }

}

@Composable
fun EditableJsonImportSource(
    source: ImportSource.JSON,
    onChangeSource: (ImportSource) -> Unit
){
    Box{
        var showPicker by remember { mutableStateOf(false) }
        Row{
            Button(
                onClick = { showPicker = true },
                modifier = Modifier
            ){
                Text("Choose a file")
            }
            if(source.path != null){
                Text(source.path)
            }

        }
        FilePicker(
            show = showPicker
        ) { path ->
            showPicker = false
            path?.path?.let { onChangeSource(ImportSource.JSON(it)) }
        }
    }
}

@Composable
fun EditableImportSource(
    source: ImportSource,
    onChangeSource: (ImportSource) -> Unit
){
    Column {
        ImportSourceDropDown(source, onChangeSource)
        when(source){
            is ImportSource.SELECT -> {}
            is ImportSource.JSON -> EditableJsonImportSource(source, onChangeSource)
            is ImportSource.WIKIDOT -> {}
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
        navBar = navBar
    )
}

@Composable
fun ImportScreen(
    state: ImportScreenState,
    onChangeSource: (ImportSource) -> Unit = {},
    navBar: @Composable () -> Unit
){
    Scaffold(
        bottomBar = navBar
    ){ padding ->
        Box(modifier=Modifier.padding(padding)){
            EditableImportSource(
                state.importSource,
                onChangeSource = onChangeSource
            )
        }

    }
}