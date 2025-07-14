package com.example.attendance.homeScreen
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import com.example.attendance.R
import com.example.attendance.homeScreen.attendanceScreen.AttendanceScreen
import com.example.attendance.homeScreen.timeTableScreen.TimeTableScreen

@Composable
fun HomeScreenBottomBar(
    navController: NavController
) {
    var selected by rememberSaveable {
        mutableStateOf(Screens.ATTENDANCE)
    }

    NavigationBar {
        NavigationBarItem(
            selected = selected == Screens.ATTENDANCE,
            onClick = {
                if (selected == Screens.ATTENDANCE) {
                    return@NavigationBarItem
                }
                selected = Screens.ATTENDANCE
                navController.popBackStack()
                navController.navigate(AttendanceScreen)
            },
            icon = {
                Icon(
                    painter = painterResource(
                        id = if (selected == Screens.ATTENDANCE) {
                                R.drawable.checkboxfilled
                            } else {
                            R.drawable.check_box
                            }
                    ),
                    contentDescription = "goto attendance screen"
                )
            },
            alwaysShowLabel = false
        )

        NavigationBarItem(
            selected = selected == Screens.TIME_TABLE,
            onClick = {
                if (selected == Screens.TIME_TABLE) {
                    return@NavigationBarItem
                }
                selected = Screens.TIME_TABLE
                navController.popBackStack()
                navController.navigate(TimeTableScreen)
            },
            icon = {
                Icon(
                    painter = painterResource(
                        id = if (selected == Screens.ATTENDANCE) {
                            R.drawable.checkboxfilled
                        } else {
                            R.drawable.check_box
                        }
                    ),
                    contentDescription = "goto attendance screen"
                )
            },
            alwaysShowLabel = false
        )
    }
}