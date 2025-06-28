package org.fufu.spellbook.spell.domain

data class ImportPolicy(
    val matchByName: Boolean = false,
    val matchByLevel: Boolean = false
){
    fun filterShouldImport(
        existingSpells: List<Spell>,
        possibleImports: List<Spell>
    ): List<Spell> {
        if(!matchByName && !matchByLevel) return possibleImports
        return possibleImports.filter { incomingSpell ->
            SpellListFilter(
                name = incomingSpell.info.name.takeIf { matchByName },
                level = setOf(incomingSpell.info.level).takeIf{ matchByLevel }

            ).filter(existingSpells).isEmpty() // only import if no existing spell
        }
    }
}