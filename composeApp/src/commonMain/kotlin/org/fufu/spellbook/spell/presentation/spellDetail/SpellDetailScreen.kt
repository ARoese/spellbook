package org.fufu.spellbook.spell.presentation.spellDetail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.fufu.spellbook.composables.DropdownSelector
import org.fufu.spellbook.spell.domain.Condition
import org.fufu.spellbook.spell.domain.SpellInfo
import org.fufu.spellbook.spell.domain.formatAsOrdinalSchool
import org.jetbrains.compose.resources.painterResource
import spellbook.composeapp.generated.resources.Res
import spellbook.composeapp.generated.resources.content_copy
import spellbook.composeapp.generated.resources.open_in_new

@Composable
fun SpellDetailScreenRoot(
    viewModel: SpellDetailVM,
    onCloseClicked: () -> Unit,
    onPopoutClicked: (() -> Unit)? = null
    ){
    val state by viewModel.state.collectAsStateWithLifecycle()

    fun doCloseFunction(){
        // if the viewModel lets us, forward it to navigation
        val vmRes = viewModel.onAction(
            SpellDetailVM.Action.OnCloseClicked
        )
        if (vmRes != null) {
            onCloseClicked()
        }
    }

    SpellDetailScreen(
        state,
        onCloseClicked = { doCloseFunction() },
        onSpellEdited = { viewModel.onAction(SpellDetailVM.Action.OnSpellEdited(it)) },
        onEditClicked = { viewModel.onAction(SpellDetailVM.Action.OnEditClicked) },
        onDeleteClicked = {
            val vmRes = viewModel.onAction(SpellDetailVM.Action.OnDeleteClicked)
            if(vmRes != null){
                onCloseClicked()
            }
        },
        onCopyClicked = {
            viewModel.duplicateSpell()
        },
        onPopoutClicked = onPopoutClicked?.let{
            {
                it()
                doCloseFunction()
            }
        },
        onConditionClicked = {
            viewModel.showCondition(conditionName = it)
        },
        onConditionHidden = {
            viewModel.hideCondition()
        }
    )
}

@Composable
fun ConditionDetail(condition: Condition){
    Column(modifier = Modifier.padding(10.dp)){
        Text(
            "${condition.name}:",
            fontWeight = FontWeight.Bold
        )
        Text(condition.desc)
        Spacer(modifier = Modifier.height(40.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpellDetailScreen(
    state: SpellDetailState,
    onCloseClicked: () -> Unit = {},
    onPopoutClicked: (() -> Unit)? = null,
    onSpellEdited: (SpellInfo) -> Unit = {},
    onEditClicked: () -> Unit = {},
    onCopyClicked: () -> Unit = {},
    onDeleteClicked: () -> Unit = {},
    onConditionClicked: (String) -> Unit = {},
    onConditionHidden: () -> Unit = {}
){
    val editButtonIcon = if(state.isEditing){
        Icons.Filled.Check
    }else{
        Icons.Filled.Edit
    }

    val floatingActionButton: @Composable () -> Unit =
        if(onPopoutClicked != null && (state.originalSpell?.key ?: 0) != 0 ) {
            @Composable {
                FloatingActionButton(
                    onClick = { onPopoutClicked() }
                ) {
                    Icon(painterResource(Res.drawable.open_in_new), "Popout")
                }
            }
        }else {
            @Composable {}
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
                        onClick = onCopyClicked
                    ){
                        Icon(painterResource(Res.drawable.content_copy), "Edit")
                    }

                    IconButton(
                        onClick = onCloseClicked,
                    ) {
                        Icon(Icons.Filled.Close, "Close")
                    }
                }
            }
        },
        floatingActionButton = floatingActionButton
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
        ) {
            LoadingSpellDetail(
                state,
                onSpellEdited = onSpellEdited,
                onConditionClicked = onConditionClicked,
                onConditionHidden = onConditionHidden
            )
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoadingSpellDetail(
    state: SpellDetailState,
    onSpellEdited: (SpellInfo) -> Unit = {},
    onConditionClicked: (String) -> Unit,
    onConditionHidden: () -> Unit = {}
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
            if(state.viewedCondition != null){
                ModalBottomSheet(
                    onDismissRequest = onConditionHidden
                ){
                    ConditionDetail(state.viewedCondition)
                }
            }
            SpellDetail(
                state.toConcrete(),
                onSpellEdited,
                onConditionClicked = onConditionClicked
            )
        }
    }
}

@Composable
fun CleanSmallStringListDisplay(
    title: String,
    list: List<String>
){
    if(list.isEmpty()) return
    val cleanString = list
        .joinToString(", ") { it.trim() }
        .let {
            if (list.size > 1)
                "$title: ( $it )"
            else
                "$title: $it"
        }

    Text(
        cleanString,
        style = MaterialTheme.typography.labelSmall
    )
}

