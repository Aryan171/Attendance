package com.example.attendance.database

import androidx.room.TypeConverter
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.time.LocalDate

class Converter {
    private val gson = GsonBuilder()
        .registerTypeAdapter(LocalDate::class.java, LocalDateAdapter())
        .create()

    @TypeConverter
    fun fromMutableMap(value: MutableMap<LocalDate, Boolean>): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toMutableMap(value: String): MutableMap<LocalDate, Boolean> {
        val mapType = object : TypeToken<MutableMap<LocalDate, Boolean>>() {}.type
        return gson.fromJson(value, mapType)
    }
}