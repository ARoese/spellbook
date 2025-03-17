package org.fufu.spellbook

import android.app.Application
import org.fufu.spellbook.di.initKoin
import org.koin.android.ext.koin.androidContext

class SpellBookApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@SpellBookApplication)
        }
    }
}