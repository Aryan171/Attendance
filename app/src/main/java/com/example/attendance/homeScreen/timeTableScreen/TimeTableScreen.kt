package com.example.attendance.homeScreen.timeTableScreen

import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import java.time.DayOfWeek

@Serializable
object TimeTableScreen

@Composable
fun TimeTableScreen(
    paddingValues: PaddingValues
) {
    Scaffold (
        modifier = Modifier
            .padding(paddingValues),
        topBar = { TimeTableTopBar() }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            val pagerState = rememberPagerState (
                initialPage = 0,
                pageCount = { 7 }
            )

            var selectedWeekDay by remember(pagerState.currentPage) {
                mutableStateOf(DayOfWeek.entries[pagerState.currentPage])
            }

            val animationScope = rememberCoroutineScope()

            WeekDaySelector(
                selectedWeekDay = selectedWeekDay
            ) { dayOfWeek ->
                selectedWeekDay = dayOfWeek
                animationScope.launch {
                    pagerState.animateScrollToPage(selectedWeekDay.ordinal)
                }
            }

            HorizontalPager(
                state = pagerState
            ) { pageNumber ->
                TimeTableDay(DayOfWeek.entries[pageNumber])
            }
        }
    }
}

@Composable
fun WeekDaySelector(
    selectedWeekDay: DayOfWeek,
    setSelectedWeekDay: (DayOfWeek) -> Unit
) {
    var width by remember { mutableStateOf(0.dp) }
    val density = LocalDensity.current

    val transition = updateTransition(targetState = selectedWeekDay, label = "WeekDaySelectorTransition")

    val leftOffset by transition.animateDp(
        transitionSpec = {
            if (targetState > initialState) {
                tween(400, 100)
            } else {
                tween(400)
            }
        },
        label = "leftOffset"
    ) { dayOfWeek ->
        (dayOfWeek.value - 1) * width / 7
    }

    val rightOffset by transition.animateDp(
        transitionSpec = {
            if (targetState > initialState) {
                tween(400)
            } else {
                tween(400, 100)
            }
        },
        label = "rightOffset"
    ) { dayOfWeek ->
        dayOfWeek.value * width / 7
    }

    Box (
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 5.dp)
            .onGloballyPositioned {
                with (density) {
                    width = it.size.width.toDp()
                }
            },
        contentAlignment = Alignment.CenterStart
    ) {
        Box(
            modifier = Modifier
                .offset(x = leftOffset)
                .shadow(
                    elevation = 5.dp,
                    shape = MaterialTheme.shapes.medium
                )
                .clip(MaterialTheme.shapes.medium)
                .background(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = MaterialTheme.shapes.medium
                )
                .size(width = rightOffset - leftOffset, height = width / 14)
        )

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
        val weekDays = remember { listOf("MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN") }
        for (day in 0..6) {
            Text(
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable(
                        onClick = { setSelectedWeekDay(DayOfWeek.entries[day]) },
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    )
                    .weight(1f),
                text = weekDays[day],
                textAlign = TextAlign.Center
            )
        }
    }
}