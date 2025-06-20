package com.example.attendance.homeScreen

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.attendance.ui.theme.roundedCornerShape
import com.example.attendance.viewModel.AttendanceViewModel
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenTopBar(
    viewModel: AttendanceViewModel
) {
    var showDropdownMenu by remember {mutableStateOf(false)}

    val currentDate = LocalDate.now()
    val textList = remember { listOf(
        "Set all Present Today",
        "Set all Absent Today",
        "Reset all attendance for today",
        "Sort by Name",
        "Sort by most attended percent",
        "Sort by least attended percent",
        "Sort by most attended days",
        "Sort by least attended days",
        "Sort by most absent days",
        "Sort by least absent days"
    ) }

    val onClickList = remember { listOf(
        { viewModel.setAllPresent(currentDate) },
        { viewModel.setAllAbsent(currentDate) },
        {viewModel.clearAllAttendance(currentDate)},
        {viewModel.sortSubjectListBy{a, b ->
            a.name.compareTo(b.name)
        }},
        {viewModel.sortSubjectListBy{a, b ->
            viewModel.getAttendanceRatio(b).compareTo(viewModel.getAttendanceRatio(a))
        }},
        {viewModel.sortSubjectListBy{a, b ->
            viewModel.getAttendanceRatio(a).compareTo(viewModel.getAttendanceRatio(b))
        }},
        {viewModel.sortSubjectListBy{a, b ->
            b.presentDays.compareTo(a.presentDays)
        }},
        {viewModel.sortSubjectListBy { a, b ->
            a.presentDays.compareTo(b.presentDays)
        }},
        {viewModel.sortSubjectListBy{a, b ->
            b.absentDays.compareTo(a.absentDays)
        }},
        {viewModel.sortSubjectListBy { a, b ->
            a.absentDays.compareTo(b.absentDays)
        }}
    )}

    TopAppBar(
        title = {
            Text("Attendance", maxLines = 1, overflow = TextOverflow.Ellipsis)
        },
        actions = {
            DropdownMenu (
                expanded = showDropdownMenu,
                onDismissRequest = { showDropdownMenu = false },
                shape = roundedCornerShape
            ) {
                for (i in 0 until textList.size) {
                    DropdownMenuItem(
                        text = {
                            Text(textList[i])
                        },
                        onClick = onClickList[i]
                    )
                }
            }
            IconButton(
                onClick = {
                    showDropdownMenu = true
                }
            ) {
                Icon(
                    imageVector = Icons.Filled.MoreVert,
                    contentDescription = "open menu"
                )
            }
        }
    )
}