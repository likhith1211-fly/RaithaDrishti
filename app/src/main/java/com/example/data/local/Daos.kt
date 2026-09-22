package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DiagnosisDao {
    @Query("SELECT * FROM diagnoses ORDER BY createdAt DESC")
    fun getAllDiagnoses(): Flow<List<DiagnosisEntity>>

    @Query("SELECT * FROM diagnoses WHERE farmerId = :farmerId ORDER BY createdAt DESC")
    fun getDiagnosesForFarmerId(farmerId: String): Flow<List<DiagnosisEntity>>

    @Query("SELECT * FROM diagnoses WHERE farmerName = :name AND village = :village AND birthYear = :birthYear ORDER BY createdAt DESC")
    fun getDiagnosesForFarmerDetails(name: String, village: String, birthYear: Int): Flow<List<DiagnosisEntity>>

    @Query("SELECT * FROM diagnoses WHERE farmerPhone = :phone ORDER BY createdAt DESC")
    fun getDiagnosesForFarmer(phone: String): Flow<List<DiagnosisEntity>>

    @Query("SELECT * FROM diagnoses WHERE id = :id")
    suspend fun getDiagnosisById(id: Long): DiagnosisEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDiagnosis(diagnosis: DiagnosisEntity): Long

    @Query("DELETE FROM diagnoses WHERE id = :id")
    suspend fun deleteDiagnosisById(id: Long)

    @Query("DELETE FROM diagnoses")
    suspend fun clearAll()
}

@Dao
interface FarmerAccountDao {
    @Query("SELECT * FROM farmer_accounts ORDER BY lastSignedIn DESC")
    fun getAllAccounts(): Flow<List<FarmerAccountEntity>>

    @Query("SELECT * FROM farmer_accounts WHERE farmerId = :farmerId LIMIT 1")
    suspend fun getAccountById(farmerId: String): FarmerAccountEntity?

    @Query("SELECT * FROM farmer_accounts WHERE fullName = :name AND village = :village AND birthYear = :birthYear LIMIT 1")
    suspend fun getAccountByDetails(name: String, village: String, birthYear: Int): FarmerAccountEntity?

    @Query("SELECT * FROM farmer_accounts WHERE phoneNumber = :phone LIMIT 1")
    suspend fun getAccountByPhone(phone: String): FarmerAccountEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateAccount(account: FarmerAccountEntity)
}

@Dao
interface ProfileDao {
    @Query("SELECT * FROM profiles WHERE id = 1 LIMIT 1")
    fun getProfile(): Flow<ProfileEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProfile(profile: ProfileEntity)
}

@Dao
interface MarketPriceCacheDao {
    @Query("SELECT * FROM market_price_cache WHERE commodity = :commodity LIMIT 1")
    fun getCache(commodity: String): Flow<MarketPriceCacheEntity?>

    @Query("SELECT * FROM market_price_cache WHERE commodity = :commodity LIMIT 1")
    suspend fun getCacheSync(commodity: String): MarketPriceCacheEntity?

    @Query("SELECT * FROM market_price_cache")
    fun getAllCache(): Flow<List<MarketPriceCacheEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(cache: MarketPriceCacheEntity)

    @Query("DELETE FROM market_price_cache")
    suspend fun clearAllCache()
}

@Dao
interface WeatherCacheDao {
    @Query("SELECT * FROM weather_cache WHERE locationKey = :locationKey LIMIT 1")
    fun getWeatherCache(locationKey: String): Flow<WeatherCacheEntity?>

    @Query("SELECT * FROM weather_cache WHERE locationKey = :locationKey LIMIT 1")
    suspend fun getWeatherCacheSync(locationKey: String): WeatherCacheEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(cache: WeatherCacheEntity)
}

@Dao
interface PriceAlertDao {
    @Query("SELECT * FROM price_alerts ORDER BY createdAt DESC")
    fun getAllAlerts(): Flow<List<PriceAlertEntity>>

    @Query("SELECT * FROM price_alerts WHERE isEnabled = 1")
    suspend fun getActiveAlerts(): List<PriceAlertEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlert(alert: PriceAlertEntity): Long

    @androidx.room.Update
    suspend fun updateAlert(alert: PriceAlertEntity)

    @Query("DELETE FROM price_alerts WHERE id = :id")
    suspend fun deleteAlertById(id: Long)
}


