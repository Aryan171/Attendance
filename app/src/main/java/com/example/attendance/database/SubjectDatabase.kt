package com.example.attendance.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Subject::class], version = 1)
@TypeConverters(Converter::class)
abstract class SubjectDatabase : RoomDatabase() {
    abstract fun subjectDao() : SubjectDao
}