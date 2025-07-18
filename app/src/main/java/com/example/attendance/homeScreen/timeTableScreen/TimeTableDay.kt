package com.example.attendance.homeScreen.timeTableScreen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.changedToDown
import androidx.compose.ui.input.pointer.changedToUp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import com.example.attendance.database.subject.SubjectUiModel
import com.example.attendance.database.timeTable.TimeTable
import com.example.attendance.homeScreen.attendanceScreen.AddSubjectDialog
import com.example.attendance.viewModel.AttendanceViewModel
import kotlinx.coroutines.flow.compose
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import kotlin.math.max
import kotlin.math.min

const val MILLIS_IN_HOUR = 3600000L
const val MILLIS_IN_DAY = 86400000L
const val MILLIS_IN_MINUTE = 60000L

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
                                val pointerMillis = ((pointer.y - hourHeight.toPx() / 2).toDouble() / hourHeight.toPx().toDouble() * MILLIS_IN_HOUR).toLong()
                                val startTime = pointerMillis - (pointerMillis % MILLIS_IN_HOUR)
                                val endTime = startTime + MILLIS_IN_HOUR

                                var bounds = startTime..endTime

                                // if there is no place the slot then range will throw an exception
                                try {
                                    for (s in viewModel.timeTableList[day.ordinal]) {
                                        bounds = if (s.endTimeMillis <= pointerMillis) {
                                            max(bounds.start, s.endTimeMillis)..bounds.last
                                        } else if (s.startTimeMillis >= pointerMillis ) {
                                            bounds.start..min(bounds.last, s.startTimeMillis)
                                        } else {
                                            throw Exception("Slot overlaps with another slot")
                                        }
                                    }

                                    if (startTime in 0L until MILLIS_IN_DAY) {
                                        val slot = TimeTable(
                                            subjectId = null,
                                            day = day.ordinal,
                                            startTimeMillis = startTime.coerceIn(bounds),
                                            endTimeMillis = endTime.coerceIn(bounds),
                                        )

                                        viewModel.addTimeTable(slot)
                                    }
                                } finally {
                                    // consuming the tap gesture
                                    event.changes.forEach { it.consume() }
                                }
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

        HourGridLines(canvasHeight, hourHeight, xOffset)

        HoursLabelColumn(canvasHeight, hourHeight, xOffset)

        // showing all the time-table slots in front of the grid
        val dayTimeTable = viewModel.timeTableList[day.ordinal]

        val dragHandleSize = 20.dp
        val dragHandleCollisionBoxSize = 50.dp

        val timeTableMutatedTrigger by viewModel.timeTableListUpdatedTrigger.collectAsState()

        // recalculating the bounds when timeTableList is mutated
        LaunchedEffect(timeTableMutatedTrigger) {
            for (slot in dayTimeTable) {
                val slotList = viewModel.timeTableList[slot.day]
                var minStart = 0L
                var maxEnd = MILLIS_IN_DAY

                for (s in slotList) {
                    if (s.id == slot.id) continue

                    if (s.endTimeMillis <= slot.startTimeMillis) {
                        minStart = maxOf(minStart, s.endTimeMillis)
                    } else if (s.startTimeMillis >= slot.endTimeMillis) {
                        maxEnd = minOf(maxEnd, s.startTimeMillis)
                    }
                }

                viewModel.setSlotBound(slot.id, minStart..maxEnd)
            }
        }

        for (slot in dayTimeTable) {
            TimeTableSlot(slot, hourHeight, xOffset, scrollState, dragHandleSize, viewModel)
        }

        for (slot in dayTimeTable) {
            SlotDragHandles(
                slot,
                xOffset,
                hourHeight,
                scrollState,
                dragHandleSize,
                dragHandleCollisionBoxSize,
                viewModel
            )
        }
    }
}

