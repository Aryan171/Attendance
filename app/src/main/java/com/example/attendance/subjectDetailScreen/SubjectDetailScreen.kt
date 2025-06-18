package com.example.attendance.subjectDetailScreen

import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.twotone.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.attendance.MainActivity
import com.example.attendance.database.Subject
import com.example.attendance.viewModel.AttendanceViewModel
import kotlinx.serialization.Serializable
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Month
import kotlin.math.abs

@Serializable
data class SubjectDetailScreen(
    val subjectIndex: Int
)

@Composable
fun SubjectDetailScreen(
    subject: Subject = Subject(0, "Subject", 2, 3, mutableMapOf(LocalDate.now() to true)),
    viewModel: AttendanceViewModel = AttendanceViewModel(MainActivity.db.subjectDao()),
    onBackPress: () -> Unit = {}
) {
    Scaffold (
        topBar = {SubjectDetailScreenTopBar(subject, onBackPress)},
        containerColor = Color.White
    ) {
        Calendar(
            modifier = Modifier.padding(it),
            viewModel = viewModel
        )
    }
}

@Composable
fun Calendar(
    modifier: Modifier = Modifier,
    viewModel: AttendanceViewModel,
    initialMonth: Month = LocalDate.now().month,
    initialYear: Int = LocalDate.now().year
) {
    var monthYear by remember {mutableStateOf(LocalDate.of(initialYear, initialMonth, 1))}
    val velocityTracker = remember { VelocityTracker() }
    var swipeOffset by remember { mutableFloatStateOf(0f) }

    Column (
        modifier = modifier
            .padding(5.dp)
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragStart = {
                        swipeOffset = 0f
                        velocityTracker.resetTracking()
                    },
                    onHorizontalDrag = { change, dragAmount ->
                        change.consume()
                        swipeOffset += dragAmount
                        velocityTracker.addPosition(change.uptimeMillis, change.position)
                    },
                    onDragEnd = {
                        val velocity = velocityTracker.calculateVelocity().x
                        val screenWidth = size.width

                        val swipeThreshold = screenWidth / 4f
                        val velocityThreshold = 500f

                        if (abs(swipeOffset) > swipeThreshold || abs(velocity) > velocityThreshold) {
                            if (swipeOffset < 0 || (swipeOffset == 0f && velocity < -velocityThreshold)) {
                                monthYear = monthYear.plusMonths(1)
                            } else if (swipeOffset > 0 || velocity > velocityThreshold) {
                                monthYear = monthYear.minusMonths(1)
                            }
                        }
                        swipeOffset = 0f
                    }
                )
            }
        ,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "${monthYear.month.name.substring(0, 3)} ${monthYear.year}",
            fontSize = 30.sp,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(5.dp))

        WeekDays()

        Spacer(modifier = Modifier.height(5.dp))

        MonthGrid(viewModel, monthYear)
    }
}

@Composable
fun MonthGrid(
    viewModel: AttendanceViewModel,
    monthYear: LocalDate
) {
    val density = LocalDensity.current
    var boxSize by remember { mutableStateOf(0.dp) }

    val daysList = constructDaysList(monthYear.month, monthYear.year)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .onSizeChanged {
                boxSize = with(density) { it.width.toDp() / 7 }
            }
    ) {
        for (week in 0 until 6) {
            Row (
                modifier = Modifier.fillMaxWidth()
            ) {
                for (day in 0 until 7) {
                    val boxNumber = week * 7 + day

                    Box (
                        modifier = Modifier
                            .size(boxSize)
                            .border(
                                width = 0.25.dp,
                                color = Color.Black
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = daysList[boxNumber].dayOfMonth.toString(),
                            color = Color.Black
                        )
                    }
                }
            }
        }
    }
}

fun constructDaysList(month: Month, year: Int): List<LocalDate> {
    val daysList = mutableListOf<LocalDate>()
    var firstDay = LocalDate.of(year, month, 1)

    while(firstDay.dayOfWeek != DayOfWeek.MONDAY) {
        println(firstDay.toString())
        firstDay = firstDay.minusDays(1)
    }

    for (i in 0..41) {
        daysList.add(firstDay)
        println(firstDay.toString())
        firstDay = firstDay.plusDays(1)
    }

    return daysList
}

@Composable
fun WeekDays() {
    Row (
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (day in DayOfWeek.entries) {
            Text(
                text = day.name.substring(0, 3)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubjectDetailScreenTopBar(
    subject: Subject,
    onBackPress: () -> Unit
) {
    TopAppBar(
        title = {
            Text(subject.name, color = Color.Black)
        },
        navigationIcon = {
            IconButton(
                onClick = onBackPress
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.TwoTone.ArrowBack,
                    contentDescription = "back to home screen",
                    tint = Color.Black
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors().copy(
            containerColor = Color(166, 166, 166, 255),
        )
    )
}