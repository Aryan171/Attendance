package com.example.attendance.subjectDetailScreen

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.attendance.attendanceUiElements.CircularProgressIndicator
import com.example.attendance.database.Subject
import com.example.attendance.ui.theme.absent
import com.example.attendance.ui.theme.present
import com.example.attendance.viewModel.AttendanceViewModel
import java.time.Month

@Composable
fun InfoBox(
    subject: Subject,
    month: Month,
    year: Int,
    viewModel: AttendanceViewModel
) {
    Row {
        CircularProgressIndicator(
            modifier = Modifier
                .weight(1f)
                .padding(5.dp),
            bottomText = "Monthly Attendance",
            intPair = Pair(
                viewModel.presentDaysInMonth(subject, month, year),
                viewModel.absentDaysInMonth(subject, month, year)
            ),
            percentageFontSize = 50.sp,
            strokeWidth = 10.dp,
            progress = viewModel.attendanceRatio(subject, month, year),
            color = present,
            trackColor = absent
        )

        CircularProgressIndicator(
            modifier = Modifier
                .weight(1f)
                .padding(5.dp),
            bottomText = "Total Attendance",
            intPair = Pair(subject.presentDays, subject.absentDays),
            percentageFontSize = 50.sp,
            strokeWidth = 10.dp,
            progress = viewModel.attendanceRatio(subject),
            color = present,
            trackColor = absent
        )
    }
}