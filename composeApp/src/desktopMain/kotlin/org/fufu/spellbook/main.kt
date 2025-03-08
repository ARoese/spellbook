package org.fufu.spellbook

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Spell Book",
    ) {
        App()
    }
}