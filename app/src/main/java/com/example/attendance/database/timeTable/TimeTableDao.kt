package com.example.attendance.database.timeTable

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface TimeTableDao {
    @Query("SELECT * FROM timeTable WHERE day = :day")
    suspend fun getTimeTableForDay(day: Int): List<TimeTable>

    @Insert
    suspend fun insertTimeTable(timeTable: TimeTable): Long

    @Update
    suspend fun updateTimeTable(timeTable: TimeTable)

    @Delete
    suspend fun deleteTimeTable(timeTable: TimeTable)
}