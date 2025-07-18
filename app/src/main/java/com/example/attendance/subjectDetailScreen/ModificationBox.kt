package com.example.attendance.subjectDetailScreen

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.attendance.database.subject.SubjectUiModel
import com.example.attendance.ui.theme.absent
import com.example.attendance.ui.theme.present
import com.example.attendance.viewModel.AttendanceViewModel
import java.time.LocalDate

@Composable
fun ModificationBox(
    viewModel: AttendanceViewModel,
    subject: SubjectUiModel,
    showModificationButtons: Boolean,
    date: LocalDate?,
    showResetButton: Boolean,
    onReset: () -> Unit
) {
    var rowWidth by remember { mutableStateOf(0.dp) }
    val density = LocalDensity.current

    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .onGloballyPositioned {
                    rowWidth = with(density) { it.size.width.toDp() }
                },
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val value = subject.attendance[date]
            val maxButtonHeight = 50.dp
            val maxButtonPadding = 10.dp
            val maxButtonWidth = rowWidth / 3 - maxButtonPadding * 2

            // absent button
            ModificationBoxButton(
                visible = date != null && showModificationButtons && value != false,
                text = "Absent",
                maxButtonHeight = maxButtonHeight,
                maxButtonWidth = maxButtonWidth,
                maxButtonPadding = maxButtonPadding,
                backgroundColor = absent
            ) {
                if (date != null) {
                    viewModel.markAbsent(subject, date)
                }
            }

            // present button
            ModificationBoxButton(
                visible = date != null && showModificationButtons && value != true,
                text = "Present",
                maxButtonHeight = maxButtonHeight,
                maxButtonWidth = maxButtonWidth,
                maxButtonPadding = maxButtonPadding,
                backgroundColor = present
            ) {
                if (date != null) {
                    viewModel.markPresent(subject, date)
                }
            }

            // clear button
            ModificationBoxButton(
                visible = date != null && showModificationButtons && value != null,
                text = "Clear",
                maxButtonHeight = maxButtonHeight,
                maxButtonWidth = maxButtonWidth,
                maxButtonPadding = maxButtonPadding
            ) {
                if (date != null) {
                    viewModel.clearAttendance(subject, date)
                }
            }

            // reset button
            ModificationBoxButton(
                visible = showResetButton,
                text = "Reset",
                onClick = onReset,
                maxButtonHeight = maxButtonHeight,
                maxButtonWidth = maxButtonWidth,
                maxButtonPadding = maxButtonPadding
            )
        }
    }
}

@Composable
fun ModificationBoxButton(
    visible: Boolean,
    text: String,
    maxButtonHeight: Dp,
    maxButtonWidth: Dp,
    maxButtonPadding: Dp,
    backgroundColor: Color = Color.Unspecified,
    onClick: () -> Unit
) {
    val transition = updateTransition(
        targetState = visible,
        label = "buttonVisibleTransition"
    )

    val animatedHeight by transition.animateDp (
        transitionSpec = {tween(500)}
    ) {
        if (it) {
            maxButtonHeight
        } else {
            0.dp
        }
    }

    val animatedWidth by transition.animateDp (
        transitionSpec = {tween(500)}
    ) {
        if (it) {
            maxButtonWidth
        } else {
            0.dp
        }
    }

    val animatedPadding by transition.animateDp (
        transitionSpec = {tween(500)}
    ) {
        if (it) {
            maxButtonPadding
        } else {
            0.dp
        }
    }

    if (animatedWidth > 0.dp) {
        Box (
            modifier = Modifier
                .padding(horizontal = animatedPadding)
        ) {
            FilledTonalButton(
                modifier = Modifier
                    .size(animatedWidth, animatedHeight)
                    .animateContentSize(),
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = backgroundColor
                ),
                onClick = onClick
            ) {
                if (animatedWidth > maxButtonWidth - 5.dp) {
                    Text(text = text)
                }
            }
        }
    }
}