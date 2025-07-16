package com.example.attendance.preferences

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import com.example.attendance.ui.theme.AppTheme
import com.example.attendance.ui.theme.NUMBER_OF_THEMES
import com.example.attendance.ui.theme.appThemeValues
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PreferencesRepository(
    context: Context
) {
    private val minAttendanceKey = floatPreferencesKey("minimumRequiredAttendanceRatio")
    private val themeKey = intPreferencesKey("theme")

    private val dataStore = context.dataStore

    fun getMinimumRequiredAttendanceRatio(): Flow<Float> {
        return dataStore.data.map { it[minAttendanceKey] ?: 0.75f }
    }

    suspend fun setMinimumRequiredAttendanceRatio(minAttendance: Float) {
        dataStore.edit {
            it[minAttendanceKey] = minAttendance
        }
    }

    fun getTheme(): Flow<AppTheme> {
        return dataStore.data.map {
            val res = it[themeKey]
            if (res != null && res in 0 until NUMBER_OF_THEMES) {
                appThemeValues[res]
            } else {
                AppTheme.DYNAMIC
            }
        }
    }

    suspend fun setTheme(theme: AppTheme) {
        dataStore.edit {
            it[themeKey] = theme.ordinal
        }
    }
}