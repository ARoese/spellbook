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
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.AddCircle
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.fufu.spellbook.character.domain.Character
import org.fufu.spellbook.character.domain.SpellSlotLevel
import org.fufu.spellbook.spell.domain.Spell
import org.fufu.spellbook.character.domain.hasPreparedSpell
import org.fufu.spellbook.character.domain.knowsSpell
import org.fufu.spellbook.composables.ClickableToken
import org.fufu.spellbook.composables.KnownToken
import org.fufu.spellbook.composables.PreparedToken
import org.fufu.spellbook.di.CHARACTER_CLASS_SPELL_LIST
import org.fufu.spellbook.di.CHARACTER_KNOWN_SPELL_LIST
import org.fufu.spellbook.di.CHARACTER_PREPARED_SPELL_LIST
import org.fufu.spellbook.spell.presentation.spellList.SpellList
import org.fufu.spellbook.spell.presentation.spellList.SpellListFilter
import org.fufu.spellbook.spell.presentation.spellList.SpellListVM
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.qualifier.qualifier
import kotlin.math.max
import kotlin.math.min

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
    val knownSpellListVM = koinViewModel<SpellListVM>(
        qualifier = qualifier(CHARACTER_KNOWN_SPELL_LIST)
    )
    val classSpellListVM = koinViewModel<SpellListVM>(
        qualifier = qualifier(CHARACTER_CLASS_SPELL_LIST)
    )

    preparedSpellListVM.useFilter(
        SpellListFilter(
            onlyIds = state.character?.let{ character ->
                character.spells.filter { it.value }.keys
            } ?: emptySet()
        )
    )

    knownSpellListVM.useFilter(
        SpellListFilter(
            onlyIds = state.character?.spells?.keys ?: emptySet()
        )
    )

    val preparedSpellListState by preparedSpellListVM.state.collectAsStateWithLifecycle()
    val knownSpellListState by knownSpellListVM.state.collectAsStateWithLifecycle()
    val classSpellListState by classSpellListVM.state.collectAsStateWithLifecycle()

    var listType : SpellListType by remember { mutableStateOf(SpellListType.PREPARED) }
    val variant by derivedStateOf {
        when(listType){
            SpellListType.PREPARED -> SpellListVariant(listType, preparedSpellListState)
            SpellListType.KNOWN -> SpellListVariant(listType, knownSpellListState)
            SpellListType.CLASS -> SpellListVariant(listType, classSpellListState)
        }
    }

    CharacterDetailScreen(
        state,
        variant = variant
    ) { intent ->
        when(intent){
            Intent.Back -> onBack()
            is Intent.ChangeListVariant -> {
                listType = intent.type
            }
            is Intent.EditCharacter -> onClickEditCharacter(intent.characterId)
            is Intent.SetSpellLearnedness -> viewModel.onSetSpellLearned(intent.spell.key, intent.learned)
            is Intent.SetSpellPreparedness -> viewModel.onSetSpellPrepared(intent.spell.key, intent.prepared)
            is Intent.ViewSpell -> onViewSpell(intent.spell)
            is Intent.SetSpellSlotLevel -> viewModel.onSetSpellSlot(intent.level, intent.slotLevel)
            is Intent.SetListFilter -> classSpellListVM.useFilter(intent.filter.copy(onlyIds = null))
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
fun SpellSlotLevelDisplay(
    level: SpellSlotLevel,
    onChange: (SpellSlotLevel) -> Unit
){
    Row {
        fun decrement(){
            onChange(level.copy(slots=max(0, level.slots-1)))
        }

        fun increment(){
            onChange(level.copy(slots=min(level.slots+1, level.maxSlots)))
        }

        (1..level.maxSlots).forEach {
            if(it <= level.slots){
                IconButton(onClick = { decrement() }){
                    Icon(Icons.Filled.CheckCircle, "checkCircle")
                }
            }else{
                IconButton(onClick = { increment() }){
                    Icon(Icons.Outlined.AddCircle, "add Circle")
                }
            }
        }
    }
}

@Composable
fun SpellListVariantDisplay(
    variant: SpellListVariant,
    character: Character,
    intend: (Intent) -> Unit
){
    Column {
        NavigationBar {
            NavigationBarItem(
                onClick = { intend(Intent.ChangeListVariant(SpellListType.PREPARED)) },
                label = { Text("Prepared") },
                selected = variant.type == SpellListType.PREPARED,
                icon = {},
                alwaysShowLabel = true
            )
            NavigationBarItem(
                onClick = { intend(Intent.ChangeListVariant(SpellListType.KNOWN)) },
                label = { Text("Known") },
                selected = variant.type == SpellListType.KNOWN,
                icon = {},
                alwaysShowLabel = true
            )
            NavigationBarItem(
                onClick = { intend(Intent.ChangeListVariant(SpellListType.CLASS)) },
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
                val hasMaxPrepared = character.spells.count { it.value } >=
                        character.maxPreparedSpells
                val isEnabled = !hasMaxPrepared || prepared
                ClickableToken(
                    enabled = isEnabled,
                    onClick = {intend(Intent.SetSpellPreparedness(spell, !prepared))}
                ) {
                    PreparedToken(prepared = prepared, enabled = isEnabled)
                }
            }
            SpellListType.CLASS -> { spell ->
                val known = character.knowsSpell(spell.key)
                ClickableToken(
                    onClick = {intend(Intent.SetSpellLearnedness(spell, !known))}
                ) {
                    KnownToken(known = known)
                }
            }
        }

        val headerContent : @Composable (Int) -> Unit = when(variant.type){
            SpellListType.PREPARED -> { level ->
                character.spellSlots[level]?.let { slotLevel ->
                    if(slotLevel.maxSlots != 0){
                        SpellSlotLevelDisplay(
                            slotLevel,
                            onChange = { newLevel ->
                                intend(Intent.SetSpellSlotLevel(level, newLevel))
                            }
                        )
                    }
                }
            }
            else -> {_ -> Unit}
        }

        val necessarySpellLevels = character.spellSlots
            .filter { it.value.maxSlots != 0 }
            .keys

        SpellList(variant.state,
            onSpellSelected = { intend(Intent.ViewSpell(it)) },
            rightSideButton,
            headerContent = headerContent,
            showFilterOptions = variant.type == SpellListType.CLASS,
            onChangeFilter = {
                intend(Intent.SetListFilter(it))
            },
            shouldGroupByLevel = true,
            necessarySpellLevels = necessarySpellLevels
        )
    }
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
                IconButton(
                    onClick = {
                        state.character?.let {
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
            LoadingCharacterDetail(
                state,
                variant,
                intend
            )
        }
    }
}

@Composable
fun LoadingCharacterDetail(
    state: CharacterDetailState,
    variant: SpellListVariant,
    intend: (Intent) -> Unit
){
    // check and handle loading status and nullability of stuff
    if(state.loading){
        Box(modifier = Modifier.fillMaxSize()){
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }else{
        if(!state.canBecomeConcrete()){
            intend(Intent.Back)
        }else{
            CharacterDetail(
                state.toConcrete(),
                variant,
                intend
            )
        }
    }
}

@Composable
fun CharacterDetail(
    state: ConcreteCharacterDetailState,
    variant: SpellListVariant,
    intend: (Intent) -> Unit
){
    val character = state.character
    Box(modifier = Modifier.fillMaxSize()){
        Column {
            Text("Name: ${character.name}")
            Text("Class: ${character.characterClass}")
            Text("Level: ${character.level}")
            Text("Max prepared spells: ${character.maxPreparedSpells}")
            SpellListVariantDisplay(
                variant = variant,
                character = state.character,
                intend
            )
        }
    }
}