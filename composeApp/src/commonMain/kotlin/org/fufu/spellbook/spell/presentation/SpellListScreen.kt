package org.fufu.spellbook.spell.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import org.fufu.spellbook.spell.domain.DefaultSpellInfo
import org.fufu.spellbook.spell.domain.MagicSchool
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
        onSpellSelected
    )
}

@Composable
fun SpellListScreen(
    state: SpellListState,
    onSpellSelected: (Spell) -> Unit,
    onNewClicked: () -> Unit = {},
    navBar: @Composable () -> Unit
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
                onSpellSelected
            )
        }
    }
}

@Composable
fun SpellList(
    state: SpellListState,
    onSpellSelected: (Spell) -> Unit,
    rightSideButton: (@Composable (Spell) -> Unit)? = null
) {
    Box(modifier = Modifier.fillMaxWidth()){
        if(state.loading){
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }else{
            LazyColumn(modifier = Modifier
                .padding(horizontal = 5.dp)
            ){
                state.displayedSpells.forEach{
                    item(key=it.key){
                        SpellListItem(it, {onSpellSelected(it)}, rightSideButton)
                        HorizontalDivider()
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
        Column(modifier=Modifier){
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