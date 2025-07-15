package com.example.attendance.homeScreen.attendanceScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.attendance.R
import com.example.attendance.ui.theme.AppTheme
import com.example.attendance.viewModel.AttendanceViewModel
import java.time.LocalDate
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenTopBar(
    viewModel: AttendanceViewModel
) {
    var showDropdownMenu by remember {mutableStateOf(false)}
    var showSortByDropdownMenu by remember {mutableStateOf(false)}
    var showChangeMinimumAttendanceDialog by remember { mutableStateOf(false) }
    var showThemeSelectorDropDown by remember { mutableStateOf(false) }

    val minimumRequiredAttendanceRatio by viewModel.minimumRequiredAttendanceRatio.collectAsState()

    val currentDate = LocalDate.now()

    val theme by viewModel.theme.collectAsState()

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
            viewModel.attendanceRatio(b).compareTo(viewModel.attendanceRatio(a))
        }},
        {viewModel.sortSubjectListBy{a, b ->
            viewModel.attendanceRatio(a).compareTo(viewModel.attendanceRatio(b))
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
            IconButton(
                onClick = {
                    showChangeMinimumAttendanceDialog = true
                }
            ) {
                Text(
                    text = (minimumRequiredAttendanceRatio * 100f).roundToInt().toString() + "%",
                    fontSize = MaterialTheme.typography.bodyMedium.fontSize
                )
            }

            IconButton(
                onClick = { showThemeSelectorDropDown = true }
            ) {
                Icon(
                    painter = painterResource(
                        when (theme) {
                            AppTheme.LIGHT -> {
                                R.drawable.clear_day_24dp_e3e3e3_fill0_wght400_grad0_opsz24
                            }
                            AppTheme.DARK -> {
                                R.drawable.dark_mode_24dp_e3e3e3_fill0_wght400_grad0_opsz24
                            }
                            AppTheme.SYSTEM_DEFAULT -> {
                                R.drawable.brightness_auto_24dp_e3e3e3_fill0_wght400_grad0_opsz24
                            }
                            else -> {
                                R.drawable.water_drop_24dp_e3e3e3_fill0_wght400_grad0_opsz24
                            }
                        }
                    ),
                    contentDescription =
                        when (theme) {
                            AppTheme.LIGHT -> {
                                "light theme"
                            }
                            AppTheme.DARK -> {
                                "dark theme"
                            }
                            AppTheme.SYSTEM_DEFAULT -> {
                                "system theme"
                            }
                            else -> {
                                "dynamic theme"
                            }
                        }
                )
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

            TopBarDropdownMenu(
                expanded = showDropdownMenu,
                dropdownMenuTextList = dropdownMenuTextList,
                dropdownMenuOnClickList = dropdownMenuOnClickList,
                showSortByDropdownMenu = { showSortByDropdownMenu = true },
                hideDropdownMenu = { showDropdownMenu = false }
            )

            SortByDropdownMenu(
                expanded = showSortByDropdownMenu,
                hideDropdownMenu = { showSortByDropdownMenu = false },
                dropdownMenuTextList = sortByDropdownMenuTextList,
                dropdownMenuOnClickList = sortByDropdownMenuOnClickList
            )

            ThemeSelectorDropDown(
                expanded = showThemeSelectorDropDown,
                viewModel = viewModel,
                hideDropdownMenu = { showThemeSelectorDropDown = false }
            )

            ChangeMinimumAttendanceDialog(
                showDialog = showChangeMinimumAttendanceDialog,
                viewModel = viewModel,
                hideDialog = { showChangeMinimumAttendanceDialog = false }
            )
        }
    )
}

