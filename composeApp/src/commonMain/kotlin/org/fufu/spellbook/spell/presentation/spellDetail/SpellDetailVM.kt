package org.fufu.spellbook.spell.presentation.spellDetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.fufu.spellbook.spell.domain.DefaultSpellInfo
import org.fufu.spellbook.spell.domain.Spell
import org.fufu.spellbook.spell.domain.SpellInfo
import org.fufu.spellbook.spell.domain.SpellMutator
import org.fufu.spellbook.spell.domain.SpellProvider
import javax.naming.OperationNotSupportedException

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

fun SpellDetailState.canBecomeConcrete() : Boolean {
    return !(originalSpell == null || spellInfo == null)
}

fun SpellDetailState.toConcrete() : ConcreteSpellDetailState {
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
    private var spellId: Int,
    private val provider: SpellProvider
) : ViewModel() {
    public val mutable = provider is SpellMutator
    sealed interface Action{
        data object OnCloseClicked : Action
        data object OnEditClicked : Action
        data class OnSpellEdited(val newInfo: SpellInfo) : Action
        data object OnDeleteClicked : Action
    }

    private val _state = MutableStateFlow(
        SpellDetailState(
            originalSpell = Spell(0, DefaultSpellInfo()),
            spellInfo = DefaultSpellInfo(),
            loading = spellId != 0,
            isEditing = spellId == 0
        )
    )
    val state = _state
        .onStart {
            observeSpell()
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            _state.value
        )

    private var observeSpellJob: Job? = null
    private fun observeSpell(){
        observeSpellJob?.cancel()
        // if id is 0, it means we're making a new spell
        if(spellId == 0){
            return
        }
        observeSpellJob = provider.getSpell(spellId)
            .onEach { actualSpell ->
                _state.update{
                    SpellDetailState(actualSpell, loading = false)
                }
            }
            .launchIn(viewModelScope)
    }

    fun duplicateSpell() {
        observeSpellJob?.cancel()
        spellId = 0
        _state.update {
            val newSpell = if(it.originalSpell != null){
                it.originalSpell.copy(
                    key = 0,
                    it.originalSpell.info.copy(
                        name = "${it.originalSpell.info.name} copy")
                )
            }else{
                Spell(0, DefaultSpellInfo())
            }
            it.copy(originalSpell = newSpell, spellInfo = newSpell.info, isEditing = true)
        }
    }

    private var updateJob: Job? = null
    fun onAction(action: Action) : Action? {
        return when(action){
            is Action.OnCloseClicked ->
                if(spellId != 0 && _state.value.isEditing){
                    _state.update{
                        it.copy(isEditing = false, spellInfo = it.originalSpell?.info)
                    }
                    null
                }else{
                    Action.OnCloseClicked
                }

            is Action.OnEditClicked -> {

                _state.update {
                    // if loading, don't allow this
                    // TODO: do something more clever than this
                    // nasty logic
                    if(it.loading){return null}
                    if(it.isEditing){
                        if(!mutable){
                            throw OperationNotSupportedException(
                                "tried to save to a provider instead of mutator"
                            )
                        }
                        val mutator : SpellMutator = provider as SpellMutator
                        if(it.spellInfo == null){
                            throw OperationNotSupportedException(
                                "tried to save a null spell info"
                            )
                        }
                        updateJob?.cancel()
                        updateJob = if(spellId == 0){
                            CoroutineScope(Dispatchers.IO).launch {
                                spellId = mutator.addSpell(it.spellInfo)
                                observeSpell()
                            }
                        }else{
                            CoroutineScope(Dispatchers.IO).launch {
                                mutator.setSpell(Spell(spellId, it.spellInfo))
                            }
                        }
                    }
                    it.copy(isEditing = !it.isEditing)
                }
                null
            }

            is Action.OnSpellEdited -> {
                _state.update{
                    it.copy(spellInfo = action.newInfo)
                }
                null
            }

            is Action.OnDeleteClicked -> {
                if( provider !is SpellMutator){
                    throw OperationNotSupportedException(
                        "tried to delete using a provider instead of mutator"
                    )
                }

                CoroutineScope(Dispatchers.IO).launch {
                    provider.deleteSpell(spellId)
                    observeSpell()
                }

                return action
            }
        }
    }

}