package com.example.attendance.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.attendance.database.attendance.Attendance
import com.example.attendance.database.attendance.AttendanceDao
import com.example.attendance.database.subject.Subject
import com.example.attendance.database.subject.SubjectDao
import com.example.attendance.database.timeTable.TimeTable
import com.example.attendance.database.timeTable.TimeTableDao

@Database(entities = [Subject::class, Attendance::class, TimeTable::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun subjectDao() : SubjectDao
    abstract fun attendanceDao() : AttendanceDao
    abstract fun timetableDao() : TimeTableDao
}