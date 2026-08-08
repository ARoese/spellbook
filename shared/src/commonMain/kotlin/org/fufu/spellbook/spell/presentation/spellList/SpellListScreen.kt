package org.fufu.spellbook.spell.presentation.spellList

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.fufu.spellbook.composables.FloatingAddButton
import org.fufu.spellbook.spell.domain.Spell
import org.fufu.spellbook.spell.domain.SpellListFilter

@Composable
fun SpellListScreenRoot(
    viewModel: SpellListVM,
    onSpellSelected: (Spell) -> Unit,
    onNewClicked: () -> Unit,
    navBar: @Composable () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    SpellListScreen(
        state,
        navBar,
    ){ intent ->
        when(intent){
            Intent.NewSpell -> onNewClicked()
            is Intent.UseFilter -> viewModel.useFilter(intent.filter)
            is Intent.ViewSpell -> onSpellSelected(intent.spell)
        }
    }
}

sealed interface Intent {
    data object NewSpell : Intent
    data class ViewSpell(val spell: Spell) : Intent
    data class UseFilter(val filter: SpellListFilter) : Intent
}

@Composable
fun SpellListRoot(
    viewModel: SpellListVM,
    onSpellSelected: (Spell) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    SpellList(
        state,
        onSpellSelected,
        onChangeFilter = {viewModel.useFilter(it)},
        showFilterOptions = true
    )
}

@Composable
fun SpellListScreen(
    state: SpellListState,
    navBar: @Composable () -> Unit,
    intend: (Intent) -> Unit
){
    Scaffold(
        bottomBar = navBar,
        floatingActionButton = {
            FloatingAddButton({ intend(Intent.NewSpell) })
        }
    ){ padding ->
        Box(modifier = Modifier.padding(padding)){
            SpellList(
                state,
                { intend(Intent.ViewSpell(it)) },
                onChangeFilter = { intend(Intent.UseFilter(it)) },
                showFilterOptions = true
            )
        }
    }
}