package com.example.data.repository

import android.graphics.Bitmap
import com.example.data.local.CacheConverter
import com.example.data.local.CachedMandiPriceDao
import com.example.data.local.CachedMandiPriceEntity
import com.example.data.local.DiagnosisDao
import com.example.data.local.DiagnosisEntity
import com.example.data.local.FarmerAccountDao
import com.example.data.local.FarmerAccountEntity
import com.example.data.local.MarketPriceCacheDao
import com.example.data.local.MarketPriceCacheEntity
import com.example.data.local.PriceAlertDao
import com.example.data.local.PriceAlertEntity
import com.example.data.local.ProfileDao
import com.example.data.local.ProfileEntity
import com.example.data.local.WeatherCacheDao
import com.example.data.local.WeatherCacheEntity
import com.example.data.model.AppLanguage
import com.example.data.model.CropDiagnosisResult
import com.example.data.model.GeminiWeatherAdvisory
import com.example.data.model.MarketAnalytics
import com.example.data.model.WeeklyMarketAnalysis
import com.example.data.model.WeatherData
import com.example.data.remote.ApmcKarnatakaMarketService
import com.example.data.remote.GeminiDiagnosisService
import com.example.data.remote.WeatherApiService
import kotlinx.coroutines.flow.Flow

class RaithaDrishtiRepository(
    private val diagnosisDao: DiagnosisDao,
    private val profileDao: ProfileDao,
    private val farmerAccountDao: FarmerAccountDao,
    private val marketPriceCacheDao: MarketPriceCacheDao,
    private val cachedMandiPriceDao: CachedMandiPriceDao,
    private val weatherCacheDao: WeatherCacheDao,
    private val priceAlertDao: PriceAlertDao,
    private val weatherService: WeatherApiService,
    private val geminiService: GeminiDiagnosisService,
    private val marketService: ApmcKarnatakaMarketService
) {
    // Room Flow
    val allDiagnoses: Flow<List<DiagnosisEntity>> = diagnosisDao.getAllDiagnoses()
    val farmerProfile: Flow<ProfileEntity?> = profileDao.getProfile()
    val allFarmerAccounts: Flow<List<FarmerAccountEntity>> = farmerAccountDao.getAllAccounts()
    val allPriceAlerts: Flow<List<PriceAlertEntity>> = priceAlertDao.getAllAlerts()
    val cachedCommoditiesCount: Flow<Int> = marketPriceCacheDao.getCacheCount()

    fun observeMarketCache(commodity: String): Flow<MarketPriceCacheEntity?> {
        return marketPriceCacheDao.getCache(commodity)
    }

    fun observeMandiPrices(commodity: String): Flow<List<CachedMandiPriceEntity>> {
        return cachedMandiPriceDao.getPricesForCommodity(commodity)
    }

    fun getDiagnosesForFarmerId(farmerId: String): Flow<List<DiagnosisEntity>> {
        return diagnosisDao.getDiagnosesForFarmerId(farmerId)
    }

    fun getDiagnosesForFarmerDetails(name: String, village: String, birthYear: Int): Flow<List<DiagnosisEntity>> {
        return diagnosisDao.getDiagnosesForFarmerDetails(name, village, birthYear)
    }

    fun getDiagnosesForFarmer(phone: String): Flow<List<DiagnosisEntity>> {
        return diagnosisDao.getDiagnosesForFarmer(phone)
    }

    suspend fun getFarmerAccountById(farmerId: String): FarmerAccountEntity? {
        return farmerAccountDao.getAccountById(farmerId)
    }

    suspend fun getFarmerAccountByDetails(name: String, village: String, birthYear: Int): FarmerAccountEntity? {
        return farmerAccountDao.getAccountByDetails(name, village, birthYear)
    }

    suspend fun getFarmerAccount(phone: String): FarmerAccountEntity? {
        return farmerAccountDao.getAccountByPhone(phone)
    }

    suspend fun saveFarmerAccount(account: FarmerAccountEntity) {
        farmerAccountDao.insertOrUpdateAccount(account)
    }

    suspend fun saveDiagnosis(
        result: CropDiagnosisResult,
        farmerNotes: String,
        weatherContext: String,
        imageUri: String? = null,
        farmerId: String = "unregistered_farmer",
        farmerName: String = "",
        village: String = "",
        birthYear: Int = 0,
        farmerPhone: String = ""
    ): Long {
        val entity = DiagnosisEntity.fromDomain(
            domain = result,
            farmerNotes = farmerNotes,
            weatherContext = weatherContext,
            imageUri = imageUri,
            farmerId = farmerId,
            farmerName = farmerName,
            village = village,
            birthYear = birthYear,
            farmerPhone = farmerPhone
        )
        return diagnosisDao.insertDiagnosis(entity)
    }

    suspend fun deleteDiagnosis(id: Long) {
        diagnosisDao.deleteDiagnosisById(id)
    }

    suspend fun saveProfile(profile: ProfileEntity) {
        profileDao.insertOrUpdateProfile(profile)
    }

    // --- Weather with Room Offline Caching ---
    suspend fun fetchWeather(lat: Double, lon: Double, locationName: String): WeatherData {
        return try {
            val live = weatherService.fetchLiveWeather(lat, lon, locationName)
            // Cache to Room database
            val existing = weatherCacheDao.getWeatherCacheSync(locationName)
            val advisoryJson = existing?.weatherAdvisoryJson ?: ""
            weatherCacheDao.insertOrUpdate(
                WeatherCacheEntity(
                    locationKey = locationName,
                    weatherDataJson = CacheConverter.weatherDataToJson(live),
                    weatherAdvisoryJson = advisoryJson,
                    lastSyncedAt = System.currentTimeMillis()
                )
            )
            live
        } catch (e: Exception) {
            // Offline fallback: load from Room
            val cached = weatherCacheDao.getWeatherCacheSync(locationName)
            if (cached != null) {
                CacheConverter.weatherDataFromJson(cached.weatherDataJson) ?: WeatherData(
                    location = locationName,
                    latitude = lat,
                    longitude = lon,
                    temperature = 26.5,
                    humidity = 68.0,
                    precipitation = 0.0,
                    windSpeed = 9.0,
                    weatherCode = 1,
                    agriAdvice = "Offline cached advisory for $locationName"
                )
            } else {
                WeatherData(
                    location = locationName,
                    latitude = lat,
                    longitude = lon,
                    temperature = 26.5,
                    humidity = 68.0,
                    precipitation = 0.0,
                    windSpeed = 9.0,
                    weatherCode = 1,
                    agriAdvice = "Offline default advisory for $locationName"
                )
            }
        }
    }

    suspend fun diagnoseCrop(
        cropName: String,
        farmerNotes: String,
        weatherData: WeatherData?,
        imageBitmap: Bitmap?
    ): CropDiagnosisResult {
        return geminiService.diagnoseCrop(cropName, farmerNotes, weatherData, imageBitmap)
    }

    suspend fun queryVoiceAssistant(
        userQuery: String,
        language: AppLanguage,
        weatherContext: String?
    ): String {
        return geminiService.queryVoiceAssistant(userQuery, language, weatherContext)
    }

    suspend fun generateWeatherAdvisory(
        weatherData: WeatherData,
        language: AppLanguage
    ): GeminiWeatherAdvisory {
        val advisory = geminiService.generateGeminiWeatherAdvisory(weatherData, language)
        // Update advisory cache in Room
        try {
            val existing = weatherCacheDao.getWeatherCacheSync(weatherData.location)
            weatherCacheDao.insertOrUpdate(
                WeatherCacheEntity(
                    locationKey = weatherData.location,
                    weatherDataJson = existing?.weatherDataJson ?: CacheConverter.weatherDataToJson(weatherData),
                    weatherAdvisoryJson = CacheConverter.weatherAdvisoryToJson(advisory),
                    lastSyncedAt = System.currentTimeMillis()
                )
            )
        } catch (e: Exception) {
            // Ignore cache write error
        }
        return advisory
    }

    suspend fun getCachedWeatherAdvisory(locationName: String): GeminiWeatherAdvisory? {
        val cached = weatherCacheDao.getWeatherCacheSync(locationName)
        return cached?.weatherAdvisoryJson?.let { CacheConverter.weatherAdvisoryFromJson(it) }
    }

    // --- Market Analytics with Room Offline Caching ---

    suspend fun seedAllMarketPricesIfEmpty() {
        try {
            val count = marketPriceCacheDao.getCacheCountSync()
            if (count == 0) {
                val commodities = marketService.getSupportedCommodities()
                val cacheEntities = mutableListOf<MarketPriceCacheEntity>()
                val mandiEntities = mutableListOf<CachedMandiPriceEntity>()

                for (commodity in commodities) {
                    val analytics = marketService.getMarketAnalytics(commodity)
                    val weekly = marketService.getWeeklyMarketAnalysis(commodity)
                    cacheEntities.add(
                        MarketPriceCacheEntity(
                            commodity = commodity,
                            marketAnalyticsJson = CacheConverter.marketAnalyticsToJson(analytics),
                            weeklyAnalysisJson = CacheConverter.weeklyAnalysisToJson(weekly),
                            lastSyncedAt = System.currentTimeMillis(),
                            syncedDateString = weekly.reportDate,
                            isOfflineCached = true,
                            sourceMandi = "APMC Karnataka"
                        )
                    )
                    for (mandi in analytics.mandiPrices) {
                        mandiEntities.add(
                            CachedMandiPriceEntity(
                                commodity = commodity,
                                mandiName = mandi.mandiName,
                                modalPrice = mandi.modalPrice,
                                minPrice = mandi.minPrice,
                                maxPrice = mandi.maxPrice,
                                dailyChangePercent = mandi.dailyChangePercent,
                                reportDate = weekly.reportDate,
                                cachedAt = System.currentTimeMillis()
                            )
                        )
                    }
                }
                marketPriceCacheDao.insertAll(cacheEntities)
                cachedMandiPriceDao.insertMandiPrices(mandiEntities)
            }
        } catch (e: Exception) {
            // Seeding exception caught safely
        }
    }

    suspend fun getCachedMarketAnalytics(commodity: String): MarketAnalytics? {
        val cached = marketPriceCacheDao.getCacheSync(commodity)
        return cached?.let { CacheConverter.marketAnalyticsFromJson(it.marketAnalyticsJson) }
    }

    suspend fun getCachedWeeklyAnalysis(commodity: String): WeeklyMarketAnalysis? {
        val cached = marketPriceCacheDao.getCacheSync(commodity)
        return cached?.let { CacheConverter.weeklyAnalysisFromJson(it.weeklyAnalysisJson) }
    }

    suspend fun refreshMarketPriceData(commodity: String): Result<MarketAnalytics> {
        return try {
            val live = marketService.getMarketAnalytics(commodity)
            val weekly = marketService.getWeeklyMarketAnalysis(commodity)

            // Cache aggregate data to Room DB
            marketPriceCacheDao.insertOrUpdate(
                MarketPriceCacheEntity(
                    commodity = commodity,
                    marketAnalyticsJson = CacheConverter.marketAnalyticsToJson(live),
                    weeklyAnalysisJson = CacheConverter.weeklyAnalysisToJson(weekly),
                    lastSyncedAt = System.currentTimeMillis(),
                    syncedDateString = weekly.reportDate,
                    isOfflineCached = false,
                    sourceMandi = "APMC Karnataka"
                )
            )

            // Cache individual mandi records to Room DB
            cachedMandiPriceDao.deletePricesForCommodity(commodity)
            val mandiEntities = live.mandiPrices.map { mandi ->
                CachedMandiPriceEntity(
                    commodity = commodity,
                    mandiName = mandi.mandiName,
                    modalPrice = mandi.modalPrice,
                    minPrice = mandi.minPrice,
                    maxPrice = mandi.maxPrice,
                    dailyChangePercent = mandi.dailyChangePercent,
                    reportDate = weekly.reportDate,
                    cachedAt = System.currentTimeMillis()
                )
            }
            cachedMandiPriceDao.insertMandiPrices(mandiEntities)

            Result.success(live)
        } catch (e: Exception) {
            val cached = getCachedMarketAnalytics(commodity)
            if (cached != null) {
                Result.failure(e)
            } else {
                val fallback = marketService.getMarketAnalytics(commodity)
                Result.success(fallback)
            }
        }
    }

    suspend fun getMarketAnalytics(commodity: String): MarketAnalytics {
        // Cache-first: try reading from Room DB first for instant response
        val cached = getCachedMarketAnalytics(commodity)
        if (cached != null) {
            return cached
        }
        // If not cached, fetch and populate Room
        return try {
            val live = marketService.getMarketAnalytics(commodity)
            val weekly = marketService.getWeeklyMarketAnalysis(commodity)
            marketPriceCacheDao.insertOrUpdate(
                MarketPriceCacheEntity(
                    commodity = commodity,
                    marketAnalyticsJson = CacheConverter.marketAnalyticsToJson(live),
                    weeklyAnalysisJson = CacheConverter.weeklyAnalysisToJson(weekly),
                    lastSyncedAt = System.currentTimeMillis(),
                    syncedDateString = weekly.reportDate,
                    isOfflineCached = false
                )
            )
            live
        } catch (e: Exception) {
            marketService.getMarketAnalytics(commodity)
        }
    }

    suspend fun getWeeklyMarketAnalysis(commodity: String): WeeklyMarketAnalysis {
        val cached = getCachedWeeklyAnalysis(commodity)
        if (cached != null) {
            return cached
        }
        return try {
            val weekly = marketService.getWeeklyMarketAnalysis(commodity)
            val live = marketService.getMarketAnalytics(commodity)
            marketPriceCacheDao.insertOrUpdate(
                MarketPriceCacheEntity(
                    commodity = commodity,
                    marketAnalyticsJson = CacheConverter.marketAnalyticsToJson(live),
                    weeklyAnalysisJson = CacheConverter.weeklyAnalysisToJson(weekly),
                    lastSyncedAt = System.currentTimeMillis(),
                    syncedDateString = weekly.reportDate,
                    isOfflineCached = false
                )
            )
            weekly
        } catch (e: Exception) {
            marketService.getWeeklyMarketAnalysis(commodity)
        }
    }

    suspend fun getLastPriceSyncTime(commodity: String): Long? {
        return marketPriceCacheDao.getCacheSync(commodity)?.lastSyncedAt
    }

    fun getSupportedCommodities(): List<String> {
        return marketService.getSupportedCommodities()
    }

    // --- Price Threshold Alerts ---
    suspend fun getActivePriceAlerts(): List<PriceAlertEntity> {
        return priceAlertDao.getActiveAlerts()
    }

    suspend fun savePriceAlert(alert: PriceAlertEntity): Long {
        return priceAlertDao.insertAlert(alert)
    }

    suspend fun updatePriceAlert(alert: PriceAlertEntity) {
        priceAlertDao.updateAlert(alert)
    }

    suspend fun deletePriceAlert(id: Long) {
        priceAlertDao.deleteAlertById(id)
    }
}

