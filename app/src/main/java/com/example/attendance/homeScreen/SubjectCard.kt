package com.example.attendance.homeScreen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Close
import androidx.compose.material.icons.twotone.Delete
import androidx.compose.material.icons.twotone.Done
import androidx.compose.material.icons.twotone.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.example.attendance.AttendanceUiElements.ButtonColumn
import com.example.attendance.database.Subject
import com.example.attendance.AttendanceUiElements.InternalCircularProgressIndicator
import com.example.attendance.viewModel.AttendanceViewModel
import java.time.LocalDate

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SubjectCard(
    subject: Subject,
    viewModel: AttendanceViewModel,
    onClick: (subject: Subject) -> Unit
) {
    var showPopup by remember { mutableStateOf(false) }
    var rowHeight by remember { mutableIntStateOf(0) }

    val currentDate = LocalDate.now()

    val presentToday: Boolean? =
        if (currentDate in subject.attendance) {
            subject.attendance[currentDate]
        } else {
            null
        }

    val backGroundColor =
        if (presentToday != null) {
            if (presentToday) {
                Color(212, 255, 143, 255)
            } else {
                Color(255, 128, 128, 255)
            }
        }
        else {
            Color(236, 236, 236, 255)
        }

    Row(
        modifier = Modifier
            .padding(top = 10.dp, start = 10.dp, end = 10.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(backGroundColor)
            .combinedClickable(
                onClick = { onClick(subject) },
                onLongClick = {
                    showPopup = true
                }
            )
            .onGloballyPositioned(
                onGloballyPositioned = {
                    rowHeight = it.size.height
                }
            )
            .padding(5.dp)
        ,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = subject.name,
            color = Color.Black
        )

        // Popup which appears on long press
        if (showPopup) {
            ResetDeletePopup(
                subject = subject,
                offset = IntOffset(0, rowHeight),
                viewModel = viewModel,
                hidePopup = {
                    showPopup = false
                },
            )
        }

        Row (
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if(presentToday == null || !presentToday){
                // Present button
                IconButton(
                    onClick = {
                        viewModel.markPresent(subject, currentDate)
                    }
                ) {
                    Icon(
                        imageVector = Icons.TwoTone.Done,
                        contentDescription = "Present",
                        tint = Color.Green
                    )
                }
            }

            if(presentToday == null || presentToday){
                // Absent button
                IconButton(
                    onClick = {
                        viewModel.markAbsent(subject, currentDate)
                    }
                ) {
                    Icon(
                        imageVector = Icons.TwoTone.Close,
                        contentDescription = "Absent",
                        tint = Color.Red
                    )
                }
            }

            if(presentToday != null) {
                // Clear button
                IconButton(
                    onClick = {
                        viewModel.clearAttendance(subject, currentDate)
                    }
                ) {
                    Icon(
                        imageVector = Icons.TwoTone.Refresh,
                        contentDescription = "Clear",
                        tint = Color.Black
                    )
                }
            }

            InternalCircularProgressIndicator(
                modifier = Modifier
                    .size(40.dp),
                bottomText = null,
                percentageFontSize = 15.sp,
                strokeWidth = 2.dp,
                progress = viewModel.getAttendanceRatio(subject)
            )
        }
    }
}

@Composable
fun ResetDeletePopup(
    subject: Subject,
    offset: IntOffset,
    viewModel: AttendanceViewModel,
    hidePopup: () -> Unit
) {

    Popup(
        offset = offset,
        onDismissRequest = hidePopup,
        properties = PopupProperties(focusable = true)
    ) {
        ButtonColumn(
            hidePopup = hidePopup,
            hidePopupOnButtonPress = true,
            width = 125.dp,
            iconList = listOf(
                Icons.TwoTone.Refresh,
                Icons.TwoTone.Delete
            ),
            buttonTextList = listOf(
                "Reset",
                "Delete"
            ),
            onClickList = listOf(
                { viewModel.resetAttendance(subject) },
                { viewModel.deleteSubject(subject) }
            )
        )
    }
}