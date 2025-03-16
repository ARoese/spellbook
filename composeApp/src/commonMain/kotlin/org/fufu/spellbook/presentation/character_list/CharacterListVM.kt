package org.fufu.spellbook.presentation.character_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.fufu.spellbook.domain.Character
import org.fufu.spellbook.domain.CharacterProvider

data class CharacterListState(
    val characters : List<Character> = emptyList(),
    val loading : Boolean = true
)

class CharacterListVM(val provider : CharacterProvider) : ViewModel() {
    private val _state = MutableStateFlow(CharacterListState())
    val state = _state
        .onStart {
            viewModelScope.launch{
                delay(1000)
                val characters = provider.GetCharacters()
                _state.update{
                    it.copy(
                        characters = characters,
                        loading = false
                    )
                }
            }
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            _state.value
        )
}