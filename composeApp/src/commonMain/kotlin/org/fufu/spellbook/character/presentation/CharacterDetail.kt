package org.fufu.spellbook.character.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.fufu.spellbook.character.domain.Character
import org.fufu.spellbook.spell.domain.Spell
import org.fufu.spellbook.character.domain.hasPreparedSpell
import org.fufu.spellbook.character.domain.knowsSpell
import org.fufu.spellbook.di.CHARACTER_CLASS_SPELL_LIST
import org.fufu.spellbook.di.CHARACTER_KNOWN_SPELL_LIST
import org.fufu.spellbook.di.CHARACTER_PREPARED_SPELL_LIST
import org.fufu.spellbook.spell.presentation.ChipSize
import org.fufu.spellbook.spell.presentation.ClickableToken
import org.fufu.spellbook.spell.presentation.KnownToken
import org.fufu.spellbook.spell.presentation.PreparedToken
import org.fufu.spellbook.spell.presentation.SpellList
import org.fufu.spellbook.spell.presentation.SpellListFilter
import org.fufu.spellbook.spell.presentation.SpellListState
import org.fufu.spellbook.spell.presentation.SpellListVM
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.qualifier.qualifier

enum class SpellListType{
    PREPARED,
    KNOWN,
    CLASS
}

data class SpellListVariant(
    val type: SpellListType,
    val state: SpellListState
)

@Composable
fun CharacterDetailScreenRoot(
    viewModel: CharacterDetailVM,
    onBack: () -> Unit = {},
    onViewSpell: (Spell) -> Unit = {},
    onClickEditCharacter: (Int) -> Unit = {}
){
    val state by viewModel.state.collectAsStateWithLifecycle()
    val preparedSpellListVM = koinViewModel<SpellListVM>(
        qualifier = qualifier(CHARACTER_PREPARED_SPELL_LIST)
    )
    val preparedSpellListState by preparedSpellListVM.state.collectAsStateWithLifecycle()
    val knownSpellListVM = koinViewModel<SpellListVM>(
        qualifier = qualifier(CHARACTER_KNOWN_SPELL_LIST)
    )
    val knownSpellListState by knownSpellListVM.state.collectAsStateWithLifecycle()
    val classSpellListVM = koinViewModel<SpellListVM>(
        qualifier = qualifier(CHARACTER_CLASS_SPELL_LIST)
    )
    val classSpellListState by classSpellListVM.state.collectAsStateWithLifecycle()

    preparedSpellListVM.useFilter(
        SpellListFilter(
            onlyIds = state.character?.spells?.keys ?: emptySet()
        )
    )

    knownSpellListVM.useFilter(
        SpellListFilter(
            onlyIds = state.character?.spells?.keys ?: emptySet()
        )
    )

    classSpellListVM.useFilter(
        SpellListFilter(
            onlyIds = state.character?.let{ character ->
                character.spells.filter { it.value }.keys
            } ?: emptySet()
        )
    )

    CharacterDetailScreen(
        state,
        classSpellListState,
        onBack = onBack,
        onViewSpell = onViewSpell,
        onClickEditCharacter = onClickEditCharacter
    )
}

@Composable
fun SpellListVariantDisplay(
    variant: SpellListVariant,
    character: Character,
    onChangeVariant: (SpellListType) -> Unit = {},
    onSpellClicked: (Spell) -> Unit = {},
    onSetSpellPreparedness: (Spell, Boolean) -> Unit = {_,_ -> },
    onSetSpellLearnedness: (Spell, Boolean) -> Unit = {_,_ -> }
){
    Column {
        NavigationBar {
            NavigationBarItem(
                onClick = { onChangeVariant(SpellListType.PREPARED) },
                label = { Text("Prepared") },
                selected = variant.type == SpellListType.PREPARED,
                icon = {},
                alwaysShowLabel = true
            )
            NavigationBarItem(
                onClick = { onChangeVariant(SpellListType.KNOWN) },
                label = { Text("Known") },
                selected = variant.type == SpellListType.KNOWN,
                icon = {},
                alwaysShowLabel = true
            )
            NavigationBarItem(
                onClick = { onChangeVariant(SpellListType.CLASS) },
                label = { Text("Class") },
                selected = variant.type == SpellListType.CLASS,
                icon = {},
                alwaysShowLabel = true
            )
        }
        val rightSideButton : @Composable ((Spell) -> Unit)? = when(variant.type){
            SpellListType.PREPARED -> null
            SpellListType.KNOWN -> { spell ->
                val prepared = character.hasPreparedSpell(spell.key)
                ClickableToken(
                    onClick = {onSetSpellPreparedness(spell, !prepared)}
                ) {
                    PreparedToken(prepared = prepared)
                }
            }
            SpellListType.CLASS -> { spell ->
                val known = character.knowsSpell(spell.key)
                ClickableToken(
                    onClick = {onSetSpellLearnedness(spell, !known)}
                ) {
                    KnownToken(known = known)
                }
            }
        }
        SpellList(variant.state, onSpellSelected = onSpellClicked, rightSideButton)
    }
}

@Composable
fun CharacterDetailScreen(
    state: CharacterDetailState,
    spellListState: SpellListState,
    onBack: () -> Unit = {},
    onViewSpell: (Spell) -> Unit = {},
    onClickEditCharacter: (Int) -> Unit = {}
){
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
                        state.character?.let {
                            onClickEditCharacter(it.id)
                        }
                    }
                ){
                    Icon(Icons.Default.Edit, contentDescription = "Back")
                }
            }
        }
    ){ padding ->
        Box(modifier = Modifier.padding(padding)){
            LoadingCharacterDetail(
                state,
                spellListState,
                onViewSpell,
                onBack = onBack
            )
        }
    }
}

@Composable
fun LoadingCharacterDetail(
    state: CharacterDetailState,
    spellListState: SpellListState,
    onViewSpell: (Spell) -> Unit = {},
    onBack: () -> Unit = {}
){
    // check and handle loading status and nullability of stuff
    if(state.loading){
        Box(modifier = Modifier.fillMaxSize()){
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }else{
        if(!state.canBecomeConcrete()){
            onBack()
        }else{
            CharacterDetail(
                state.toConcrete(),
                spellListState,
                onViewSpell
            )
        }
    }
}

@Composable
fun CharacterDetail(
    state: ConcreteCharacterDetailState,
    spellListState: SpellListState,
    onViewSpell: (Spell) -> Unit = {}
){
    val character = state.character
    Box(modifier = Modifier.fillMaxSize()){
        Column {
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