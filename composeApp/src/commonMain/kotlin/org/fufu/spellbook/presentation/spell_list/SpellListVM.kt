package org.fufu.spellbook.presentation.spell_list

import androidx.lifecycle.ViewModel
import org.fufu.spellbook.domain.Spell

class SpellListVM(
    private val allSpells: List<Spell>
) : ViewModel() {
    private val _state = mutableStateFlow()
}