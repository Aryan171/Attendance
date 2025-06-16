package com.example.attendance.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Date
import java.util.SortedMap

class Converter {
    @TypeConverter
    fun fromSortedMap(value: SortedMap<Date, Boolean>): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun toSortedMap(value: String): SortedMap<Date, Boolean> {
        val mapType = object : TypeToken<SortedMap<Date, Boolean>>() {}.type
        return Gson().fromJson(value, mapType)
    }
}