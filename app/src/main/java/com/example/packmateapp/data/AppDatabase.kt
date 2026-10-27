package com.example.packmateapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.packmateapp.data.dao.DefaultItemDao
import com.example.packmateapp.data.dao.PackingItemDao
import com.example.packmateapp.data.dao.TripDao
import com.example.packmateapp.data.entity.DefaultItemEntity
import com.example.packmateapp.data.entity.PackingItemEntity
import com.example.packmateapp.data.entity.TripEntity
import com.example.packmateapp.model.ItemCategory

@Database(
    entities = [TripEntity::class, PackingItemEntity::class, DefaultItemEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun tripDao(): TripDao
    abstract fun packingItemDao(): PackingItemDao
    abstract fun defaultItemDao(): DefaultItemDao

    companion object {
        private const val DB_NAME = "packmate_database"

        // DB를 처음 만들 때 넣어 두는 자주 챙기는 기본 짐
        private val INITIAL_DEFAULT_ITEMS = listOf(
            "휴대폰 충전기" to ItemCategory.ELECTRONICS,
            "보조배터리" to ItemCategory.ELECTRONICS,
            "칫솔·치약" to ItemCategory.TOILETRY,
            "신분증" to ItemCategory.DOCUMENT,
            "상비약" to ItemCategory.MEDICINE,
            "속옷" to ItemCategory.CLOTHES
        )

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DB_NAME
                )
                    .addCallback(PrepopulateCallback)
                    .build()
                    .also { INSTANCE = it }
            }

        private object PrepopulateCallback : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INITIAL_DEFAULT_ITEMS.forEach { (name, category) ->
                    db.execSQL(
                        "INSERT OR IGNORE INTO default_items (name, category, quantity) VALUES (?, ?, 1)",
                        arrayOf<Any>(name, category.name)
                    )
                }
            }
        }
    }
}
