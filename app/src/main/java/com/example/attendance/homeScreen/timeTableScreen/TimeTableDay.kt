package com.example.attendance.homeScreen.timeTableScreen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.calculateCentroid
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.attendance.database.timeTable.TimeTable
import com.example.attendance.viewModel.AttendanceViewModel
import kotlinx.coroutines.launch
import java.time.DayOfWeek

@Composable
fun TimeTableDay(
    day: DayOfWeek,
    viewModel: AttendanceViewModel
) {
    var parentWidth by remember { mutableStateOf(0.dp) }
    val xOffsetRatio = 0.15f
    val density = LocalDensity.current
    val scrollState = rememberScrollState()

    Box (
        modifier = Modifier
            .fillMaxSize()
            .height(100.dp)
            .verticalScroll(scrollState)
            .onGloballyPositioned {
                with(density) {
                    parentWidth = it.size.width.toDp()
                }
            }
    ) {
        TimeLineGrid(
            parentWidth * xOffsetRatio,
            scrollState,
            parentWidth / 10 .. parentWidth / 2,
            day,
            viewModel
        )
    }
}

@Composable
fun TimeLineGrid(
    xOffset: Dp,
    scrollState: ScrollState,
    hourHeightRange: ClosedRange<Dp>,
    day: DayOfWeek,
    viewModel: AttendanceViewModel
) {
    val hourHeight by viewModel.timeLineHourHeight.collectAsState()

    val lineColor = MaterialTheme.colorScheme.outlineVariant

    val lineWidth = 1.5.dp
    val animationScope = rememberCoroutineScope()

    Box (
        modifier = Modifier
            .pointerInput(Unit) {
                awaitEachGesture {
                    do {
                        val event = awaitPointerEvent()
                        val zoom = event.calculateZoom()
                        val centroid = event.calculateCentroid()
                        if (zoom != 1f && centroid != Offset.Unspecified) {
                            val newHourHeight = (hourHeight * zoom).coerceIn(hourHeightRange)
                            val effectiveZoom = newHourHeight / hourHeight

                            // scrolling so that zoom is done at the centroid
                            animationScope.launch {
                                scrollState.scrollBy(value = centroid.y * (effectiveZoom - 1f))
                            }

                            // changing the size of hourHeight
                            viewModel.setTimeLineHourHeight(newHourHeight)

                            // consuming the events
                            event.changes.forEach { it.consume() }
                        }
                    } while (event.changes.any() {it.pressed})
                }
            },
        contentAlignment = Alignment.TopStart
    ) {
        val canvasHeight = hourHeight * 25

        // canvas is used to draw the grid
        Canvas(
            modifier = Modifier
                .height(canvasHeight)
                .fillMaxWidth()
        ) {
            drawLine(
                color = lineColor,
                start = Offset(xOffset.toPx(), 0f),
                end = Offset(xOffset.toPx(), canvasHeight.toPx()),
                strokeWidth = lineWidth.toPx()
            )

            for (i in 0..24) {
                val yOffset = (hourHeight * (i + 0.5f)).toPx()
                drawLine(
                    color = lineColor,
                    start = Offset(xOffset.toPx(), yOffset),
                    end = Offset(size.width, yOffset),
                    strokeWidth = lineWidth.toPx()
                )
            }
        }

        // used to write the time at left hand side
        Column (
            modifier = Modifier
                .size(width = xOffset, height = canvasHeight)
        ) {
            GridHourText("12 am", xOffset, hourHeight)

            listOf("am", "pm").forEach { it ->
                (1..12).forEach { hour ->
                    GridHourText("$hour $it", xOffset, hourHeight)
                }
            }
        }

        // showing oll the time-table slots on top of the grid as boxes
        val timeTableList = viewModel.timeTableList[day.ordinal]

        for (slot in timeTableList) {
            TimeTableSlot(slot, hourHeight, xOffset)
        }
    }
}

@Composable
fun TimeTableSlot(slot: TimeTable, hourHeight: Dp, xOffset: Dp) {
    val yOffset = hourHeight * (slot.startTimeMillis / 3600000).toInt()

    Column {
        Spacer(modifier = Modifier.height(yOffset))

        Row {
            Spacer(modifier = Modifier.width(xOffset))

            Box (
                modifier = Modifier
                    .weight(1f)
                    .height(hourHeight)
                    .background(Color.Red)
            ) {

            }
        }
    }
}

/**
 * A composable function that displays a text representing an hour in the timetable grid.
 *
 * @param text The text to be displayed (e.g., "12 am", "1 pm").
 * @param width The width of the text container.
 * @param height The height of the text container.
 */
@Composable
fun GridHourText(text: String, width: Dp, height: Dp) {
    Box (
        modifier = Modifier
            .size(width, height),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = MaterialTheme.typography.bodySmall.fontSize
        )
    }
}