package com.example.attendance.Preferences

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PreferencesRepository(
    context: Context
) {
    private val minAttendanceKey = floatPreferencesKey("minimumRequiredAttendanceRatio")
    private val dataStore = context.dataStore

    fun getMinimumRequiredAttendanceRatio(): Flow<Float> {
        return dataStore.data.map { it[minAttendanceKey] ?: 0.75f }
    }

    suspend fun setMinimumRequiredAttendanceRatio(minAttendance: Float) {
        dataStore.edit {
            it[minAttendanceKey] = minAttendance
        }
    }
}