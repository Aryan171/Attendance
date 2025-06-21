package com.example.attendance.homeScreen

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.twotone.Close
import androidx.compose.material.icons.twotone.Done
import androidx.compose.material.icons.twotone.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.attendance.attendanceUiElements.CircularProgressIndicator
import com.example.attendance.database.Subject
import com.example.attendance.ui.theme.absent
import com.example.attendance.ui.theme.buttonAbsent
import com.example.attendance.ui.theme.buttonPresent
import com.example.attendance.ui.theme.present
import com.example.attendance.ui.theme.smallRoundedCornerShape
import com.example.attendance.viewModel.AttendanceViewModel
import java.time.LocalDate

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SubjectCard(
    subject: Subject,
    viewModel: AttendanceViewModel,
    onClick: (Subject) -> Unit
) {
    var showDropdownMenu by remember {mutableStateOf(false)}
    val cardPaddingValues = PaddingValues(10.dp, 10.dp, 10.dp)
    val dropdownMenuOffset = DpOffset(10.dp, 10.dp)
    val maxIconButtonSize = 50.dp
    val maxIconButtonPadding = 5.dp
    val currentDate = LocalDate.now()
    val presentToday: Boolean? =
        if (currentDate in subject.attendance) {
            subject.attendance[currentDate]
        } else {
            null
        }
    val backgroundColor =
        if (presentToday != null) {
            if (presentToday) {
                present
            } else {
                absent
            }
        }
        else {
            Color.Unspecified
        }

    Box {
        DropdownMenu(
            offset = dropdownMenuOffset,
            shape = smallRoundedCornerShape,
            expanded = showDropdownMenu,
            onDismissRequest = { showDropdownMenu = false }
        ) {
            DropdownMenuItem(
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Refresh,
                        contentDescription = "delete subject",
                        tint = MaterialTheme.colorScheme.error
                    )
                },
                text = { Text("Reset") },
                onClick = {
                    viewModel.clearAttendance(subject)
                    showDropdownMenu = false
                }
            )

            DropdownMenuItem(
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = "delete subject",
                        tint = MaterialTheme.colorScheme.error
                    )
                },
                text = {
                    Text(text = "Delete")
                       },
                onClick = {
                    viewModel.deleteSubject(subject)
                    showDropdownMenu = false
                }
            )
        }

        Card(
            modifier = Modifier
                .padding(cardPaddingValues)
                .fillMaxWidth()
                .clip(smallRoundedCornerShape)
                .combinedClickable(
                    onClick = { onClick(subject) },
                    onLongClick = {
                        showDropdownMenu = true
                    }
                ),
            colors = CardDefaults.cardColors(
                containerColor = backgroundColor
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.padding(start = 10.dp),
                    text = subject.name
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        // absent button
                        SubjectCardIconButton(
                            maxSize = maxIconButtonSize,
                            maxPadding = maxIconButtonPadding,
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

                        // clear button
                        SubjectCardIconButton(
                            maxSize = maxIconButtonSize,
                            maxPadding = maxIconButtonPadding,
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

                        // present button
                        SubjectCardIconButton(
                            maxSize = maxIconButtonSize,
                            maxPadding = maxIconButtonPadding,
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
                    }

                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(40.dp),
                        bottomText = null,
                        percentageFontSize = 15.sp,
                        strokeWidth = 2.dp,
                        progress = viewModel.getAttendanceRatio(subject),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        trackColor = Color.Transparent
                    )
                }
            }
        }
    }
}

@Composable
fun SubjectCardIconButton(
    maxSize: Dp,
    maxPadding: Dp,
    showButton: Boolean,
    onClick: () -> Unit,
    icon: @Composable () -> Unit
) {

    val animatedSize by animateDpAsState(
        targetValue = if (showButton) {
            maxSize
        } else {
            0.dp
        }
    )

    val animatedPadding by animateDpAsState(
        targetValue = if (showButton) {
            maxPadding
        } else {
            0.dp
        }
    )

    if (animatedSize > 0.dp) {
        Box(
            modifier = Modifier
                .size(animatedSize)
                .padding(animatedPadding)
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