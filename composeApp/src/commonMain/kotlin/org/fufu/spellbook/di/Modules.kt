package org.fufu.spellbook.di

import org.fufu.spellbook.domain.CharacterProvider
import org.fufu.spellbook.domain.MockCharacterProvider
import org.fufu.spellbook.domain.MockSpellProvider
import org.fufu.spellbook.domain.PreviewSpells
import org.fufu.spellbook.domain.SpellProvider
import org.fufu.spellbook.presentation.character_detail.CharacterDetailVM
import org.fufu.spellbook.presentation.character_list.CharacterListVM
import org.fufu.spellbook.presentation.spell_list.SpellListVM
import org.fufu.spellbook.presentation.spell_detail.SpellDetailVM
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

import org.koin.core.context.startKoin
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.qualifier
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.bind

const val MAIN_SPELL_LIST = "mainSpellList"
const val CHARACTER_SPELL_LIST_QUALIFIER = "characterSpellList"

val sharedModule = module{
    single{ MockSpellProvider() }.bind<SpellProvider>()
    single{ MockCharacterProvider() }.bind<CharacterProvider>()
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
        modules(sharedModule)
    }
}