package org.fufu.spellbook.spell.data.room.entities

import androidx.room.TypeConverter

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
}