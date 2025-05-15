package org.fufu.spellbook.character.presentation.characterDetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import org.fufu.spellbook.character.domain.SpellSlotLevel
import org.fufu.spellbook.spell.presentation.spellList.SpellListFilter
import org.fufu.spellbook.spell.presentation.spellList.SpellListState
import org.fufu.spellbook.spell.presentation.spellList.SpellListVM

data class CharacterDetailState(
    val character: Character? = null,
    val selectedSpellList: SpellListType = SpellListType.PREPARED,
    val loading: Boolean = true
)

data class ConcreteCharacterDetailState(
    val character: Character,
    val selectedSpellList: SpellListType = SpellListType.PREPARED,
    val loading: Boolean = false
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
        selectedSpellList,
        loading
    )
}

enum class SpellListType{
    PREPARED,
    KNOWN,
    CLASS
}

data class SpellListVariant(
    val type: SpellListType,
    val state: SpellListState
)

class CharacterDetailVM(
    private val characterId : Int,
    private val provider : CharacterMutator,
    val preparedSpellList: SpellListVM,
    val knownSpellList: SpellListVM,
    val classSpellList: SpellListVM
) : ViewModel() {

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
                preparedSpellList.useFilter(
                    SpellListFilter(
                        onlyIds = character?.spells?.filter { it.value }?.keys ?: emptySet()
                    )
                )

                knownSpellList.useFilter(
                    SpellListFilter(
                        onlyIds = character?.spells?.keys ?: emptySet()
                    )
                )

                val lastClass = _state.value.character?.characterClass
                val newClass = character?.characterClass
                // so updates to character that don't change the class
                // do not make the user need to fight with the class filter
                if(lastClass != newClass){
                    val classes = newClass?.let { setOf(it) } ?: emptySet()
                    classSpellList.updateFilter {
                        it.copy(classes = classes)
                    }
                }

                _state.update{
                    it.copy(
                        character = character,
                        loading = false
                    )
                }
            }.launchIn(viewModelScope)
    }

    fun onSetSpellListType(newType: SpellListType){
        _state.update {
            it.copy(selectedSpellList = newType)
        }
    }

    fun onSetSpellLearned(spell: Int, learned: Boolean){
        state.value.character?.let { character ->
            viewModelScope.launch {
                provider.setCharacter(
                    character.copy(
                        spells = if(learned){
                            character.spells.plus(spell to false)
                        }else{
                            character.spells.filter { it.key != spell }
                        }
                    )
                )
            }
        }
    }

    fun onSetSpellPrepared(spell: Int, prepared: Boolean){
        state.value.character?.let { character ->
            viewModelScope.launch {
                provider.setCharacter(
                    character.copy(
                        spells = character.spells.plus(
                            spell to prepared
                        )
                    )
                )
            }
        }
    }

    fun onSetSpellSlot(level: Int, slotLevel: SpellSlotLevel){
        state.value.character?.let { character ->
            viewModelScope.launch {
                val newSpellSlots = character.spellSlots.plus(level to slotLevel)
                provider.setCharacter(
                    character.copy(
                        spellSlots = newSpellSlots
                    )
                )
            }
        }
    }
}