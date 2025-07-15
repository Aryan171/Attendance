package com.example.attendance.homeScreen.timeTableScreen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.attendance.viewModel.AttendanceViewModel
import java.time.DayOfWeek

@Composable
fun TimeTableDay(
    day: DayOfWeek,
    viewModel: AttendanceViewModel
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        TimeLineGrid(15.dp, viewModel)
    }
}

@Composable
fun TimeLineGrid(
    xOffset: Dp,
    viewModel: AttendanceViewModel
) {
    val hourHeight by viewModel.timeLineHourHeight.collectAsState()
    var boxHeight by remember { mutableStateOf(0.dp) }

    val lineColor = MaterialTheme.colorScheme.outlineVariant

    val lineWidth = 1.5.dp

    val density = LocalDensity.current

    val scrollState = rememberScrollState()

    Box (
        modifier = Modifier
            .fillMaxHeight()
            .onGloballyPositioned {
                with(density) {
                    boxHeight = it.size.height.toDp()
                }
            }
            .verticalScroll(scrollState)
            .pointerInput(Unit) {
                awaitEachGesture {
                    do {
                        val event = awaitPointerEvent()
                        val zoom = event.calculateZoom()
                        if (zoom > 0f) {
                            val modifiedHourHeight = hourHeight * zoom
                            if (modifiedHourHeight * 25 >= boxHeight) {
                                viewModel.setTimeLineHourHeight(hourHeight * zoom)
                            }
                        }
                    } while (event.changes.any() {it.pressed})
                }
            }
    ) {
        val canvasHeight = hourHeight * 25
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
    }
}