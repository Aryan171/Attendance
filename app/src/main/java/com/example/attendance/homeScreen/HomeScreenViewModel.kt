package com.example.attendance.homeScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.attendance.MainActivity
import com.example.attendance.database.Subject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class HomeScreenViewModel: ViewModel() {
    private val dao = MainActivity.db.subjectDao()

    private val _subjectList = MutableStateFlow<List<Subject>>(emptyList())
    var subjectList = _subjectList

    fun addSubject(subject: Subject) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.insert(subject)
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