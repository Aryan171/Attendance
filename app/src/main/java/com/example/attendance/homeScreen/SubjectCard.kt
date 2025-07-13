package com.example.attendance.homeScreen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Close
import androidx.compose.material.icons.twotone.Done
import androidx.compose.material.icons.twotone.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.attendance.attendanceUiElements.CircularProgressIndicator
import com.example.attendance.database.subject.SubjectUiModel
import com.example.attendance.ui.theme.absent
import com.example.attendance.ui.theme.present
import com.example.attendance.ui.theme.smallRoundedCornerShape
import com.example.attendance.viewModel.AttendanceViewModel
import java.time.LocalDate

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SubjectCard(
    subject: SubjectUiModel,
    viewModel: AttendanceViewModel,
    onClick: (SubjectUiModel) -> Unit
) {
    var showDetailedCardView by remember {mutableStateOf(false)}
    val cardPaddingValues = PaddingValues(10.dp, 10.dp, 10.dp)
    val maxIconButtonSize = 40.dp
    val currentDate = LocalDate.now()
    val presentToday: Boolean? =
        if (currentDate in subject.attendance) {
            subject.attendance[currentDate]
        } else {
            null
        }
    val backgroundColor by animateColorAsState(
        targetValue = if (presentToday != null) {
            if (presentToday) {
                present
            } else {
                absent
            }
        } else {
            MaterialTheme.colorScheme.surfaceVariant
        },
        animationSpec = tween(durationMillis = 500)
    )

    Column (
        modifier = Modifier
            .animateContentSize()
            .padding(cardPaddingValues)
            .background(color = MaterialTheme.colorScheme.surfaceVariant, smallRoundedCornerShape)
            .clip(smallRoundedCornerShape)
            // closing the details card when clicking on the card
            .combinedClickable(
                onClick = { showDetailedCardView = false },
                onLongClick = { showDetailedCardView = false },
                onDoubleClick = { showDetailedCardView = false }
            ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(backgroundColor, smallRoundedCornerShape)
                .clip(smallRoundedCornerShape)
                .combinedClickable (
                    onClick = { onClick(subject) },
                    onLongClick = { showDetailedCardView = !showDetailedCardView },
                    onDoubleClick = { showDetailedCardView = !showDetailedCardView }
                )
                .padding(start = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(subject.name)

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                // absent button
                SubjectCardIconButton(
                    maxSize = maxIconButtonSize,
                    showButton = presentToday == null || presentToday,
                    onClick = {
                        viewModel.markAbsent(subject, currentDate)
                    }
                ) {
                    Icon(
                        imageVector = Icons.TwoTone.Close,
                        contentDescription = "Absent"
                    )
                }

                // present button
                SubjectCardIconButton(
                    maxSize = maxIconButtonSize,
                    showButton = presentToday == null || !presentToday,
                    onClick = {
                        viewModel.markPresent(subject, currentDate)
                    }
                ) {
                    Icon(
                        imageVector = Icons.TwoTone.Done,
                        contentDescription = "Present"
                    )
                }

                // clear button
                SubjectCardIconButton(
                    maxSize = maxIconButtonSize,
                    showButton = presentToday != null,
                    onClick = {
                        viewModel.clearAttendance(subject, currentDate)
                    }
                ) {
                    Icon(
                        imageVector = Icons.TwoTone.Refresh,
                        contentDescription = "Clear"
                    )
                }

                CircularProgressIndicator(
                    modifier = Modifier
                        .size(60.dp)
                        .padding(5.dp)
                    ,
                    bottomText = null,
                    percentageFontSize = 13.sp,
                    strokeWidth = 3.dp,
                    progress = viewModel.attendanceRatio(subject),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    trackColor = Color.Transparent
                )
            }
        }

        AnimatedVisibility(
            visible = showDetailedCardView,
            enter = expandVertically(
                expandFrom = Alignment.Top
            ),
            exit = shrinkVertically(
                shrinkTowards = Alignment.Top
            )
        ) {
            SubjectDetails(
                subject = subject,
                viewModel = viewModel
            )
        }
    }
}

