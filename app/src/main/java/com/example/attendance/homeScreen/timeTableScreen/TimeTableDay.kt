package com.example.attendance.homeScreen.timeTableScreen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
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
import androidx.compose.ui.input.pointer.changedToDown
import androidx.compose.ui.input.pointer.changedToUp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import com.example.attendance.database.timeTable.TimeTable
import com.example.attendance.viewModel.AttendanceViewModel
import kotlinx.coroutines.launch
import java.time.DayOfWeek

const val MILLIS_IN_HOUR = 3600000L
const val MILLIS_IN_DAY = 86400000L

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

    val tapTimeOutMs = 100L

    Box (
        modifier = Modifier
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    var pointerDownTime: Long = 0
                    var pointerDownPosition = Offset(0f, 0f)
                    while(true) {
                        val event = awaitPointerEvent()
                        val pointers = event.changes

                        // identifying tap gesture
                        if (pointers.size == 1) {
                            if (pointers[0].changedToDown()) {
                                pointerDownTime = System.currentTimeMillis()
                                pointerDownPosition = pointers[0].position
                            } else if (pointers[0].changedToUp() &&
                                System.currentTimeMillis() - pointerDownTime < tapTimeOutMs &&
                                (pointers[0].position - pointerDownPosition).getDistance() == 0f
                                ) {
                                val pointer = pointers[0].position

                                // when a tap is detected a one hour slot is added to the timetable
                                val startTime = ((pointer.y - hourHeight.toPx() / 2) / hourHeight.toPx()).toLong() * MILLIS_IN_HOUR


                                if (startTime in 0L until MILLIS_IN_DAY) {
                                    val slot = TimeTable(
                                        subjectId = null,
                                        day = day.ordinal,
                                        startTimeMillis = startTime,
                                        endTimeMillis = startTime + MILLIS_IN_HOUR,
                                    )

                                    viewModel.addTimeTable(slot)
                                }

                                // consuming the tap gesture
                                event.changes.forEach { it.consume() }
                            }
                        }
                        // identifying zoom gesture
                        else if (pointers.size == 2) {
                            pointerDownTime = 0

                            val a = pointers[0].position
                            val b = pointers[1].position
                            val prevA = pointers[0].previousPosition
                            val prevB = pointers[1].previousPosition

                            if (a == Offset.Unspecified ||
                                b == Offset.Unspecified ||
                                prevA == Offset.Unspecified ||
                                prevB == Offset.Unspecified
                                ) {
                                continue
                            }

                            val zoom = (a.y - b.y) / (prevA.y - prevB.y)
                            val newHourHeight = (zoom * hourHeight).coerceIn(hourHeightRange)
                            val effectiveZoom = newHourHeight / hourHeight
                            val centroid = (a.y + b.y) / 2f
                            val previousCentroid = (prevA.y + prevB.y) / 2f

                            if (zoom != 1f) {
                                println(scrollState.value / (hourHeight.toPx() * 25))
                                animationScope.launch {
                                    scrollState.scrollTo (
                                        scrollState.value + (
                                                previousCentroid * (effectiveZoom - 1) + (previousCentroid - centroid) / effectiveZoom
                                                ).toInt()
                                    )
                                }

                                // modifying the hourHeight
                                viewModel.setTimeLineHourHeight(newHourHeight)

                                // consuming the zoom gesture
                                event.changes.forEach { it.consume() }
                            }
                        }
                    }
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
    val yOffset = hourHeight * (slot.startTimeMillis / MILLIS_IN_HOUR).toInt() + hourHeight / 2

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