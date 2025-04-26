package org.fufu.spellbook.character.presentation.editingCharacterDetail

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
    ){ intent ->
        when(intent){
            Intent.Back -> onBack()
            is Intent.DeleteCharacter -> viewModel.onDeleteCharacter(intent.character)
            is Intent.SaveCharacter -> viewModel.onSaveCharacter(intent.character)
            is Intent.EditCharacter -> viewModel.onCharacterChanged(intent.character)
        }
    }
}

sealed interface Intent {
    data class EditCharacter(val character: Character) : Intent
    data class SaveCharacter(val character: Character) : Intent
    data class DeleteCharacter(val character: Character) : Intent
    data object Back : Intent
}

@Composable
fun EditingCharacterDetailScreen(
    state: EditingCharacterDetailState,
    intend: (Intent) -> Unit
) {
    Scaffold(
        topBar = {
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = { intend(Intent.Back) }){
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
                Spacer(modifier = Modifier.weight(1f))
                IconButton(
                    onClick = {
                        state.character?.let{intend(Intent.DeleteCharacter(it))}
                    }
                ){
                    Icon(Icons.Filled.Delete, contentDescription = "Delete")
                }
                Spacer(modifier = Modifier.weight(1f))
                IconButton(
                    onClick = {
                        state.character?.let{intend(Intent.SaveCharacter(it))}
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
                intend(Intent.Back)
            }else{
                ConcreteEditingCharacterDetailScreen(state.toConcrete(), intend)
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
    intend: (Intent) -> Unit
){
    val character = state.character
    Column {
        Text("Name")
        TextField(
            character.name,
            onValueChange = { intend(Intent.EditCharacter(character.copy(name = it))) }
        )
        Text("Level")
        NumberField(
            character.level,
            onValueChange = { intend(Intent.EditCharacter(character.copy(level = it))) }
        )
        Text("Max Prepared Spells")
        NumberField(
            character.maxPreparedSpells,
            onValueChange = { intend(Intent.EditCharacter(character.copy(maxPreparedSpells = it))) }
        )

        Text("Icon")
        DropdownSelector(
            options = CharacterIcon.options().toList(),
            selected = setOf(character.characterIcon),
            optionPresenter = { Icon(CharacterIcon(it).fromString(), it) },
            onOptionPicked = { intend(Intent.EditCharacter(character.copy(characterIcon = it))) },
            singleSelect = true,
            buttonContent = { Icon(CharacterIcon(character.characterIcon).fromString(), character.characterIcon) },
        )
    }
}