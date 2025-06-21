package com.example.attendance.subjectDetailScreen

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.sp
import com.example.attendance.database.Subject
import com.example.attendance.ui.theme.mediumRoundedCornerShape
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
    subject: Subject,
    viewModel: AttendanceViewModel,
    onBackPress: () -> Unit
) {
    var initialMonthYear = LocalDate.of(LocalDate.now().year, LocalDate.now().month, 1)
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
    var currentMonthYear = remember (pagerState.currentPage) {
        initialMonthYear.addOffset(pagerState.currentPage - initialCalendarPage.toLong())
    }

    Scaffold (
        topBar = {SubjectDetailScreenTopAppBar(subject, onBackPress)}
    ) { paddingValues->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "${currentMonthYear.month.name.substring(0, 3)} ${currentMonthYear.year}",
                fontSize = MaterialTheme.typography.titleLarge.fontSize
            )

            HorizontalPager(
                modifier = Modifier
                    .animateContentSize()
                    .clip(mediumRoundedCornerShape),
                verticalAlignment = Alignment.Top,
                state = pagerState,
                key = { pageIndex ->
                    val monthOffset = pageIndex.toLong() - initialCalendarPage.toLong()
                    initialMonthYear.plusMonths(monthOffset).toString()
                }
            ) { page ->
                var pageMonthYear = initialMonthYear.addOffset((page - initialCalendarPage).toLong())

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