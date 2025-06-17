package com.example.attendance.database

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class LocalDateAdapter : TypeAdapter<LocalDate>() {
    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE

    override fun write(out: JsonWriter, value: LocalDate) {
        out.value(value.format(formatter))
    }

    override fun read(input: JsonReader): LocalDate {
        return LocalDate.parse(input.nextString(), formatter)
    }
}
