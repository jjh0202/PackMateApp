package com.example.packmateapp.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "packing_items",
    foreignKeys = [
        ForeignKey(
            entity = TripEntity::class,
            parentColumns = ["tripId"],
            childColumns = ["tripId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("tripId")]
)
data class PackingItemEntity(
    @PrimaryKey(autoGenerate = true) val itemId: Long = 0,
    val tripId: Long,
    val name: String,
    val category: String, // ItemCategory.name
    @ColumnInfo(defaultValue = "1") val quantity: Int = 1,
    @ColumnInfo(defaultValue = "0") val isPacked: Boolean = false
)
