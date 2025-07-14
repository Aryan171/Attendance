package com.example.attendance

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import androidx.room.Room
import com.example.attendance.preferences.PreferencesRepository
import com.example.attendance.database.AppDatabase
import com.example.attendance.database.DatabaseRepository
import com.example.attendance.homeScreen.HomeScreen
import com.example.attendance.homeScreen.attendanceScreen.AttendanceScreen
import com.example.attendance.subjectDetailScreen.SubjectDetailScreen
import com.example.attendance.ui.theme.AppTheme
import com.example.attendance.viewModel.AttendanceViewModel

class MainActivity : ComponentActivity() {
    companion object {
        lateinit var db : AppDatabase
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        db = Room.databaseBuilder(
                applicationContext,
                AppDatabase::class.java,
                "subject_database"
            ).fallbackToDestructiveMigration(true).build()

        setContent {
            val navController = rememberNavController()

            val viewModel by viewModels<AttendanceViewModel>{
                viewModelFactory {
                    initializer {
                        AttendanceViewModel(
                            DatabaseRepository(
                                db.attendanceDao(),
                                db.subjectDao()
                            ),
                            PreferencesRepository(this@MainActivity))
                    }
                }
            }

            val theme = viewModel.theme.collectAsState()

            AppTheme(
                theme = theme.value
            ) {
                NavHost(
                    navController = navController,
                    startDestination = HomeScreen,
                ) {
                    composable<HomeScreen> {
                        HomeScreen(
                            viewModel = viewModel,
                            subjectCardOnClick = { subject->
                                navController.navigate(
                                    SubjectDetailScreen(
                                        subjectIndex = viewModel.subjectList.indexOfFirst {
                                            it.id == subject.id
                                        }
                                    )
                                )
                            }
                        )
                    }

                    composable<SubjectDetailScreen> (
                        enterTransition = {
                            slideInHorizontally(
                                initialOffsetX = {
                                    it
                                }
                            )
                        },
                        exitTransition = {
                            slideOutHorizontally(
                                targetOffsetX = {
                                    it
                                }
                            )
                        }
                    ) {
                        val subjectDetailScreen: SubjectDetailScreen = it.toRoute()

                        SubjectDetailScreen(
                            subject = viewModel.subjectList[subjectDetailScreen.subjectIndex],
                            viewModel = viewModel,
                            onBackPress = {
                                navController.navigate(AttendanceScreen) {
                                    popUpTo(AttendanceScreen) {
                                        inclusive = true
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}