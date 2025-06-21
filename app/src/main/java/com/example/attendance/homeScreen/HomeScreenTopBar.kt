package com.example.attendance.homeScreen

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import com.example.attendance.R
import com.example.attendance.ui.theme.mediumRoundedCornerShape
import com.example.attendance.viewModel.AttendanceViewModel
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenTopBar(
    viewModel: AttendanceViewModel
) {
    var showDropdownMenu by remember {mutableStateOf(false)}
    var showSortByDropdownMenu by remember {mutableStateOf(false)}

    val currentDate = LocalDate.now()

    val dropdownMenuTextList = remember { listOf(
        "Set all Present Today",
        "Set all Absent Today",
        "Reset all attendance for today"
    ) }

    val dropdownMenuOnClickList = remember { listOf(
        { viewModel.setAllPresent(currentDate) },
        { viewModel.setAllAbsent(currentDate) },
        {viewModel.clearAllAttendance(currentDate)}
    )}

    val sortByDropdownMenuTextList = remember { listOf(
        "Sort by Name",
        "Sort by most attended percent",
        "Sort by least attended percent",
        "Sort by most attended days",
        "Sort by least attended days",
        "Sort by most absent days",
        "Sort by least absent days"
    ) }

    val sortByDropdownMenuOnClickList = remember { listOf(
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
                shape = mediumRoundedCornerShape
            ) {
                for (i in 0 until dropdownMenuTextList.size) {
                    DropdownMenuItem(
                        text = { Text(dropdownMenuTextList[i]) },
                        onClick = {
                            dropdownMenuOnClickList[i]()
                            showDropdownMenu = false
                        }
                    )
                }

                HorizontalDivider()

                DropdownMenuItem(
                    text = { Text("Sort by") },
                    trailingIcon = {
                        Icon (
                            painter = painterResource(R.drawable.swap_vert_24dp_e3e3e3_fill0_wght400_grad0_opsz24),
                            contentDescription = "sort by"
                        )
                    },
                    onClick = { showSortByDropdownMenu = true }
                )
            }

            DropdownMenu (
                expanded = showSortByDropdownMenu,
                onDismissRequest = { showSortByDropdownMenu = false },
                shape = mediumRoundedCornerShape
            ) {
                for (i in 0 until sortByDropdownMenuTextList.size) {
                    DropdownMenuItem(
                        text = { Text(sortByDropdownMenuTextList[i]) },
                        onClick = {
                            sortByDropdownMenuOnClickList[i]()
                            showSortByDropdownMenu = false
                            showDropdownMenu = false
                        }
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