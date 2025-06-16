package com.example.attendance.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.time.LocalDate
import java.util.SortedMap

class Converter {
    @TypeConverter
    fun fromSortedMap(value: SortedMap<LocalDate, Boolean>): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun toSortedMap(value: String): SortedMap<LocalDate, Boolean> {
        val mapType = object : TypeToken<SortedMap<LocalDate, Boolean>>() {}.type
        return Gson().fromJson(value, mapType)
    }
}