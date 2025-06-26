package org.fufu.spellbook

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
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
import androidx.compose.ui.tooling.preview.Preview
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.dialogs.init
import org.fufu.spellbook.character.presentation.characterDetail.CharacterDetailScreen
import org.fufu.spellbook.character.presentation.characterDetail.CharacterDetailState
import org.fufu.spellbook.character.presentation.characterDetail.CircleBoolButton
import org.fufu.spellbook.character.presentation.characterDetail.SpellListType
import org.fufu.spellbook.character.presentation.characterDetail.SpellListVariant
import org.fufu.spellbook.character.presentation.characterList.CharacterListScreen
import org.fufu.spellbook.character.presentation.characterList.CharacterListState
import org.fufu.spellbook.character.presentation.editingCharacterDetail.EditingCharacterDetailScreen
import org.fufu.spellbook.character.presentation.editingCharacterDetail.EditingCharacterDetailState
import org.fufu.spellbook.spell.presentation.importScreen.ImportScreen
import org.fufu.spellbook.spell.presentation.importScreen.ImportScreenState
import org.fufu.spellbook.spell.presentation.spellDetail.SpellDetailScreen
import org.fufu.spellbook.spell.presentation.spellDetail.SpellDetailState
import org.fufu.spellbook.spell.presentation.spellList.SpellListScreen
import org.fufu.spellbook.spell.presentation.spellList.SpellListState

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FileKit.init(this)
        setContent {
            App()
        }
    }
}

@Composable
fun AppAndroidPreview() {
    App()
}

@Composable
fun FakeBottomNavBar(){
    NavigationBar(containerColor = Color.LightGray) {
        NavigationBarItem(
            selected = false,
            label = { Text("item1") },
            onClick = {},
            icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = "item") }
        )
        NavigationBarItem(
            selected = true,
            label = { Text("item2") },
            onClick = {},
            icon = { Icon(Icons.Filled.Person, contentDescription = "item") }
        )
        NavigationBarItem(
            selected = false,
            label = { Text("item3") },
            onClick = {},
            icon = { Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "import") }
        )
        NavigationBarItem(
            selected = false,
            label = { Text("item4") },
            onClick = {},
            icon = { Icon(Icons.Filled.Settings, contentDescription = "item") }
        )
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
@Preview(showBackground = true, showSystemUi = true)
fun NavigationPreview(){
    Scaffold(
        bottomBar = {
            FakeBottomNavBar()
        }
    ){
        Box(modifier = Modifier.fillMaxSize()){
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = "Content"
            )
        }

    }
}

@Preview(showBackground = true)
@Composable
fun SpellListScreenPreview(){
    SpellListScreen(
        SpellListState(
            displayedSpells = PreviewSpells,
            loading = false
        ),
        navBar = { FakeBottomNavBar() }
    ){}
}

val previewSpell = PreviewSpells[5]

@Preview(showBackground = true)
@Composable
fun SpellDetailScreenPreview(){
    SpellDetailScreen(SpellDetailState(previewSpell, isEditing = false, loading = false))
}

@Preview(showBackground = true)
@Composable
fun SpellDetailEditingScreenPreview(){
    SpellDetailScreen(SpellDetailState(previewSpell, isEditing = true, loading = false))
}

@Preview(showBackground = true)
@Composable
fun CharacterListPreview(){
    CharacterListScreen(
        CharacterListState(
            PreviewCharacters,
            false
        ),
        navBar = { FakeBottomNavBar() }
    ){}
}

val PreviewCharacter = PreviewCharacters[3]

@Preview(showBackground = true)
@Composable
fun CharacterDetailPreview(){
    val listState = SpellListState(
        displayedSpells = PreviewCharacter.spells
            .keys
            .map{ PreviewSpells[it]},
        loading = false
    )
    CharacterDetailScreen(
        CharacterDetailState(
            PreviewCharacter,
            SpellListType.PREPARED,
            false
        ),
        SpellListVariant(
            SpellListType.KNOWN,
            listState
        )
    ){}
}

@Preview(showBackground = true)
@Composable
fun ImportScreenPreview(){
    ImportScreen(
        ImportScreenState(
            PreviewSpells,
            loading = false
        ),
        navBar = { FakeBottomNavBar() }
    )
}

@Preview(showBackground = true)
@Composable
fun EditingCharacterDetailScreenPreview() {
    EditingCharacterDetailScreen(
        EditingCharacterDetailState(
            character = PreviewCharacter,
            loading = false,
        )
    ){}
}

@Preview(showBackground = true)
@Composable
fun CircleBoolButtonPreview(){
    Box(modifier = Modifier.fillMaxSize()){
        var state by remember { mutableStateOf(false) }
        CircleBoolButton(
            state,
            onClick = {
                state = it
            }
        )
    }
}