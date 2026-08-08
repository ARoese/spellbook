package org.fufu.spellbook.spell.presentation.deImport

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.fufu.spellbook.composables.ComposeLoadable
import org.fufu.spellbook.spell.domain.Spell
import org.fufu.spellbook.spell.domain.SpellListFilter
import org.fufu.spellbook.spell.domain.SpellMutator

data class DeImportScreenState(
    val spells: ComposeLoadable<List<Spell>> = ComposeLoadable(),
    val selectedImportKeys: Set<String> = emptySet(),
    val deImporting: Boolean = false
)

class DeImportScreenVM(val mutator: SpellMutator) : ViewModel() {
    private val _state = MutableStateFlow(
        DeImportScreenState()
    )

    val state = _state
        .onStart {
            observeSpells()
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            _state.value
        )

    private var observeSpellsJob : Job? = null;
    private fun observeSpells(){
        observeSpellsJob?.cancel()
        _state.update { it.copy(spells = ComposeLoadable()) }
        observeSpellsJob = mutator.getSpells().onEach{ spells ->
            _state.update { it.copy(spells = ComposeLoadable(spells)) }
        }.launchIn(viewModelScope)
    }

    private var deleteJob: Job? = null
    fun doDeImport(){
        if(deleteJob != null){return}
        _state.update { it.copy(deImporting = true) }
        deleteJob = viewModelScope.launch {
            doDeImportSus()
            _state.update { it.copy(deImporting = false, selectedImportKeys = emptySet()) }
            deleteJob = null
        }
    }

    private suspend fun doDeImportSus(){
        val state = state.value
        val filter = SpellListFilter(sources = state.selectedImportKeys)
        val allSpells = state.spells.concreteState ?: return
        val spellIdsToDelete = filter.filter(allSpells).map { it.key }
        coroutineScope {
            spellIdsToDelete.map {
                async { mutator.deleteSpell(it) }
            }.awaitAll()
        }
    }

    fun setSelectedImportKeys(newSet: Set<String>){
        _state.update { it.copy(selectedImportKeys = newSet) }
    }
}