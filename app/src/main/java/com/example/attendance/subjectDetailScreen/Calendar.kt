package com.example.attendance.subjectDetailScreen

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.attendance.database.subject.SubjectUiModel
import com.example.attendance.ui.theme.absent
import com.example.attendance.ui.theme.present
import com.example.attendance.ui.theme.smallRoundedCornerShape
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Month

@Composable
fun Calendar(
    subject: SubjectUiModel,
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
    subject: SubjectUiModel,
    month: Month,
    year: Int,
    selectedDate: LocalDate?,
    dayClicked: (LocalDate) -> Unit
) {
    val density = LocalDensity.current
    var boxSize by remember { mutableStateOf(50.dp) }

    var day = getFirstDayOfGrid(month, year)
    val currentDate = LocalDate.now()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .onGloballyPositioned {
                boxSize = with(density) { it.size.width.toDp() / 7 }
            }
    ) {
        while (day.month.equalToOrIsPreviousMonthOf(month)) {
            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .height(boxSize)
            ) {
                (0..6).forEach { _ ->
                    if (day.month.value != month.value) {
                        Box(
                            modifier = Modifier.size(boxSize)
                        )

                        day = day.plusDays(1)
                        return@forEach
                    }

                    val value = subject.attendance[day]
                    val dayCopy = LocalDate.of(day.year, day.month, day.dayOfMonth)

                    DayCard(
                        size = boxSize,
                        color = if (value != null) {
                            if (value) {
                                present
                            } else {
                                absent
                            }
                        } else if (day == currentDate) {
                            MaterialTheme.colorScheme.primaryContainer
                        } else {
                            Color.Transparent
                        },
                        day = day,
                        selected = selectedDate != null &&
                                selectedDate.dayOfMonth == day.dayOfMonth &&
                                selectedDate.month == day.month
                    ) {
                        dayClicked(dayCopy)
                    }

                    day = day.plusDays(1)
                }
            }
        }
    }
}

@Composable
fun DayCard(
    size: Dp,
    color: Color,
    day: LocalDate,
    selected: Boolean,
    onClick: () -> Unit
) {
    val animatedColor by animateColorAsState(
        targetValue = color,
        animationSpec = tween(500)
    )

    Box (
        modifier = Modifier
            .size(size)
            .padding(1.dp)
            .border(
                width = 1.dp,
                color = if (selected) {
                    MaterialTheme.colorScheme.onSurface
                } else {
                    Color.Transparent
                },
                shape = smallRoundedCornerShape
            )
            .background(animatedColor, shape = smallRoundedCornerShape)
            .clip(smallRoundedCornerShape)
            .clickable(
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(day.dayOfMonth.toString())
    }
}

/**
 *returns true if the provided month is equal to this month or is the previous month
 */
fun Month.equalToOrIsPreviousMonthOf(month: Month): Boolean {
    return month.value == this.value || this.value % 12 == month.value - 1
}

fun getFirstDayOfGrid(month: Month, year: Int): LocalDate {
    var firstDay = LocalDate.of(year, month, 1)

    while(firstDay.dayOfWeek != DayOfWeek.MONDAY) {
        firstDay = firstDay.minusDays(1)
    }

    return firstDay
}

/**
 * Composable function that displays the days of the week (MON, TUE, etc.) in a row.
 * Each day is a Text composable, and they are evenly spaced across the width of the screen.
 */
@Composable
fun WeekDays() {
    Row (
        modifier = Modifier
            .fillMaxWidth()
    ) {
        val daysOfWeek = remember { listOf("MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN") }
        for (day in daysOfWeek) {
            Text(
                modifier = Modifier.weight(1f),
                text = day,
                textAlign = TextAlign.Center
            )
        }
    }
}