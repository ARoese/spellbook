package org.fufu.spellbook

import org.koin.android.ext.koin.androidContext
import android.app.Application
import org.fufu.spellbook.di.initKoin

class SpellBookApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@SpellBookApplication)
        }
    }
}