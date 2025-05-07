package org.fufu.spellbook.spell.data.room.entities

import androidx.room.TypeConverter
import org.fufu.spellbook.spell.domain.DamageType
import org.fufu.spellbook.spell.domain.DragonMark
import org.fufu.spellbook.spell.domain.MagicSchool
import org.fufu.spellbook.spell.domain.SaveType
import org.fufu.spellbook.toEnumUsingName
import org.fufu.spellbook.toStringUsingName

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

    @TypeConverter
    fun toMagicSchool(s: String) : MagicSchool {
        return toEnumUsingName(s, MagicSchool.OTHER)
    }

    @TypeConverter
    fun fromMagicSchool(school: MagicSchool) : String {
        return toStringUsingName(school)
    }

    @TypeConverter
    fun toDamageTypeList(s: String) : List<DamageType> {
        if(s.isEmpty()){
            return listOf()
        }
        return toStringList(s).map{ toEnumUsingName(it) }
    }

    @TypeConverter
    fun fromDamageTypeList(types: List<DamageType>) : String {
        return fromStringList(types.map{ toStringUsingName(it) })
    }

    @TypeConverter
    fun toDragonMarkList(s: String) : List<DragonMark> {
        if(s.isEmpty()){
            return listOf()
        }
        return toStringList(s).map{ toEnumUsingName(it) }
    }

    @TypeConverter
    fun fromDragonMarkList(marks: List<DragonMark>) : String {
        return fromStringList(marks.map{ toStringUsingName(it) })
    }

    @TypeConverter
    fun toSaveTypeList(s: String) : List<SaveType> {
        if(s.isEmpty()){
            return listOf()
        }
        return toStringList(s).map{ toEnumUsingName(it) }
    }

    @TypeConverter
    fun fromSaveTypeList(marks: List<SaveType>) : String {
        return fromStringList(marks.map{ toStringUsingName(it) })
    }
}