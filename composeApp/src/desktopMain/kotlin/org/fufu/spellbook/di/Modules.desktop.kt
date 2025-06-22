package org.fufu.spellbook.di

import org.fufu.spellbook.jvm.createDataStore
import org.fufu.spellbook.jvm.getDatabaseBuilder
import org.koin.core.module.Module
import org.koin.dsl.module

actual val databaseModule: Module = module {
    single { getDatabaseBuilder() }
    single { createDataStore() }
}