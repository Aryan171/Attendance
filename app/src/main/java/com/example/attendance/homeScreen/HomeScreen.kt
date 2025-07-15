package com.example.attendance.homeScreen
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.attendance.database.subject.SubjectUiModel
import com.example.attendance.homeScreen.attendanceScreen.AttendanceScreen
import com.example.attendance.homeScreen.locationsScreen.LocationsScreen
import com.example.attendance.homeScreen.timeTableScreen.TimeTableScreen
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
        bottomBar = { HomeScreenBottomBar(navController) }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = AttendanceScreen,
            enterTransition = {
                fadeIn(animationSpec = tween(600))
            },
            exitTransition = {
                fadeOut(animationSpec = tween(600))
            }
        ) {
            composable<AttendanceScreen> {
                AttendanceScreen(
                    paddingValues = PaddingValues(bottom = paddingValues.calculateBottomPadding()),
                    viewModel = viewModel,
                    subjectCardOnClick = subjectCardOnClick
                )
            }

            composable<TimeTableScreen> {
                TimeTableScreen(
                    paddingValues = PaddingValues(bottom = paddingValues.calculateBottomPadding()),
                    viewModel = viewModel
                )
            }

            composable<LocationsScreen> {
                LocationsScreen(
                    paddingValues = PaddingValues(bottom = paddingValues.calculateBottomPadding())
                )
            }
        }
    }
}