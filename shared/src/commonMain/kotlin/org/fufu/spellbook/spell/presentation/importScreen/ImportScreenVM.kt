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
import org.fufu.spellbook.composables.ComposeLoadable
import org.fufu.spellbook.spell.data.json.JsonSpellProvider
import org.fufu.spellbook.spell.data.srd5eapi.SRD5eSpellProvider
import org.fufu.spellbook.spell.domain.ImportPolicy
import org.fufu.spellbook.spell.domain.Spell
import org.fufu.spellbook.spell.domain.SpellMutator
import org.fufu.spellbook.spell.domain.SpellProvider
import org.fufu.spellbook.spell.domain.getMaxImportNum
import org.fufu.spellbook.spell.domain.importTagFor

sealed interface ImportSource {
    data class JSON(val file: PlatformFile?) : ImportSource
    data object WIKIDOT : ImportSource
    data object SRD5E : ImportSource
    data object SELECT : ImportSource
}

data class ImportScreenState(
    val currentSpells: ComposeLoadable<List<Spell>> = ComposeLoadable(),
    val importSource: ImportSource = ImportSource.SELECT,
    val availableSpells: ComposeLoadable<List<Spell>> = ComposeLoadable(emptyList()),
    val filteredAvailableSpells: ComposeLoadable<ImportPolicy.Segmentation> = ComposeLoadable(),
    val importPolicy: ImportPolicy = ImportPolicy(update = true, unique = true, matchByLevel = false),
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
            setImportPolicy(_state.value.importPolicy)
        }.launchIn(viewModelScope)
    }

    private var observeCurrentSpellsJob : Job? = null;
    private fun observeCurrentSpells(){
        observeCurrentSpellsJob?.cancel()
        _state.update { it.copy(currentSpells = ComposeLoadable()) }
        observeCurrentSpellsJob = destination.getSpells().onEach{ spells ->
            _state.update { it.copy(currentSpells = ComposeLoadable(spells)) }
            setImportPolicy(_state.value.importPolicy)
        }.launchIn(viewModelScope)
    }

    fun onChangeSource(source: ImportSource){
        _state.update { it.copy(importSource = source, availableSpells = ComposeLoadable(emptyList())) }
        when(source){
            is ImportSource.JSON -> source.file?.let{useProvider(JsonSpellProvider(it))}
            ImportSource.WIKIDOT -> return
            ImportSource.SRD5E -> useProvider(SRD5eSpellProvider())
            ImportSource.SELECT -> return
        }
    }

    fun setImportPolicy(new: ImportPolicy) {
        _state.update {
            val currentSpells = it.currentSpells.concreteState
            val availableSpells = it.availableSpells.concreteState
            if (currentSpells == null || availableSpells == null) {
                return@update it
            }

            it.copy(
                importPolicy = new,
                filteredAvailableSpells = ComposeLoadable(new.segment(currentSpells, availableSpells))
            )
        }
    }

    private var importJob: Job? = null
    fun doImport() {
        importJob?.cancel("canceled")
        _state.update { it.copy(importing = true) }
        val segmentationToUse = _state.value.filteredAvailableSpells.concreteState ?: return
        importJob = CoroutineScope(Dispatchers.IO).launch {
            val totalWork = segmentationToUse.update.size + segmentationToUse.unique.size
            val progress = { workDone: Int ->
                _state.update { it.copy(importProgress = workDone.toFloat()/totalWork) }
            }
            var workDone = 0
            val importNum = segmentationToUse.unique.getMaxImportNum()+1
            val importTag = importTagFor(importNum)
            // import unique spells
            for(spell in segmentationToUse.unique) {
                destination.addSpell(spell.info.copy(sources = spell.info.sources.plus(importTag)))
                workDone += totalWork
                progress(workDone)
            }
            // update existing spells
            for(spell in segmentationToUse.update) {
                destination.setSpell(spell)
                workDone += totalWork
                progress(workDone)
            }
            _state.update { ImportScreenState() }
        }
    }

    private fun useProvider(provider: SpellProvider){
        this.provider = provider
        observeImportSpells()
    }
}