package org.fufu.spellbook.spell.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.vinceglb.filekit.PlatformFile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.fufu.spellbook.spell.data.json.JsonSpellProvider
import org.fufu.spellbook.spell.domain.Spell
import org.fufu.spellbook.spell.domain.SpellMutator
import org.fufu.spellbook.spell.domain.SpellProvider
import org.fufu.spellbook.spell.domain.importFrom

sealed interface ImportSource {
    data class JSON(val file: PlatformFile?) : ImportSource
    data object WIKIDOT : ImportSource
    data object SELECT : ImportSource
}

data class ImportScreenState(
    val availableSpells: List<Spell> = emptyList(),
    val importSource: ImportSource = ImportSource.SELECT,
    val loading: Boolean = false,
    val importing: Boolean = false,
    val importProgress: Float = 0f
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
        _state.update { it.copy(availableSpells = emptyList(), loading = true) }
        observeSpellsJob = provider.getSpells().onEach{ spells ->
            _state.update { it.copy(availableSpells = spells, loading = false) }
        }.launchIn(viewModelScope)
    }

    fun onChangeSource(source: ImportSource){
        _state.update { it.copy(importSource = source, availableSpells = emptyList()) }
        when(source){
            is ImportSource.JSON -> source.file?.let{useProvider(JsonSpellProvider(it))}
            ImportSource.SELECT -> return
            ImportSource.WIKIDOT -> return
        }
    }

    private var importJob: Job? = null
    fun doImport() {
        val prov = provider ?: return
        importJob?.cancel("canceled")
        _state.update { it.copy(importing = true) }
        importJob = CoroutineScope(Dispatchers.IO).launch {
            destination.importFrom(prov) { progress ->
                _state.update {it.copy(importProgress = progress)}
            }
            _state.update { it.copy(importing = false) }
        }
    }

    private fun useProvider(provider: SpellProvider){
        this.provider = provider
        observeSpells()
    }
}