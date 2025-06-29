package org.fufu.spellbook.character.presentation.characterDetail

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import org.fufu.spellbook.character.domain.Character
import org.fufu.spellbook.character.domain.hasPreparedSpell
import org.fufu.spellbook.character.domain.knowsSpell
import org.fufu.spellbook.composables.ClickableToken
import org.fufu.spellbook.composables.KnownToken
import org.fufu.spellbook.composables.PreparedToken
import org.fufu.spellbook.spell.domain.Spell
import org.fufu.spellbook.spell.presentation.spellList.SpellList

class SpellListVariantView(
    val variant: SpellListVariant,
    val character: Character,
    val intend: (Intent) -> Unit
) {
    @Composable
    fun Nav(){
        NavigationBar {
            NavigationBarItem(
                onClick = { intend(Intent.ChangeListVariant(SpellListType.PREPARED)) },
                label = { Text("Prepared") },
                selected = variant.type == SpellListType.PREPARED,
                icon = {},
                alwaysShowLabel = true
            )
            NavigationBarItem(
                onClick = { intend(Intent.ChangeListVariant(SpellListType.KNOWN)) },
                label = { Text("Known") },
                selected = variant.type == SpellListType.KNOWN,
                icon = {},
                alwaysShowLabel = true
            )
            NavigationBarItem(
                onClick = { intend(Intent.ChangeListVariant(SpellListType.CLASS)) },
                label = { Text("Class") },
                selected = variant.type == SpellListType.CLASS,
                icon = {},
                alwaysShowLabel = true
            )
        }
    }

    @Composable
    private fun KnownSpellListRightSideButton(spell: Spell){
        val prepared = character.hasPreparedSpell(spell.key)
        val hasMaxPrepared = character.spells.count { it.value } >=
                character.maxPreparedSpells
        val isEnabled = !hasMaxPrepared || prepared
        ClickableToken(
            enabled = isEnabled,
            onClick = {intend(Intent.SetSpellPreparedness(spell, !prepared))}
        ) {
            PreparedToken(prepared = prepared, enabled = isEnabled)
        }
    }

    @Composable
    private fun ClassSpellListRightSideButton(spell: Spell){
        val known = character.knowsSpell(spell.key)
        ClickableToken(
            onClick = {intend(Intent.SetSpellLearnedness(spell, !known))}
        ) {
            KnownToken(known = known)
        }
    }

    @Composable
    private fun RightSideButton(spell: Spell){
        when(variant.type){
            SpellListType.PREPARED -> null
            SpellListType.KNOWN -> KnownSpellListRightSideButton(spell)
            SpellListType.CLASS -> ClassSpellListRightSideButton(spell)
        }
    }

    @Composable
    private fun PreparedHeaderContent(level: Int){
        character.spellSlots[level]?.let { slotLevel ->
            if(slotLevel.maxSlots != 0){
                SpellSlotLevelDisplay(
                    slotLevel,
                    onChange = { newLevel ->
                        intend(Intent.SetSpellSlotLevel(level, newLevel))
                    }
                )
            }
        }
    }

    @Composable
    private fun HeaderContent(level: Int){
        if(variant.type == SpellListType.PREPARED){
            PreparedHeaderContent(level)
        }
    }

    @Composable
    fun Display(){
        Column {
            Nav()

            val necessarySpellLevels = when(variant.type){
                SpellListType.PREPARED -> character.spellSlots
                    .filter { it.value.maxSlots != 0 }
                    .keys
                else -> emptySet()
            }


            SpellList(variant.state,
                onSpellSelected = { intend(Intent.ViewSpell(it)) },
                rightSideButton = { RightSideButton(it)},
                headerContent = { HeaderContent(it) },
                showFilterOptions = variant.type == SpellListType.CLASS,
                onChangeFilter = {
                    intend(Intent.SetListFilter(it))
                },
                shouldGroupByLevel = true,
                necessarySpellLevels = necessarySpellLevels
            )
        }
    }
}