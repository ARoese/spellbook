package org.fufu.spellbook.domain

enum class Book {
    WILDEMOUNT,
    LOSTLAB,
    SCAG,
    ELEMENTALEVIL,
    XANATHAR,
    STRIX,
    FIZBAN,
    PHB,
    RIME,
    ACQINC,
    TASHA,
    RAVNICA,
    OTHER
}

enum class DamageType {
    RADIANT,
    POISON,
    NECROTIC,
    THUNDER,
    PIERCING,
    PSYCHIC,
    COLD,
    BLUDGEONING,
    SLASHING,
    FIRE,
    LIGHTNING,
    FORCE,
    ACID,
}

enum class DragonMark {
    HEALING,
    SHADOW,
    SCRIBING,
    FINDING,
    SENTINEL,
    MAKING,
    HOSPITALITY,
    STORM,
    DETECTION,
    HANDLING,
    PASSAGE,
    WARDING,
}

enum class MagicSchool {
    DIVINATION,
    ABJURATION,
    ENCHANTMENT,
    ILLUSION,
    EVOCATION,
    CONJURATION,
    NECROMANCY,
    TRANSFORMATION
}

enum class SaveType {
    CON,
    INT,
    WIS,
    STR,
    CHA,
    DEX
}

data class Spell(
    val book: List<Book>,
    val classes: List<String>,
    val components: String,
    val duration: String,
    val guilds: List<String>,
    val key: Int,
    val level: Int,
    val name: String,
    val optional: List<String>,
    val range: String,
    val ritual: Boolean,
    val school: MagicSchool,
    val subclasses: List<String>,
    val text: String,
    val time: String,
    val tag: List<String>,
    val damages: List<DamageType>,
    val saves: List<SaveType>,
    val dragonmarks: List<DragonMark>
)
