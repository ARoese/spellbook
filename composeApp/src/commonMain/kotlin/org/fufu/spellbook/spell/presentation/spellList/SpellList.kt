package org.fufu.spellbook.spell.presentation.spellList

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.FlowRowOverflow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.fufu.spellbook.composables.ChipSize
import org.fufu.spellbook.composables.TagChip
import org.fufu.spellbook.spell.domain.Spell

fun orderSpellList(
    spells: List<Spell>,
    shouldGroupByLevel: Boolean
) : List<Spell> {
    return spells
        .sortedBy { it.info.name }
        .let {
            if(shouldGroupByLevel)
            { it.sortedBy { e -> e.info.level } }
            else
            { it }
        }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SpellList(
    state: SpellListState,
    onSpellSelected: (Spell) -> Unit,
    rightSideButton: (@Composable (Spell) -> Unit)? = null,
    shouldGroupByLevel: Boolean = true,
    onChangeFilter: (SpellListFilter) -> Unit = {},
    showFilterOptions: Boolean = false
) {
    Box(modifier = Modifier.fillMaxWidth()){
        if(state.loading){
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }else{
            Column {
                if(showFilterOptions){
                    SpellListFilterSelector(state, onChangeFilter)
                }

                // sorted by name, then by level. Levels are grouped together, and
                // within that, names are sorted alphabetically
                val sortedSpells : List<Spell> = orderSpellList(
                    state.displayedSpells,
                    shouldGroupByLevel
                )
                val numSpells = sortedSpells.size
                LazyColumn(modifier = Modifier
                    .padding(horizontal = 5.dp)
                ){
                    (0 until numSpells).forEach{
                        val spell = sortedSpells[it]
                        val lastSpell = sortedSpells.getOrNull(it-1)
                        // if the last spell has a different level than this one
                        // if there is no last spell, this is also true
                        val needsStickyHeader =
                            shouldGroupByLevel && lastSpell?.info?.level != spell.info.level
                        if(needsStickyHeader){
                            stickyHeader{
                                SpellListStickyHeader("Level ${spell.info.level}")
                            }
                        }
                        item(key=spell.key){
                            SpellListItem(spell, {onSpellSelected(spell)}, rightSideButton)
                            HorizontalDivider()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SpellListStickyHeader(
    text: String
){
    Box(
        modifier= Modifier
            .background(color = Color.LightGray)
            .fillMaxWidth()
    ){
        Text(
            text,
            modifier = Modifier.align(Alignment.CenterEnd)
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SpellListItem(
    spell: Spell,
    onClick: () -> Unit,
    rightSideButton: (@Composable (Spell) -> Unit)? = null
){
    Row(modifier = Modifier
        .padding(vertical=5.dp)
        .fillMaxWidth()
        .clickable(onClick = onClick)
    ){
        Column(modifier= Modifier.weight(1f)){
            Text(
                spell.info.name,
                style= MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                overflow= TextOverflow.Ellipsis
            )

            Text(
                text=spell.info.school.name,
                fontStyle= FontStyle.Italic,
                style= MaterialTheme.typography.titleSmall
            )
        }

        FlowRow (
            modifier= Modifier
                .weight(1f)
                .align(Alignment.CenterVertically),
            horizontalArrangement = Arrangement.Center,
            overflow= FlowRowOverflow.Clip
        ){
            spell.info.tag.forEach{
                Box(modifier= Modifier.padding(2.dp)){
                    TagChip(it, ChipSize.SMALL)
                }
            }
        }

        if(rightSideButton == null){
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Open spell",
                modifier= Modifier.align(Alignment.CenterVertically)
            )
        }else{
            Box(
                modifier = Modifier.align(Alignment.CenterVertically)
            ){
                rightSideButton(spell)
            }
        }
    }
}