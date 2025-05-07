package org.fufu.spellbook.spell.presentation.spellDetail

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.fufu.spellbook.spell.domain.MagicSchool
import org.fufu.spellbook.spell.domain.SpellInfo
import org.fufu.spellbook.spell.domain.formatAsOrdinalSchool

@Composable
fun SpellDetailScreenRoot(
    viewModel: SpellDetailVM,
    onCloseClicked: () -> Unit
    ){
    val state by viewModel.state.collectAsStateWithLifecycle()

    SpellDetailScreen(
        state,
        onCloseClicked = { // if the viewModel lets us, forward it to navigation
            val vmRes = viewModel.onAction(
                SpellDetailVM.Action.OnCloseClicked
            )
            if (vmRes != null) {
                onCloseClicked()
            }
        },
        onSpellEdited = { viewModel.onAction(SpellDetailVM.Action.OnSpellEdited(it)) },
        onEditClicked = { viewModel.onAction(SpellDetailVM.Action.OnEditClicked) },
        onDeleteClicked = {
            val vmRes = viewModel.onAction(SpellDetailVM.Action.OnDeleteClicked)
            if(vmRes != null){
                onCloseClicked()
            }
        }
    )
}

@Composable
fun SpellDetailScreen(
    state: SpellDetailState,
    onCloseClicked: () -> Unit = {},
    onSpellEdited: (SpellInfo) -> Unit = {},
    onEditClicked: () -> Unit = {},
    onDeleteClicked: () -> Unit = {}
){
    val editButtonIcon = if(state.isEditing){
        Icons.Filled.Check
    }else{
        Icons.Filled.Edit
    }
    Scaffold (
        topBar = {
            Box(modifier = Modifier.fillMaxWidth()){
                if(state.isEditing){
                    IconButton(
                        onClick = onDeleteClicked
                    ){
                        Icon(Icons.Filled.Delete, "Delete")
                    }
                }

                Row(modifier = Modifier.align(Alignment.CenterEnd)){
                    IconButton(
                        onClick = onEditClicked,
                    ) {
                        Icon(editButtonIcon, "Edit")
                    }

                    IconButton(
                        onClick = onCloseClicked,
                    ) {
                        Icon(Icons.Filled.Close, "Close")
                    }
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
        ) {
            LoadingSpellDetail(
                state,
                onSpellEdited = onSpellEdited
            )
        }
    }
}



@Composable
fun LoadingSpellDetail(
    state: SpellDetailState,
    onSpellEdited: (SpellInfo) -> Unit = {}
){
    // check and handle loading status and nullability of stuff
    if(state.loading){
        Box(modifier = Modifier.fillMaxSize()){
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }else{
        if(!state.canBecomeConcrete()){
            Box(modifier = Modifier.fillMaxSize()){
                Text("Spell Missing")
            }
        }else{
            SpellDetail(
                state.toConcrete(),
                onSpellEdited
            )
        }
    }
}

@Composable
fun SpellDetail(
    state: ConcreteSpellDetailState,
    onSpellEdited: (SpellInfo) -> Unit = {}
){
    val spellInfo = state.spellInfo
    val isEditing = state.isEditing
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(5.dp)
    ) {
        Column {
            // title and subtitle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .padding(bottom = 10.dp)
                        .weight(1f)
                ) {
                    EditableText(
                        spellInfo.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        isEditing = isEditing
                    )

                    if (isEditing) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("level ")
                            Box {
                                var expanded by remember { mutableStateOf(false) }
                                val scrollState = rememberScrollState()
                                IconButton(
                                    onClick = { expanded = true },
                                    modifier = Modifier
                                        .border(Dp.Hairline, Color.Black, CircleShape)
                                ) {
                                    Text(spellInfo.level.toString())
                                }
                                DropdownMenu(
                                    expanded,
                                    onDismissRequest = { expanded = false },
                                    scrollState = scrollState
                                ) {
                                    (0..10).forEach {
                                        DropdownMenuItem(
                                            text = { Text(it.toString()) },
                                            onClick = {
                                                expanded = false
                                                onSpellEdited(spellInfo.copy(level = it))
                                            }
                                        )
                                    }
                                }
                            }
                            Box {
                                var expanded by remember { mutableStateOf(false) }
                                val scrollState = rememberScrollState()
                                IconButton(
                                    onClick = { expanded = true },
                                    modifier = Modifier
                                        .border(
                                            Dp.Hairline,
                                            Color.Black,
                                            RoundedCornerShape(6.dp)
                                        )
                                ) {
                                    Text(spellInfo.school.toString())
                                }
                                DropdownMenu(
                                    expanded,
                                    onDismissRequest = { expanded = false },
                                    scrollState = scrollState
                                ) {
                                    MagicSchool.entries.forEach {
                                        DropdownMenuItem(
                                            text = { Text(it.name) },
                                            onClick = {
                                                expanded = false
                                                onSpellEdited(spellInfo.copy(school = it))
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    } else {
                        Text(
                            spellInfo.formatAsOrdinalSchool(),
                            style = MaterialTheme.typography.labelMedium,
                            fontStyle = FontStyle.Italic
                        )
                    }

                }
            }

            // info block
            EditableDataBlock(
                mapOf(
                    Pair(
                        "Casting Time",
                        EditableValue(
                            spellInfo.time,
                            onChange = { onSpellEdited(spellInfo.copy(time = it)) })
                    ),
                    Pair(
                        "Range",
                        EditableValue(
                            spellInfo.range,
                            onChange = { onSpellEdited(spellInfo.copy(range = it)) })
                    ),
                    Pair(
                        "Components",
                        EditableValue(
                            spellInfo.components,
                            onChange = { onSpellEdited(spellInfo.copy(components = it)) })
                    ),
                    Pair(
                        "Duration",
                        EditableValue(
                            spellInfo.duration,
                            onChange = { onSpellEdited(spellInfo.copy(duration = it)) }
                        )
                    )
                ),
                modifier = Modifier.padding(bottom = 10.dp),
                isEditing = isEditing
            )

            // spell text
            EditableText(
                spellInfo.text,
                style = MaterialTheme.typography.bodyMedium,
                isEditing = isEditing,
                onChange = { onSpellEdited(spellInfo.copy(text = it)) }
            )
        }
    }
}