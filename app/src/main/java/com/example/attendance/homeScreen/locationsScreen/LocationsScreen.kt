package com.example.attendance.homeScreen.locationsScreen

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import kotlinx.serialization.Serializable

@Serializable
object LocationsScreen

@Composable
fun LocationsScreen(
    paddingValues: PaddingValues
) {
    Text("Locations screen")
}