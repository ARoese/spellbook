package org.fufu.spellbook

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform