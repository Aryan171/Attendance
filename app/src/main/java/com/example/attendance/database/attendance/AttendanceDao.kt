package com.example.attendance.database.attendance

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface AttendanceDao {
    @Query("DELETE FROM attendance WHERE subjectId = :subjectId AND date = :date")
    suspend fun delete(subjectId: Long, date: String)

    @Query("DELETE FROM attendance WHERE subjectId = :subjectId")
    suspend fun deleteBySubjectId(subjectId: Long)

    @Query("SELECT COUNT(*) FROM attendance WHERE subjectId = :subjectId AND isPresent = 1")
    suspend fun getNumberOfPresentDays(subjectId: Long) : Int

    @Query("SELECT COUNT(*) FROM attendance WHERE subjectId = :subjectId AND isPresent = 0")
    suspend fun getNumberOfAbsentDays(subjectId: Long) : Int

    @Upsert
    suspend fun upsert(attendance: Attendance)

    @Query("SELECT * FROM attendance WHERE subjectId = :subjectId")
    suspend fun getAttendance(subjectId: Long) : List<Attendance>
}