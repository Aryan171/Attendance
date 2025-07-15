package com.example.attendance.homeScreen
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.attendance.R
import com.example.attendance.homeScreen.attendanceScreen.AttendanceScreen
import com.example.attendance.homeScreen.locationsScreen.LocationsScreen
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
                    contentDescription = "attendance screen"
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
                        id = if (selected == Screens.TIME_TABLE) {
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

        NavigationBarItem(
            selected = selected == Screens.LOCATIONS,
            onClick = {
                if (selected == Screens.LOCATIONS) {
                    return@NavigationBarItem
                }
                selected = Screens.LOCATIONS
                navController.popBackStack()
                navController.navigate(LocationsScreen)
            },
            icon = {
                Icon(
                    imageVector = if (selected == Screens.LOCATIONS) {
                            Icons.Filled.LocationOn
                        } else {
                            Icons.Outlined.LocationOn
                        },
                    contentDescription = "locations screen"
                )
            },
            alwaysShowLabel = false
        )
    }
}