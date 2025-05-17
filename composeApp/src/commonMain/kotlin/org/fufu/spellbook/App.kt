package org.fufu.spellbook

import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.lifecycle.ViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import org.fufu.spellbook.character.presentation.characterDetail.CharacterDetailScreenRoot
import org.fufu.spellbook.character.presentation.characterDetail.CharacterDetailVM
import org.fufu.spellbook.character.presentation.characterList.CharacterListRoot
import org.fufu.spellbook.character.presentation.characterList.CharacterListVM
import org.fufu.spellbook.character.presentation.editingCharacterDetail.EditingCharacterDetailScreenRoot
import org.fufu.spellbook.character.presentation.editingCharacterDetail.EditingCharacterDetailVM
import org.fufu.spellbook.di.MAIN_SPELL_LIST
import org.fufu.spellbook.navigation.Route
import org.fufu.spellbook.spell.presentation.ImportScreenRoot
import org.fufu.spellbook.spell.presentation.ImportScreenVM
import org.fufu.spellbook.spell.presentation.spellDetail.SpellDetailScreenRoot
import org.fufu.spellbook.spell.presentation.spellDetail.SpellDetailVM
import org.fufu.spellbook.spell.presentation.spellList.SpellListScreenRoot
import org.fufu.spellbook.spell.presentation.spellList.SpellListVM
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
fun Backable(
    navController: NavHostController,
    content: @Composable () -> Unit
){
    val focusRequester = remember { FocusRequester() }
    // prevents double-backs
    var triggered by remember { mutableStateOf(false) }
    LaunchedEffect(null){
        focusRequester.requestFocus()
    }

    Box(
        modifier = Modifier
            .focusRequester(focusRequester)
            .focusable()
            .onKeyEvent {
                if(!triggered && it.key == Key.Escape && it.type == KeyEventType.KeyUp){
                    navController.popBackStack()
                    triggered = true
                    true
                }else{
                    false
                }
            }
    ){
        content()
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
            label = { Text("Import") },
            onClick = {navController.popNavigateDistinct(Route.ImportScreen, currentRoute)},
            icon = { Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "import") }
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
fun App(
    requestWindowForSpell: ((sid: Int) -> Unit)? = null
) {
    MaterialTheme {
        val navController = rememberNavController()
        NavHost(
            navController = navController,
            startDestination = Route.RouteGraph
        ){
            navigation<Route.RouteGraph>(
                startDestination = Route.MainSpellList
            ){
                composable<Route.MainSpellList>(
                    //exitTransition = { slideOutHorizontally()},
                    //popEnterTransition = { slideInHorizontally() }
                ){
                    val listViewModel = koinViewModel<SpellListVM>(
                        qualifier = qualifier(MAIN_SPELL_LIST)
                    )
                    SpellListScreenRoot(
                        listViewModel,
                        onSpellSelected = {
                            navController.navigate(Route.SpellDetail(it.key))
                        },
                        navBar = {BottomNavBar(navController, Route.MainSpellList)},
                        onNewClicked = {
                            navController.navigate(Route.SpellDetail(0))
                        }
                    )
                }
                composable<Route.SpellDetail>(
                    enterTransition = { slideInVertically { initialOffset ->
                        initialOffset
                    } },
                    exitTransition = { slideOutVertically { initialOffset ->
                        initialOffset
                    } }
                ){ backStack ->
                    val spellID = backStack.toRoute<Route.SpellDetail>().spellID
                    val detailViewModel = koinViewModel<SpellDetailVM>(
                        parameters = { parametersOf(spellID) }
                    )
                    //backStack.destination.parent.

                    Backable(navController){
                        SpellDetailScreenRoot(
                            detailViewModel,
                            onCloseClicked = {
                                navController.popBackStack()
                            },
                            onPopoutClicked = requestWindowForSpell?.let{ { it(spellID) } }
                        )
                    }
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
                        navBar = {BottomNavBar(navController, Route.CharacterList)},
                        onNewClicked = {
                            navController.navigate(Route.EditingCharacterDetail(0))
                        }
                    )
                }
                composable<Route.CharacterDetail>(
//                    enterTransition = { slideInHorizontally { initialOffset ->
//                        initialOffset
//                    } },
//                    exitTransition = { slideOutHorizontally { initialOffset ->
//                        initialOffset
//                    } }
                ){ backStack ->
                    val characterID = backStack.toRoute<Route.CharacterDetail>().characterID
                    val detailViewModel = koinViewModel<CharacterDetailVM>(
                        parameters = { parametersOf(characterID) }
                    )

                    Backable(navController){
                        CharacterDetailScreenRoot(
                            detailViewModel,
                            onBack = {
                                navController.popBackStack()
                            },
                            onViewSpell = {
                                navController.navigate(Route.SpellDetail(it.key))
                            },
                            onClickEditCharacter = {
                                navController.navigate(Route.EditingCharacterDetail(it))
                            }
                        )
                    }
                }
                composable<Route.EditingCharacterDetail>{ backStack ->
                    val characterID = backStack.toRoute<Route.EditingCharacterDetail>().characterId
                    val editingDetailViewModel = koinViewModel<EditingCharacterDetailVM>(
                        parameters = { parametersOf(characterID) }
                    )

                    Backable(navController){
                        EditingCharacterDetailScreenRoot(
                            editingDetailViewModel,
                            onBack = {navController.popBackStack()}
                        )
                    }
                }
                composable<Route.ImportScreen>(

                ){
                    val importViewModel = koinViewModel<ImportScreenVM>()
                    ImportScreenRoot(
                        importViewModel,
                        navBar = { BottomNavBar(navController, Route.ImportScreen) }
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