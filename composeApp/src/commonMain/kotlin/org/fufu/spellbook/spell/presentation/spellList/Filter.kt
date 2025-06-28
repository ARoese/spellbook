package org.fufu.spellbook.spell.presentation.spellList

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.unit.dp
import org.fufu.spellbook.composables.BooleanSelector
import org.fufu.spellbook.composables.DropdownSelector
import org.fufu.spellbook.nullingXor
import org.fufu.spellbook.spell.domain.SpellListFilter

@Composable
fun SpellListFilterSelector(
    state: SpellListState,
    onChangeFilter: (SpellListFilter) -> Unit = {}
){
    Column {
        var textFieldExpanded by remember { mutableStateOf(false) }
        Row{
            val scrollState = rememberScrollState()
            Row(
                modifier = Modifier
                    .horizontalScroll(scrollState)
                    .weight(1f),
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ){
                SpellListFilterSelectorItems(
                    state,
                    onChangeFilter,
                    onClickNameSearch = {
                        textFieldExpanded = !textFieldExpanded
                    }
                )
            }
            if(state.filter.hasActiveCriteria()){
                IconButton(
                    onClick = { onChangeFilter(state.filter.clear()) }
                ){
                    Icon(Icons.Default.Close, "Clear")
                }
            }
        }
        AnimatedVisibility(visible = textFieldExpanded){
            Box{
                val textFieldFocusRequester = remember { FocusRequester() }
                LaunchedEffect(textFieldExpanded){
                    if(textFieldExpanded){
                        textFieldFocusRequester.requestFocus()
                    }
                }
                TextField(
                    state.filter.name ?: "",
                    {
                        onChangeFilter(
                            state.filter.copy(name = it.ifEmpty { null })
                        )
                    },
                    modifier = Modifier
                        .focusRequester(focusRequester = textFieldFocusRequester)
                        .fillMaxWidth(),
                    singleLine = true
                )
                if(state.filter.name != null){
                    IconButton(
                        onClick = {
                            onChangeFilter(
                                state.filter.copy(name = null)
                            )
                        },
                        modifier = Modifier.align(Alignment.CenterEnd)
                    ){
                        Icon(Icons.Default.Close, "Clear")
                    }
                }
            }
        }
    }
}

@Composable
fun RowScope.SpellListFilterSelectorItems(
    state: SpellListState,
    onChangeFilter: (SpellListFilter) -> Unit = {},
    onClickNameSearch: () -> Unit = {}
){
    Button(onClick = onClickNameSearch){
        Icon(Icons.Default.Search, "Search")
        state.filter.name?.let{ Text(it) }
    }
    DropdownSelector(
        (0..9).toList(),
        state.filter.level,
        { Text("$it") },
        { level ->
            onChangeFilter(state.filter.let{
                it.copy(level = nullingXor(it.level, level))
            })
        }
    ){
        Text("Level")
    }
    //VerticalDivider()
    val allMagicSchools = state.knownSpells.map { it.info.school }.toSet()
    DropdownSelector(
        allMagicSchools.toList(),
        state.filter.school,
        { Text(it) },
        { school ->
            onChangeFilter(state.filter.let {
                it.copy(school = nullingXor(it.school, school))
            })
        }
    ){
        Text("School")
    }
    //VerticalDivider()
    val allSources = state.knownSpells.flatMap { it.info.sources }.toSet()
    DropdownSelector(
        allSources.toList(),
        state.filter.sources,
        { Text(it) },
        { source ->
            onChangeFilter(state.filter.let {
                it.copy(sources = nullingXor(it.sources, source))
            })
        }
    ){
        Text("Source")
    }
    val allVersions = state.knownSpells.flatMap { it.info.versions }.toSet()
    DropdownSelector(
        allVersions.toList(),
        state.filter.versions,
        { Text(it) },
        { version ->
            onChangeFilter(state.filter.let {
                it.copy(versions = nullingXor(it.versions, version))
            })
        }
    ){
        Text("Version")
    }
    BooleanSelector(
        state.filter.ritual,
        {onChangeFilter(state.filter.copy(ritual = it))}
    ){
        Text("Ritual")
    }
    val allTags = state.knownSpells
        .map { it.info.tag }
        .flatten()
        .toSet()
    DropdownSelector(
        allTags.toList(),
        state.filter.tag,
        { Text(it) },
        { tag ->
            onChangeFilter(state.filter.let {
                it.copy(tag = nullingXor(it.tag, tag))
            })
        }
    ){
        Text("Tag")
    }
    val allDamageTypes = state.knownSpells.flatMap { it.info.damages }.toSet()
    DropdownSelector(
        allDamageTypes.toList(),
        state.filter.damages,
        { Text(it) },
        { damage ->
            onChangeFilter(state.filter.let {
                it.copy(damages = nullingXor(it.damages, damage))
            })
        }
    ){
        Text("Damage")
    }
    val allSaveTypes = state.knownSpells.flatMap { it.info.saves }.toSet()
    DropdownSelector(
        allSaveTypes.toList(),
        state.filter.saves,
        { Text(it) },
        { save ->
            onChangeFilter(state.filter.let {
                it.copy(saves = nullingXor(it.saves, save))
            })
        }
    ){
        Text("Save")
    }
    val allClasses = state.knownSpells.flatMap { it.info.classes }.toSet()
    DropdownSelector(
        options = allClasses.toList(),
        selected = state.filter.classes,
        optionPresenter = { Text(it) },
        onOptionPicked = { cl ->
            onChangeFilter(state.filter.let {
                it.copy(classes = nullingXor(it.classes, cl))
            })
        }
    ){
        Text("Class")
    }
}