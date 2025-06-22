package org.fufu.spellbook.navigation

import kotlinx.serialization.Serializable


interface Route {
    @Serializable
    data object RouteGraph: Route

    @Serializable
    data object MainSpellList: Route

    @Serializable
    data class SpellDetail(val spellID : Int): Route

    @Serializable
    data object CharacterList: Route

    @Serializable
    data object ImportScreen: Route

    @Serializable
    data object SettingsScreen: Route

    @Serializable
    data class CharacterDetail(val characterID : Int): Route

    @Serializable
    data class EditingCharacterDetail(val characterId: Int): Route
}