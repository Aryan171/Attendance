package com.example.attendance.homeScreen

import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.twotone.Close
import androidx.compose.material.icons.twotone.Delete
import androidx.compose.material.icons.twotone.Done
import androidx.compose.material.icons.twotone.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.example.attendance.database.Subject
import com.example.attendance.viewModel.AttendanceViewModel
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
object HomeScreen

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: AttendanceViewModel,
    subjectCardOnClick: (subject: Subject) -> Unit
) {
    val subjectList = viewModel.subjectList

    Scaffold(
        containerColor = Color.White,
        topBar = {HomeScreenTopBar(viewModel)}
    ) {
        LazyColumn(
            modifier = Modifier
                .padding(it)
        ) {
            items (
                items = subjectList,
                key = {it.id}
            ){
                SubjectCard(
                    subject = it,
                    viewModel = viewModel,
                    onClick = subjectCardOnClick
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenTopBar(
    viewModel: AttendanceViewModel
) {
    var showAddPopup by remember {mutableStateOf(false)}
    var text by remember {mutableStateOf("")}

    if (showAddPopup) {
        Popup(
            alignment = Alignment.Center,
            onDismissRequest = {text = ""
                showAddPopup = false },
            properties = PopupProperties(focusable = true)
        ) {
            OutlinedTextField(
                colors = OutlinedTextFieldDefaults.colors().copy(
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
                    .padding(vertical = 40.dp),
                value = text,
                keyboardActions = KeyboardActions(
                    onGo = {
                        if (text != "") {
                            viewModel.addSubject(Subject(subjectName = text.trim()))
                            text = ""
                            showAddPopup = false
                        }
                    }
                ),
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Go
                ),
                onValueChange = {
                    text = it
                },
                shape = RoundedCornerShape(10.dp),
                label = {
                    Text("Subject Name")
                },
                trailingIcon = {
                    IconButton(
                        onClick = {
                            if (text != "") {
                                viewModel.addSubject(Subject(subjectName = text.trim()))
                                text = ""
                                showAddPopup = false
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "add subject",
                            tint = Color.Black
                        )
                    }
                }
            )
        }

    }

    TopAppBar(
        title = {
            Text("Attendance", maxLines = 1, overflow = TextOverflow.Ellipsis)
        },
        actions = {
            // add button
            IconButton(onClick = {
                showAddPopup = true
            }) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "add subject"
                )
            }
        },
        colors = TopAppBarColors(
            containerColor = Color(89, 89, 89, 255),
            scrolledContainerColor = Color.Black,
            navigationIconContentColor = Color.White,
            titleContentColor = Color.White,
            actionIconContentColor = Color.White
        )
    )
}

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
            .fillMaxWidth()
            .padding(top = 10.dp, start = 10.dp, end = 10.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(backGroundColor)
            .padding(5.dp)
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
                            showPopup = false
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
                            showPopup = false
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
                val totalDays = (subject.presentDays + subject.absentDays).toFloat()

                CircularProgressIndicator(
                progress = {
                    if (totalDays != 0f) {
                        subject.presentDays.toFloat() / totalDays
                    } else {
                        1f
                    }
                },
                modifier = Modifier,
                color = Color(255, 132, 0, 255),
                strokeWidth = 2.dp,
                trackColor = Color.Transparent,
                strokeCap = StrokeCap.Round,
                )

                Text(
                    text = "${
                        if (totalDays != 0f) {
                            ((subject.presentDays.toFloat() * 100.0f) / totalDays).toInt()
                        } else {
                            100
                        }
                    }%",
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
