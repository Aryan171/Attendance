package com.example.attendance.subjectDetailScreen

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.text.style.TextAlign
import com.example.attendance.database.subject.SubjectUiModel
import com.example.attendance.ui.theme.absent
import com.example.attendance.ui.theme.present
import com.example.attendance.viewModel.AttendanceViewModel
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
data class SubjectDetailScreen(
    val subjectIndex: Int
)

@Composable
fun SubjectDetailScreen(
    subject: SubjectUiModel,
    viewModel: AttendanceViewModel,
    onBackPress: () -> Unit
) {
    val initialMonthYear = LocalDate.of(LocalDate.now().year, LocalDate.now().month, 1)
    var selectedDate: LocalDate? by remember {mutableStateOf(null)}

    val calendarPageCount = 24000 // 2000 years
    val initialCalendarPage = 12000

    val pagerState = rememberPagerState(
        pageCount = {
            calendarPageCount
        },
        initialPage = initialCalendarPage
    )

    val animationScope = rememberCoroutineScope()

    // stores the month and year value of the page that is showing currently
    val currentMonthYear = remember (pagerState.currentPage) {
        initialMonthYear.addOffset(pagerState.currentPage - initialCalendarPage.toLong())
    }

    fun findPageIndex(date: LocalDate): Int {
        return initialCalendarPage + (date.year - initialMonthYear.year) * 12 +
                date.monthValue - initialMonthYear.monthValue
    }

    Scaffold (
        topBar =
            {
                SubjectDetailScreenTopAppBar(
                    subject = subject,
                    onDateSelected =
                        {
                            animationScope.launch {
                                pagerState.animateScrollToPage(findPageIndex(it))
                            }
                            selectedDate = it
                        },
                    onBackPress = onBackPress)
            },
        bottomBar = {
            val attendanceBuffer by viewModel.attendanceBuffer(subject).collectAsState()

            Text(
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                text = if (attendanceBuffer < 0) {
                    "Must attend ${-attendanceBuffer} " +
                    if (attendanceBuffer == -1) {"class"} else {"classes"}
                } else if (attendanceBuffer > 0) {
                    "Can miss : $attendanceBuffer " +
                            if (attendanceBuffer == 1) {"class"} else {"classes"}
                } else {
                    "Right on edge, don't miss any class"
                },
                color = if (attendanceBuffer <= 0) { absent } else { present }
            )
        }
    ) { paddingValues->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row (
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = {
                        animationScope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage - 1)
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "previous month"
                    )
                }

                Text(
                    text = "${
                        currentMonthYear.month.name.substring(
                            0,
                            3
                        )
                    } ${currentMonthYear.year}",
                    fontSize = MaterialTheme.typography.titleLarge.fontSize
                )

                IconButton(
                    onClick = {
                        animationScope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "next month"
                    )
                }
            }

            HorizontalPager(
                modifier = Modifier
                    .animateContentSize(
                        animationSpec = tween(500)
                    ),
                verticalAlignment = Alignment.Top,
                state = pagerState,
                key = { pageIndex ->
                    val monthOffset = pageIndex.toLong() - initialCalendarPage.toLong()
                    initialMonthYear.plusMonths(monthOffset).toString()
                }
            ) { page ->
                val pageMonthYear = initialMonthYear.addOffset((page - initialCalendarPage).toLong())

                Calendar(
                    subject = subject,
                    month = pageMonthYear.month,
                    year = pageMonthYear.year,
                    selectedDate = selectedDate,
                    dayClicked = { selectedDate = it }
                )
            }

            // finding the month and year that the current page is showing
            var initialMonthYear: LocalDate = initialMonthYear
            val page = pagerState.currentPage
            if (page < initialCalendarPage) {
                initialMonthYear = initialMonthYear.minusMonths((initialCalendarPage - page).toLong())
            } else if (page > initialCalendarPage) {
                initialMonthYear = initialMonthYear.plusMonths((page - initialCalendarPage).toLong())
            }

            InfoBox(subject, initialMonthYear.month, initialMonthYear.year, viewModel)

            val showModificationButtons = selectedDate != null &&
                    (selectedDate!!.month == initialMonthYear.month && selectedDate!!.year == initialMonthYear.year)

            ModificationBox(
                viewModel = viewModel,
                subject = subject,
                showModificationButtons = showModificationButtons,
                date = selectedDate,
                showResetButton = pagerState.currentPage != initialCalendarPage,
                onReset = {
                    animationScope.launch {
                        pagerState.animateScrollToPage(initialCalendarPage)
                    }
                }
            )
        }
    }
}

fun LocalDate.addOffset(numberOfMonths: Long): LocalDate {
    return if (numberOfMonths < 0) {
        this.minusMonths(-numberOfMonths)
    } else {
        this.plusMonths(numberOfMonths)
    }
}