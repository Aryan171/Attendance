package com.example.attendance.database.subject

import java.time.LocalDate

/**
 * Data class representing the UI model for a subject.
 *
 * @property id The unique identifier of the subject. Defaults to 0.
 * @property name The name of the subject.
 * @property presentDays The number of days the student was present for this subject. Defaults to 0.
 * @property absentDays The number of days the student was absent for this subject. Defaults to 0.
 * @property attendance A mutable map storing the attendance record for the subject, where the key is the date
 *                      and the value is a boolean indicating presence (true) or absence (false).
 *                      Defaults to an empty mutable map.
 */
data class SubjectUiModel (
    val id: Long = 0,
    val name: String,
    val presentDays: Int = 0,
    val absentDays: Int = 0,
    val attendance: MutableMap<LocalDate, Boolean> = mutableMapOf<LocalDate, Boolean>()
)