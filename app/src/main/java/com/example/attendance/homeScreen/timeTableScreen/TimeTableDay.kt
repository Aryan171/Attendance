package com.example.attendance.homeScreen.timeTableScreen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.drawText
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import java.time.DayOfWeek

@Composable
fun TimeTableDay(
    day: DayOfWeek
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        TimeLineGrid(50.dp, 15.dp)
    }
}

@Composable
fun TimeLineGrid(
    hourHeight: Dp,
    xOffset: Dp
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val scrollState = rememberScrollState()
        val lineColor = MaterialTheme.colorScheme.outlineVariant

        val lineWidth = 1.5.dp

        Box (
            modifier = Modifier
                .verticalScroll(scrollState)
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
}