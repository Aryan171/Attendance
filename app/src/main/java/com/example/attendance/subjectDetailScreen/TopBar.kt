package com.example.attendance.subjectDetailScreen

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.attendance.database.subject.SubjectUiModel
import java.time.Instant
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubjectDetailScreenTopAppBar(
    subject: SubjectUiModel,
    onDateSelected: (LocalDate) -> Unit,
    onBackPress: () -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }

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
        },
        actions = {
            IconButton(
                onClick = {
                    showDatePicker = true
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "search date"
                )
            }
            val datePickerState = rememberDatePickerState(initialDisplayMode = DisplayMode.Input)

            if (showDatePicker) {
                DatePickerDialog(
                    onDismissRequest = {
                        datePickerState.displayMode = DisplayMode.Input
                        datePickerState.selectedDateMillis = Instant.now().toEpochMilli()
                        showDatePicker = false
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            if (datePickerState.selectedDateMillis != null) {
                                onDateSelected(
                                    Instant.ofEpochMilli(
                                        datePickerState.selectedDateMillis!!
                                    ).atZone(java.time.ZoneId.systemDefault()).toLocalDate()
                                )
                            }
                            datePickerState.displayMode = DisplayMode.Input
                            datePickerState.selectedDateMillis = Instant.now().toEpochMilli()
                            showDatePicker = false
                        }) {
                            Text("OK")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = {
                            datePickerState.displayMode = DisplayMode.Input
                            datePickerState.selectedDateMillis = Instant.now().toEpochMilli()
                            showDatePicker = false
                        }) {
                            Text("Cancel")
                        }
                    }
                ) {
                    DatePicker(state = datePickerState)
                }
            }
        }
    )
}