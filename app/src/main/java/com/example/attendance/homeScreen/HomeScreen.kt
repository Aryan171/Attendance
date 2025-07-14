package com.example.attendance.homeScreen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.attendance.database.subject.SubjectUiModel
import com.example.attendance.homeScreen.attendanceScreen.AttendanceScreen
import com.example.attendance.viewModel.AttendanceViewModel
import kotlinx.serialization.Serializable

@Serializable
object HomeScreen

@Composable
fun HomeScreen(
    viewModel: AttendanceViewModel,
    subjectCardOnClick: (SubjectUiModel) -> Unit
) {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {}
    ) { _->
        NavHost(
            navController = navController,
            startDestination = AttendanceScreen
        ) {
            composable<AttendanceScreen> {
                AttendanceScreen(
                    viewModel = viewModel,
                    subjectCardOnClick = subjectCardOnClick
                )
            }
        }
    }
}