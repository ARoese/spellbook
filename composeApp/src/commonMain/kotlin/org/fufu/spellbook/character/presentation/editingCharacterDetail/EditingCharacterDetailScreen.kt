package org.fufu.spellbook.character.presentation.editingCharacterDetail

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RangeSliderState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.fufu.spellbook.character.domain.Character
import org.fufu.spellbook.character.domain.CharacterIcon
import org.fufu.spellbook.character.domain.SpellSlotLevel
import org.fufu.spellbook.composables.DropdownSelector
import kotlin.math.min

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
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier.verticalScroll(scrollState)
    ){
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

        SpellSlotList(character.spellSlots){
            intend(Intent.EditCharacter(state.character.copy(spellSlots = it)))
        }
    }
}

@Composable
fun IntegerRangeSelector(
    value: Int,
    range: IntRange,
    onChange: (Int) -> Unit
){
    var sliderValue by remember { mutableStateOf(value.toFloat()) }
    Slider(
        value = sliderValue,
        steps = range.last - range.first - 2,
        valueRange = range.first.toFloat()..range.last.toFloat(),
        onValueChange = { sliderValue = it },
        onValueChangeFinished = { onChange(sliderValue.toInt()) },
        modifier = Modifier.padding(horizontal = 10.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpellSlotList(
    spellSlots: Map<Int, SpellSlotLevel>,
    onValueChange: (Map<Int, SpellSlotLevel>) -> Unit
){
    val maxSpellSlotLevel = spellSlots.keys.maxOrNull() ?: 0
    fun dilateSpellSlots(num: Int): Map<Int, SpellSlotLevel> {
        val withoutExtras = spellSlots
            .filterKeys { it in (1..num) }

        val newPairs = ((1..num).toSet() - spellSlots.keys)
            .associateWith { SpellSlotLevel(0,0) }

        return withoutExtras + newPairs
    }
    Column {
        Text("Max Spell slot level:")
        Row {
            Text("$maxSpellSlotLevel", Modifier
                .align(Alignment.CenterVertically)
                .padding(horizontal = 5.dp)
            )
            IntegerRangeSelector(
                maxSpellSlotLevel,
                range = 0..9,
                onChange = { onValueChange(dilateSpellSlots(it)) }
            )
        }

        Column{
            spellSlots.forEach{ e ->
                Row {
                    val spellLevel = e.value
                    val maxPossibleNumSlots = 10
                    Text("Level ${e.key}")
                    IntegerRangeSelector(
                        value = spellLevel.maxSlots,
                        range = 0..maxPossibleNumSlots,
                        onChange = {
                            val newLevel = SpellSlotLevel(
                                it,
                                min(it, spellLevel.slots)
                            )
                            onValueChange(
                                spellSlots.plus(e.key to newLevel)
                            )
                        }
                    )
                }
            }
        }
    }
}
