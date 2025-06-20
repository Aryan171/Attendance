package com.example.attendance.subjectDetailScreen

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.twotone.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.attendance.database.Subject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubjectDetailScreenTopAppBar(
    subject: Subject,
    onBackPress: () -> Unit
) {
    TopAppBar(
        title = {
            Text(subject.name, color = Color.Black)
        },
        navigationIcon = {
            IconButton(
                onClick = onBackPress
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.TwoTone.ArrowBack,
                    contentDescription = "back to home screen",
                    tint = Color.Black
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors().copy(
            containerColor = Color(166, 166, 166, 255),
        )
    )
}