package com.example.attendance.homeScreen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDp
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.twotone.Close
import androidx.compose.material.icons.twotone.Done
import androidx.compose.material.icons.twotone.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeCompilerApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.attendance.attendanceUiElements.CircularProgressIndicator
import com.example.attendance.database.Subject
import com.example.attendance.ui.theme.absent
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

    Card(
        modifier = Modifier
            .animateContentSize()
            .padding(cardPaddingValues)
            .fillMaxWidth()
            .clip(smallRoundedCornerShape)
            // closing the details card when clicking on the card
            .combinedClickable(
                onClick = {
                    if (showDetailedCardView) {
                        showDetailedCardView = false
                    } else {
                        onClick(subject)
                    }
                          },
                onLongClick = {
                    showDetailedCardView = !showDetailedCardView
                },
                onDoubleClick = {
                    showDetailedCardView = !showDetailedCardView
                }
            ),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp),
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

        AnimatedVisibility (
            visible = showDetailedCardView,
            enter = expandVertically(),
            exit = shrinkVertically()
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
    subject: Subject,
    viewModel: AttendanceViewModel
) {
    Column (
        modifier = Modifier
            .animateContentSize()
            .background(MaterialTheme.colorScheme.surfaceVariant, smallRoundedCornerShape)
            .clip(smallRoundedCornerShape)
            .padding(vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        val attendanceBuffer = viewModel.attendanceBuffer(subject)

        if (attendanceBuffer < 0) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                text = "Must attend ${-attendanceBuffer} " +
                        if (attendanceBuffer == -1) {"class"} else {"classes"},
                color = absent
            )
        } else {
            Text(
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                text = "Can miss : $attendanceBuffer " +
                        if (attendanceBuffer == 1) {"class"} else {"classes"},
                color = present
            )
        }

        Row (
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
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