package org.fufu.spellbook.spell.domain

data class ImportPolicy(
    val matchByLevel: Boolean = false,
    val update: Boolean = true,
    val unique: Boolean = true,
){
    fun matches(
        destination: SpellInfo,
        source: SpellInfo
    ): Boolean {


        if(matchByLevel && destination.level != source.level || destination.name != source.name) {
            return false
        }

        return true
    }

    data class Segmentation(
        /**
         * List of spells that should be updated in the destination database. Keys are already set.
         */
        val update: List<Spell>,
        /**
         * Each element is a unique Spell that should be added to the destination as a new spell
         */
        val unique: List<Spell>
    ) {
        fun isEmpty(): Boolean {
            return update.isEmpty() && unique.isEmpty()
        }
    }

    fun segment(
        destination: Iterable<Spell>,
        source: Iterable<Spell>
    ): Segmentation {
        val update: MutableList<Spell> = mutableListOf()
        val unique: MutableList<Spell> = mutableListOf()

        source.forEach { sourceSpell ->
            // find a spell in the destination that matches the one from the source
            val match = destination.find { this.matches(it.info, sourceSpell.info) }
            if(match != null) {
                if(this.update && !match.info.isCustomSpell() && match.info.withoutImportTags() != sourceSpell.info.withoutImportTags()) {
                    var sources = sourceSpell.info.sources
                    getMaxImportNum(match)?.let {
                        sources = sources.plus(importTagFor(it))
                    }
                    val spellToUpdate = sourceSpell.copy(
                        key=match.key,
                        info=sourceSpell.info.copy(sources=sources)
                    )
                    update.add(spellToUpdate)
                }
            } else if(this.unique) {
                unique.add(sourceSpell)
            }
        }

        return Segmentation(update, unique)
    }
}