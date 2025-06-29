package org.fufu.spellbook.character.presentation.characterDetail

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.fufu.spellbook.character.domain.SpellSlotLevel
import org.fufu.spellbook.spell.domain.Spell
import org.fufu.spellbook.spell.domain.SpellListFilter

@Composable
fun CharacterDetailScreenRoot(
    viewModel: CharacterDetailVM,
    onBack: () -> Unit = {},
    onViewSpell: (Spell) -> Unit = {},
    onClickEditCharacter: (Int) -> Unit = {}
){
    val state by viewModel.state.collectAsStateWithLifecycle()

    val preparedSpellListState by viewModel.preparedSpellList.state.collectAsStateWithLifecycle()
    val knownSpellListState by viewModel.knownSpellList.state.collectAsStateWithLifecycle()
    val classSpellListState by viewModel.classSpellList.state.collectAsStateWithLifecycle()

    val variant by derivedStateOf {
        when(state.selectedSpellList){
            SpellListType.PREPARED -> SpellListVariant(state.selectedSpellList, preparedSpellListState)
            SpellListType.KNOWN -> SpellListVariant(state.selectedSpellList, knownSpellListState)
            SpellListType.CLASS -> SpellListVariant(state.selectedSpellList, classSpellListState)
        }
    }

    CharacterDetailScreen(
        state,
        variant = variant
    ) { intent ->
        when(intent){
            Intent.Back -> onBack()
            is Intent.ChangeListVariant -> {
                viewModel.onSetSpellListType(intent.type)
            }
            is Intent.EditCharacter -> onClickEditCharacter(intent.characterId)
            is Intent.SetSpellLearnedness -> viewModel.onSetSpellLearned(intent.spell.key, intent.learned)
            is Intent.SetSpellPreparedness -> viewModel.onSetSpellPrepared(intent.spell.key, intent.prepared)
            is Intent.ViewSpell -> onViewSpell(intent.spell)
            is Intent.SetSpellSlotLevel -> viewModel.onSetSpellSlot(intent.level, intent.slotLevel)
            is Intent.SetListFilter -> viewModel.classSpellList.useFilter(intent.filter.copy(onlyIds = null))
        }
    }
}

sealed interface Intent {
    data class ViewSpell(val spell: Spell) : Intent
    data object Back : Intent
    data class EditCharacter(val characterId: Int) : Intent
    data class ChangeListVariant(val type: SpellListType) : Intent
    data class SetSpellLearnedness(val spell: Spell, val learned: Boolean) : Intent
    data class SetSpellPreparedness(val spell: Spell, val prepared: Boolean) : Intent
    data class SetSpellSlotLevel(val level: Int, val slotLevel: SpellSlotLevel) : Intent
    data class SetListFilter(val filter: SpellListFilter) : Intent
}

@Composable
fun CharacterDetailScreen(
    state: CharacterDetailState,
    variant: SpellListVariant,
    intend: (Intent) -> Unit
){
    Scaffold(
        topBar = {
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = { intend(Intent.Back) }){
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    state.character.concreteState?.name ?: "",
                    modifier = Modifier.align(Alignment.CenterVertically),
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(modifier = Modifier.weight(1f))
                IconButton(
                    onClick = {
                        state.character.concreteState?.let {
                            intend(Intent.EditCharacter(it.id))
                        }
                    }
                ){
                    Icon(Icons.Default.Edit, contentDescription = "Back")
                }
            }
        }
    ){ padding ->
        Box(modifier = Modifier.padding(padding)){
            CharacterDetail(state,variant,intend)
        }
    }
}

@Composable
fun CharacterDetail(
    state: CharacterDetailState,
    variant: SpellListVariant,
    intend: (Intent) -> Unit
){
    state.character.map(
        ifNotLoaded = {
            Box(modifier = Modifier.fillMaxSize()){
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        },
        ifLoadedNull = { intend(Intent.Back) }
    ){ character ->
        Box(modifier = Modifier.fillMaxSize()){
            Column {
                val view = SpellListVariantView(variant, character, intend)
                view.Display()
            }
        }
    }
}