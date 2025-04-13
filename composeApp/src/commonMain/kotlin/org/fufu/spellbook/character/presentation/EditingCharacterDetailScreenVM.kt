package org.fufu.spellbook.character.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.fufu.spellbook.character.domain.Character
import org.fufu.spellbook.character.domain.CharacterMutator
import org.fufu.spellbook.character.domain.defaultCharacter


data class EditingCharacterDetailState(
    val character: Character? = null,
    val loading: Boolean = true,
)

data class ConcreteEditingCharacterDetailState(
    val character: Character
)

fun EditingCharacterDetailState.canBecomeConcrete() : Boolean {
    return character != null
}

fun EditingCharacterDetailState.toConcrete() : ConcreteEditingCharacterDetailState {
    if(!canBecomeConcrete()){
        throw KotlinNullPointerException("null character is not allowed")
    }

    return ConcreteEditingCharacterDetailState(
        character!!
    )
}

class EditingCharacterDetailVM(
    private var characterId : Int,
    private val provider : CharacterMutator
) : ViewModel() {
    private val _state = MutableStateFlow(EditingCharacterDetailState())

    val state = _state
        .onStart {
            if(characterId == 0){
                _state.update {
                    it.copy(
                        loading = false,
                        character = defaultCharacter
                    )
                }
            }else{
                observeCharacter()
            }

        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            _state.value
        )

    private var observeCharacterJob: Job? = null
    private fun observeCharacter(){
        if(characterId == 0){return}
        observeCharacterJob?.cancel()
        observeCharacterJob = provider.getCharacter(characterId)
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


    fun onCharacterChanged(newCharacter: Character){
        _state.update { it.copy(character = newCharacter) }
    }

    private var updateCharacterJob: Job? = null
    fun onSaveCharacter(newCharacter: Character) {
        if(updateCharacterJob?.isCompleted == false){
            updateCharacterJob?.cancel()
        }

        updateCharacterJob = CoroutineScope(Dispatchers.IO).launch {
            if(newCharacter.id == 0){
                characterId = provider.addCharacter(newCharacter)
                observeCharacter()
            }else{
                provider.setCharacter(newCharacter)
            }
        }
    }

    private var deleteCharacterJob: Job? = null
    fun onDeleteCharacter(character: Character) {
        if(character.id == 0){
            return
        }
        updateCharacterJob?.cancel("deleting character instead")
        deleteCharacterJob?.cancel("deleting another character instead")
        updateCharacterJob = CoroutineScope(Dispatchers.IO).launch {
            provider.deleteCharacter(character)
        }
    }
}