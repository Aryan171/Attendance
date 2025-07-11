package com.example.attendance

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import androidx.room.Room
import com.example.attendance.database.SubjectDatabase
import com.example.attendance.homeScreen.HomeScreen
import com.example.attendance.subjectDetailScreen.SubjectDetailScreen
import com.example.attendance.ui.theme.AppTheme
import com.example.attendance.viewModel.AttendanceViewModel

class MainActivity : ComponentActivity() {
    companion object {
        lateinit var db : SubjectDatabase
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        db = Room.databaseBuilder(
            applicationContext,
            SubjectDatabase::class.java,
            "subject_database"
        ).build()

        setContent {
            val navController = rememberNavController()
            val viewModelFactory = AttendanceViewModel.Factory
            val viewModel by viewModels<AttendanceViewModel>{viewModelFactory}

            AppTheme {
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

                    composable<SubjectDetailScreen> {
                        val subjectDetailScreen: SubjectDetailScreen = it.toRoute()

                        SubjectDetailScreen(
                            subject = viewModel.subjectList[subjectDetailScreen.subjectIndex],
                            viewModel = viewModel,
                            onBackPress = {
                                navController.navigate(HomeScreen) {
                                    popUpTo(HomeScreen) {
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