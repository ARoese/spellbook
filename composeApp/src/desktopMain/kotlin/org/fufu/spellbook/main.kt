package org.fufu.spellbook

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import io.github.vinceglb.filekit.FileKit
import org.fufu.spellbook.di.initKoin

fun main(){
    FileKit.init(appId = "org.fufu.spellbook")
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