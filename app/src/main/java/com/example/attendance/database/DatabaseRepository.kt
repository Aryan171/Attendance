package com.example.attendance.database

import com.example.attendance.database.attendance.Attendance
import com.example.attendance.database.attendance.AttendanceDao
import com.example.attendance.database.subject.Subject
import com.example.attendance.database.subject.SubjectDao
import com.example.attendance.database.subject.SubjectUiModel
import java.time.LocalDate

class DatabaseRepository(
    private val attendanceDao: AttendanceDao,
    private val subjectDao: SubjectDao
    ) {
    suspend fun getAllSubjects(): List<SubjectUiModel> =
        subjectDao.getAllSubjects().map { subject ->
            SubjectUiModel(
                subject.id,
                subject.name,
                attendanceDao.getNumberOfPresentDays(subject.id),
                absentDays = attendanceDao.getNumberOfAbsentDays(subject.id),
                attendance = attendanceDao.getAttendance(subject.id)
                    .associate { attendance ->
                        LocalDate.parse(attendance.date) to attendance.isPresent
                    } as MutableMap<LocalDate, Boolean>
            )
        }

    suspend fun clearAllAttendance(subjectId: Long) {
        attendanceDao.deleteBySubjectId(subjectId)
    }

    suspend fun markPresent(subjectId: Long, date: LocalDate) {
        attendanceDao.upsert(Attendance(subjectId, date.toString(), true))
    }

    suspend fun clearAttendance(subjectId: Long, date: LocalDate) {
        attendanceDao.delete(subjectId, date.toString())
    }

    suspend fun markAbsent(subjectId: Long, date: LocalDate) {
        attendanceDao.upsert(Attendance(subjectId, date.toString(), false))
    }

    suspend fun deleteSubject(subjectId: Long) {
        attendanceDao.deleteBySubjectId(subjectId)
        subjectDao.deleteBySubjectId(subjectId)
    }

    suspend fun renameSubject(subjectId: Long, name: String) {
        subjectDao.update(Subject(subjectId, name))
    }

    suspend fun insertSubject(subject: Subject): Long = subjectDao.insertSubject(subject)
}