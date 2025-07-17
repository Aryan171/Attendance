package com.example.attendance.database.timeTable

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a timetable entry in the database.
 *
 * This data class is used as an Entity in Room to define the structure of the "timeTable" table.
 * Each instance of this class represents a specific class or lecture scheduled for a subject on a particular day and time.
 *
 * @property subjectId The unique identifier of the subject associated with this timetable entry. This is a foreign key referencing the `subjectId` in the "subject" table.
 * @property day An integer representing the day of the week for this timetable entry (e.g., 0 for Monday, 1 for Tuesday, etc.).
 * @property startTimeMillis The start time of the class or lecture, represented as milliseconds since the start of the day.
 * @property endTimeMillis The end time of the class or lecture, represented as milliseconds since the start of the day.
 */
@Entity (
    tableName = "timeTable"
)
data class TimeTable(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val subjectId: Long,
    val day: Int,
    val startTimeMillis: Long,
    val endTimeMillis: Long
)
