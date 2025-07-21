package com.example.attendance.homeScreen
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.attendance.homeScreen.attendanceScreen.AttendanceScreen
import com.example.attendance.homeScreen.timeTableScreen.TimeTableScreen
import com.example.attendance.subjectDetailScreen.SubjectDetailScreen
import com.example.attendance.viewModel.AttendanceViewModel
import kotlinx.serialization.Serializable

@Serializable
object HomeScreen : Screen

@Composable
fun HomeScreen(
    viewModel: AttendanceViewModel
) {
    val navController = rememberNavController()

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
            HomeScreenScaffold(navController) { paddingValues ->
                AttendanceScreen(
                    paddingValues = PaddingValues(bottom = paddingValues.calculateBottomPadding()),
                    viewModel = viewModel
                ) {
                    subject ->
                    navController.navigate(SubjectDetailScreen(subject.id))
                }
            }
        }

        composable<TimeTableScreen> {
            HomeScreenScaffold(navController) { paddingValues ->
                TimeTableScreen(
                    paddingValues = PaddingValues(bottom = paddingValues.calculateBottomPadding()),
                    viewModel = viewModel
                )
            }
        }

        composable<SubjectDetailScreen> { navBackStackEntry ->
            val subjectDetailScreen: SubjectDetailScreen = navBackStackEntry.toRoute()
            val subject = viewModel.getSubject(subjectDetailScreen.subjectId)

            if (subject != null) {
                SubjectDetailScreen(
                    subject = subject,
                    viewModel = viewModel
                ) {
                    navController.popBackStack()
                }
            }
        }
    }
}

@Composable
fun HomeScreenScaffold(
    navController: NavHostController,
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold (
        bottomBar = { HomeScreenBottomBar(navController) }
    ) { paddingValues ->
        content(paddingValues)
    }
}