@Composable
fun SlotDragHandles(
    slot: TimeTable,
    xOffset: Dp,
    hourHeight: Dp,
    scrollState: ScrollState,
    dragHandleSize: Dp,
    dragHandleCollisionBoxSize: Dp,
    viewModel: AttendanceViewModel
) {
    val yOffset = slot.startTimeMillis.millisToDp(hourHeight) + hourHeight / 2f
    val animationScope = rememberCoroutineScope()
    val density = LocalDensity.current
    val slotHeight = (slot.endTimeMillis - slot.startTimeMillis).millisToDp(hourHeight)
    val slotBounds by viewModel.slotBounds.collectAsState()

    val bounds = slotBounds[slot.id] ?: 0L..MILLIS_IN_DAY

    Column {
        Spacer(modifier = Modifier.height(yOffset - dragHandleCollisionBoxSize / 2))

        Row {
            Spacer(modifier = Modifier.width(xOffset))

            Row (
                modifier = Modifier
                    .height(slotHeight + dragHandleCollisionBoxSize)
            ) {
                val startTimeDraggableState = rememberDraggableState {
                    val drag = with (density) { it.toDp() }
                    val offsetMillis = drag.toMillis(hourHeight)

                    if (slot.startTimeMillis + offsetMillis in bounds.start..slot.endTimeMillis) {
                        viewModel.updateTimeTable(
                            slot.copy(
                                startTimeMillis = slot.startTimeMillis + offsetMillis
                            )
                        )
                    } else {
                        animationScope.launch {
                            scrollState.scrollBy(-it)
                        }
                    }
                }

                DragHandle(
                    modifier = Modifier
                        .draggable(
                            state = startTimeDraggableState,
                            orientation = Orientation.Vertical
                        ),
                    collisionBoxSize = dragHandleCollisionBoxSize,
                    dragHandleSize = dragHandleSize
                )

                Spacer(modifier = Modifier.weight(1f))

                val endTimeDraggableState = rememberDraggableState {
                    val drag = with (density) { it.toDp() }
                    val offsetMillis = drag.toMillis(hourHeight)

                    if (slot.endTimeMillis + offsetMillis in slot.startTimeMillis..bounds.last) {
                        viewModel.updateTimeTable(
                            slot.copy(
                                endTimeMillis = slot.endTimeMillis + offsetMillis
                            )
                        )
                    } else {
                        animationScope.launch {
                            scrollState.scrollBy(-it)
                        }
                    }
                }

                Column {
                    Spacer(modifier = Modifier.weight(1f))

                    DragHandle(
                        modifier = Modifier
                            .draggable(
                                state = endTimeDraggableState,
                                orientation = Orientation.Vertical
                            ),
                        collisionBoxSize = dragHandleCollisionBoxSize,
                        dragHandleSize = dragHandleSize
                    )
                }
            }
        }
    }
}

@Composable
fun HoursLabelColumn(
    canvasHeight: Dp,
    hourHeight: Dp,
    xOffset: Dp
) {
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
}

/**
 * This composable function draws the grid lines for the timetable.
 * It draws a vertical line at the given xOffset and horizontal lines for each hour.
 *
 * @param canvasHeight The total height of the canvas.
 * @param hourHeight The height of each hour slot in the grid.
 * @param xOffset The horizontal offset from the left edge of the canvas where the vertical line is drawn.
 */
@Composable
fun HourGridLines (
    canvasHeight: Dp,
    hourHeight: Dp,
    xOffset: Dp
) {
    val lineColor = MaterialTheme.colorScheme.outlineVariant
    val lineWidth = 1.5.dp

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
                start = Offset((xOffset * 0.9f).toPx(), yOffset),
                end = Offset(size.width, yOffset),
                strokeWidth = lineWidth.toPx()
            )
        }
    }
}

