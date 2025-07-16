package com.example.attendance.viewModel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.attendance.Preferences.PreferencesRepository
import com.example.attendance.database.Subject
import com.example.attendance.database.SubjectDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.Month
import kotlin.math.ceil
import kotlin.math.floor

class AttendanceViewModel(
    private val dao: SubjectDao,
    private val preferencesRepository: PreferencesRepository
): ViewModel() {
    init {
        loadSubjectList()
    }

    val _subjectList = mutableStateListOf<Subject>()
    val subjectList = _subjectList

    val minimumRequiredAttendanceRatio = preferencesRepository.getMinimumRequiredAttendanceRatio()
        .stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), 0.75f
        )

    fun setMinimumRequiredAttendanceRatio(minAttendanceRatio: Float) {
        viewModelScope.launch(Dispatchers.IO) {
            preferencesRepository.setMinimumRequiredAttendanceRatio(minAttendanceRatio)
        }
    }

    fun clearAttendance(subject: Subject) {
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

    fun loadSubjectList() {
        viewModelScope.launch(Dispatchers.IO) {
            _subjectList.clear()
            _subjectList.addAll(dao.getAllSubjects())
        }
    }

    fun attendanceRatio(subject: Subject, month: Month, year: Int): Float {
        var day = LocalDate.of(year, month, 1)
        var absentDays = 0.0f
        var presentDays = 0.0f

        while(day.month == month) {
            if (subject.attendance[day] == true) {
                presentDays++
            } else if (subject.attendance[day] == false) {
                absentDays++
            }
            day = day.plusDays(1)
        }

        val totalDays = absentDays + presentDays
        return if (totalDays == 0f) {
            1f
        } else {
            presentDays / totalDays
        }
    }

    fun attendanceRatio(subject: Subject): Float {
        val totalDays = subject.absentDays + subject.presentDays

        return if (totalDays == 0) {
            1f
        } else {
            subject.presentDays.toFloat() / totalDays
        }
    }

    fun setAllPresent(date: LocalDate) {
        for(subject in _subjectList) {
            markPresent(subject, date)
        }
    }

    fun setAllAbsent(date: LocalDate) {
        for(subject in _subjectList) {
            markAbsent(subject, date)
        }
    }

    fun clearAllAttendance(date: LocalDate) {
        for(subject in _subjectList) {
            clearAttendance(subject, date)
        }
    }

    fun sortSubjectListBy(
        comparator: (Subject, Subject) -> Int
    ) {
        _subjectList.sortWith(comparator)
    }

    fun presentDaysInMonth(subject: Subject, month: Month, year: Int): Int {
        var day = LocalDate.of(year, month, 1)
        var res = 0

        while(day.month == month) {
            if (subject.attendance[day] == true) {
                res++
            }
            day = day.plusDays(1)
        }

        return res
    }

    fun absentDaysInMonth(subject: Subject, month: Month, year: Int): Int {
        var day = LocalDate.of(year, month, 1)
        var res = 0

        while(day.month == month) {
            if (subject.attendance[day] == false) {
                res++
            }
            day = day.plusDays(1)
        }

        return res
    }

    fun renameSubject(subject: Subject, name: String) {
        updateSubject(subject.copy(name = name))

    }



    /**
     * Calculates the attendance buffer for a subject.
     * The attendance buffer is the number of classes a student can miss or must attend
     * to maintain a minimum required attendance ratio (currently 75%).
     *
     * If the current attendance ratio is below the minimum:
     *  - The function returns a negative integer. The absolute value of this integer
     *    represents the number of consecutive classes the student **must attend**
     *    to reach the minimum required attendance.
     *
     * If the current attendance ratio is at or above the minimum:
     *  - The function returns a positive integer. This integer represents the number of
     *    consecutive classes the student **can miss** and still maintain the minimum
     *    required attendance.
     *
     * @param subject The subject for which to calculate the attendance buffer.
     * @return An integer representing the attendance buffer.
     *         Negative if below minimum, positive if at or above minimum.
     */
    fun attendanceBuffer(subject: Subject): Int {
        return if (attendanceRatio(subject) < minimumRequiredAttendanceRatio.value) {
            val presents = (minimumRequiredAttendanceRatio.value * (subject.presentDays + subject.absentDays)
            - subject.presentDays) / (1 - minimumRequiredAttendanceRatio.value)

            -ceil(presents).toInt()
        } else {
            val absents = (subject.presentDays / minimumRequiredAttendanceRatio.value) -
            subject.absentDays - subject.presentDays

            floor(absents).toInt()
        }
    }
}