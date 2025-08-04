package com.example.attendance

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.attendance.alarms.alarmSchedurer.AlarmScheduler
import com.example.attendance.database.AppDatabase
import com.example.attendance.database.DatabaseRepository
import com.example.attendance.homeScreen.HomeScreen
import com.example.attendance.preferences.PreferencesRepository
import com.example.attendance.ui.theme.AppTheme
import com.example.attendance.viewModel.AttendanceViewModel

class MainActivity : ComponentActivity() {
    companion object {
        lateinit var db : AppDatabase
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        db = AppDatabase.getInstance(this)

        val databaseRepository = DatabaseRepository(
            db.attendanceDao(),
            db.subjectDao(),
            db.timetableDao()
        )

        val alarmScheduler = AlarmScheduler(this, databaseRepository)

        alarmScheduler.scheduleAllAlarms()

        setContent {
            val viewModel by viewModels<AttendanceViewModel>{
                viewModelFactory {
                    initializer {
                        AttendanceViewModel(
                            alarmScheduler,
                            databaseRepository,
                            PreferencesRepository(this@MainActivity),
                            this@MainActivity
                        )
                    }
                }
            }

            val theme by viewModel.theme.collectAsState()

            AppTheme(
                theme = theme
            ) {
                HomeScreen(viewModel)
            }
        }
    }
}