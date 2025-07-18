package com.example.attendance.viewModel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.attendance.database.DatabaseRepository
import com.example.attendance.database.subject.Subject
import com.example.attendance.database.subject.SubjectUiModel
import com.example.attendance.database.timeTable.TimeTable
import com.example.attendance.preferences.PreferencesRepository
import com.example.attendance.ui.theme.AppTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.Month
import kotlin.math.ceil
import kotlin.math.floor

class AttendanceViewModel(
    private val databaseRepository: DatabaseRepository,
    private val preferencesRepository: PreferencesRepository
): ViewModel() {

    // subjectList is declared and initialized before init block because it is being used in init
    // block to load the subject list from the database and kotlin initializes variables, and runs
    // the init block in textual order
    val subjectList = mutableStateListOf<SubjectUiModel>()
    val timeTableList = mutableStateListOf<SnapshotStateList<TimeTable>>()

    var timeTableListUpdatedTrigger = MutableStateFlow(ULong.MIN_VALUE)
    private set

    val slotBounds = mutableStateMapOf<Long, LongRange>()

    init {
        loadSubjectList()
        loadTimeTableList()
    }

    val timeLineHourHeight = preferencesRepository.getTimeLineHourHeight()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), initialValue = 50.dp)

    val theme = preferencesRepository.getTheme()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppTheme.DYNAMIC)


    val minimumRequiredAttendanceRatio = preferencesRepository.getMinimumRequiredAttendanceRatio()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.75f)

    fun setTheme(theme: AppTheme) {
        viewModelScope.launch(Dispatchers.IO) {
            preferencesRepository.setTheme(theme)
        }
    }

    fun setMinimumRequiredAttendanceRatio(minAttendanceRatio: Float) {
        viewModelScope.launch(Dispatchers.IO) {
            preferencesRepository.setMinimumRequiredAttendanceRatio(minAttendanceRatio)
        }
    }

    fun setTimeLineHourHeight(height: Dp) {
        viewModelScope.launch(context = Dispatchers.IO) {
            preferencesRepository.setTimeLineHourHeight(height)
        }
    }

    fun clearAttendance(subject: SubjectUiModel) {
        viewModelScope.launch(Dispatchers.IO) {
            databaseRepository.clearAllAttendance(subject.id)
        }

        updateSubjectInMemory(subject.copy(
            presentDays = 0,
            absentDays = 0,
            attendance = mutableMapOf()
        ))
    }

    fun markPresent(subject: SubjectUiModel, date: LocalDate) {
        if (subject.attendance[date] == true) {
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            databaseRepository.markPresent(subject.id, date)
        }

        var updatedPresentDays = subject.presentDays
        var updatedAbsentDays = subject.absentDays

        if (subject.attendance[date] == false) {
            updatedPresentDays++
            updatedAbsentDays--
        }
        else {
            updatedPresentDays++
        }
        subject.attendance[date] = true
        updateSubjectInMemory(subject.copy(
            presentDays = updatedPresentDays,
            absentDays = updatedAbsentDays,
            attendance = subject.attendance
        ))
    }

    fun clearAttendance(subject: SubjectUiModel, date: LocalDate) {
        if (subject.attendance[date] == null) {
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            databaseRepository.clearAttendance(subject.id, date)
        }

        var updatedPresentDays = subject.presentDays
        var updatedAbsentDays = subject.absentDays

        if (subject.attendance[date] == true) {
            updatedPresentDays--
        }
        else {
            updatedAbsentDays--
        }
        subject.attendance.remove(date)
        updateSubjectInMemory(subject.copy(
            presentDays = updatedPresentDays,
            absentDays = updatedAbsentDays,
            attendance = subject.attendance
        ))
    }

    fun markAbsent(subject: SubjectUiModel, date: LocalDate) {
        if (subject.attendance[date] == false) {
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            databaseRepository.markAbsent(subject.id, date)
        }

        var updatedPresentDays = subject.presentDays
        var updatedAbsentDays = subject.absentDays

        if (subject.attendance[date] == true) {
            updatedAbsentDays++
            updatedPresentDays--
        }
        else {
            updatedAbsentDays++
        }
        subject.attendance[date] = false
        updateSubjectInMemory(subject.copy(
            presentDays = updatedPresentDays,
            absentDays = updatedAbsentDays,
            attendance = subject.attendance
        ))
    }

    fun addSubject(subject: SubjectUiModel) {
        viewModelScope.launch {
            val generatedId = withContext(Dispatchers.IO) {
                databaseRepository.insertSubject(subject.toSubject())
            }
            subjectList.add(subject.copy(id = generatedId) )
        }
    }

    private fun SubjectUiModel.toSubject(): Subject {
        return Subject(
            id = this.id,
            name = this.name
        )
    }

    /**
     * Updates the given subject in the local list. This function changes the SnapshotStateList which
     * triggers recomposition
     *
     * This function finds the index of the subject in the `subjectList` based on its ID.
     * If the subject is found (index is not -1), it replaces the existing subject
     * at that index with the updated subject.
     *
     * Note: This function only modifies the local `subjectList` and does not persist
     * changes to the database.
     *
     * @param subject The [SubjectUiModel] object containing the updated information for the subject.
     */
    fun updateSubjectInMemory(subject: SubjectUiModel) {
        val index = subjectList.indexOfFirst { it.id == subject.id }
        if (index != -1) {
            subjectList[index] = subject
        }
    }

    fun deleteSubject(subject: SubjectUiModel) {
        viewModelScope.launch(Dispatchers.IO) {
            databaseRepository.deleteSubject(subject.id)
        }
        subjectList.removeIf {
            it.id == subject.id
        }
    }

    fun loadSubjectList() {
        viewModelScope.launch(Dispatchers.IO) {
            subjectList.addAll(databaseRepository.getAllSubjects())
        }
    }

    fun loadTimeTableList() {
        viewModelScope.launch(Dispatchers.IO) {
            for (day in 0..6) {
                timeTableList.add(databaseRepository.getTimeTableForDay(day).toMutableStateList())
            }
        }
        timeTableListMutated()
    }

    fun attendanceRatio(subject: SubjectUiModel, month: Month, year: Int): Float {
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

    fun attendanceRatio(subject: SubjectUiModel): Float {
        val totalDays = subject.absentDays + subject.presentDays

        return if (totalDays == 0) {
            1f
        } else {
            subject.presentDays.toFloat() / totalDays
        }
    }

    fun setAllPresent(date: LocalDate) {
        for(subject in subjectList) {
            markPresent(subject, date)
        }
    }

    fun setAllAbsent(date: LocalDate) {
        for(subject in subjectList) {
            markAbsent(subject, date)
        }
    }

    fun clearAllAttendance(date: LocalDate) {
        for(subject in subjectList) {
            clearAttendance(subject, date)
        }
    }

    fun sortSubjectListBy(
        comparator: (SubjectUiModel, SubjectUiModel) -> Int
    ) {
        subjectList.sortWith(comparator)
    }

    fun presentDaysInMonth(subject: SubjectUiModel, month: Month, year: Int): Int {
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

    fun absentDaysInMonth(subject: SubjectUiModel, month: Month, year: Int): Int {
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

    fun renameSubject(subject: SubjectUiModel, name: String) {
        viewModelScope.launch(Dispatchers.IO) {
            databaseRepository.renameSubject(subject.id, name)
        }
        updateSubjectInMemory(subject.copy(name = name))
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
    fun attendanceBuffer(subject: SubjectUiModel): StateFlow<Int> {
        return minimumRequiredAttendanceRatio.map { ratio->
            if (attendanceRatio(subject) < ratio) {
                val presents =
                    (ratio * (subject.presentDays + subject.absentDays)
                            - subject.presentDays) / (1 - ratio)

                -ceil(presents).toInt()
            } else {
                val absents = (subject.presentDays / ratio) -
                        subject.absentDays - subject.presentDays

                floor(absents).toInt()
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )
    }

    fun addTimeTable(timeTable: TimeTable) {
        if (timeTable.day !in 0..6) {
            throw IndexOutOfBoundsException("Day must be between 0 and 6")
        }

        viewModelScope.launch(Dispatchers.IO) {
            val generatedId = databaseRepository.insertTimeTable(timeTable)
            timeTableList[timeTable.day].add(timeTable.copy(id = generatedId))
            timeTableListMutated()
        }
    }

    fun deleteTimeTable(timeTable: TimeTable) {
        viewModelScope.launch {
            databaseRepository.deleteTimeTable(timeTable)
        }
        timeTableList[timeTable.day].removeIf {
            it.id == timeTable.id
        }
        timeTableListMutated()
    }

    /**
     * Updates an existing entry in the timetable.
     *
     * This function first validates if the `day` property of the `timeTable` is within the valid range (0-6).
     * If the day is out of bounds, it throws an [IndexOutOfBoundsException].
     *
     * It then launches a coroutine in the `viewModelScope` to update the timetable entry
     * in the database using `databaseRepository.updateTimeTable(timeTable)`.
     *
     * After updating in the database, it finds the index of the old timetable entry in the local `timeTableList`
     * based on its `id` and `day`.
     * If the entry is found (index is not -1), it replaces the old entry at that index with the
     * updated `timeTable` object.
     *
     * @param timeTable The [TimeTable] object containing the updated information for the timetable entry.
     * @throws IndexOutOfBoundsException if `timeTable.day` is not between 0 and 6 (inclusive).
     */
    fun updateTimeTable(timeTable: TimeTable) {
        if (timeTable.day !in 0..6) {
            throw IndexOutOfBoundsException("day should be in bound 0..6")
        }

        viewModelScope.launch(Dispatchers.IO) {
            databaseRepository.updateTimeTable(timeTable)
        }

        val index = timeTableList[timeTable.day].indexOfFirst { it.id == timeTable.id }
        if (index != -1) {
            timeTableList[timeTable.day][index] = timeTable
            timeTableListMutated()
        }
    }

    fun getSubject(subjectId: Long): SubjectUiModel? = subjectList.find { it.id == subjectId }

    fun timeTableListMutated() {
        timeTableListUpdatedTrigger.value++
    }

    fun setSlotBound(slotId: Long, bound: LongRange) {
        slotBounds[slotId] = bound
    }
}