package com.example.attendance.database.subject

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface SubjectDao {
    @Insert
    suspend fun insertSubject(subject : Subject): Long

    @Query("DELETE FROM subject WHERE id = :subjectId")
    suspend fun deleteBySubjectId(subjectId: Long)

    @Update
    suspend fun update(subject : Subject)

    @Query("SELECT * FROM subject")
    suspend fun getAllSubjects() : List<Subject>
}