package org.fufu.spellbook

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import org.fufu.spellbook.domain.PreviewCharacters
import org.fufu.spellbook.domain.PreviewSpells
import org.fufu.spellbook.presentation.character_detail.CharacterDetail
import org.fufu.spellbook.presentation.character_detail.CharacterDetailState
import org.fufu.spellbook.presentation.character_detail.ConcreteCharacterDetailState
import org.fufu.spellbook.presentation.character_list.CharacterList
import org.fufu.spellbook.presentation.character_list.CharacterListScreen
import org.fufu.spellbook.presentation.character_list.CharacterListState
import org.fufu.spellbook.presentation.spell_detail.LoadingSpellDetailScreen
import org.fufu.spellbook.presentation.spell_detail.SpellDetailState
import org.fufu.spellbook.presentation.spell_list.SpellListScreen
import org.fufu.spellbook.presentation.spell_list.SpellListState

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
        onSpellSelected = {},
        navBar = { FakeBottomNavBar() }
    )
}

val previewSpell = PreviewSpells[5]

@Preview(showBackground = true)
@Composable
fun SpellDetailScreenPreview(){
    LoadingSpellDetailScreen(SpellDetailState(previewSpell, isEditing = false, loading = false))
}

@Preview(showBackground = true)
@Composable
fun SpellDetailEditingScreenPreview(){
    LoadingSpellDetailScreen(SpellDetailState(previewSpell, isEditing = true, loading = false))
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
    )
}

val PreviewCharacter = PreviewCharacters[3]

@Preview(showBackground = true)
@Composable
fun CharacterDetailPreview(){
    CharacterDetail(
        ConcreteCharacterDetailState(PreviewCharacter, false),
        SpellListState(
            displayedSpells = PreviewCharacter.spells
                .keys
                .map{PreviewSpells[it]},
            loading = false
        )
    )
}