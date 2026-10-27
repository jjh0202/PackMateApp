package com.example.packmateapp.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.packmateapp.data.entity.TripEntity

@Dao
interface TripDao {
    @Insert
    suspend fun insert(trip: TripEntity): Long

    @Query("SELECT * FROM trips ORDER BY startDate ASC, tripId ASC")
    suspend fun getAll(): List<TripEntity>

    @Query("SELECT * FROM trips WHERE tripId = :tripId")
    suspend fun getById(tripId: Long): TripEntity?

    @Delete
    suspend fun delete(trip: TripEntity)
}
