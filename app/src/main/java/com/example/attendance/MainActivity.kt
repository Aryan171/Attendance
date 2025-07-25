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
import androidx.room.Room
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

        val alarmScheduler = AlarmScheduler(this)

        alarmScheduler.schedulePeriodicAlarm()

        db = Room.databaseBuilder(
                applicationContext,
                AppDatabase::class.java,
                "appDataBase"
            ).fallbackToDestructiveMigration(true).build()

        setContent {
            val viewModel by viewModels<AttendanceViewModel>{
                viewModelFactory {
                    initializer {
                        AttendanceViewModel(
                            DatabaseRepository(
                                db.attendanceDao(),
                                db.subjectDao(),
                                db.timetableDao()
                            ),
                            PreferencesRepository(this@MainActivity)
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