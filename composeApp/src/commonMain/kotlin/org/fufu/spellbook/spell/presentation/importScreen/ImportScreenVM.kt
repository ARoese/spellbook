package org.fufu.spellbook.spell.presentation.importScreen

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
import kson.KsonApi
import org.fufu.spellbook.composables.ComposeLoadable
import org.fufu.spellbook.spell.data.json.JsonSpellProvider
import org.fufu.spellbook.spell.data.srd5eapi.SRD5eSpellProvider
import org.fufu.spellbook.spell.data.srd5eapi.makeClient
import org.fufu.spellbook.spell.domain.Spell
import org.fufu.spellbook.spell.domain.SpellMutator
import org.fufu.spellbook.spell.domain.SpellProvider
import org.fufu.spellbook.spell.domain.importFrom

sealed interface ImportSource {
    data class JSON(val file: PlatformFile?) : ImportSource
    data object WIKIDOT : ImportSource
    data object SRD5E : ImportSource
    data object SELECT : ImportSource
}

data class ImportScreenState(
    val availableSpells: ComposeLoadable<List<Spell>> = ComposeLoadable(emptyList()),
    val currentSpells: ComposeLoadable<List<Spell>> = ComposeLoadable(),
    val importSource: ImportSource = ImportSource.SELECT,
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
            observeImportSpells()
            observeCurrentSpells()
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            _state.value
        )

    private var observeImportSpellsJob : Job? = null;
    private fun observeImportSpells(){
        observeImportSpellsJob?.cancel()
        val provider = provider ?: return
        _state.update { it.copy(availableSpells = ComposeLoadable()) }
        observeImportSpellsJob = provider.getSpells().onEach{ spells ->
            _state.update { it.copy(availableSpells = ComposeLoadable(spells)) }
        }.launchIn(viewModelScope)
    }

    private var observeCurrentSpellsJob : Job? = null;
    private fun observeCurrentSpells(){
        observeCurrentSpellsJob?.cancel()
        _state.update { it.copy(currentSpells = ComposeLoadable()) }
        observeCurrentSpellsJob = destination.getSpells().onEach{ spells ->
            _state.update { it.copy(currentSpells = ComposeLoadable(spells)) }
        }.launchIn(viewModelScope)
    }

    fun onChangeSource(source: ImportSource){
        _state.update { it.copy(importSource = source, availableSpells = ComposeLoadable(emptyList())) }
        when(source){
            is ImportSource.JSON -> source.file?.let{useProvider(JsonSpellProvider(it))}
            ImportSource.WIKIDOT -> return
            ImportSource.SRD5E -> useProvider(SRD5eSpellProvider(KsonApi(makeClient())))
            ImportSource.SELECT -> return
        }
    }

    private var importJob: Job? = null
    fun doImport(ids: Set<Int>? = null) {
        val prov = provider ?: return
        importJob?.cancel("canceled")
        _state.update { it.copy(importing = true) }
        importJob = CoroutineScope(Dispatchers.IO).launch {
            destination.importFrom(prov, ids=ids, scope = viewModelScope) { progress ->
                _state.update {it.copy(importProgress = progress)}
            }
            _state.update { ImportScreenState() }
        }
    }

    private fun useProvider(provider: SpellProvider){
        this.provider = provider
        observeImportSpells()
    }
}