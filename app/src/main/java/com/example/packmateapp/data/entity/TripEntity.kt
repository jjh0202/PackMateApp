package com.example.packmateapp.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "trips")
data class TripEntity(
    @PrimaryKey(autoGenerate = true) val tripId: Long = 0,
    val name: String,
    val place: String,
    val startDate: String, // yyyy-MM-dd
    val days: Int,
    val emoji: String
)
