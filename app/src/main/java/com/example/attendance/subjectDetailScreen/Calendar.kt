package com.example.attendance.subjectDetailScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.attendance.database.Subject
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Month

@Composable
fun Calendar(
    subject: Subject,
    month: Month,
    year: Int,
    selectedDate: LocalDate?,
    dayClicked: (LocalDate) -> Unit
) {
    Column (
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "${month.name.substring(0, 3)} $year",
            fontSize = 30.sp,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(5.dp))

        WeekDays()

        Spacer(modifier = Modifier.height(5.dp))

        MonthGrid(
            subject = subject,
            month = month,
            year = year,
            selectedDate = selectedDate,
            dayClicked = {date ->
                dayClicked(date)
            }
        )
    }
}

@Composable
fun MonthGrid(
    subject: Subject,
    month: Month,
    year: Int,
    selectedDate: LocalDate?,
    dayClicked: (LocalDate) -> Unit
) {
    val density = LocalDensity.current
    var boxSize by remember { mutableStateOf(50.dp) }

    var day = getFirstDayOfGrid(month, year)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .onGloballyPositioned {
                boxSize = with(density) { it.size.width.toDp() / 7 }
            }
    ) {
        (0 until 6).forEach { week ->
            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .height(boxSize)
            ) {
                (0 until 7).forEach { dayOfWeek ->
                    val value = subject.attendance[day]

                    val boxSelected: Boolean = selectedDate != null &&
                            selectedDate.dayOfMonth == day.dayOfMonth &&
                            selectedDate.month == day.month

                    val boxColor = if (value != null) {
                        if (value) {
                            Color.Green
                        } else {
                            Color.Red
                        }
                    } else {
                        Color.White
                    }

                    val dayCopy = LocalDate.of(day.year, day.month, day.dayOfMonth)

                    Box (
                        modifier = Modifier
                            .size(boxSize)
                            .padding(2.dp)
                            .border(
                                width = 1.dp,
                                color = if (boxSelected) {
                                    Color.Black
                                } else {
                                    Color.Transparent
                                },
                                shape = CircleShape
                            )
                            .background(boxColor, shape = CircleShape)
                            .clip(CircleShape)
                            .clickable(
                                onClick = { dayClicked(dayCopy) }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = day.dayOfMonth.toString(),
                            color = Color.Black
                        )
                    }

                    day = day.plusDays(1)
                }
            }
        }
    }
}

fun getFirstDayOfGrid(month: Month, year: Int): LocalDate {
    var firstDay = LocalDate.of(year, month, 1)

    while(firstDay.dayOfWeek != DayOfWeek.MONDAY) {
        firstDay = firstDay.minusDays(1)
    }

    return firstDay
}

@Composable
fun WeekDays() {
    val density = LocalDensity.current

    var textWidth by remember { mutableStateOf(0.dp) }

    Row (
        modifier = Modifier
            .fillMaxWidth()
            .onGloballyPositioned(
                onGloballyPositioned = {
                    textWidth = with(density) { it.size.width.toDp() / 7 }
                }
            )
    ) {
        for (day in DayOfWeek.entries) {
            Text(
                text = day.name.substring(0, 3),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .width(textWidth)
            )
        }
    }
}