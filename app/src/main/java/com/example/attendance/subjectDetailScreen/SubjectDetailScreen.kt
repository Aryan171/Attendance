package com.example.attendance.subjectDetailScreen

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.twotone.ArrowBack
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.attendance.database.Subject
import com.example.attendance.viewModel.AttendanceViewModel
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Month

@Serializable
data class SubjectDetailScreen(
    val subjectIndex: Int
)

@Composable
fun SubjectDetailScreen(
    subject: Subject,
    viewModel: AttendanceViewModel,
    onBackPress: () -> Unit
) {
    var monthYear by remember {
        mutableStateOf(LocalDate.of(LocalDate.now().year, LocalDate.now().month, 1))
    }

    var selectedDate: LocalDate? by remember {mutableStateOf(null)}

    val totalNumberOfPages = 24000 // 2000 years
    val initialPageNumber = 12000

    val pagerState = rememberPagerState(
        pageCount = {
            totalNumberOfPages
        },
        initialPage = initialPageNumber
    )

    val animationScope = rememberCoroutineScope()

    Scaffold (
        topBar = {SubjectDetailScreenTopBar(subject, onBackPress)},
        containerColor = Color.White
    ) { paddingValues->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            HorizontalPager(
                state = pagerState,
                key = { pageIndex ->
                    val monthOffset = pageIndex.toLong() - initialPageNumber.toLong()
                    monthYear.plusMonths(monthOffset).toString()
                },
                beyondViewportPageCount = 1
            ) { page ->
                var currentMonthYear: LocalDate = monthYear
                if (page < initialPageNumber) {
                    currentMonthYear = monthYear.minusMonths((initialPageNumber - page).toLong())
                } else if (page > initialPageNumber) {
                    currentMonthYear = monthYear.plusMonths((page - initialPageNumber).toLong())
                }

                Calendar(
                    subject = subject,
                    month = currentMonthYear.month,
                    year = currentMonthYear.year,
                    selectedDate = selectedDate,
                    dayClicked = {
                        val selectedMonth = it.monthValue
                        val monthOnScreen = currentMonthYear.monthValue

                        selectedDate = it

                        if (((selectedMonth) % 12) + 1 == monthOnScreen) {
                            animationScope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage - 1)
                            }
                        }
                        else if (((selectedMonth + 10) % 12) + 1 == monthOnScreen ) {
                            animationScope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        }
                    }
                )
            }

            // finding the month and year that the current page is showing
            var currentMonthYear: LocalDate = monthYear
            val page = pagerState.currentPage
            if (page < initialPageNumber) {
                currentMonthYear = monthYear.minusMonths((initialPageNumber - page).toLong())
            } else if (page > initialPageNumber) {
                currentMonthYear = monthYear.plusMonths((page - initialPageNumber).toLong())
            }

            InfoBox(subject, currentMonthYear.month, currentMonthYear.year, viewModel)

            val showModificationButtons = selectedDate != null &&
                    (selectedDate!!.month == currentMonthYear.month && selectedDate!!.year == currentMonthYear.year)

            ModificationBox(
                viewModel = viewModel,
                subject = subject,
                showModificationButtons = showModificationButtons,
                date = selectedDate,
                showResetButton = pagerState.currentPage != initialPageNumber,
                onReset = {
                    animationScope.launch {
                        pagerState.animateScrollToPage(initialPageNumber)
                    }
                }
            )
        }
    }
}

@Composable
fun InfoBox(
    subject: Subject,
    month: Month,
    year: Int,
    viewModel: AttendanceViewModel
) {
    Row {
        InternalCircularProgressIndicator(
            modifier = Modifier
                .weight(1f)
                .padding(5.dp),
            bottomText = "Monthly Attendance",
            intPair = Pair(
                viewModel.presentDaysInMonth(subject, month, year),
                viewModel.absentDaysInMonth(subject, month, year)
            ),
            percentageFontSize = 50.sp,
            strokeWidth = 10.dp,
            progress = viewModel.getAttendanceRatio(subject, month, year)
        )

        InternalCircularProgressIndicator(
            modifier = Modifier
                .weight(1f)
                .padding(5.dp),
            bottomText = "Total Attendance",
            intPair = Pair(subject.presentDays, subject.absentDays),
            percentageFontSize = 50.sp,
            strokeWidth = 10.dp,
            progress = viewModel.getAttendanceRatio(subject)
        )
    }
}

