package org.fufu.spellbook.spell.presentation.spellList

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.FlowRowOverflow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.fufu.spellbook.composables.BooleanSelector
import org.fufu.spellbook.composables.ChipSize
import org.fufu.spellbook.composables.DropdownSelector
import org.fufu.spellbook.composables.FloatingAddButton
import org.fufu.spellbook.composables.TagChip
import org.fufu.spellbook.spell.domain.Book
import org.fufu.spellbook.spell.domain.DamageType
import org.fufu.spellbook.spell.domain.MagicSchool
import org.fufu.spellbook.spell.domain.SaveType
import org.fufu.spellbook.spell.domain.Spell

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
        onSpellSelected,
        onNewClicked,
        navBar,
        onChangeFilter = {viewModel.useFilter(it)},
    )
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
    onSpellSelected: (Spell) -> Unit,
    onNewClicked: () -> Unit = {},
    navBar: @Composable () -> Unit,
    onChangeFilter: (SpellListFilter) -> Unit = {}
){
    Scaffold(
        bottomBar = navBar,
        floatingActionButton = {
            FloatingAddButton(onNewClicked)
        }
    ){ padding ->
        Box(modifier = Modifier.padding(padding)){
            SpellList(
                state,
                onSpellSelected,
                onChangeFilter = onChangeFilter,
                showFilterOptions = true
            )
        }
    }
}