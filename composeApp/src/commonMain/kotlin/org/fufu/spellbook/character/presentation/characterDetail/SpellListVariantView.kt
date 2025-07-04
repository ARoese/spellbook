package org.fufu.spellbook.character.presentation.characterDetail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import org.fufu.spellbook.character.domain.Character
import org.fufu.spellbook.character.domain.hasPreparedSpell
import org.fufu.spellbook.character.domain.knowsSpell
import org.fufu.spellbook.composables.ClickableToken
import org.fufu.spellbook.composables.DropdownSelector
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
            onClick = {intend(Intent.SetSpellPreparedness(spell.key, !prepared))}
        ) {
            PreparedToken(prepared = prepared, enabled = isEnabled)
        }
    }

    @Composable
    private fun ClassSpellListRightSideButton(spell: Spell){
        val known = character.knowsSpell(spell.key)
        ClickableToken(
            onClick = {intend(Intent.SetSpellLearnedness(spell.key, !known))}
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

    private fun saveCurrentPrepListAs(
        name: String
    ){
        val newListItems = character.spells.filter { it.value }.keys.toSet()
        val newList = character.preparedSpellLists.plus(
            name to newListItems
        )
        intend(Intent.SetSpellPrepLists(newList))
    }

    private fun deletePrepList(
        name: String
    ){
        val newLists = character.preparedSpellLists.filter { it.key != name }
        intend(Intent.SetSpellPrepLists(newLists))
    }

    @Composable
    private fun SpellPrepListNameField(
        visible: Boolean,
        onMakeInvisible: () -> Unit
    ){
        var name by remember { mutableStateOf("") }
        AnimatedVisibility(visible){
            Row(
                modifier = Modifier.fillMaxWidth()
            ){
                TextField(name, onValueChange = {name = it})
                IconButton(onClick = onMakeInvisible){
                    Icon(Icons.Default.Close, "Cancel")
                }
                IconButton(
                    enabled = name.isNotEmpty(),
                    onClick = {
                        saveCurrentPrepListAs(name)
                        name = ""
                        onMakeInvisible()
                    }
                ){
                    Icon(Icons.Default.Done, "submit")
                }
            }

        }
    }

    private fun setSpellsPrepared(spells: Set<Int>) {
        val newSpellPreps = character.spells.mapValues {
            it.key in spells
        }

        intend(Intent.SetSpellsPreparedness(newSpellPreps))
    }

    @Composable
    private fun PreparedListSelector() {
        Column {
            var isNameFieldVisible by remember { mutableStateOf(false) }
            var deleting by remember { mutableStateOf(false) }
            Row {
                Row(
                    modifier = Modifier
                        .horizontalScroll(rememberScrollState())
                        .weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ){
                    character.preparedSpellLists.forEach{ (name, spells) ->
                        val enabled = deleting || character.spells
                            .filter { it.value }
                            .map { it.key }
                            .toSet() != spells
                        Button(
                            onClick = {
                                if(deleting){
                                    deleting = false
                                    deletePrepList(name)
                                }else{
                                    setSpellsPrepared(spells)
                                }
                            },
                            enabled = enabled
                        ){
                            if(deleting){
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                                ){
                                    Icon(Icons.Default.Delete, "Delete")
                                    Text(name)
                                }
                            }else{
                                Text(name)
                            }
                        }
                    }

                    IconButton(
                        onClick = {
                            isNameFieldVisible = !isNameFieldVisible
                        }
                    ){
                        Icon(Icons.Default.Add, "Add prep list")
                    }
                }
                IconButton(
                    onClick = {
                        deleting = !deleting
                    }
                ){
                    if(deleting){
                        Icon(Icons.Default.Done, "Done deleting")
                    }else{
                        Icon(Icons.Default.Delete, "Delete prep list")
                    }
                }
            }
            SpellPrepListNameField(isNameFieldVisible){
                isNameFieldVisible = false
            }
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

            if(variant.type == SpellListType.KNOWN){
                PreparedListSelector()
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