@Composable
fun EditableStringListDisplay(
    title: String,
    list: List<String>,
    isEditing: Boolean,
    onChange: (List<String>) -> Unit
){
    if(isEditing){
        Text("$title:")
        StringListEditor(
            list,
            onChange
        )
    }else{
        CleanSmallStringListDisplay(
            title,
            list
        )
    }
}

@Composable
fun StringListEditor(
    list: List<String>,
    onChange: (List<String>) -> Unit
){
    val commaSeparated = list.joinToString(", ")
    var actualTextState by remember { mutableStateOf(commaSeparated) }

    fun onChangeInternal(str: String){
        actualTextState = str
        val newList = str.split(",")
            .map { it.trim() }
            .filter { it.isNotEmpty() }

        onChange(newList)
    }

    TextField(
        actualTextState,
        onValueChange = { onChangeInternal(it) }
    )
}

@Composable
fun ListDisplays(
    state: ConcreteSpellDetailState,
    onSpellEdited: (SpellInfo) -> Unit
){
    EditableStringListDisplay(
        "Versions",
        state.spellInfo.versions,
        state.isEditing,
        onChange = { onSpellEdited(state.spellInfo.copy(versions = it)) }
    )

    EditableStringListDisplay(
        "Sources",
        state.spellInfo.sources,
        state.isEditing,
        onChange = { onSpellEdited(state.spellInfo.copy(sources = it)) }
    )

    EditableStringListDisplay(
        "Subclasses",
        state.spellInfo.subclasses,
        state.isEditing,
        onChange = { onSpellEdited(state.spellInfo.copy(subclasses = it)) }
    )

    EditableStringListDisplay(
        "Optional Subclass",
        state.spellInfo.optional,
        state.isEditing,
        onChange = { onSpellEdited(state.spellInfo.copy(optional = it)) }
    )

    EditableStringListDisplay(
        "DragonMarks",
        state.spellInfo.dragonmarks,
        state.isEditing,
        onChange = { onSpellEdited(state.spellInfo.copy(dragonmarks = it)) }
    )

    EditableStringListDisplay(
        "Guilds",
        state.spellInfo.guilds,
        state.isEditing,
        onChange = { onSpellEdited(state.spellInfo.copy(guilds = it)) }
    )

    EditableStringListDisplay(
        "Tags",
        state.spellInfo.tag,
        state.isEditing,
        onChange = { onSpellEdited(state.spellInfo.copy(tag = it)) }
    )

    EditableStringListDisplay(
        "Damages",
        state.spellInfo.damages,
        state.isEditing,
        onChange = { onSpellEdited(state.spellInfo.copy(damages = it)) }
    )

    EditableStringListDisplay(
        "Saves",
        state.spellInfo.saves,
        state.isEditing,
        onChange = { onSpellEdited(state.spellInfo.copy(saves = it)) }
    )

}

@Composable
fun SpellDetail(
    state: ConcreteSpellDetailState,
    onSpellEdited: (SpellInfo) -> Unit = {},
    onConditionClicked: (String) -> Unit = {}
){
    val spellInfo = state.spellInfo
    val isEditing = state.isEditing
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(5.dp)
    ) {
        Column(modifier = Modifier.verticalScroll(rememberScrollState())){
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
                        isEditing = isEditing,
                        onChange = {
                            onSpellEdited(spellInfo.copy(name = it))
                        }
                    )

                    if (isEditing) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("level ")
                            DropdownSelector(
                                (0..12).toList(),
                                setOf(spellInfo.level),
                                optionPresenter = { Text("$it") },
                                onOptionPicked = { onSpellEdited(spellInfo.copy(level = it)) },
                                singleSelect = true
                            ) {
                                Text("${spellInfo.level}")
                            }

                            TextField(
                                spellInfo.school,
                                onValueChange = {
                                    onSpellEdited(spellInfo.copy(school = it))
                                }
                            )

                            Column {
                                val ritualText = if(spellInfo.ritual){
                                    "Ritual"
                                }else{
                                    "Non-Ritual"
                                }
                                Text(ritualText)
                                Switch(
                                    checked = spellInfo.ritual,
                                    onCheckedChange = {
                                        onSpellEdited(spellInfo.copy(ritual = it))
                                    }
                                )
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
            if(state.isEditing){
                androidx.compose.material3.TextField(
                    state.spellInfo.text,
                    onValueChange = { onSpellEdited(state.spellInfo.copy(text=it)) },
                    minLines = 4
                )
            }else{
                SpellText(
                    spellInfo.text,
                    state.conditions ?: emptySet(),
                    onConditionClicked
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            ListDisplays(state, onSpellEdited)

            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}