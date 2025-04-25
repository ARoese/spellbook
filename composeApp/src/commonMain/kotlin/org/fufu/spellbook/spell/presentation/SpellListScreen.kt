package org.fufu.spellbook.spell.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.FlowRowOverflow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DropdownMenu
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.fufu.spellbook.composables.FloatingAddButton
import org.fufu.spellbook.spell.domain.Book
import org.fufu.spellbook.spell.domain.DamageType
import org.fufu.spellbook.spell.domain.MagicSchool
import org.fufu.spellbook.spell.domain.SaveType
import org.fufu.spellbook.spell.domain.Spell

@Composable
fun SpellListScreenRoot(
    viewModel: SpellListVM,
    onSpellSelected: (Spell) -> Unit,
    onNewClicked: () -> Unit,
    navBar: @Composable () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    SpellListScreen(
        state,
        onSpellSelected,
        onNewClicked,
        navBar,
        onChangeFilter = {viewModel.useFilter(it)},
    )
}

@Composable
fun SpellListRoot(
    viewModel: SpellListVM,
    onSpellSelected: (Spell) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    SpellList(
        state,
        onSpellSelected,
        onChangeFilter = {viewModel.useFilter(it)},
        showFilterOptions = true
    )
}

@Composable
fun SpellListScreen(
    state: SpellListState,
    onSpellSelected: (Spell) -> Unit,
    onNewClicked: () -> Unit = {},
    navBar: @Composable () -> Unit,
    onChangeFilter: (SpellListFilter) -> Unit = {}
){
    Scaffold(
        bottomBar = navBar,
        floatingActionButton = {
            FloatingAddButton(onNewClicked)
        }
    ){ padding ->
        Box(modifier = Modifier.padding(padding)){
            SpellList(
                state,
                onSpellSelected,
                onChangeFilter = onChangeFilter,
                showFilterOptions = true
            )
        }
    }
}

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

@Composable
fun <T>DropdownSelector(
    options: List<T>,
    selected: Set<T>? = null,
    optionPresenter: @Composable (T) -> Unit,
    onOptionPicked: (T)-> Unit= {},
    buttonContent: @Composable RowScope.() -> Unit
){
    Box{
        var expanded by remember { mutableStateOf(false) }
        val scrollState = rememberScrollState()
        Button(
            onClick = {expanded = true},
            content = buttonContent
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = {expanded = false},
            scrollState = scrollState
        ){
            options.forEach {
                val background = if(it in (selected ?: emptySet()))
                    {Color.LightGray }
                    else{ Color.Unspecified }
                DropdownMenuItem(
                    modifier = Modifier.background(background),
                    text = { optionPresenter(it) },
                    onClick = {
                        //expanded = false
                        onOptionPicked(it)
                    }
                )
            }
        }
    }
}

@Composable
fun BooleanSelector(
    state: Boolean?,
    onChange: (Boolean?) -> Unit,
    content: @Composable RowScope.() -> Unit
){
    val lime = Color(0xFF64FF64)
    val cherry = Color(0xFFFF6464)
    val unspecified = Color.Unspecified
    val bgColor = state?.let{if(it){lime}else{cherry} } ?: unspecified
    fun nextState() : Boolean? {
        return when(state){
            true -> false
            false -> null
            null -> true
        }
    }
    val buttonColors = ButtonDefaults.buttonColors(containerColor = bgColor)
    OutlinedButton(
        onClick = {onChange(nextState())},
        content = content,
        colors = buttonColors
    )
}

fun <T> Set<T>.xor(other: Set<T>): Set<T> {
    return this.union(other) - this.intersect(other)
}

fun <T> nullingXor(self: Set<T>?, other: Set<T>): Set<T>? {
    return (self?.xor(other) ?: other).ifEmpty { null }
}

fun <T> nullingXor(self: Set<T>?, other: T): Set<T>? {
    val oneSet = setOf(other)
    return (self?.xor(oneSet) ?: oneSet).ifEmpty { null }
}

