package com.example.attendance.homeScreen
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.attendance.R
import com.example.attendance.homeScreen.attendanceScreen.AttendanceScreen
import com.example.attendance.homeScreen.timeTableScreen.TimeTableScreen

@Composable
fun HomeScreenBottomBar(
    navController: NavController
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()

    var selected = navBackStackEntry?.destination?.let {
        when (it.route) {
            AttendanceScreen::class.qualifiedName -> AttendanceScreen
            TimeTableScreen::class.qualifiedName -> TimeTableScreen
            else -> null
        }
    }

    NavigationBar {
        NavigationBarItem(
            selected = selected == AttendanceScreen,
            onClick = {
                if (selected != AttendanceScreen) {
                    selected = AttendanceScreen
                    navController.popBackStack()
                    navController.navigate(AttendanceScreen)
                }
            },
            icon = {
                Icon(
                    painter = painterResource(
                        id = if (selected == AttendanceScreen) {
                                R.drawable.checkboxfilled
                            } else {
                            R.drawable.check_box
                            }
                    ),
                    contentDescription = "attendance screen"
                )
            },
            alwaysShowLabel = false
        )

        NavigationBarItem(
            selected = selected == TimeTableScreen,
            onClick = {
                if (selected != TimeTableScreen) {
                    selected = TimeTableScreen
                    navController.popBackStack()
                    navController.navigate(TimeTableScreen)
                }
            },
            icon = {
                Icon(
                    painter = painterResource(
                        id = if (selected == TimeTableScreen) {
                            R.drawable.filled_time_table
                        } else {
                            R.drawable.time_table
                        }
                    ),
                    contentDescription = "time table screen"
                )
            },
            alwaysShowLabel = false
        )
    }
}