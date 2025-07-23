package com.example.attendance.homeScreen.timeTableScreen

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.painterResource
import com.example.attendance.R
import com.example.attendance.viewModel.AttendanceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeTableTopBar(
    viewModel: AttendanceViewModel
) {
    val timeTableLocked by viewModel.timeTableLocked.collectAsState()

    TopAppBar(
        title = {
            Text("Timetable")
        },
        actions = {
            IconButton(
                onClick = {
                    if (timeTableLocked) {
                        viewModel.unlockTimeTable()
                    } else {
                        viewModel.lockTimeTable()
                    }
                }
            ) {
                Icon(
                    painter = painterResource(
                        if (timeTableLocked) { R.drawable.lock }
                        else {R.drawable.open_lock}
                    ),
                    contentDescription = if (timeTableLocked) { "unlock" } else { "lock" }
                )
            }
        }
    )
}