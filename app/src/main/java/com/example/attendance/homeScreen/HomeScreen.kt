package com.example.attendance.homeScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Close
import androidx.compose.material.icons.twotone.Done
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import java.time.LocalDate

@Serializable
object HomeScreen

@Preview
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeScreenViewModel = viewModel()
) {
    val subjectList: List<Subject> by viewModel.subjectList.collectAsState()

    Scaffold(
        modifier = modifier,
        topBar = {HomeScreenTopBar()}
    ) {
        LazyColumn(
            modifier = Modifier.padding(it)
        ) {
            item {
                SubjectCard(
                    Subject(
                        subjectName = "Subject name",
                        presentDays = 20,
                        absentDays = 3,
                        attendance = sortedMapOf<LocalDate, Boolean>(LocalDate.now() to true)
                    ),
                    viewModel = viewModel
                )
            }

            items(count = subjectList.size) {
                SubjectCard(subjectList[it], viewModel)
            }
        }
    }
}

@Composable
fun HomeScreenTopBar() {

}

@Composable
fun SubjectCard(
    subject: Subject,
    viewModel: HomeScreenViewModel
) {
    val currentDate = LocalDate.now()
    val backGroundColor =
        if (currentDate in subject.attendance) {
            if (subject.attendance[currentDate] == true) {
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
        ,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = subject.subjectName,
            color = Color.Black
        )

        Row {
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
                trackColor = backGroundColor,
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