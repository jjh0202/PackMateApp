package com.example.packmateapp.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.packmateapp.data.entity.PackingItemEntity

@Dao
interface PackingItemDao {
    @Insert
    suspend fun insert(item: PackingItemEntity): Long

    @Insert
    suspend fun insertAll(items: List<PackingItemEntity>)

    // 챙기지 않은 짐 먼저, 챙긴 짐은 아래쪽
    @Query("SELECT * FROM packing_items WHERE tripId = :tripId ORDER BY isPacked ASC, itemId ASC")
    suspend fun getByTripId(tripId: Long): List<PackingItemEntity>

    @Query("UPDATE packing_items SET isPacked = :isPacked WHERE itemId = :itemId")
    suspend fun updatePacked(itemId: Long, isPacked: Boolean)

    @Delete
    suspend fun delete(item: PackingItemEntity)

    @Query("SELECT COUNT(*) FROM packing_items WHERE tripId = :tripId")
    suspend fun countAll(tripId: Long): Int

    @Query("SELECT COUNT(*) FROM packing_items WHERE tripId = :tripId AND isPacked = 1")
    suspend fun countPacked(tripId: Long): Int
}
