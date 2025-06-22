package org.fufu.spellbook.di

import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import org.fufu.spellbook.SpellBookDatabase
import org.fufu.spellbook.character.data.room.DBCharacterMutator
import org.fufu.spellbook.character.domain.CharacterMutator
import org.fufu.spellbook.character.domain.CharacterProvider
import org.fufu.spellbook.character.presentation.characterDetail.CharacterDetailVM
import org.fufu.spellbook.character.presentation.characterList.CharacterListVM
import org.fufu.spellbook.character.presentation.editingCharacterDetail.EditingCharacterDetailVM
import org.fufu.spellbook.spell.data.room.DBSpellMutator
import org.fufu.spellbook.spell.domain.SpellMutator
import org.fufu.spellbook.spell.domain.SpellProvider
import org.fufu.spellbook.spell.presentation.ImportScreenVM
import org.fufu.spellbook.spell.presentation.spellDetail.SpellDetailVM
import org.fufu.spellbook.spell.presentation.spellList.SpellListVM
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.qualifier
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.bind
import org.koin.dsl.binds
import org.koin.dsl.module

const val MAIN_SPELL_LIST = "mainSpellList"

// provides database class implementations
expect val databaseModule: Module

val sharedModule = module{
    // database
    single{
        get<RoomDatabase.Builder<SpellBookDatabase>>()
            .setDriver(BundledSQLiteDriver())
            .build()
    }.bind(SpellBookDatabase::class)
    // database DAOs
    single{ get<SpellBookDatabase>().getSpellDao() }
    single{ get<SpellBookDatabase>().getCharacterDao() }

    single{ DBSpellMutator(get()) }.binds(
        arrayOf(
            SpellProvider::class,
            SpellMutator::class
        )
    )
    single{ DBCharacterMutator(get()) }.binds(
        arrayOf(
            CharacterProvider::class,
            CharacterMutator::class
        )
    )

    single{  }

    viewModel{ (sid:Int) -> SpellDetailVM(sid, get()) }
    viewModel{
        (cid:Int) ->
            CharacterDetailVM(
                cid,
                get(),
                SpellListVM(get()),
                SpellListVM(get()),
                SpellListVM(get())
            )
    }
    viewModel{ (cid:Int) -> EditingCharacterDetailVM(cid, get()) }
    viewModel{ CharacterListVM(get()) }
    viewModel{ ImportScreenVM(get(), null) }
    viewModel(qualifier = qualifier(MAIN_SPELL_LIST)){
        SpellListVM(get())
    }
}

fun initKoin(config: KoinAppDeclaration? = null){
    startKoin{
        config?.invoke(this)
        modules(sharedModule, databaseModule)
    }
}