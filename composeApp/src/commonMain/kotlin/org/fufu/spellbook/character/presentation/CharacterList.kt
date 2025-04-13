package org.fufu.spellbook.character.presentation

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
import androidx.compose.material3.CardColors
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.fufu.spellbook.character.domain.Character
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
        onCharacterClicked,
        navBar = navBar,
        onNewClicked = onNewClicked
    )
}

@Composable
fun CharacterListScreen(
    state: CharacterListState,
    onCharacterClicked: (Character) -> Unit = {},
    navBar: @Composable () -> Unit,
    onNewClicked: () -> Unit = {}
){
    Scaffold(
        bottomBar = navBar,
        floatingActionButton = { FloatingAddButton(onNewClicked) }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)){
            CharacterList(
                state,
                onCharacterClicked
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
                onClick = {onCharacterClicked(character)}
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
            ),
        colors = CardColors(
            containerColor = Color.LightGray,
            contentColor = Color.Black,
            disabledContentColor = Color.Gray,
            disabledContainerColor = Color.Black
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
                CircularProgressIndicator()
            }
        }
    }
}