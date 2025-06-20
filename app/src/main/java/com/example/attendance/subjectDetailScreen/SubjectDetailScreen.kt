package com.example.attendance.subjectDetailScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.attendance.database.Subject
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
        topBar = {SubjectDetailScreenTopAppBar(subject, onBackPress)},
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


