package com.example.attendance.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "subject")
data class Subject(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    var subjectName: String,
    var presentDays: Int = 0,
    var absentDays: Int = 0,
    var attendance: MutableMap<LocalDate, Boolean> = mutableMapOf<LocalDate, Boolean>()
)
