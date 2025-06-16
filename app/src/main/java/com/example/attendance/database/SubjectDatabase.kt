package com.example.attendance.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Subject::class], version = 1)
abstract class SubjectDatabase : RoomDatabase() {
    abstract fun subjectDao() : SubjectDao
}