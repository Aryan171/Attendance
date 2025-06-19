package com.example.attendance.homeScreen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.twotone.Close
import androidx.compose.material.icons.twotone.Delete
import androidx.compose.material.icons.twotone.Done
import androidx.compose.material.icons.twotone.Refresh
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.example.attendance.database.Subject
import com.example.attendance.subjectDetailScreen.InternalCircularProgressIndicator
import com.example.attendance.viewModel.AttendanceViewModel
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
object HomeScreen

@Composable
fun HomeScreen(
    viewModel: AttendanceViewModel,
    subjectCardOnClick: (subject: Subject) -> Unit
) {
    val subjectList = viewModel.subjectList
    Scaffold(
        containerColor = Color.White,
        topBar = {HomeScreenTopBar(viewModel)}
    ) {
        if (subjectList.isEmpty()) {
            Box (
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No Subjects Added",
                    color = Color.LightGray,
                    fontSize = 25.sp
                )
            }
        }
        else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
            ) {
                items(
                    items = subjectList,
                    key = { it.id }
                ) {
                    SubjectCard(
                        subject = it,
                        viewModel = viewModel,
                        onClick = subjectCardOnClick
                    )
                }
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
    var showMenuPopup by remember {mutableStateOf(false)}

    if (showAddPopup) {
        AddSubjectPopup(
            viewModel
        ) {
            showAddPopup = false
        }
    }

    if (showMenuPopup) {
        MenuPopup(
            viewModel
        ) {
            showMenuPopup = false
        }
    }

    TopAppBar(
        title = {
            Text("Attendance", maxLines = 1, overflow = TextOverflow.Ellipsis)
        },
        actions = {
            // add button
            IconButton(
                onClick = {
                    showAddPopup = true
                }
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "add subject"
                )
            }

            IconButton(
                onClick = {
                    showMenuPopup = true
                }
            ) {
                Icon(
                    imageVector = Icons.Filled.MoreVert,
                    contentDescription = "open menu"
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

@Composable
fun AddSubjectPopup(
    viewModel: AttendanceViewModel,
    hidePopup: () -> Unit
) {
    var text by remember {mutableStateOf("")}

    Popup(
        alignment = Alignment.Center,
        onDismissRequest = {text = ""
            hidePopup() },
        properties = PopupProperties(focusable = true)
    ) {
        OutlinedTextField(
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                focusedContainerColor = Color.White.copy(alpha = 0.7f),
                unfocusedContainerColor = Color.White.copy(alpha = 0.7f),
                focusedLabelColor = Color.Black,
                unfocusedLabelColor = Color.Black,
                focusedBorderColor = Color.Black,
                unfocusedBorderColor = Color.Black
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .padding(vertical = 40.dp),
            value = text,
            keyboardActions = KeyboardActions(
                onGo = {
                    if (text != "") {
                        viewModel.addSubject(Subject(name = text.trim()))
                        text = ""
                        hidePopup()
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
                            viewModel.addSubject(Subject(name = text.trim()))
                            text = ""
                            hidePopup()
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

@Composable
fun MenuPopup(
    viewModel: AttendanceViewModel,
    hidePopup: () -> Unit
) {
    var text by remember {mutableStateOf("")}

    Popup(
        alignment = Alignment.TopEnd,
        onDismissRequest = {text = ""
            hidePopup() },
        properties = PopupProperties(focusable = true)
    ) {
        Column (
            modifier = Modifier
                .border(width = Dp.Hairline, color = Color.Black, shape = RoundedCornerShape(10.dp))
                .clip(RoundedCornerShape((10.dp)))
                .background(Color.White.copy(alpha = 0.7f), RoundedCornerShape(10.dp))
        ) {
            val currentDate = LocalDate.now()

            InternalCustomButton("Set all Present Today") {
                println("pupupapa")
                viewModel.setAllPresent(currentDate)
                hidePopup()
            }

            InternalCustomButton("Set all Absent Today") {
                viewModel.setAllAbsent(currentDate)
                hidePopup()
            }

            InternalCustomButton("Reset all attendance for today") {
                viewModel.clearAllAttendance(currentDate)
                hidePopup()
            }

            InternalCustomButton("Sort by Name") {
                viewModel.sortSubjectListBy{a, b ->
                    a.name.compareTo(b.name)
                }
                hidePopup()
            }

            InternalCustomButton("Sort by most attended percent") {
                viewModel.sortSubjectListBy{a, b ->
                    viewModel.getAttendanceRatio(b).compareTo(viewModel.getAttendanceRatio(a))
                }
                hidePopup()
            }

            InternalCustomButton("Sort by least attended percent") {
                viewModel.sortSubjectListBy{a, b ->
                    viewModel.getAttendanceRatio(a).compareTo(viewModel.getAttendanceRatio(b))
                }
                hidePopup()
            }

            InternalCustomButton("Sort by most attended days") {
                viewModel.sortSubjectListBy{a, b ->
                    b.presentDays.compareTo(a.presentDays)
                }
                hidePopup()
            }

            InternalCustomButton("Sort by least attended days") {
                viewModel.sortSubjectListBy {a, b ->
                    a.presentDays.compareTo(b.presentDays)
                }
                hidePopup()
            }

            InternalCustomButton("Sort by most absent days") {
                viewModel.sortSubjectListBy{a, b ->
                    b.absentDays.compareTo(a.absentDays)
                }
                hidePopup()
            }

            InternalCustomButton("Sort by least absent days") {
                viewModel.sortSubjectListBy {a, b ->
                    a.absentDays.compareTo(b.absentDays)
                }
                hidePopup()
            }
        }
    }
}

@Composable
fun InternalCustomButton(
    text: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clickable {
                onClick()
            }
            .border(width = Dp.Hairline, color = Color.Black, shape = RectangleShape)
            .width(250.dp)
            .background(Color.Transparent)
            .padding(15.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = text,
            modifier = Modifier.background(Color.Transparent)
        )
    }
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
            text = subject.name,
            color = Color.Black
        )

        // Popup which appears on long press
        if (showPopup) {
            Popup(
                offset = IntOffset(0, rowHeight),
                onDismissRequest = { showPopup = false },
                properties = PopupProperties(focusable = true)
            ) {
                Column(
                    modifier = Modifier
                        .width(125.dp)
                        .padding(6.dp)
                        .background(
                            color = Color.White.copy(alpha = 0.7f),
                            shape = RoundedCornerShape(10.dp)
                        )
                        .border(Dp.Hairline, Color.Black, RoundedCornerShape(10.dp))
                        .clip(RoundedCornerShape(10.dp)),
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
                2.dp
            ) {
                viewModel.getAttendanceRatio(subject)
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
            .fillMaxWidth()
            .background(Color.Transparent)
            .border(Dp.Hairline, Color.Black)
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        icon()

        Text(text = text, color = Color.Black, modifier = Modifier.background(Color.Transparent))
    }
}
