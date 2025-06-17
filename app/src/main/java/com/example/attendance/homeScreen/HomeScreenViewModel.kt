package com.example.attendance.homeScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.attendance.MainActivity
import com.example.attendance.database.Subject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

class HomeScreenViewModel: ViewModel() {
    private val dao = MainActivity.db.subjectDao()

    private val _subjectList = MutableStateFlow(emptyList<Subject>())
    val subjectList = _subjectList

    fun resetAttendance(subject: Subject) {
        subject.attendance.clear()
        updateSubject(subject)
    }

    fun markPresent(subject: Subject, date: LocalDate) {
        subject.attendance[date] = true
        updateSubject(subject)
    }

    fun clearAttendance(subject: Subject, date: LocalDate) {
        subject.attendance.remove(date)
        updateSubject(subject)
    }

    fun markAbsent(subject: Subject, date: LocalDate) {
        subject.attendance[date] = false
        updateSubject(subject)
    }

    fun addSubject(subject: Subject) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.insert(subject)
        }.invokeOnCompletion {
            reloadSubjectList()
        }
    }

    fun updateSubject(subject: Subject) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.update(subject)
        }.invokeOnCompletion {
            reloadSubjectList()
        }
    }

    fun deleteSubject(subject: Subject) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.delete(subject)
        }.invokeOnCompletion {
            reloadSubjectList()
        }
    }

    fun reloadSubjectList() {
        viewModelScope.launch(Dispatchers.IO) {
            _subjectList.value = dao.getAllSubjects()
        }
    }
}