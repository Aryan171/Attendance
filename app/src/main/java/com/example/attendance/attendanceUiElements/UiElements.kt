package com.example.attendance.attendanceUiElements

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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.example.attendance.ui.theme.absent
import com.example.attendance.ui.theme.present
import com.example.attendance.ui.theme.roundedCornerShape

@Composable
fun ButtonColumn(
    hidePopupOnButtonPress: Boolean = true,
    hidePopup: () -> Unit,
    width: Dp,
    iconList: List<ImageVector>? = null,
    buttonTextList: List<String>,
    onClickList: List<() -> Unit>
) {
    Column (
        modifier = Modifier
            .border(width = Dp.Hairline, color = Color.Black, shape = roundedCornerShape)
            .width(width)
            .clip(roundedCornerShape)
            .background(Color.White.copy(alpha = 0.7f), roundedCornerShape)
    ) {
        for (i in 0 until buttonTextList.size) {
            InternalCustomButton(
                text = buttonTextList[i],
                imageVector = iconList?.get(i)
            ) {
                onClickList[i]()
                if (hidePopupOnButtonPress) {
                    hidePopup()
                }
            }
        }
    }
}

@Composable
fun InternalCustomButton(
    text: String,
    imageVector: ImageVector? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .clickable {
                onClick()
            }
            .border(width = Dp.Hairline, color = Color.Black, shape = RectangleShape)
            .fillMaxWidth()
            .background(Color.Transparent)
            .padding(15.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (imageVector != null) {
            Icon(
                imageVector = imageVector,
                contentDescription = text
            )
        }

        Text(
            text = text,
            modifier = Modifier.background(Color.Transparent)
        )
    }
}

@Composable
fun CircularProgressIndicator(
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