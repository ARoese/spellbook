package org.fufu.spellbook.presentation.spell_list

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
import kotlinx.coroutines.launch
import org.fufu.spellbook.domain.Spell
import org.fufu.spellbook.domain.SpellProvider

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
            viewModelScope.launch{
                delay(1000)
                val spells = provider.getSpells()
                _state.update{
                    it.copy(
                        knownSpells = spells,
                        loading = false
                    )
                }
            }
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
            .onEach{
                _state.update {
                    it.copy(
                        displayedSpells = it.filter.filter(it.knownSpells)
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