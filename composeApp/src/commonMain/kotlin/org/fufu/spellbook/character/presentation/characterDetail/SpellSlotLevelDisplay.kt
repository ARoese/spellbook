package org.fufu.spellbook.character.presentation.characterDetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import org.fufu.spellbook.character.domain.SpellSlotLevel
import kotlin.math.max
import kotlin.math.min

@Composable
fun SpellSlotLevelDisplay(
    level: SpellSlotLevel,
    onChange: (SpellSlotLevel) -> Unit
){
    Row(horizontalArrangement = Arrangement.spacedBy(2.dp)){
        fun decrement(){
            onChange(level.copy(slots= max(0, level.slots-1)))
        }

        fun increment(){
            onChange(level.copy(slots= min(level.slots+1, level.maxSlots)))
        }

        (1..level.maxSlots).forEach {
            val size = 30.dp
            val enabled = it <= level.slots
            val onClick: (Boolean) -> Unit = if(enabled){
                { decrement() }
            }else{
                { increment() }
            }
            SimpleCircleBoolButton(enabled, onClick, size)
        }
    }
}