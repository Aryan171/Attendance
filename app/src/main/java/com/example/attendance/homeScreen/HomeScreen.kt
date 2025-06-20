package com.example.attendance.homeScreen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import com.example.attendance.database.Subject
import com.example.attendance.viewModel.AttendanceViewModel
import kotlinx.serialization.Serializable

@Serializable
object HomeScreen

@Composable
fun HomeScreen(
    viewModel: AttendanceViewModel,
    subjectCardOnClick: (subject: Subject) -> Unit
) {
    val subjectList = viewModel.subjectList
    Scaffold(
        containerColor = Color.White,
        topBar = {HomeScreenTopBar(viewModel)}
    ) {
        if (subjectList.isEmpty()) {
            Box (
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No Subjects Added",
                    color = Color.LightGray,
                    fontSize = 25.sp
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
            }
        }
    }
}