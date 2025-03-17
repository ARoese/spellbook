package org.fufu.spellbook.di

import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import org.fufu.spellbook.data.room.SpellBookDatabase
import org.fufu.spellbook.data.room.character.DBCharacterMutator
import org.fufu.spellbook.data.room.character.DBCharacterProvider
import org.fufu.spellbook.data.room.spell.DBSpellMutator
import org.fufu.spellbook.data.room.spell.DBSpellProvider
import org.fufu.spellbook.domain.CharacterMutator
import org.fufu.spellbook.domain.CharacterProvider
import org.fufu.spellbook.domain.MockCharacterProvider
import org.fufu.spellbook.domain.MockSpellProvider
import org.fufu.spellbook.domain.SpellMutator
import org.fufu.spellbook.domain.SpellProvider
import org.fufu.spellbook.presentation.character_detail.CharacterDetailVM
import org.fufu.spellbook.presentation.character_list.CharacterListVM
import org.fufu.spellbook.presentation.spell_list.SpellListVM
import org.fufu.spellbook.presentation.spell_detail.SpellDetailVM
import org.koin.dsl.module

import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.qualifier
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.bind
import org.koin.dsl.binds

const val MAIN_SPELL_LIST = "mainSpellList"
const val CHARACTER_SPELL_LIST_QUALIFIER = "characterSpellList"

// provides database class implementations
expect val databaseModule: Module

val sharedModule = module{
    // database
    single{ get<RoomDatabase.Builder<SpellBookDatabase>>().build() }
        .bind(SpellBookDatabase::class)
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

    viewModel{ (sid:Int) -> SpellDetailVM(sid, get()) }
    viewModel{ (cid:Int) -> CharacterDetailVM(cid, get()) }
    viewModel{ CharacterListVM(get()) }
    viewModel(qualifier = qualifier(MAIN_SPELL_LIST)){
        SpellListVM(get())
    }
    viewModel(qualifier = qualifier(CHARACTER_SPELL_LIST_QUALIFIER)){
        SpellListVM(get())
    }
}

fun initKoin(config: KoinAppDeclaration? = null){
    startKoin{
        config?.invoke(this)
        modules(sharedModule, databaseModule)
    }
}