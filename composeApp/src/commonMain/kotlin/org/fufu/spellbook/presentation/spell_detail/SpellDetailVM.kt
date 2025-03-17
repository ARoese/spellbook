package org.fufu.spellbook.presentation.spell_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import org.fufu.spellbook.domain.Spell
import org.fufu.spellbook.domain.SpellInfo
import org.fufu.spellbook.domain.SpellProvider

data class SpellDetailState(
    val originalSpell: Spell?,
    val spellInfo: SpellInfo? = originalSpell?.info,
    val isEditing: Boolean = false,
    val loading: Boolean = true
)

data class ConcreteSpellDetailState(
    val originalSpell: Spell,
    val spellInfo: SpellInfo = originalSpell.info,
    val isEditing: Boolean = false,
    val loading: Boolean = true
)

fun SpellDetailState.canBecomeConcrete() : Boolean{
    return !(originalSpell == null || spellInfo == null)
}

fun SpellDetailState.toConcrete() : ConcreteSpellDetailState{
    if(!canBecomeConcrete()){
        throw KotlinNullPointerException("null field is not allowed")
    }
    // this protects nullables below
    return ConcreteSpellDetailState(
        this.originalSpell!!,
        this.spellInfo!!,
        this.isEditing,
        this.loading
    )
}

class SpellDetailVM(
    private val spellId: Int,
    private val provider: SpellProvider
) : ViewModel() {
    sealed interface Action{
        data object OnCloseClicked : Action
        data object OnEditClicked : Action
        data class OnSpellEdited(val newInfo: SpellInfo) : Action
        data class OnSpellSaved(val newInfo: SpellInfo) : Action
    }

    private val _state = MutableStateFlow(SpellDetailState(null))
    val state = _state
        .onStart {
            observeSpell()
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            _state.value
        )

    private fun observeSpell(){
        provider.getSpell(spellId)
            .onEach { actualSpell ->
                delay(3000)
                _state.update{
                    SpellDetailState(actualSpell, loading = false)
                }
            }.launchIn(viewModelScope)

    }

    fun onAction(action: Action){
        when(action){
            is Action.OnCloseClicked -> TODO()
            is Action.OnEditClicked -> _state.update { it.copy(isEditing = !it.isEditing) } //TODO: push a save
            is Action.OnSpellSaved -> TODO()
            is Action.OnSpellEdited -> _state.update{
                it.copy(spellInfo = action.newInfo)
            }
        }
    }
}