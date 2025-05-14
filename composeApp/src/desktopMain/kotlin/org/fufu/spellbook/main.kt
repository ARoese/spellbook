package org.fufu.spellbook

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import io.github.vinceglb.filekit.FileKit
import org.fufu.spellbook.di.initKoin
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import spellbook.composeapp.generated.resources.Res
import spellbook.composeapp.generated.resources.app_icon

fun main(){
    val appId = if(BuildKonfig.isDebug){
        "org.fufu.spellbook.debug"
    }else{
        "org.fufu.spellbook"
    }
    FileKit.init(appId = appId)
    initKoin()
    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "Spell Book",
            icon = painterResource(Res.drawable.app_icon)
        ) {
            App()
        }
    }
}