package org.fufu.spellbook.spell.data.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.SerializationException
import org.fufu.spellbook.spell.domain.Book
import org.fufu.spellbook.spell.domain.DamageType
import org.fufu.spellbook.spell.domain.DragonMark
import org.fufu.spellbook.spell.domain.MagicSchool
import org.fufu.spellbook.spell.domain.SaveType
import org.fufu.spellbook.spell.domain.Spell
import org.fufu.spellbook.spell.domain.SpellInfo

@Dao
interface SpellDao{
    @Upsert
    suspend fun upsertSpell(item: SpellEntity)

    @Query("SELECT * FROM SpellEntity")
    fun getAllSpells(): Flow<List<SpellEntity>>

    @Query("SELECT * FROM SpellEntity where `key` in (:keys)")
    fun getAllSpells(keys: Set<Int>) : Flow<List<SpellEntity>>

    @Query("SELECT * FROM SpellEntity where `key`=:key")
    fun getSpell(key: Int) : Flow<SpellEntity?>

    @Delete
    suspend fun deleteSpell(spell: SpellEntity)
}

class Converters{
    @TypeConverter
    fun fromStringList(strings: List<String>) : String {
        return strings.joinToString("|")
    }

    @TypeConverter
    fun toStringList(s: String) : List<String> {
        if(s.isEmpty()){
            return listOf()
        }
        return s.splitToSequence('|').toList()
    }

    private inline fun <reified T : Enum<T>>toStringUsingName(enum: T) : String{
        return enum.name
    }

    private inline fun <reified T : Enum<T>>toEnumUsingName(enum: String, default: T? = null) : T {
        val map = enumValues<T>().associate { (it.name to it) }
        return map[enum] ?: default ?: throw SerializationException("illegal enum: $enum")
    }

    @TypeConverter
    fun toMagicSchool(s: String) : MagicSchool {
        return toEnumUsingName(s, MagicSchool.OTHER)
    }

    @TypeConverter
    fun fromMagicSchool(school: MagicSchool) : String {
        return toStringUsingName(school)
    }

    @TypeConverter
    fun fromBookList(books: List<Book>) : String {
        return fromStringList(books.map{toStringUsingName(it)})
    }

    @TypeConverter
    fun toBookList(s: String) : List<Book> {
        if(s.isEmpty()){
            return listOf()
        }
        return toStringList(s).map{
            toEnumUsingName(it, Book.OTHER)
        }
    }

    @TypeConverter
    fun toDamageTypeList(s: String) : List<DamageType> {
        if(s.isEmpty()){
            return listOf()
        }
        return toStringList(s).map{toEnumUsingName(it)}
    }

    @TypeConverter
    fun fromDamageTypeList(types: List<DamageType>) : String {
        return fromStringList(types.map{toStringUsingName(it)})
    }

    @TypeConverter
    fun toDragonMarkList(s: String) : List<DragonMark> {
        if(s.isEmpty()){
            return listOf()
        }
        return toStringList(s).map{toEnumUsingName(it)}
    }

    @TypeConverter
    fun fromDragonMarkList(marks: List<DragonMark>) : String {
        return fromStringList(marks.map{toStringUsingName(it)})
    }

    @TypeConverter
    fun toSaveTypeList(s: String) : List<SaveType> {
        if(s.isEmpty()){
            return listOf()
        }
        return toStringList(s).map{toEnumUsingName(it)}
    }

    @TypeConverter
    fun fromSaveTypeList(marks: List<SaveType>) : String {
        return fromStringList(marks.map{toStringUsingName(it)})
    }
}

@Entity
@TypeConverters(Converters::class)
data class SpellEntity(
    @PrimaryKey(autoGenerate = true) val key: Int = 0,
    val book: List<Book>,
    val classes: List<String>,
    val components: String,
    val duration: String,
    val guilds: List<String>,
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

fun SpellEntity.toSpell(): Spell {
    return Spell(
        key = key,
        info = SpellInfo(
            book = book,
            classes = classes,
            components = components,
            duration = duration,
            guilds = guilds,
            level = level,
            name = name,
            optional = optional,
            range = range,
            ritual = ritual,
            school = school,
            subclasses = subclasses,
            text = text,
            time = time,
            tag = tag,
            damages = damages,
            saves = saves,
            dragonmarks = dragonmarks
        )
    )
}

fun Spell.toEntity() : SpellEntity {
    return SpellEntity(
        key = key,
        book = info.book,
        classes = info.classes,
        components = info.components,
        duration = info.duration,
        guilds = info.guilds,
        level = info.level,
        name = info.name,
        optional = info.optional,
        range = info.range,
        ritual = info.ritual,
        school = info.school,
        subclasses = info.subclasses,
        text = info.text,
        time = info.time,
        tag = info.tag,
        damages = info.damages,
        saves = info.saves,
        dragonmarks = info.dragonmarks
    )
}