@Composable
fun InternalCircularProgressIndicator(
    modifier: Modifier = Modifier,
    bottomText: String?,
    intPair: Pair<Int, Int>? = null,
    percentageFontSize: TextUnit,
    strokeWidth: Dp,
    progress: Float
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 500)
    )

    val animatedPercentage by animateIntAsState(
        targetValue = (progress * 100).toInt(),
        animationSpec = tween(durationMillis = 500)
    )

    val animatedFirstInt by animateIntAsState(
        targetValue = intPair?.first ?: 0,
        animationSpec = tween(durationMillis = 500)
    )

    val animatedSecondInt by animateIntAsState(
        targetValue = intPair?.second ?: 0,
        animationSpec = tween(durationMillis = 500)
    )

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box (
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier
                    .aspectRatio(1f),
                color = Color.Green,
                trackColor = Color.Red,
                strokeWidth = strokeWidth,
                strokeCap = StrokeCap.Round,
                progress = {animatedProgress},
                gapSize = 0.dp
            )

            Column (
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("${animatedPercentage}%", fontSize = percentageFontSize)

                if (intPair != null) {
                    Row {
                        Text(
                            text = "$animatedFirstInt  ",
                            fontSize = percentageFontSize,
                            color = Color.Green
                        )

                        Text(
                            text = "$animatedSecondInt",
                            fontSize = percentageFontSize,
                            color = Color.Red
                        )
                    }
                }
            }
        }
        if (bottomText != null) {
            Text(bottomText)
        }
    }
}

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
fun ModificationBox(
    viewModel: AttendanceViewModel,
    subject: Subject,
    showModificationButtons: Boolean,
    date: LocalDate?,
    showResetButton: Boolean,
    onReset: () -> Unit
) {
    var rowWidth by remember { mutableStateOf(0.dp) }
    val density = LocalDensity.current

    Row(
        modifier = Modifier
            .fillMaxSize()
            .onGloballyPositioned {
                rowWidth = with(density) { it.size.width.toDp() }
            }
        ,
        horizontalArrangement = Arrangement.SpaceEvenly ,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val value = subject.attendance[date]
        val maxButtonHeight = 50.dp
        val maxButtonWidth = (rowWidth - 20.dp) / 3

        ModificationBoxButton(
            visible = date != null && showModificationButtons && value != true,
            text = "Mark Present",
            maxButtonHeight = maxButtonHeight,
            maxButtonWidth = maxButtonWidth
        ) {
            if (date != null) {
                viewModel.markPresent(subject, date)
            }
        }

        ModificationBoxButton(
            visible = date != null && showModificationButtons && value != false,
            text = "Mark Absent",
            maxButtonHeight = maxButtonHeight,
            maxButtonWidth = maxButtonWidth
        ) {
            if (date != null) {
                viewModel.markAbsent(subject, date)
            }
        }

        ModificationBoxButton(
            visible = date != null && showModificationButtons && value != null,
            text = "Clear",
            maxButtonHeight = maxButtonHeight,
            maxButtonWidth = maxButtonWidth
        ) {
            if (date != null) {
                viewModel.clearAttendance(subject, date)
            }
        }

        ModificationBoxButton(
            visible = showResetButton,
            text = "Reset",
            onClick = onReset,
            maxButtonHeight = maxButtonHeight,
            maxButtonWidth = maxButtonWidth
        )
    }
}

@Composable
fun ModificationBoxButton(
    visible: Boolean,
    text: String,
    maxButtonHeight: Dp,
    maxButtonWidth: Dp,
    onClick: () -> Unit
) {
    val animatedHeight by animateDpAsState(
        targetValue = if (visible) {
            maxButtonHeight
        } else {
            0.dp
        },
        animationSpec = tween(500)
    )

    val animatedWidth by animateDpAsState(
        targetValue = if (visible) {
            maxButtonWidth
        } else {
            0.dp
        },
        animationSpec = tween(500)
    )

    if (animatedWidth > 0.dp) {
        OutlinedButton(
            modifier = Modifier
                .size(animatedWidth, animatedHeight)
                .animateContentSize(),
            onClick = onClick,
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = Color.Black
            )
        ) {
            if (animatedWidth > maxButtonWidth - 10.dp) {
                Text(
                    text = text
                )
            }
        }
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubjectDetailScreenTopBar(
    subject: Subject,
    onBackPress: () -> Unit
) {
    TopAppBar(
        title = {
            Text(subject.name, color = Color.Black)
        },
        navigationIcon = {
            IconButton(
                onClick = onBackPress
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.TwoTone.ArrowBack,
                    contentDescription = "back to home screen",
                    tint = Color.Black
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors().copy(
            containerColor = Color(166, 166, 166, 255),
        )
    )
}