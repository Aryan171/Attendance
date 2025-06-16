package com.example.attendance.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date
import java.util.SortedMap

@Entity(tableName = "subject")
data class Subject(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val subjectName: String,
    val presentDays: Int,
    val absentDays: Int,
    val calendar: SortedMap<Date, Boolean>
)