@Composable
fun TimeTableSlot(
    slot: TimeTable,
    hourHeight: Dp,
    xOffset: Dp,
    scrollState: ScrollState,
    dragHandleSize: Dp,
    viewModel: AttendanceViewModel
) {
    val slotBounds by viewModel.slotBounds.collectAsState()
    val bounds = slotBounds[slot.id] ?: 0L..MILLIS_IN_DAY

    val yOffset = slot.startTimeMillis.millisToDp(hourHeight) + hourHeight / 2f
    val slotHeight = (slot.endTimeMillis - slot.startTimeMillis).millisToDp(hourHeight)
    val borderWidth = 2.dp

    val density = LocalDensity.current

    var showDeleteDialog by remember { mutableStateOf(false) }

    val animationScope = rememberCoroutineScope()

    if (showDeleteDialog) {
        SlotDeleteDialog (
            delete = {
                viewModel.deleteTimeTable(slot)
            },
            hideDialog = {
                showDeleteDialog = false
            }
        )
    }

    Column {
        Spacer(modifier = Modifier.height(yOffset - borderWidth / 2))

        Row {
            Spacer(modifier = Modifier.width(xOffset - borderWidth / 2))

            val draggableState = rememberDraggableState { dragPx ->
                val drag = with (density) { dragPx.toDp() }
                val offsetMillis = drag.toMillis(hourHeight)

                if (
                    slot.startTimeMillis + offsetMillis in bounds &&
                    slot.endTimeMillis + offsetMillis in bounds
                ) {
                    viewModel.updateTimeTable(slot.copy(
                        startTimeMillis = slot.startTimeMillis + offsetMillis,
                        endTimeMillis = slot.endTimeMillis + offsetMillis
                    ))
                } else {
                    animationScope.launch {
                        scrollState.scrollBy(-dragPx)
                    }
                }
            }

            // the box that represents the slot
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(slotHeight + borderWidth)
                    .border(
                        width = borderWidth,
                        color = MaterialTheme.colorScheme.primary,
                        shape = MaterialTheme.shapes.medium
                    )
                    .combinedClickable(
                        onClick = { showDeleteDialog = true },
                        onLongClick = { showDeleteDialog = true },
                        onDoubleClick = {
                            // making the slot occupy the maximum area it can when double clicked
                            viewModel.updateTimeTable(
                                slot.copy(
                                    startTimeMillis = bounds.start,
                                    endTimeMillis = bounds.last
                                )
                            )
                        }
                    )
                    .draggable(
                        state = draggableState,
                        orientation = Orientation.Vertical
                    )
                    .background(
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                        shape = MaterialTheme.shapes.medium
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column (
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(top = dragHandleSize / 2, start = borderWidth + 5.dp),
                    verticalArrangement = Arrangement.Top
                ) {
                    Text(
                        text = slot.startTimeMillis.toHours(),
                        fontSize = MaterialTheme.typography.bodySmall.fontSize,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Box {
                    var showSubjectSelectorDropDown by remember { mutableStateOf(false) }
                    val slotSubjectName = remember (slot.subjectId) {
                        if (slot.subjectId != null) {
                            viewModel.getSubject(slot.subjectId)?.name ?: "select a subject"
                        } else {
                            "select a subject"
                        }
                    }

                    Text(
                        text = slotSubjectName,
                        modifier = Modifier
                            .clickable {
                                showSubjectSelectorDropDown = true
                            }
                    )

                    SubjectSelectorDropDown(
                        slot = slot,
                        viewModel = viewModel,
                        expanded = showSubjectSelectorDropDown,
                        hideDropdownMenu = {
                            showSubjectSelectorDropDown = false
                        }
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                LocationSelectorDropDown()

                Column (
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(bottom = dragHandleSize / 2, end = borderWidth + 5.dp),
                    verticalArrangement = Arrangement.Bottom,
                ) {
                    Text(
                        text = slot.endTimeMillis.toHours(),
                        fontSize = MaterialTheme.typography.bodySmall.fontSize,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
    }
}

fun Dp.toMillis(hourHeight: Dp): Long {
    return ((this / hourHeight) * MILLIS_IN_HOUR).toLong()
}

@Composable
fun SubjectSelectorDropDown (
    slot: TimeTable,
    viewModel: AttendanceViewModel,
    expanded: Boolean,
    hideDropdownMenu: () -> Unit
) {
    val subjects =  viewModel.subjectList
    val scrollState  = rememberScrollState()

    DropdownMenu (
        expanded = expanded,
        onDismissRequest = hideDropdownMenu,
        shape = MaterialTheme.shapes.large,
        scrollState = scrollState
    ) {
        var showAddSubjectDialog by remember { mutableStateOf(false) }
        AddSubjectDialog(
            showDialog = showAddSubjectDialog,
            addSubject = {
                viewModel.addSubject(SubjectUiModel(name = it))
            },
            hideDialog = {
                showAddSubjectDialog = false
            }
        )

        DropdownMenuItem(
            text = { Text("Add new subject") },
            onClick = {
                showAddSubjectDialog = true
            }
        )

        HorizontalDivider()

        for (subject in subjects) {
            if (subject.id == slot.subjectId) {
                continue
            }

            DropdownMenuItem(
                text = { Text(subject.name) },
                onClick = {
                    viewModel.updateTimeTable(slot.copy(subjectId = subject.id))
                    hideDropdownMenu()
                }
            )
        }
    }
}

@Composable
fun LocationSelectorDropDown() {

}

@Composable
fun SlotDeleteDialog(
    delete: () -> Unit,
    hideDialog: () -> Unit
) {
    AlertDialog(
        onDismissRequest = hideDialog,
        confirmButton =  {
            FilledTonalButton(
                onClick = {
                    delete()
                    hideDialog()
                }
            ) {
                Text(text = "Delete")
            }
        },
        dismissButton = {
            FilledTonalButton(
                onClick = {
                    hideDialog()
                }
            ) {
                Text(text = "Cancel")
            }
        },
        title = {
            Text(
                text = "Delete slot?",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        },
        text = {
            Text(
                text = "Are you sure you want to delete this slot? " +
                        "This action cannot be undone",
                modifier = Modifier.fillMaxWidth()
            )
        }
    )
}

fun Long.millisToDp(hourHeight: Dp): Dp {
    var offset = (this / MILLIS_IN_HOUR).toInt() * hourHeight
    offset += ((this % MILLIS_IN_HOUR).toFloat() / MILLIS_IN_MINUTE.toFloat()) * hourHeight / 60f
    return offset
}

@Composable
fun DragHandle(
    modifier: Modifier = Modifier,
    collisionBoxSize: Dp,
    dragHandleSize: Dp
) {
    Box(
        modifier = modifier
            .size(collisionBoxSize)
            .padding((collisionBoxSize - dragHandleSize) / 2)
            .background(
                color = MaterialTheme.colorScheme.inversePrimary,
                shape = CircleShape
            )
            .padding(dragHandleSize / 5)
            .background(
                color = MaterialTheme.colorScheme.primary,
                shape = CircleShape
            )
    )
}

fun Long.toHours(): String {
    val hours = this / MILLIS_IN_HOUR
    var minutes = "${(this % MILLIS_IN_HOUR) / MILLIS_IN_MINUTE}"
    if (minutes.length == 1) {
        minutes = "0$minutes"
    }
    return if (hours == 0L) {
        "12:$minutes am"
    } else if (hours <= 12) {
        "$hours:$minutes am"
    } else {
        "${hours - 12}:$minutes pm"
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