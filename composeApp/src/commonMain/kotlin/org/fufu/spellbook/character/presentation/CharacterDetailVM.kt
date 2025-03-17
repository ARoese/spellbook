package org.fufu.spellbook.character.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import org.fufu.spellbook.character.domain.Character
import org.fufu.spellbook.character.domain.CharacterProvider

data class CharacterDetailState(
    val character: Character? = null,
    val loading: Boolean = true
)

data class ConcreteCharacterDetailState(
    val character: Character,
    val loading: Boolean = true
)

fun CharacterDetailState.canBecomeConcrete() : Boolean {
    return character != null
}

fun CharacterDetailState.toConcrete() : ConcreteCharacterDetailState {
    if(!canBecomeConcrete()){
        throw KotlinNullPointerException("null character is not allowed")
    }

    return ConcreteCharacterDetailState(
        character!!,
        loading
    )
}

class CharacterDetailVM(private val characterId : Int, private val provider : CharacterProvider) : ViewModel() {
    private val _state = MutableStateFlow(CharacterDetailState())
    val state = _state
        .onStart {
            observeCharacter()
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            _state.value
        )

    private fun observeCharacter(){
        provider.getCharacter(characterId)
            .onEach{ character ->
                delay(1000)
                _state.update{
                    it.copy(
                        character = character,
                        loading = false
                    )
                }
            }.launchIn(viewModelScope)
    }
}