@Composable
fun RowScope.SpellListFilterSelectorItems(
    state: SpellListState,
    onChangeFilter: (SpellListFilter) -> Unit = {}
){
    DropdownSelector(
        (0..9).toList(),
        state.filter.level,
        { Text("$it") },
        { level ->
            onChangeFilter(state.filter.let{
                it.copy(level = nullingXor(it.level, level))
            })
        }
    ){
        Text("Level")
    }
    //VerticalDivider()
    DropdownSelector(
        MagicSchool.entries,
        state.filter.school,
        { Text(it.name) },
        { school ->
            onChangeFilter(state.filter.let {
                it.copy(school = nullingXor(it.school, school))
            })
        }
    ){
        Text("School")
    }
    //VerticalDivider()
    DropdownSelector(
        Book.entries,
        state.filter.book,
        { Text(it.name) },
        { book ->
            onChangeFilter(state.filter.let {
                it.copy(book = nullingXor(it.book, book))
            })
        }
    ){
        Text("Book")
    }
    BooleanSelector(
        state.filter.ritual,
        {onChangeFilter(state.filter.copy(ritual = it))}
    ){
        Text("Ritual")
    }
    val allTags = state.knownSpells
        .map { it.info.tag }
        .flatten()
        .toSet()
    DropdownSelector(
        allTags.toList(),
        state.filter.tag,
        { Text(it) },
        { tag ->
            onChangeFilter(state.filter.let {
                it.copy(tag = nullingXor(it.tag, tag))
            })
        }
    ){
        Text("Tag")
    }
    DropdownSelector(
        DamageType.entries,
        state.filter.damages,
        { Text(it.name) },
        { damage ->
            onChangeFilter(state.filter.let {
                it.copy(damages = nullingXor(it.damages, damage))
            })
        }
    ){
        Text("Damage")
    }
    DropdownSelector(
        SaveType.entries,
        state.filter.saves,
        { Text(it.name) },
        { save ->
            onChangeFilter(state.filter.let {
                it.copy(saves = nullingXor(it.saves, save))
            })
        }
    ){
        Text("Save")
    }
}

@Composable
fun SpellListFilterSelector(
    state: SpellListState,
    onChangeFilter: (SpellListFilter) -> Unit = {}
){
    Row{
        val scrollState = rememberScrollState()
        Row(
            modifier = Modifier
                .horizontalScroll(scrollState)
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ){
            SpellListFilterSelectorItems(state, onChangeFilter)
        }
        if(state.filter.hasActiveCriteria()){
            IconButton(
                onClick = { onChangeFilter(state.filter.clear()) }
            ){
                Icon(Icons.Default.Close, "Clear")
            }
        }
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

enum class ChipSize {
    SMALL, REGULAR
}

@Composable
fun ClickableToken(
    onClick: () -> Unit = {},
    token: @Composable () -> Unit
){
    Box(
        modifier = Modifier
            .clickable(true, onClick = onClick)
    ){
        token()
    }
}

@Composable
fun PreparedToken(
    prepared: Boolean,
    size: ChipSize = ChipSize.REGULAR
){
    val actualColor = if (prepared) Color.Blue else Color.Black
    Chip(
        "Prepared",
        actualColor,
        size,
        Color.Transparent,
        BorderStroke(2.dp, actualColor)
    )
}

@Composable
fun KnownToken(
    known: Boolean,
    size: ChipSize = ChipSize.REGULAR
){
    val actualColor = if (known) Color.Blue else Color.Black
    Chip(
        "Known",
        actualColor,
        size,
        Color.Transparent,
        BorderStroke(2.dp, actualColor)
    )
}

@Composable
fun Chip(
    content: String,
    contentColor: Color = Color.Black,
    size: ChipSize = ChipSize.REGULAR,
    fillColor: Color = Color.LightGray,
    border : BorderStroke? = null
){
    Box(
        modifier = Modifier
            .widthIn(
                min = when(size) {
                    ChipSize.SMALL -> 50.dp
                    ChipSize.REGULAR -> 80.dp
                }
            )
            .clip(RoundedCornerShape(6.dp))
            .let { if (border != null) it.border(border = border) else it }
            .background(fillColor)
            .padding(
                vertical = 2.dp,
                horizontal = 4.dp
            )
    ){
        Text(
            content,
            color = contentColor,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier
                .align(Alignment.Center)
        )
    }
}

@Composable
fun TagChip(
    tag: String,
    size: ChipSize = ChipSize.REGULAR
){
    Chip(tag, Color.Black, size, fillColor = Color.LightGray)
}

@Composable
fun SpellListStickyHeader(
    text: String
){
    Box(
        modifier=Modifier
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
        Column(modifier=Modifier.weight(1f)){
            Text(
                spell.info.name,
                style=MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                overflow= TextOverflow.Ellipsis
            )

            Text(
                text=spell.info.school.name,
                fontStyle= FontStyle.Italic,
                style=MaterialTheme.typography.titleSmall
            )
        }

        FlowRow (
            modifier=Modifier
                .weight(1f)
                .align(Alignment.CenterVertically),
            horizontalArrangement = Arrangement.Center,
            overflow= FlowRowOverflow.Clip
        ){
            spell.info.tag.forEach{
                Box(modifier=Modifier.padding(2.dp)){
                    TagChip(it, ChipSize.SMALL)
                }
            }
        }

        if(rightSideButton == null){
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Open spell",
                modifier=Modifier.align(Alignment.CenterVertically)
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