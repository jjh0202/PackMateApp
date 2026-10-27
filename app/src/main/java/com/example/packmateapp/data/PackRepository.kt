package com.example.packmateapp.data

import android.content.Context
import com.example.packmateapp.data.entity.DefaultItemEntity
import com.example.packmateapp.data.entity.PackingItemEntity
import com.example.packmateapp.data.entity.TripEntity

class PackRepository private constructor(db: AppDatabase) {

    private val tripDao = db.tripDao()
    private val packingItemDao = db.packingItemDao()
    private val defaultItemDao = db.defaultItemDao()

    // 여행
    suspend fun getTripSummaries(): List<TripSummary> =
        tripDao.getAll().map { trip ->
            TripSummary(
                trip = trip,
                totalCount = packingItemDao.countAll(trip.tripId),
                packedCount = packingItemDao.countPacked(trip.tripId)
            )
        }

    suspend fun getTrip(tripId: Long): TripEntity? = tripDao.getById(tripId)

    suspend fun addTrip(trip: TripEntity): Long = tripDao.insert(trip)

    // packing_items는 CASCADE로 함께 삭제된다
    suspend fun deleteTrip(trip: TripEntity) = tripDao.delete(trip)

    // 짐
    suspend fun getItems(tripId: Long): List<PackingItemEntity> = packingItemDao.getByTripId(tripId)

    suspend fun countAll(tripId: Long): Int = packingItemDao.countAll(tripId)

    suspend fun countPacked(tripId: Long): Int = packingItemDao.countPacked(tripId)

    suspend fun addItem(item: PackingItemEntity, saveAsDefault: Boolean) {
        packingItemDao.insert(item)
        if (saveAsDefault) {
            defaultItemDao.insert(
                DefaultItemEntity(name = item.name, category = item.category, quantity = item.quantity)
            )
        }
    }

    suspend fun setPacked(itemId: Long, isPacked: Boolean) = packingItemDao.updatePacked(itemId, isPacked)

    suspend fun deleteItem(item: PackingItemEntity) = packingItemDao.delete(item)

    // 기본 짐을 현재 여행에 복사한다. 이미 같은 이름의 짐은 건너뛰고, 추가된 개수를 돌려준다.
    suspend fun loadDefaultItems(tripId: Long): Int {
        val existingNames = packingItemDao.getByTripId(tripId).map { it.name }.toSet()
        val newItems = defaultItemDao.getAll()
            .filter { it.name !in existingNames }
            .map {
                PackingItemEntity(
                    tripId = tripId,
                    name = it.name,
                    category = it.category,
                    quantity = it.quantity
                )
            }
        if (newItems.isNotEmpty()) packingItemDao.insertAll(newItems)
        return newItems.size
    }

    companion object {
        @Volatile
        private var INSTANCE: PackRepository? = null

        fun getInstance(context: Context): PackRepository =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: PackRepository(AppDatabase.getDatabase(context)).also { INSTANCE = it }
            }
    }
}
