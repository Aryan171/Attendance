package com.example.attendance.homeScreen.timeTableScreen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import kotlinx.serialization.Serializable
import java.time.DayOfWeek

@Serializable
object TimeTableScreen

@Composable
fun TimeTableScreen() {
    Scaffold (
        topBar = { TimeTableTopBar() }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            var selectedWeekDay by remember { mutableStateOf(DayOfWeek.MONDAY) }
            WeekDaySelector(
                selectedWeekDay = selectedWeekDay
            ) { dayOfWeek ->
                selectedWeekDay = dayOfWeek
            }
            Text(text = selectedWeekDay.toString())
        }
    }
}

@Composable
fun WeekDaySelector(
    selectedWeekDay: DayOfWeek,
    setSelectedWeekDay: (DayOfWeek) -> Unit
) {
    Box {
        InteractableWeekDays(setSelectedWeekDay)
    }
}
@Composable
fun InteractableWeekDays(
    setSelectedWeekDay: (DayOfWeek) -> Unit
) {
    Row (
        modifier = Modifier
            .fillMaxWidth()
    ) {
        for (day in DayOfWeek.entries) {
            Text(
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable(
                        onClick = { setSelectedWeekDay(day) }
                    )
                    .weight(1f),
                text = day.name.substring(0..2),
                textAlign = TextAlign.Center
            )
        }
    }
}