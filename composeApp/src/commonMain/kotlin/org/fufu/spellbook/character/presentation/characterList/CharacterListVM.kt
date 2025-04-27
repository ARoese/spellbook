package org.fufu.spellbook.character.presentation.characterList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import org.fufu.spellbook.character.domain.Character
import org.fufu.spellbook.character.domain.CharacterProvider

data class CharacterListState(
    val characters : List<Character> = emptyList(),
    val loading : Boolean = true
)

class CharacterListVM(private val provider : CharacterProvider) : ViewModel() {
    private val _state = MutableStateFlow(CharacterListState())
    val state = _state
        .onStart {
            observeCharacters()
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            _state.value
        )

    private fun observeCharacters(){
        provider.getCharacters()
            .onEach{characters ->
                _state.update{
                    it.copy(
                        characters = characters,
                        loading = false
                    )
                }
            }.launchIn(viewModelScope)
    }
}