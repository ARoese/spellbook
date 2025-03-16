package org.fufu.spellbook

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import org.fufu.spellbook.di.MAIN_SPELL_LIST
import org.fufu.spellbook.presentation.character_detail.CharacterDetailScreenRoot
import org.fufu.spellbook.presentation.character_detail.CharacterDetailVM
import org.fufu.spellbook.presentation.character_list.CharacterListRoot
import org.fufu.spellbook.presentation.character_list.CharacterListVM
import org.fufu.spellbook.presentation.spell_detail.SpellDetailScreenRoot
import org.fufu.spellbook.presentation.spell_detail.SpellDetailVM
import org.fufu.spellbook.presentation.spell_list.SpellListRoot
import org.fufu.spellbook.presentation.spell_list.SpellListVM
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.qualifier

fun NavHostController.popNavigateDistinct(newRoute: Route, currentRoute: Route){
    if(newRoute != currentRoute){
        popBackStack(route = Route.RouteGraph, inclusive = false)
        navigate(route = newRoute)
    }
}

@Composable
fun BottomNavBar(navController: NavHostController, currentRoute: Route){
    NavigationBar(containerColor = Color.LightGray) {
        NavigationBarItem(
            selected = currentRoute == Route.MainSpellList,
            label = { Text("Spells")},
            onClick = {navController.popNavigateDistinct(Route.MainSpellList, currentRoute)},
            icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = "Spells")}
        )
        NavigationBarItem(
            selected = currentRoute == Route.CharacterList,
            label = { Text("Characters")},
            onClick = {navController.popNavigateDistinct(Route.CharacterList, currentRoute)},
            icon = { Icon(Icons.Filled.Person, contentDescription = "Characters")}
        )
        NavigationBarItem(
            selected = false,
            label = { Text("Settings")},
            onClick = {},
            icon = { Icon(Icons.Filled.Settings, contentDescription = "Settings")}
        )
    }
}

@Composable
fun App() {
    MaterialTheme {
        val navController = rememberNavController()
        NavHost(
            navController = navController,
            startDestination = Route.RouteGraph
        ){
            navigation<Route.RouteGraph>(
                startDestination = Route.CharacterList
            ){
                composable<Route.MainSpellList>(
                    //exitTransition = { slideOutHorizontally()},
                    //popEnterTransition = { slideInHorizontally() }
                ){
                    val listViewModel = koinViewModel<SpellListVM>(
                        qualifier = qualifier(MAIN_SPELL_LIST)
                    )
                    SpellListRoot(
                        listViewModel,
                        onSpellSelected = {
                            navController.navigate(Route.SpellDetail(it.key))
                        },
                        navBar = {BottomNavBar(navController, Route.MainSpellList)}
                    )
                }
                composable<Route.SpellDetail>(
                    enterTransition = { slideInHorizontally { initialOffset ->
                        initialOffset
                    } },
                    exitTransition = { slideOutHorizontally { initialOffset ->
                        initialOffset
                    } }
                ){ backStack ->
                    NavigationBar {  }
                    val spellID = backStack.toRoute<Route.SpellDetail>().spellID
                    val detailViewModel = koinViewModel<SpellDetailVM>(
                        parameters = { parametersOf(spellID) }
                    )
                    //backStack.destination.parent.

                    SpellDetailScreenRoot(
                        detailViewModel,
                        onCloseClicked = {
                            navController.popBackStack()
                        }
                    )
                }
                composable<Route.CharacterList>(
                    //enterTransition = { slideInHorizontally { initialOffset ->
                    //    initialOffset
                    //} },
                    //exitTransition = { slideOutHorizontally { initialOffset ->
                    //    initialOffset
                    //} }
                ){
                    val characterListViewModel = koinViewModel<CharacterListVM>()

                    CharacterListRoot(
                        characterListViewModel,
                        onCharacterClicked = {
                            navController.navigate(Route.CharacterDetail(it.id))
                        },
                        navBar = {BottomNavBar(navController, Route.CharacterList)}
                    )
                }
                composable<Route.CharacterDetail>(
                    enterTransition = { slideInHorizontally { initialOffset ->
                        initialOffset
                    } },
                    exitTransition = { slideOutHorizontally { initialOffset ->
                        initialOffset
                    } }
                ){ backStack ->
                    val characterID = backStack.toRoute<Route.CharacterDetail>().characterID
                    val detailViewModel = koinViewModel<CharacterDetailVM>(
                        parameters = { parametersOf(characterID) }
                    )

                    CharacterDetailScreenRoot(
                        detailViewModel,
                        onBack = {
                            navController.popBackStack()
                        },
                        onViewSpell = {
                            navController.navigate(Route.SpellDetail(it.key))
                        }
                    )
                }
            }
        }
    }
}

@Composable
private inline fun <reified T: ViewModel> NavBackStackEntry.sharedKoinViewModel(
    navController: NavController
): T {
    val navGraphRoute = destination.parent?.route ?: return koinViewModel<T>()
    val parentEntry = remember(this) {
        navController.getBackStackEntry(navGraphRoute)
    }
    return koinViewModel(
        viewModelStoreOwner = parentEntry
    )
}