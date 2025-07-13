package com.example.attendance.database

import com.example.attendance.database.attendance.AttendanceDao
import com.example.attendance.database.subject.Subject
import com.example.attendance.database.subject.SubjectDao

class DatabaseRepository(
    private val attendanceDao: AttendanceDao,
    private val subjectDao: SubjectDao
    ) {
    suspend fun getAllSubjects(): List<Subject> = subjectDao.getAllSubjects()

    suspend fun deleteSubject(subject: Subject) {
        attendanceDao.deleteBySubjectId(subject.id)
        subjectDao.delete(subject)
    }

    suspend fun insertSubject(subject: Subject) {
        subjectDao.insertSubject(subject)
    }
}