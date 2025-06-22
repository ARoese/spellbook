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
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.fufu.spellbook.composables.ChipSize
import org.fufu.spellbook.composables.TagChip
import org.fufu.spellbook.spell.domain.Spell

// sorted by name, then by level. Levels are grouped together, and
// within that, names are sorted alphabetically
fun orderSpellList(
    spells: List<Spell>,
    shouldGroupByLevel: Boolean
) : List<Spell> {
    return spells
        .sortedBy { it.info.name.lowercase().filter { it.isLetterOrDigit() } }
        .let {
            if(shouldGroupByLevel)
            { it.sortedBy { e -> e.info.level } }
            else
            { it }
        }
}

fun groupSpellsByLevels(
    spells: List<Spell>,
    necessaryLevels: Set<Int>
): Map<Int, List<Spell>>{
    val groups = orderSpellList(spells, true)
        .groupBy { it.info.level }
    val remainingLevels = necessaryLevels
        .subtract(groups.keys)
        .associateWith { emptyList<Spell>() }
    return groups + remainingLevels
}

fun LazyListScope.ungroupedSpellList(
    state: SpellListState,
    onSpellSelected: (Spell) -> Unit,
    rightSideButton: (@Composable (Spell) -> Unit)? = null,
){
    val sortedSpells : List<Spell> = orderSpellList(
        state.displayedSpells,
        false
    )
    sortedSpells.forEach{ spell ->
        item(key=spell.key){
            SpellListItem(spell, {onSpellSelected(spell)}, rightSideButton)
            HorizontalDivider()
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
fun LazyListScope.groupedSpellList(
    state: SpellListState,
    onSpellSelected: (Spell) -> Unit,
    rightSideButton: (@Composable (Spell) -> Unit)? = null,
    headerContent: @Composable (Int) -> Unit = {},
    necessaryLevels: Set<Int>,
){
    val spellGroups = groupSpellsByLevels(state.displayedSpells, necessaryLevels)

    spellGroups.toList()
        .sortedBy{it.first}
        .forEach{ mapItem ->
        val (spellLevel, spells) = mapItem
        stickyHeader{
            SpellListStickyHeader(
                "Level $spellLevel"
            ) { headerContent(spellLevel) }
        }
        spells.forEach{ spell ->
            item(key=spell.key){
                SpellListItem(spell, {onSpellSelected(spell)}, rightSideButton)
                HorizontalDivider()
            }
        }
    }
}

@Composable
fun SpellList(
    state: SpellListState,
    onSpellSelected: (Spell) -> Unit,
    rightSideButton: (@Composable (Spell) -> Unit)? = null,
    headerContent: @Composable (Int) -> Unit = {},
    shouldGroupByLevel: Boolean = true,
    necessarySpellLevels: Set<Int> = emptySet(),
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
                LazyColumn(modifier = Modifier
                    .padding(horizontal = 5.dp)
                ) {
                    if (shouldGroupByLevel) {
                        groupedSpellList(
                            state,
                            onSpellSelected,
                            rightSideButton,
                            headerContent,
                            necessarySpellLevels
                        )
                    } else {
                        ungroupedSpellList(
                            state,
                            onSpellSelected,
                            rightSideButton,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SpellListStickyHeader(
    text: String,
    centerContent: (@Composable () -> Unit)? = null
){
    Box(
        modifier= Modifier
            .background(color = MaterialTheme.colorScheme.secondaryContainer)
            .fillMaxWidth()
    ){
        centerContent?.let{
            Box(
                modifier = Modifier.align(Alignment.Center)
            ){
                it()
            }
        }
        Text(
            text,
            color = MaterialTheme.colorScheme.onSecondaryContainer,
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
                text=spell.info.school,
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