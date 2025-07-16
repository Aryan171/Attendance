package com.example.attendance.homeScreen.timeTableScreen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.calculateCentroid
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.gestures.scrollBy
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.attendance.viewModel.AttendanceViewModel
import kotlinx.coroutines.launch
import java.time.DayOfWeek

@Composable
fun TimeTableDay(
    day: DayOfWeek,
    viewModel: AttendanceViewModel
) {
    var boxWidth by remember { mutableStateOf(0.dp) }
    val xOffsetRatio = 0.15f
    val density = LocalDensity.current

    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .onGloballyPositioned {
                with(density) {
                    boxWidth = it.size.width.toDp()
                }
            }
    ) {
        TimeLineGrid(boxWidth * xOffsetRatio, scrollState, viewModel)
    }
}

@Composable
fun TimeLineGrid(
    xOffset: Dp,
    scrollState: ScrollState,
    viewModel: AttendanceViewModel
) {
    val hourHeight by viewModel.timeLineHourHeight.collectAsState()
    var boxHeight by remember { mutableStateOf(0.dp) }

    val lineColor = MaterialTheme.colorScheme.outlineVariant

    val lineWidth = 1.5.dp

    val density = LocalDensity.current

    val animationScope = rememberCoroutineScope()

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
                        val centroid = event.calculateCentroid()

                        if (zoom != 1f) {
                            val newHourHeight = (hourHeight * zoom).coerceIn(
                                range = (boxHeight / 12)..(boxHeight / 5)
                            )

                            if (centroid != Offset.Unspecified) {
                                val effectiveZoom = newHourHeight / hourHeight
                                animationScope.launch {
                                    scrollState.scrollBy(centroid.y * (effectiveZoom - 1f))
                                }
                            }

                            // changing the size of hourHeight
                            viewModel.setTimeLineHourHeight(newHourHeight)

                            // consuming the events
                            event.changes.forEach { it.consume() }
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