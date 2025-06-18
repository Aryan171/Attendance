package com.example.attendance.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "subject")
data class Subject(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val subjectName: String,
    val presentDays: Int = 0,
    val absentDays: Int = 0,
    val attendance: MutableMap<LocalDate, Boolean> = mutableMapOf<LocalDate, Boolean>()
)
