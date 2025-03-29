package org.fufu.spellbook.spell.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.vinceglb.filekit.PlatformFile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
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

sealed interface ImportSource {
    data class JSON(val file: PlatformFile?) : ImportSource
    data object WIKIDOT : ImportSource
    data object SELECT : ImportSource
}

data class ImportScreenState(
    val availableSpells: List<Spell> = emptyList(),
    val importSource: ImportSource = ImportSource.SELECT,
    val loading: Boolean = true
)

class ImportScreenVM(
    private val destination: SpellMutator,
    private var provider: SpellProvider?
) : ViewModel() {
    private val _state = MutableStateFlow(
        ImportScreenState()
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
        val provider = provider ?: return
        provider.getSpells().onEach{ spells ->
            _state.update { it.copy(availableSpells = spells, loading = false) }
        }.launchIn(viewModelScope)
    }

    fun onChangeSource(source: ImportSource){
        _state.update { it.copy(importSource = source) }
    }

    fun useProvider(provider: SpellProvider){
        this.provider = provider
        observeSpells()
    }
}