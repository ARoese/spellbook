package org.fufu.spellbook.di

import org.fufu.spellbook.android.createDataStore
import org.fufu.spellbook.android.getDatabaseBuilder
import org.koin.android.ext.koin.androidApplication
import org.koin.core.module.Module
import org.koin.dsl.module

actual val databaseModule: Module = module {
    single { getDatabaseBuilder(androidApplication()) }
    single { createDataStore(androidApplication()) }
}