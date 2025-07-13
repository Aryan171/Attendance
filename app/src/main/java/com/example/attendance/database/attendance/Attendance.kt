package com.example.attendance.database.attendance

import androidx.room.Entity

@Entity(
    tableName = "attendance",
    primaryKeys = ["subjectId", "date"]
    )
data class Attendance (
    val subjectId: Long,
    val date: String,
    val isPresent: Boolean
)