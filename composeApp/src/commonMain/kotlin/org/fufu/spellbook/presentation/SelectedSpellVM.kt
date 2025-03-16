package org.fufu.spellbook.presentation

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.fufu.spellbook.domain.Spell

class SelectedSpellVM: ViewModel() {

    private val _selectedSpell = MutableStateFlow<Spell?>(null)
    val selectedBook = _selectedSpell.asStateFlow()

    fun onSelectSpell(book: Spell?) {
        _selectedSpell.value = book
    }
}