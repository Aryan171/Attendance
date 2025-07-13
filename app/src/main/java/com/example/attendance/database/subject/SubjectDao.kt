package com.example.attendance.database.subject

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface SubjectDao {
    @Insert
    suspend fun insertSubjectAndGetId(subject : Subject): Long

    @Delete
    suspend fun delete(subject : Subject)

    @Update
    suspend fun update(subject : Subject)

    @Query("SELECT * FROM subject")
    suspend fun getAllSubjects() : List<Subject>
}