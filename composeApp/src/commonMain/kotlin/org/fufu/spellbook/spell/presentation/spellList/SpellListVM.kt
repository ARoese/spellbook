package org.fufu.spellbook.spell.presentation.spellList

import androidx.compose.ui.text.toLowerCase
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import org.fufu.spellbook.spell.domain.Book
import org.fufu.spellbook.spell.domain.DamageType
import org.fufu.spellbook.spell.domain.DragonMark
import org.fufu.spellbook.spell.domain.MagicSchool
import org.fufu.spellbook.spell.domain.SaveType
import org.fufu.spellbook.spell.domain.Spell
import org.fufu.spellbook.spell.domain.SpellProvider
import java.util.Locale

data class SpellListFilter(
    val book: Set<Book>? = null,
    val classes: Set<String>? = null,
    val components: String? = null,
    val duration: String? = null,
    val guilds: Set<String>? = null,
    val level: Set<Int>? = null,
    val name: String? = null,
    val optional: List<String>? = null,
    val range: String? = null,
    val ritual: Boolean? = null,
    val school: Set<MagicSchool>? = null,
    val text: String? = null,
    val time: String? = null,
    val tag: Set<String>? = null,
    val damages: Set<DamageType>? = null,
    val saves: Set<SaveType>? = null,
    val dragonmarks: Set<DragonMark>? = null,

    // non-user configurable
    val onlyIds: Set<Int>? = null
){
    fun matches(spell: Spell) : Boolean {
        val info = spell.info
        return onlyIds?.let { spell.key in it } != false
                && book?.let{ info.book.intersect(it).isEmpty() } != true
                && level?.let { info.level in it } != false
                && ritual?.let { info.ritual == ritual } != false
                && school?.let { info.school in it } != false
                && damages?.let { info.damages.intersect(it).isEmpty() } != true
                && saves?.let { info.saves.intersect(it).isEmpty() } != true
                && tag?.let { info.tag.intersect(it).isEmpty() } != true
                && dragonmarks?.let { info.dragonmarks.intersect(it).isEmpty() } != true
                && name?.let { hasString(spell.info.name, it) } != false
                && classes?.let { classes -> info.classes.any { cl -> cl in classes } } != false
    }

    private fun hasString(string: String, filter: String) : Boolean {
        return filterableString(string).contains(filterableString(filter))
    }

    private fun filterableString(string: String) : String {
        return string
            .lowercase()
            .filter { it.isLetter() }
    }

    fun hasActiveCriteria() : Boolean {
        return book != null
                || classes != null
                || components != null
                || duration != null
                || guilds != null
                || level != null
                || name != null
                || optional != null
                || range != null
                || ritual != null
                || school != null
                || text != null
                || time != null
                || tag != null
                || damages != null
                || saves != null
                || dragonmarks != null
    }

    fun clear() : SpellListFilter {
        return SpellListFilter().copy(onlyIds = onlyIds)
    }

    fun filter(spells: List<Spell>) : List<Spell> {
        return spells.filter { matches(it) }
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

    private fun observeFilter(){
        _state
            .map{it.filter}
            .distinctUntilChanged()
            //.debounce(250)
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