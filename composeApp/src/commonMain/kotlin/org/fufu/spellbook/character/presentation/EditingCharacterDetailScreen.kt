package org.fufu.spellbook.character.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.fufu.spellbook.character.domain.Character
import org.fufu.spellbook.character.domain.CharacterIcon
import org.fufu.spellbook.composables.DropdownSelector

@Composable
fun EditingCharacterDetailScreenRoot(
    viewModel: EditingCharacterDetailVM,
    onBack: () -> Unit = {}
){
    val state by viewModel.state.collectAsStateWithLifecycle()
    EditingCharacterDetailScreen(
        state,
        onBack = onBack,
        onCharacterChanged = {viewModel.onCharacterChanged(it)},
        onSaveCharacter = {viewModel.onSaveCharacter(it)},
        onDeleteCharacter = {viewModel.onDeleteCharacter(it)}
    )
}

@Composable
fun EditingCharacterDetailScreen(
    state: EditingCharacterDetailState,
    onBack: () -> Unit = {},
    onCharacterChanged: (Character) -> Unit = {},
    onSaveCharacter: (Character) -> Unit = {},
    onDeleteCharacter: (Character) -> Unit = {}
) {
    Scaffold(
        topBar = {
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = onBack){
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
                Spacer(modifier = Modifier.weight(1f))
                IconButton(
                    onClick = {
                        state.character?.let{onDeleteCharacter(it)}
                    }
                ){
                    Icon(Icons.Filled.Delete, contentDescription = "Delete")
                }
                Spacer(modifier = Modifier.weight(1f))
                IconButton(
                    onClick = {
                        state.character?.let{onSaveCharacter(it)}
                    }
                ){
                    Icon(Icons.Filled.Check, contentDescription = "Edit")
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)){
            if(state.loading){
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }else if(!state.canBecomeConcrete()){
                onBack()
            }else{
                ConcreteEditingCharacterDetailScreen(state.toConcrete(), onCharacterChanged)
            }

        }
    }
}

@Composable
fun NumberField(contents: Int, onValueChange: (Int) -> Unit){
    TextField(
        contents.toString(),
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Number
        ),
        onValueChange = { onValueChange(it.toIntOrNull() ?: contents) }
    )
}

@Composable
fun ConcreteEditingCharacterDetailScreen(
    state: ConcreteEditingCharacterDetailState,
    onCharacterChanged: (Character) -> Unit = {}
){
    val character = state.character
    Column {
        Text("Name")
        TextField(
            character.name,
            onValueChange = { onCharacterChanged(character.copy(name = it)) }
        )
        Text("Level")
        NumberField(
            character.level,
            onValueChange = {onCharacterChanged(character.copy(level = it))}
        )
        Text("Max Prepared Spells")
        NumberField(
            character.maxPreparedSpells,
            onValueChange = {onCharacterChanged(character.copy(maxPreparedSpells = it))}
        )
        // TODO: build a selector for this
        Text("Icon")
//        TextField(
//            character.characterIcon,
//            onValueChange = {onCharacterChanged(character.copy(characterIcon = it))}
//        )
        DropdownSelector(
            options = CharacterIcon.options().toList(),
            selected = setOf(character.characterIcon),
            optionPresenter = { Icon(CharacterIcon(it).fromString(), it) },
            onOptionPicked = { onCharacterChanged(character.copy(characterIcon = it)) },
            singleSelect = true,
            buttonContent = { Icon(CharacterIcon(character.characterIcon).fromString(), character.characterIcon) },
        )
    }
}