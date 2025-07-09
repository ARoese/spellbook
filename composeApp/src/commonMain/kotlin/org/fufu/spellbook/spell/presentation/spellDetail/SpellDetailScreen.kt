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

fun SpellDetailVM.handleIntent(
    intent: Intent,
    onPopoutClicked: (() -> Unit)?,
    onBack: () -> Unit
){
    when(intent){
        is Intent.OnCloseClicked -> this.closeClicked()
        is Intent.SetEditing -> this.setEditing(intent.editing)
        is Intent.OnSpellEdited -> this.spellEdited(intent.newInfo)
        is Intent.OnDeleteClicked -> this.deleteClicked()
        is Intent.OnViewCondition -> this.showCondition(intent.conditionName)
        is Intent.OnDuplicateSpell -> this.duplicateSpell()
        is Intent.OnHideCondition -> this.hideCondition()
        is Intent.OnPopoutClicked -> onPopoutClicked?.let{
            it()
            onBack()
        }

        Intent.OnSaveSpell -> this.saveSpell()
    }
}

@Composable
fun SpellDetailScreenRoot(
    viewModel: SpellDetailVM,
    onBack: () -> Unit,
    onPopoutClicked: (() -> Unit)? = null
    ){
    val state by viewModel.state.collectAsStateWithLifecycle()

    val canPopout = onPopoutClicked != null
            && (state.originalSpell?.key ?: 0) != 0

    SpellDetailScreen(
        state,
        canPopout
    ){
        viewModel.handleIntent(it, onPopoutClicked, onBack)
    }
}

