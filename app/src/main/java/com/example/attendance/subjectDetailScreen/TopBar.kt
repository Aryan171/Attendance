package com.example.attendance.subjectDetailScreen

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import com.example.attendance.database.subject.Subject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubjectDetailScreenTopAppBar(
    subject: Subject,
    onBackPress: () -> Unit
) {
    TopAppBar(
        title = {
            Text(subject.name)
        },
        navigationIcon = {
            IconButton(
                onClick = onBackPress
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "back to home screen"
                )
            }
        }
    )
}