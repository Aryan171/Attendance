package com.example.attendance.database.attendance

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.attendance.database.subject.Subject

@Dao
interface AttendanceDao {
    @Query("DELETE FROM attendance WHERE subjectId = :subjectId AND date = :date")
    suspend fun delete(subjectId: Long, date: String)

    @Upsert
    suspend fun upsert(attendance: Attendance)

    @Query("SELECT * FROM subject")
    suspend fun getAllSubjects() : List<Subject>
}