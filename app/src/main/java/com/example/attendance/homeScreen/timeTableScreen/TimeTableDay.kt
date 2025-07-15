package com.example.attendance.homeScreen.timeTableScreen

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import java.time.DayOfWeek

@Composable
fun TimeTableDay(
    day: DayOfWeek
) {
    Text(day.name)
}