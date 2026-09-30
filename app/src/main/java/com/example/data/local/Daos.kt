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

    @Query("SELECT * FROM market_price_cache ORDER BY lastSyncedAt DESC")
    fun getAllCache(): Flow<List<MarketPriceCacheEntity>>

    @Query("SELECT * FROM market_price_cache ORDER BY lastSyncedAt DESC")
    suspend fun getAllCacheSync(): List<MarketPriceCacheEntity>

    @Query("SELECT COUNT(*) FROM market_price_cache")
    fun getCacheCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM market_price_cache")
    suspend fun getCacheCountSync(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(cache: MarketPriceCacheEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(caches: List<MarketPriceCacheEntity>)

    @Query("DELETE FROM market_price_cache WHERE commodity = :commodity")
    suspend fun deleteCache(commodity: String)

    @Query("DELETE FROM market_price_cache")
    suspend fun clearAllCache()
}

@Dao
interface CachedMandiPriceDao {
    @Query("SELECT * FROM cached_mandi_prices WHERE commodity = :commodity ORDER BY modalPrice DESC")
    fun getPricesForCommodity(commodity: String): Flow<List<CachedMandiPriceEntity>>

    @Query("SELECT * FROM cached_mandi_prices WHERE commodity = :commodity ORDER BY modalPrice DESC")
    suspend fun getPricesForCommoditySync(commodity: String): List<CachedMandiPriceEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMandiPrices(prices: List<CachedMandiPriceEntity>)

    @Query("DELETE FROM cached_mandi_prices WHERE commodity = :commodity")
    suspend fun deletePricesForCommodity(commodity: String)

    @Query("DELETE FROM cached_mandi_prices")
    suspend fun clearAll()
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
interface SavedMandiPriceDao {
    @Query("SELECT * FROM saved_mandi_prices ORDER BY savedAt DESC")
    fun getAllSavedPrices(): Flow<List<SavedMandiPriceEntity>>

    @Query("SELECT * FROM saved_mandi_prices WHERE commodity = :commodity ORDER BY savedAt DESC")
    fun getSavedPricesForCommodity(commodity: String): Flow<List<SavedMandiPriceEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM saved_mandi_prices WHERE commodity = :commodity AND mandiName = :mandiName LIMIT 1)")
    fun isPriceSaved(commodity: String, mandiName: String): Flow<Boolean>

    @Query("SELECT EXISTS(SELECT 1 FROM saved_mandi_prices WHERE commodity = :commodity AND mandiName = :mandiName LIMIT 1)")
    suspend fun isPriceSavedSync(commodity: String, mandiName: String): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSavedPrice(price: SavedMandiPriceEntity): Long

    @Query("DELETE FROM saved_mandi_prices WHERE id = :id")
    suspend fun deleteSavedPriceById(id: Long)

    @Query("DELETE FROM saved_mandi_prices WHERE commodity = :commodity AND mandiName = :mandiName")
    suspend fun deleteSavedPrice(commodity: String, mandiName: String)

    @Query("DELETE FROM saved_mandi_prices")
    suspend fun clearAllSavedPrices()
}

@Dao
interface CachedWeatherReportDao {
    @Query("SELECT * FROM cached_weather_reports ORDER BY cachedAt DESC")
    fun getAllReports(): Flow<List<CachedWeatherReportEntity>>

    @Query("SELECT * FROM cached_weather_reports ORDER BY cachedAt DESC LIMIT :limit")
    fun getRecentReports(limit: Int = 10): Flow<List<CachedWeatherReportEntity>>

    @Query("SELECT * FROM cached_weather_reports WHERE locationName = :locationName ORDER BY cachedAt DESC LIMIT 1")
    fun getLatestForLocation(locationName: String): Flow<CachedWeatherReportEntity?>

    @Query("SELECT * FROM cached_weather_reports WHERE locationName = :locationName ORDER BY cachedAt DESC LIMIT 1")
    suspend fun getLatestForLocationSync(locationName: String): CachedWeatherReportEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReport(report: CachedWeatherReportEntity): Long

    @Query("DELETE FROM cached_weather_reports WHERE id = :id")
    suspend fun deleteReportById(id: Long)

    @Query("DELETE FROM cached_weather_reports")
    suspend fun clearAllReports()
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



