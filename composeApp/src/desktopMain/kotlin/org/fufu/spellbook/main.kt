package org.fufu.spellbook

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.vinceglb.filekit.FileKit
import org.fufu.spellbook.di.initKoin
import org.fufu.spellbook.spell.presentation.spellDetail.LoadingSpellDetail
import org.fufu.spellbook.spell.presentation.spellDetail.SpellDetailVM
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import spellbook.composeapp.generated.resources.Res
import spellbook.composeapp.generated.resources.app_icon
import java.awt.Dimension

fun main(){
    val appId = if(BuildKonfig.isDebug){
        "org.fufu.spellbook.debug"
    }else{
        "org.fufu.spellbook"
    }
    FileKit.init(appId = appId)
    initKoin()
    application {
        WithCustomTheme {
            var spellsToDisplay by remember { mutableStateOf(emptyList<Int>()) }

            val mainWindowState = rememberWindowState()
            Window(
                state = mainWindowState,
                onCloseRequest = ::exitApplication,
                title = "Spell Book",
                icon = painterResource(Res.drawable.app_icon)
            ) {
                App(
                    requestWindowForSpell = {
                        if(it != 0){
                            spellsToDisplay = spellsToDisplay.plus(it)
                        }
                    }
                )
            }

            spellsToDisplay.forEach { spellId ->
                var windowName by remember { mutableStateOf("Spell View") }
                key(spellId){
                    val state = rememberWindowState()
                    state.isMinimized = mainWindowState.isMinimized
                    Window(
                        state = state,
                        onCloseRequest = { spellsToDisplay = spellsToDisplay.minus(spellId) },
                        title = windowName,
                        icon = painterResource(Res.drawable.app_icon)
                    ) {
                        window.size = Dimension(400, 400)
                        val vm = koinViewModel<SpellDetailVM>(
                            parameters = { parametersOf(spellId) }
                        )
                        val spellState by vm.state.collectAsStateWithLifecycle()
                        windowName = spellState.spellInfo?.name ?: "Spell View"

                        Scaffold {
                            LoadingSpellDetail(
                                spellState
                            )
                        }

                    }

                }
            }
        }
    }
}