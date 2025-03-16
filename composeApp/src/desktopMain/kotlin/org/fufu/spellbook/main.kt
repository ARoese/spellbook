package org.fufu.spellbook

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import org.fufu.spellbook.di.initKoin

fun main(){
    initKoin()
    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "Spell Book",
        ) {
            App()
        }
    }
}