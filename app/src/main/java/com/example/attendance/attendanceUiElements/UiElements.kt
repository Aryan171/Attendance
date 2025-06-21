package com.example.attendance.attendanceUiElements

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateInt
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.example.attendance.ui.theme.absent
import com.example.attendance.ui.theme.present

@Composable
fun CircularProgressIndicator(
    modifier: Modifier = Modifier,
    bottomText: String?,
    intPair: Pair<Int, Int>? = null,
    percentageFontSize: TextUnit,
    strokeWidth: Dp,
    progress: Float,
    color: Color,
    trackColor: Color
) {
    val transition = updateTransition(
        targetState = progress,
        label = "progress transition"
    )

    val animatedProgress by transition.animateFloat (
        transitionSpec = { tween(durationMillis = 500) }
    ) { it }

    val animatedPercentage by transition.animateInt (
        transitionSpec = { tween(durationMillis = 500) }
    ) { (it * 100f).toInt() }

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
                color = color,
                trackColor = trackColor,
                strokeWidth = strokeWidth,
                strokeCap = StrokeCap.Butt,
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
                            color = present
                        )

                        Text(
                            text = "$animatedSecondInt",
                            fontSize = percentageFontSize,
                            color = absent
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