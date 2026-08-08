package org.fufu.spellbook.spell.data.room.entities

import androidx.room3.ColumnTypeConverter

class Converters{
    @ColumnTypeConverter
    fun fromStringList(strings: List<String>) : String {
        return strings.joinToString("|")
    }

    @ColumnTypeConverter
    fun toStringList(s: String) : List<String> {
        if(s.isEmpty()){
            return listOf()
        }
        return s.splitToSequence('|').toList()
    }
}