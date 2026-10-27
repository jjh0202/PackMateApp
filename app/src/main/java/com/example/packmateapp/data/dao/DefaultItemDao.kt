package com.example.packmateapp.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.packmateapp.data.entity.DefaultItemEntity

@Dao
interface DefaultItemDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(item: DefaultItemEntity): Long

    @Query("SELECT * FROM default_items ORDER BY defaultId ASC")
    suspend fun getAll(): List<DefaultItemEntity>
}
