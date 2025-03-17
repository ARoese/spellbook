package org.fufu.spellbook.spell.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import org.fufu.spellbook.spell.domain.Spell
import org.fufu.spellbook.spell.domain.SpellProvider

data class SpellListFilter(
    val name: String? = null,
    val onlyIds: Set<Int>? = null
){
    fun filter(spells: List<Spell>) : List<Spell>{
        return spells.filter{onlyIds?.contains(it.key) ?: true}
    }
}

data class SpellListState(
    val knownSpells: List<Spell> = emptyList(),
    val displayedSpells: List<Spell> = emptyList(),
    val filter: SpellListFilter = SpellListFilter(),
    val loading: Boolean = true
)

class SpellListVM(
    private val provider : SpellProvider
) : ViewModel() {
    private val _state = MutableStateFlow(SpellListState())
    val state = _state
        .onStart {
            observeFilter()
            observeSpells()
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            _state.value
        )

    @OptIn(FlowPreview::class)
    private fun observeFilter(){
        _state
            .map{it.filter}
            .distinctUntilChanged()
            .debounce(1000)
            .onEach{ filter ->
                _state.update {
                    it.copy(
                        displayedSpells = filter.filter(it.knownSpells)
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    private fun observeSpells(){
        provider.getSpells()
            .onEach { spells ->
                delay(1000)
                _state.update{
                    it.copy(
                        knownSpells = spells,
                        displayedSpells = it.filter.filter(spells),
                        loading = false
                    )
                }
            }
            .launchIn(viewModelScope)

    }

    fun useFilter(newFilter : SpellListFilter){
        _state.update {
            it.copy(filter = newFilter)
        }
    }
}