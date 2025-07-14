package com.example.attendance.homeScreen.timeTableScreen

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeTableTopBar() {
    TopAppBar(
        title = {
            Text("Timetable")
        }
    )
}