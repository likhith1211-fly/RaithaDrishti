package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        DiagnosisEntity::class,
        ProfileEntity::class,
        FarmerAccountEntity::class,
        MarketPriceCacheEntity::class,
        WeatherCacheEntity::class,
        PriceAlertEntity::class
    ],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun diagnosisDao(): DiagnosisDao
    abstract fun profileDao(): ProfileDao
    abstract fun farmerAccountDao(): FarmerAccountDao
    abstract fun marketPriceCacheDao(): MarketPriceCacheDao
    abstract fun weatherCacheDao(): WeatherCacheDao
    abstract fun priceAlertDao(): PriceAlertDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "raitha_drishti_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
