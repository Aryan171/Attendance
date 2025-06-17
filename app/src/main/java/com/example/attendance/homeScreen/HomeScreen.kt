package com.example.attendance.homeScreen

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Close
import androidx.compose.material.icons.twotone.Delete
import androidx.compose.material.icons.twotone.Done
import androidx.compose.material.icons.twotone.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.attendance.database.Subject
import kotlinx.serialization.Serializable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import java.time.LocalDate

@Serializable
object HomeScreen

@Preview
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeScreenViewModel = viewModel(),
    subjectCardOnClick: (subject: Subject) -> Unit = {},
) {
    val subjectList: List<Subject> by viewModel.subjectList.collectAsState()

    Scaffold(
        containerColor = Color.White,
        topBar = {HomeScreenTopBar()}
    ) {
        LazyColumn(
            modifier = Modifier
                .padding(it),
            contentPadding = PaddingValues(10.dp)
        ) {
            item {
                SubjectCard(
                    Subject(
                        subjectName = "Subject name",
                        presentDays = 20,
                        absentDays = 3,
                        attendance = sortedMapOf<LocalDate, Boolean>(LocalDate.now() to true)
                    ),
                    viewModel = viewModel,
                    onClick = subjectCardOnClick
                )
            }

            items(count = subjectList.size) {
                SubjectCard(subjectList[it], viewModel, subjectCardOnClick)
            }
        }
    }
}

@Composable
fun HomeScreenTopBar() {

}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SubjectCard(
    subject: Subject,
    viewModel: HomeScreenViewModel,
    onClick: (subject: Subject) -> Unit
) {
    var showPopup by remember { mutableStateOf(true) }
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
            Color.White
        }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(backGroundColor)
            .padding(5.dp)
            .combinedClickable(
                onClick = {onClick(subject)},
                onLongClick = {
                    showPopup = true
                }
            )
            .onGloballyPositioned(
                onGloballyPositioned = {
                    rowHeight = it.size.height
                }
            )
        ,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = subject.subjectName,
            color = Color.Black
        )

        // Popup which appears on long press
        if (showPopup) {
            Popup(
                alignment = Alignment.TopStart,
                offset = IntOffset(0, rowHeight),
                onDismissRequest = { showPopup = false },
                properties = PopupProperties(focusable = true)
            ) {
                Column(
                    modifier = Modifier
                        .padding(6.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0, 0, 0, 20)),
                    horizontalAlignment = Alignment.Start
                ) {
                    // Reset Button
                    SubjectCardPopupButton(
                        onClick = {
                            viewModel.resetAttendance(subject)
                        },
                        text = "Reset"
                    ) {
                        Icon(
                            imageVector = Icons.TwoTone.Refresh,
                            contentDescription = "Reset",
                            tint = Color.Red
                        )
                    }

                    // Delete Button
                    SubjectCardPopupButton(
                        onClick = {
                            viewModel.deleteSubject(subject)
                        },
                        text = "Delete"
                    ) {
                        Icon(
                            imageVector = Icons.TwoTone.Delete,
                            contentDescription = "Delete",
                            tint = Color.Red
                        )
                    }
                }
            }
        }

        Row {
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

            // Percent Attended
            Box(
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(
                progress = {
                    subject.presentDays.toFloat() /
                                            (subject.presentDays + subject.absentDays).toFloat()
                },
                modifier = Modifier,
                color = Color(255, 132, 0, 255),
                strokeWidth = 2.dp,
                trackColor = Color.Transparent,
                strokeCap = StrokeCap.Round,
                )

                Text(
                    text = "${((subject.presentDays.toFloat() * 100.0f)/ 
                            ((subject.presentDays + subject.absentDays).toFloat())).toInt()}%",
                    color = Color.Black
                )
            }
        }
    }
}

@Composable
fun SubjectCardPopupButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    text: String,
    icon: @Composable () -> Unit
) {
    Row(
        modifier = modifier
            .clickable(
                onClick = onClick
            )
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        icon()

        Text(text = text, color = Color.Black)
    }
}
