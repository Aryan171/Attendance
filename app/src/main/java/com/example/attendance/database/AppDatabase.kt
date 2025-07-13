package com.example.attendance.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.attendance.database.attendance.Attendance
import com.example.attendance.database.attendance.AttendanceDao
import com.example.attendance.database.subject.Subject
import com.example.attendance.database.subject.SubjectDao

@Database(entities = [Subject::class, Attendance::class], version = 1)
@TypeConverters(Converter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun subjectDao() : SubjectDao
    abstract fun attendanceDao() : AttendanceDao
}