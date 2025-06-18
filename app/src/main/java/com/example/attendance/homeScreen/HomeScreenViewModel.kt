package com.example.attendance.homeScreen

import androidx.compose.animation.core.copy
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.attendance.database.Subject
import com.example.attendance.database.SubjectDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate

class HomeScreenViewModel(
    private val dao: SubjectDao
): ViewModel() {
    init {
        reloadSubjectList()
    }

    private val _subjectList = mutableStateListOf<Subject>()
    val subjectList = _subjectList

    fun resetAttendance(subject: Subject) {
        updateSubject(subject.copy(
            presentDays = 0,
            absentDays = 0,
            attendance = mutableMapOf<LocalDate, Boolean>()
        ))
    }

    fun markPresent(subject: Subject, date: LocalDate) {
        var updatedPresentDays = subject.presentDays
        var updatedAbsentDays = subject.absentDays
        var updatedAttendance = subject.attendance//.toMutableMap()

        if (subject.attendance[date] == false) {
            updatedPresentDays++
            updatedAbsentDays--
        }
        else if (subject.attendance[date] == null){
            updatedPresentDays++
        }
        updatedAttendance[date] = true
        updateSubject(subject.copy(
            presentDays = updatedPresentDays,
            absentDays = updatedAbsentDays,
            attendance = updatedAttendance
        ))
    }

    fun clearAttendance(subject: Subject, date: LocalDate) {
        var updatedPresentDays = subject.presentDays
        var updatedAbsentDays = subject.absentDays
        var updatedAttendance = subject.attendance//.toMutableMap()

        if (updatedAttendance[date] == true) {
            updatedPresentDays--
        }
        else if (updatedAttendance[date] == false) {
            updatedAbsentDays--
        }
        updatedAttendance.remove(date)
        updateSubject(subject.copy(
            presentDays = updatedPresentDays,
            absentDays = updatedAbsentDays,
            attendance = updatedAttendance
        ))
    }

    fun markAbsent(subject: Subject, date: LocalDate) {
        var updatedPresentDays = subject.presentDays
        var updatedAbsentDays = subject.absentDays
        var updatedAttendance = subject.attendance//.toMutableMap()

        if (updatedAttendance[date] == true) {
            updatedAbsentDays++
            updatedPresentDays--
        }
        else if (updatedAttendance[date] == null){
            updatedAbsentDays++
        }
        updatedAttendance[date] = false
        updateSubject(subject.copy(
            presentDays = updatedPresentDays,
            absentDays = updatedAbsentDays,
            attendance = updatedAttendance
        ))
    }

    fun addSubject(subject: Subject) {
        viewModelScope.launch {
            val generatedId = withContext(Dispatchers.IO) {
                dao.insertSubjectAndGetId(subject)
            }
            _subjectList.add(subject.copy(id = generatedId) )
        }
    }

    fun updateSubject(subject: Subject) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.update(subject)
        }
        val index = _subjectList.indexOfFirst { it.id == subject.id }

        if (index != -1) {
            _subjectList[index] = subject
        }
    }

    fun deleteSubject(subject: Subject) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.delete(subject)
        }
        _subjectList.removeIf {
            it.id == subject.id
        }
    }

    fun reloadSubjectList() {
        viewModelScope.launch(Dispatchers.IO) {
            _subjectList.clear()
            _subjectList.addAll(dao.getAllSubjects())
        }
    }
}