@Composable
fun ThemeSelectorDropDown(
    expanded: Boolean,
    viewModel: AttendanceViewModel,
    hideDropdownMenu: () -> Unit
) {
    val theme by viewModel.theme.collectAsState()
    DropdownMenu (
        expanded = expanded,
        onDismissRequest = hideDropdownMenu,
        shape = MaterialTheme.shapes.large
    ) {
        if (theme != AppTheme.LIGHT) {
            DropdownMenuItem(
                text = { Text("Light") },
                leadingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.clear_day_24dp_e3e3e3_fill0_wght400_grad0_opsz24),
                        contentDescription = "light theme"
                    )
                },
                onClick = {
                    viewModel.setTheme(AppTheme.LIGHT)
                    hideDropdownMenu()
                }
            )
        }

        if (theme != AppTheme.DARK) {
            DropdownMenuItem(
                text = { Text("Dark") },
                leadingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.dark_mode_24dp_e3e3e3_fill0_wght400_grad0_opsz24),
                        contentDescription = "dark theme"
                    )
                },
                onClick = {
                    viewModel.setTheme(AppTheme.DARK)
                    hideDropdownMenu()
                }
            )
        }

        if (theme != AppTheme.SYSTEM_DEFAULT) {
            DropdownMenuItem(
                text = { Text("System") },
                leadingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.brightness_auto_24dp_e3e3e3_fill0_wght400_grad0_opsz24),
                        contentDescription = "system default"
                    )
                },
                onClick = {
                    viewModel.setTheme(AppTheme.SYSTEM_DEFAULT)
                    hideDropdownMenu()
                }
            )
        }

        if (theme != AppTheme.DYNAMIC) {
            DropdownMenuItem(
                text = { Text("Dynamic") },
                leadingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.water_drop_24dp_e3e3e3_fill0_wght400_grad0_opsz24),
                        contentDescription = "dynamic"
                    )
                },
                onClick = {
                    viewModel.setTheme(AppTheme.DYNAMIC)
                    hideDropdownMenu()
                }
            )
        }
    }
}

@Composable
fun ChangeMinimumAttendanceDialog(
    showDialog: Boolean,
    viewModel: AttendanceViewModel,
    hideDialog: () -> Unit
) {
    if (!showDialog) {
        return
    }

    var minimumAttendance by remember {
        mutableStateOf("")
    }

    fun setMinimumAttendance() {
        val minimumRequiredAttendanceRatio = minimumAttendance.toFloat() / 100f

        if (minimumRequiredAttendanceRatio !in 0f..1f) {
            minimumAttendance = ""
        } else {
            viewModel.setMinimumRequiredAttendanceRatio(minimumRequiredAttendanceRatio)
            hideDialog()
        }
    }

    Dialog(
        onDismissRequest = hideDialog,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        )
    ) {
        Surface (
            modifier = Modifier.padding(10.dp),
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = AlertDialogDefaults.TonalElevation
        ) {
            Column(
                modifier = Modifier.padding(vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Minimum required attendance",
                    style = MaterialTheme.typography.titleLarge
                )

                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp),
                    shape = CircleShape,
                    value = minimumAttendance,
                    onValueChange = {
                        minimumAttendance = it
                    },
                    label = {
                        Text(text = "Change minimum attendance")
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Done,
                        keyboardType = KeyboardType.Number
                    ),
                    keyboardActions = KeyboardActions { setMinimumAttendance() }
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    FilledTonalButton(
                        onClick = hideDialog
                    ) {
                        Text(text = "Cancel")
                    }

                    FilledTonalButton(
                        onClick = { setMinimumAttendance() }
                    ) {
                        Text(text = "Change")
                    }
                }
            }
        }
    }
}

@Composable
fun SortByDropdownMenu(
    expanded: Boolean,
    hideDropdownMenu: () -> Unit,
    dropdownMenuTextList: List<String>,
    dropdownMenuOnClickList: List<() -> Unit>
) {
    DropdownMenu (
        expanded = expanded,
        onDismissRequest = hideDropdownMenu,
        shape = MaterialTheme.shapes.large
    ) {
        for (i in 0 until dropdownMenuTextList.size) {
            DropdownMenuItem(
                text = { Text(dropdownMenuTextList[i]) },
                onClick = {
                    dropdownMenuOnClickList[i]()
                    hideDropdownMenu()
                }
            )
        }
    }
}

@Composable
fun TopBarDropdownMenu(
    expanded: Boolean,
    dropdownMenuTextList: List<String>,
    dropdownMenuOnClickList: List<() -> Unit>,
    showSortByDropdownMenu: () -> Unit,
    hideDropdownMenu: () -> Unit
) {
    DropdownMenu (
        expanded = expanded,
        onDismissRequest = hideDropdownMenu,
        shape = MaterialTheme.shapes.large
    ) {
        for (i in 0 until dropdownMenuTextList.size) {
            DropdownMenuItem(
                text = { Text(dropdownMenuTextList[i]) },
                onClick = {
                    dropdownMenuOnClickList[i]()
                    hideDropdownMenu()
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
            onClick = showSortByDropdownMenu
        )
    }
}