package com.example.attendance.homeScreen.attendanceScreen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.attendance.database.subject.SubjectUiModel
import com.example.attendance.viewModel.AttendanceViewModel
import kotlinx.serialization.Serializable

@Serializable
object AttendanceScreen

@Composable
fun AttendanceScreen(
    viewModel: AttendanceViewModel,
    subjectCardOnClick: (subject: SubjectUiModel) -> Unit
) {
    val subjectList = viewModel.subjectList
    Scaffold(
        topBar = { HomeScreenTopBar(viewModel) },
        floatingActionButton = {
            AddSubjectButton(viewModel)
        }
    ) {
        if (subjectList.isEmpty()) {
            Box (
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No Subjects Added",
                    fontSize = MaterialTheme.typography.titleLarge.fontSize
                )
            }
        }
        else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
            ) {
                items(
                    items = subjectList,
                    key = { it.id }
                ) {
                    SubjectCard(
                        subject = it,
                        viewModel = viewModel,
                        onClick = subjectCardOnClick
                    )
                }

                // empty space at the end so that the end element can be seen properly
                item {
                    Box(
                        modifier = Modifier.height(100.dp)
                    )
                }
            }
        }
    }
}