package org.fufu.spellbook.character.domain

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.vectorResource
import spellbook.composeapp.generated.resources.Res
import spellbook.composeapp.generated.resources.eye_outline
import spellbook.composeapp.generated.resources.flash
import spellbook.composeapp.generated.resources.hand_back_left
import spellbook.composeapp.generated.resources.lightning_bolt
import spellbook.composeapp.generated.resources.pentagram
import spellbook.composeapp.generated.resources.pistol
import spellbook.composeapp.generated.resources.weather_windy
import spellbook.composeapp.generated.resources.wizard_hat

// TODO: add level to this. Sort order is not enough
data class SpellSlotLevel(
    val maxSlots: Int,
    val slots: Int
)

data class Character (
    val id: Int,
    val name: String,
    // spell id -> preparedness
    val spells: Map<Int, Boolean>,
    val characterClass: String,
    val subclass: String,
    val level: Int,
    val maxPreparedSpells: Int,
    val spellSlots: Map<Int, SpellSlotLevel>,
    val characterIcon: String
)

fun Character.normalized(): Character {
    return this.copy(
        characterClass=characterClass.lowercase(),
        subclass=subclass.lowercase()
    )
}

data class CharacterIcon (val icon: String) {
    companion object {
        private val mapping: Map<String, DrawableResource> = mapOf(
            "eye_outline" to Res.drawable.eye_outline,
            "flash" to Res.drawable.flash,
            "hand_back_left" to Res.drawable.hand_back_left,
            "lightning_bolt" to Res.drawable.lightning_bolt,
            "pentagram" to Res.drawable.pentagram,
            "pistol" to Res.drawable.pistol,
            "weather_windy" to Res.drawable.weather_windy,
            "wizard_hat" to Res.drawable.wizard_hat
        )

        fun options() : Set<String> {
            return mapping.keys
        }
    }

    @Composable
    fun fromString() : ImageVector {
        return mapping[icon]?.let{ vectorResource(it) } ?: Icons.Default.Close
    }


}

val defaultCharacter: Character = Character(
    id = 0,
    name = "",
    spells = emptyMap(),
    characterClass = "",
    subclass = "",
    level = 0,
    maxPreparedSpells = 0,
    spellSlots = emptyMap(),
    characterIcon = "Icon1"
)

fun Character.hasPreparedSpell(id: Int) : Boolean {
    return spells[id] ?: false
}

fun Character.knowsSpell(id: Int) : Boolean {
    return spells.containsKey(id)
}