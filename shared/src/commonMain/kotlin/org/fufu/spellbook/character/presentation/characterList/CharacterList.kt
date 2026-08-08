package org.fufu.spellbook.character.presentation.characterList

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.fufu.spellbook.character.domain.Character
import org.fufu.spellbook.character.domain.CharacterIcon
import org.fufu.spellbook.composables.FloatingAddButton

@Composable
fun CharacterListRoot(
    viewModel: CharacterListVM,
    onCharacterClicked: (Character) -> Unit = {},
    navBar: @Composable () -> Unit,
    onNewClicked: () -> Unit = {}
){
    val state by viewModel.state.collectAsStateWithLifecycle()

    CharacterListScreen(
        state,
        navBar
    ) { intent ->
        when(intent){
            Intent.NewCharacter -> onNewClicked()
            is Intent.ViewCharacter -> onCharacterClicked(intent.character)
        }
    }
}

sealed interface Intent {
    data class ViewCharacter(val character: Character) : Intent
    data object NewCharacter : Intent
}

@Composable
fun CharacterListScreen(
    state: CharacterListState,
    navBar: @Composable () -> Unit,
    intend: (Intent) -> Unit
){
    Scaffold(
        bottomBar = navBar,
        floatingActionButton = { FloatingAddButton({ intend(Intent.NewCharacter) }) }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)){
            CharacterList(
                state,
                { intend(Intent.ViewCharacter(it)) }
            )
        }
    }
}

@Composable
fun CharacterList(
    state: CharacterListState,
    onCharacterClicked: (Character) -> Unit = {}
) {
    LazyVerticalGrid(
        columns=GridCells.Fixed(2),
        contentPadding = PaddingValues(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ){
        items(state.characters){ character ->
            CharacterCard(
                character = character,
                onClick = { onCharacterClicked(character) }
            )
        }
    }
}

@Composable
fun CharacterCard(
    character: Character,
    onClick: () -> Unit
){
    ElevatedCard(
        modifier = Modifier
            .aspectRatio(1f)
            .clickable(
                true,
                onClick = onClick
            )
    ){
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
        ){
            Column(horizontalAlignment = Alignment.CenterHorizontally){
                Text(character.name)
                Icon(CharacterIcon(character.characterIcon).fromString(), character.characterIcon)
            }
        }
    }
}