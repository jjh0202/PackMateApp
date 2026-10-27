package com.example.packmateapp.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "default_items",
    indices = [Index(value = ["name"], unique = true)]
)
data class DefaultItemEntity(
    @PrimaryKey(autoGenerate = true) val defaultId: Long = 0,
    val name: String,
    val category: String, // ItemCategory.name
    @ColumnInfo(defaultValue = "1") val quantity: Int = 1
)
