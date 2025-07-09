package org.fufu.spellbook.spell.presentation.spellDetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.fufu.spellbook.composables.ComposeLoadable
import org.fufu.spellbook.spell.domain.Condition
import org.fufu.spellbook.spell.domain.ConditionProvider
import org.fufu.spellbook.spell.domain.DefaultSpellInfo
import org.fufu.spellbook.spell.domain.Spell
import org.fufu.spellbook.spell.domain.SpellInfo
import org.fufu.spellbook.spell.domain.SpellMutator
import org.fufu.spellbook.spell.domain.SpellProvider
import javax.naming.OperationNotSupportedException

data class SpellDetailState(
    val originalSpell: Spell?,
    val spellInfo: ComposeLoadable<SpellInfo> =
        ComposeLoadable(originalSpell?.info),
    val viewedCondition: Condition? = null,
    val conditions: Set<String>? = null,
    val isEditing: Boolean = false
)

class SpellDetailVM(
    private var spellId: Int,
    private val provider: SpellProvider,
    private val conditionsProvider: ConditionProvider
) : ViewModel() {
    val mutable = provider is SpellMutator

    private val _state = MutableStateFlow(
        SpellDetailState(
            originalSpell = null,
            spellInfo = ComposeLoadable(
                DefaultSpellInfo().takeIf { spellId == 0 }
            ),
            isEditing = spellId == 0
        )
    )
    val state = _state
        .onStart {
            observeSpell()
            observeConditions()
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
                    it.copy(
                        originalSpell = actualSpell,
                        spellInfo = ComposeLoadable(actualSpell?.info),
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    private var observeConditionsJob: Job? = null
    private fun observeConditions(){
        observeConditionsJob?.cancel()
        observeConditionsJob = conditionsProvider.getConditions()
            .onEach { conditions ->
                _state.update { it.copy(conditions=conditions) }
            }
            .launchIn(viewModelScope)
    }

    fun showCondition(conditionName: String){
        viewModelScope.launch {
            val condition = conditionsProvider
                .getFullCondition(conditionName)
                .stateIn(viewModelScope).value
            _state.update { it.copy(viewedCondition = condition) }
        }
    }

    fun hideCondition(){
        _state.update { it.copy(viewedCondition = null) }
    }

    fun duplicateSpell() {
        observeSpellJob?.cancel()
        spellId = 0
        _state.update {
            val newSpell = if(it.originalSpell != null){
                it.originalSpell.copy(
                    key = 0,
                    info = it.originalSpell.info.copy(
                        name = "${it.originalSpell.info.name} copy")
                )
            }else{
                Spell(0, DefaultSpellInfo())
            }
            it.copy(
                originalSpell = newSpell,
                spellInfo = ComposeLoadable(newSpell.info),
                isEditing = true
            )
        }
    }

    fun closeClicked() {
        if(spellId != 0 && _state.value.isEditing){
            _state.update{
                it.copy(
                    isEditing = false,
                    spellInfo = ComposeLoadable(it.originalSpell?.info)
                )
            }
        }else{
            Intent.OnCloseClicked
        }
    }

    private var updateJob: Job? = null
    fun saveSpell(){
        val state = _state.value
        if(state.spellInfo.concreteState == null){return}
        if(!mutable){
            throw OperationNotSupportedException(
                "tried to save to a provider instead of mutator"
            )
        }
        val mutator : SpellMutator = provider as SpellMutator
        updateJob?.cancel()
        updateJob = if(spellId == 0){
            viewModelScope.launch {
                spellId = mutator.addSpell(state.spellInfo.concreteState)
                observeSpell()
            }
        }else{
            viewModelScope.launch {
                mutator.setSpell(Spell(spellId, state.spellInfo.concreteState))
            }
        }
        _state.update {
            it.copy(isEditing = false)
        }
    }


    fun setEditing(editing: Boolean) {
        _state.update {
            it.copy(isEditing = editing)
        }
    }

    fun spellEdited(newInfo: SpellInfo) {
        _state.update{
            it.copy(spellInfo = ComposeLoadable(newInfo))
        }
    }

    fun deleteClicked() {
        if( provider !is SpellMutator){
            throw OperationNotSupportedException(
                "tried to delete using a provider instead of mutator"
            )
        }

        viewModelScope.launch {
            provider.deleteSpell(spellId)
            observeSpell()
        }
    }
}