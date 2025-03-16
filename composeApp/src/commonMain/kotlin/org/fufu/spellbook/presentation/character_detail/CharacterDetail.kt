package org.fufu.spellbook.presentation.character_detail

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.fufu.spellbook.di.CHARACTER_SPELL_LIST_QUALIFIER
import org.fufu.spellbook.domain.Character
import org.fufu.spellbook.domain.Spell
import org.fufu.spellbook.domain.SpellInfo
import org.fufu.spellbook.domain.hasPreparedSpell
import org.fufu.spellbook.presentation.spell_detail.SpellDetailScreen
import org.fufu.spellbook.presentation.spell_detail.SpellDetailState
import org.fufu.spellbook.presentation.spell_detail.canBecomeConcrete
import org.fufu.spellbook.presentation.spell_detail.toConcrete
import org.fufu.spellbook.presentation.spell_list.ChipSize
import org.fufu.spellbook.presentation.spell_list.PreparedToken
import org.fufu.spellbook.presentation.spell_list.SpellList
import org.fufu.spellbook.presentation.spell_list.SpellListFilter
import org.fufu.spellbook.presentation.spell_list.SpellListRoot
import org.fufu.spellbook.presentation.spell_list.SpellListState
import org.fufu.spellbook.presentation.spell_list.SpellListVM
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.qualifier.qualifier

@Composable
fun CharacterDetailScreenRoot(
    viewModel: CharacterDetailVM,
    onBack: () -> Unit = {},
    onViewSpell: (Spell) -> Unit = {}
){
    val spellListVM = koinViewModel<SpellListVM>(
        qualifier = qualifier(CHARACTER_SPELL_LIST_QUALIFIER)
    )

    val state by viewModel.state.collectAsStateWithLifecycle()
    val spellListState by spellListVM.state.collectAsStateWithLifecycle()
    spellListVM.useFilter(
        SpellListFilter(
            onlyIds = state.character?.spells?.keys ?: emptySet()
        )
    )

    LoadingCharacterDetailScreen(state, spellListState, onBack, onViewSpell)
}

@Composable
fun LoadingCharacterDetailScreen(
    state: CharacterDetailState,
    spellListState: SpellListState,
    onBack: () -> Unit = {},
    onViewSpell: (Spell) -> Unit = {}
){
    // check and handle loading status and nullability of stuff
    if(state.loading){
        Box(modifier = Modifier.fillMaxSize()){
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }else{
        if(!state.canBecomeConcrete()){
            Box(modifier = Modifier.fillMaxSize()){
                Text("Character Missing")
            }
        }else{
            CharacterDetail(
                state.toConcrete(),
                spellListState,
                onBack, onViewSpell
            )
        }
    }
}

@Composable
fun CharacterDetail(
    state: ConcreteCharacterDetailState,
    spellListState: SpellListState,
    onBack: () -> Unit = {},
    onViewSpell: (Spell) -> Unit = {}
){
    val character = state.character
    Box(modifier = Modifier.fillMaxSize()){
        Column {
            IconButton(onClick = onBack){
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            Text("Name: ${character.name}")
            Text("Class: ${character.characterClass}")
            Text("Level: ${character.level}")
            Text("Max prepared spells: ${character.maxPreparedSpells}")
            Text("Spells:")
            SpellList(
                spellListState,
                onSpellSelected = onViewSpell,
                rightSideButton = {spell ->
                    Box(
                        modifier = Modifier
                            .clickable(true, onClick = {})
                    ){
                        PreparedToken(character.hasPreparedSpell(spell.key), ChipSize.REGULAR)
                    }
                }
            )
        }
    }
}