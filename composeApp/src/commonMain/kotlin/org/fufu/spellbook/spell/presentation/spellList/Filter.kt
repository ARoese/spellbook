package org.fufu.spellbook.spell.presentation.spellList

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.fufu.spellbook.composables.BooleanSelector
import org.fufu.spellbook.composables.DropdownSelector
import org.fufu.spellbook.nullingXor
import org.fufu.spellbook.spell.domain.Book
import org.fufu.spellbook.spell.domain.DamageType
import org.fufu.spellbook.spell.domain.MagicSchool
import org.fufu.spellbook.spell.domain.SaveType

@Composable
fun SpellListFilterSelector(
    state: SpellListState,
    onChangeFilter: (SpellListFilter) -> Unit = {}
){
    Row{
        val scrollState = rememberScrollState()
        Row(
            modifier = Modifier
                .horizontalScroll(scrollState)
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ){
            SpellListFilterSelectorItems(state, onChangeFilter)
        }
        if(state.filter.hasActiveCriteria()){
            IconButton(
                onClick = { onChangeFilter(state.filter.clear()) }
            ){
                Icon(Icons.Default.Close, "Clear")
            }
        }
    }

}

@Composable
fun RowScope.SpellListFilterSelectorItems(
    state: SpellListState,
    onChangeFilter: (SpellListFilter) -> Unit = {}
){
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
    DropdownSelector(
        MagicSchool.entries,
        state.filter.school,
        { Text(it.name) },
        { school ->
            onChangeFilter(state.filter.let {
                it.copy(school = nullingXor(it.school, school))
            })
        }
    ){
        Text("School")
    }
    //VerticalDivider()
    DropdownSelector(
        Book.entries,
        state.filter.book,
        { Text(it.name) },
        { book ->
            onChangeFilter(state.filter.let {
                it.copy(book = nullingXor(it.book, book))
            })
        }
    ){
        Text("Book")
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
    DropdownSelector(
        DamageType.entries,
        state.filter.damages,
        { Text(it.name) },
        { damage ->
            onChangeFilter(state.filter.let {
                it.copy(damages = nullingXor(it.damages, damage))
            })
        }
    ){
        Text("Damage")
    }
    DropdownSelector(
        SaveType.entries,
        state.filter.saves,
        { Text(it.name) },
        { save ->
            onChangeFilter(state.filter.let {
                it.copy(saves = nullingXor(it.saves, save))
            })
        }
    ){
        Text("Save")
    }
}