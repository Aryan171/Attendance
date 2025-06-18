package com.example.attendance.subjectDetailScreen

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.attendance.viewModel.AttendanceViewModel
import com.example.attendance.database.Subject
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
data class SubjectDetailScreen(
    val subjectIndex: Int
)

@Composable
fun SubjectDetailScreen(
    subject: Subject,
    viewModel: AttendanceViewModel
) {
    Row(modifier = Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically) {
        Text(subject.subjectName)
        Text("Presents: ${subject.presentDays}, Absents: ${subject.absentDays}")

        Button(
            onClick = {
                if (subject.attendance[LocalDate.now()] != true) {
                    viewModel.markPresent(subject, LocalDate.now())
                } else if (subject.attendance[LocalDate.now()] != false) {
                    viewModel.markAbsent(subject, LocalDate.now())
                }
            }
        ) {
            Text("Toggle")
        }
    }
}