@Composable
fun SubjectDetails(
    subject: SubjectUiModel,
    viewModel: AttendanceViewModel
) {
    Column (
        modifier = Modifier
            .padding(vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        val attendanceBuffer by viewModel.attendanceBuffer(subject).collectAsState()

        val animatedAttendanceBuffer by animateIntAsState(
            targetValue = attendanceBuffer,
            animationSpec = tween(durationMillis = 500)
        )

        Text(
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            text = if (attendanceBuffer < 0) {
                "Must attend ${-animatedAttendanceBuffer} " +
                        if (attendanceBuffer == -1) {"class"} else {"classes"}
            } else if (attendanceBuffer > 0) {
                "Can miss : $animatedAttendanceBuffer " +
                        if (attendanceBuffer == 1) {"class"} else {"classes"}
            } else {
                "Right on edge, don't miss any class"
            },
            color = if (animatedAttendanceBuffer <= 0) { absent } else { present }
        )

        Row (
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Text("Total ${subject.presentDays + subject.absentDays}")

            Text(
                text = "Present ${subject.presentDays}",
                color = present
            )

            Text(
                text = "Absent ${subject.absentDays}",
                color = absent
            )
        }

        ModificationBox(
            subject = subject,
            viewModel = viewModel
        )
    }
}

@Composable
fun DeleteDialog(
    subject: SubjectUiModel,
    viewModel: AttendanceViewModel,
    hideDialog: () -> Unit
) {
    AlertDialog(
        onDismissRequest = hideDialog,
        confirmButton =  {
            FilledTonalButton(
                onClick = {
                    viewModel.deleteSubject(subject)
                    hideDialog()
                }
            ) {
                Text(text = "Delete")
            }
        },
        dismissButton = {
            FilledTonalButton(
                onClick = {
                    hideDialog()
                }
            ) {
                Text(text = "Cancel")
            }
        },
        title = {
            Text(
                text = "Delete ${subject.name}?",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        },
        text = {
            Text(
                text = "Are you sure you want to delete ${subject.name}? " +
                    "This action cannot be undone",
                modifier = Modifier.fillMaxWidth()
            )
        }
    )
}

@Composable
fun ResetDialog(
    subject: SubjectUiModel,
    viewModel: AttendanceViewModel,
    hideDialog: () -> Unit
) {
    AlertDialog(
        onDismissRequest = hideDialog,
        confirmButton =  {
            FilledTonalButton(
                onClick = {
                    viewModel.clearAttendance(subject)
                    hideDialog()
                }
            ) {
                Text(text = "Reset")
            }
        },
        dismissButton = {
            FilledTonalButton(
                onClick = {
                    hideDialog()
                }
            ) {
                Text(text = "Cancel")
            }
        },
        title = {
            Text(
                text = "Reset ${subject.name}?",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        },
        text = {
            Text(
                text = "Are you sure you want to reset ${subject.name}? " +
                        "This action cannot be undone",
                modifier = Modifier.fillMaxWidth()
            )
        }
    )
}

@Composable
fun RenameDialog(
    subject: SubjectUiModel,
    viewModel: AttendanceViewModel,
    hideDialog: () -> Unit,
) {
    var subjectName by remember {
        mutableStateOf("")
    }

    val renameSubject = {
        val name = subjectName
        subjectName = ""
        if (name.isNotBlank()) {
            viewModel.renameSubject(subject, name.trim())
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
                    text = "Rename ${subject.name}",
                    style = MaterialTheme.typography.titleLarge
                )

                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp),
                    shape = CircleShape,
                    value = subjectName,
                    onValueChange = {
                        subjectName = it
                    },
                    label = {
                        Text(text = "Rename")
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Go
                    ),
                    keyboardActions = KeyboardActions { renameSubject() }
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
                        onClick = renameSubject
                    ) {
                        Text(text = "Rename")
                    }
                }
            }
        }
    }
}

@Composable
fun ModificationBox(
    subject: SubjectUiModel,
    viewModel: AttendanceViewModel
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showRenameDialog by remember { mutableStateOf(false) }
    var showResetDialog by remember { mutableStateOf(false) }

    Box {
        if (showDeleteDialog) {
            DeleteDialog(
                subject = subject,
                viewModel = viewModel
            ) {
                showDeleteDialog = false
            }
        }

        if (showRenameDialog) {
            RenameDialog(
                subject = subject,
                viewModel = viewModel
            ) {
                showRenameDialog = false
            }
        }

        if (showResetDialog) {
            ResetDialog(
                subject = subject,
                viewModel = viewModel
            ) {
                showResetDialog = false
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            // rename button
            TextButton (
                onClick = { showRenameDialog = true }
            ) {
                Text(text = "Rename")
            }

            // reset button
            TextButton (
                onClick = { showResetDialog = true }
            ) {
                Text(text = "Reset")
            }

            // delete button
            TextButton(
                onClick = { showDeleteDialog = true }
            ) {
                Text(text = "Delete")
            }
        }
    }
}

@Composable
fun SubjectCardIconButton(
    maxSize: Dp,
    showButton: Boolean,
    onClick: () -> Unit,
    icon: @Composable () -> Unit
) {

    val transition = updateTransition(
        targetState = showButton,
        label = "showButtonTransition"
    )

    val animatedSize by transition.animateDp {
        if (it) {
            maxSize
        } else {
            0.dp
        }
    }

    if (animatedSize > 0.dp) {
        Box(
            modifier = Modifier
                .size(animatedSize)
                .clip(CircleShape)
                .clickable(
                    onClick = onClick
                ),
            contentAlignment = Alignment.Center
        ) {
            icon()
        }
    }
}