sealed interface Intent{
    data object OnCloseClicked : Intent
    data class SetEditing(val editing: Boolean) : Intent
    data class OnSpellEdited(val newInfo: SpellInfo) : Intent
    data object OnDeleteClicked : Intent
    data class OnViewCondition(val conditionName: String): Intent
    data object OnDuplicateSpell: Intent
    data object OnHideCondition: Intent
    data object OnPopoutClicked: Intent
    data object OnSaveSpell: Intent
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

@Composable
fun SpellDetailScreen(
    state: SpellDetailState,
    canPopout: Boolean = false,
    intend: (Intent) -> Unit
){
    val editButtonIcon = if(state.isEditing){
        Icons.Filled.Check
    }else{
        Icons.Filled.Edit
    }

    val floatingActionButton: @Composable () -> Unit =
        if(canPopout) {
            @Composable {
                FloatingActionButton(
                    onClick = { intend(Intent.OnPopoutClicked) }
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
                        onClick = {intend(Intent.OnDeleteClicked)}
                    ){
                        Icon(Icons.Filled.Delete, "Delete")
                    }
                }

                Row(modifier = Modifier.align(Alignment.CenterEnd)){
                    IconButton(
                        onClick = {
                            if(state.isEditing){
                                intend(Intent.OnSaveSpell)
                            }else{
                                intend(Intent.SetEditing(true))
                            }
                                  },
                    ) {
                        Icon(editButtonIcon, "Edit")
                    }

                    IconButton(
                        onClick = {intend(Intent.OnDuplicateSpell)}
                    ){
                        Icon(painterResource(Res.drawable.content_copy), "Edit")
                    }

                    IconButton(
                        onClick = {intend(Intent.OnCloseClicked)},
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
                intend
            )
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoadingSpellDetail(
    state: SpellDetailState,
    intend: (Intent) -> Unit
){
    state.spellInfo.map(
        ifNotLoaded = {
            Box(modifier = Modifier.fillMaxSize()){
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        },
        ifLoadedNull = {
            intend(Intent.OnCloseClicked)
        }
    ){ spellInfo ->
        if(state.viewedCondition != null){
            ModalBottomSheet(
                onDismissRequest = {intend(Intent.OnHideCondition)}
            ){
                ConditionDetail(state.viewedCondition)
            }
        }
        SpellDetail(
            spellInfo,
            isEditing = state.isEditing,
            conditions = state.conditions,
            intend
        )
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
    spellInfo: SpellInfo,
    isEditing: Boolean,
    onSpellEdited: (SpellInfo) -> Unit
){
    EditableStringListDisplay(
        "Versions",
        spellInfo.versions,
        isEditing,
        onChange = { onSpellEdited(spellInfo.copy(versions = it)) }
    )

    EditableStringListDisplay(
        "Sources",
        spellInfo.sources,
        isEditing,
        onChange = { onSpellEdited(spellInfo.copy(sources = it)) }
    )

    EditableStringListDisplay(
        "Subclasses",
        spellInfo.subclasses,
        isEditing,
        onChange = { onSpellEdited(spellInfo.copy(subclasses = it)) }
    )

    EditableStringListDisplay(
        "Optional Subclass",
        spellInfo.optional,
        isEditing,
        onChange = { onSpellEdited(spellInfo.copy(optional = it)) }
    )

    EditableStringListDisplay(
        "DragonMarks",
        spellInfo.dragonmarks,
        isEditing,
        onChange = { onSpellEdited(spellInfo.copy(dragonmarks = it)) }
    )

    EditableStringListDisplay(
        "Guilds",
        spellInfo.guilds,
        isEditing,
        onChange = { onSpellEdited(spellInfo.copy(guilds = it)) }
    )

    EditableStringListDisplay(
        "Tags",
        spellInfo.tag,
        isEditing,
        onChange = { onSpellEdited(spellInfo.copy(tag = it)) }
    )

    EditableStringListDisplay(
        "Damages",
        spellInfo.damages,
        isEditing,
        onChange = { onSpellEdited(spellInfo.copy(damages = it)) }
    )

    EditableStringListDisplay(
        "Saves",
        spellInfo.saves,
        isEditing,
        onChange = { onSpellEdited(spellInfo.copy(saves = it)) }
    )

}

@Composable
fun SpellDetail(
    spellInfo: SpellInfo,
    isEditing: Boolean,
    conditions: Set<String>?,
    intend: (Intent) -> Unit
){
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
                            intend(Intent.OnSpellEdited(spellInfo.copy(name = it)))
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
                                onOptionPicked = { intend(Intent.OnSpellEdited(spellInfo.copy(level = it))) },
                                singleSelect = true
                            ) {
                                Text("${spellInfo.level}")
                            }

                            TextField(
                                spellInfo.school,
                                onValueChange = {
                                    intend(Intent.OnSpellEdited(spellInfo.copy(school = it)))
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
                                        intend(Intent.OnSpellEdited(spellInfo.copy(ritual = it)))
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
                            onChange = { intend(Intent.OnSpellEdited(spellInfo.copy(time = it))) })
                    ),
                    Pair(
                        "Range",
                        EditableValue(
                            spellInfo.range,
                            onChange = { intend(Intent.OnSpellEdited(spellInfo.copy(range = it))) })
                    ),
                    Pair(
                        "Components",
                        EditableValue(
                            spellInfo.components,
                            onChange = { intend(Intent.OnSpellEdited(spellInfo.copy(components = it))) })
                    ),
                    Pair(
                        "Duration",
                        EditableValue(
                            spellInfo.duration,
                            onChange = { intend(Intent.OnSpellEdited(spellInfo.copy(duration = it))) }
                        )
                    )
                ),
                modifier = Modifier.padding(bottom = 10.dp),
                isEditing = isEditing
            )

            // spell text
            if(isEditing){
                androidx.compose.material3.TextField(
                    spellInfo.text,
                    onValueChange = { intend(Intent.OnSpellEdited(spellInfo.copy(text=it))) },
                    minLines = 4
                )
            }else{
                SpellText(
                    spellInfo.text,
                    conditions ?: emptySet(),
                    {
                        intend(Intent.OnViewCondition(it))
                    }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            ListDisplays(spellInfo, isEditing, onSpellEdited = { intend(Intent.OnSpellEdited(it)) })

            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}