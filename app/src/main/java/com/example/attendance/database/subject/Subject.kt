package com.example.attendance.database.subject

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "subject")
data class Subject(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val presentDays: Int = 0,
    val absentDays: Int = 0,
    val attendance: MutableMap<LocalDate, Boolean> = mutableMapOf<LocalDate, Boolean>()
)