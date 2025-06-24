package com.example.attendance.ui.theme

const val NUMBER_OF_THEMES = 4

val appThemeValues = AppTheme.entries.toTypedArray()

enum class AppTheme {
    LIGHT,
    DARK,
    DYNAMIC,
    SYSTEM_DEFAULT
}