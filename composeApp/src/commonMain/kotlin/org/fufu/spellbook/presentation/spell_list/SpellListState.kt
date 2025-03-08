package org.fufu.spellbook.presentation.spell_list

import org.fufu.spellbook.domain.Spell

data class SpellListState(
    val displayedSpells: List<Spell>,
)