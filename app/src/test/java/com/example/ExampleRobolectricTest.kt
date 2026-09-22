package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("RaithaDrishti", appName)
  }

  @Test
  fun `verify APMC market service generates 14-day trends`() {
    val service = com.example.data.remote.ApmcKarnatakaMarketService()
    val analytics = service.getMarketAnalytics("Tomato")
    assertEquals("Tomato", analytics.commodity)
    assertEquals(14, analytics.trendHistory.size)
    assertEquals(3, analytics.mandiPrices.size)
  }

  @Test
  fun `verify fertilizer and chemical recommendations catalog includes maize white leaf remedies`() {
    val catalog = com.example.data.model.RECOMMENDED_FERTILIZERS
    assert(catalog.isNotEmpty())
    val zincCure = catalog.firstOrNull { it.curesWhiteLeaves }
    assert(zincCure != null)
    assertEquals("zinc_sulphate", zincCure?.id)

    val fastGrowBoosters = catalog.filter { it.isFastGrowthBooster }
    assert(fastGrowBoosters.size >= 3)

    val wantedChemicals = catalog.filter { it.category == "Wanted Crop Chemical" }
    assert(wantedChemicals.isNotEmpty())
  }

  @Test
  fun `verify three supported languages are Kannada Hindi and English`() {
    val languages = com.example.data.model.AppLanguage.values()
    assertEquals(3, languages.size)
    val codes = languages.map { it.name }
    assert(codes.contains("KANNADA"))
    assert(codes.contains("HINDI"))
    assert(codes.contains("ENGLISH"))
  }

  @Test
  fun `verify persistence under farmer name village and birth year`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val storageManager = com.example.data.local.FarmerStorageManager(context)

    val farmerName = "Mallikarjun Patil"
    val village = "Dharwad Rural"
    val birthYear = 1974
    val phone = "9448098765"

    val savedData = storageManager.saveFarmer(
        name = farmerName,
        village = village,
        birthYear = birthYear,
        phone = phone,
        district = "Dharwad",
        crops = "Cotton, Maize",
        acres = 6.5
    )

    assertEquals(farmerName, savedData.fullName)
    assertEquals(village, savedData.village)
    assertEquals(birthYear, savedData.birthYear)
    assertEquals(phone, savedData.phoneNumber)

    val retrieved = storageManager.getSavedFarmer()
    assertEquals(farmerName, retrieved.fullName)
    assertEquals(village, retrieved.village)
    assertEquals(birthYear, retrieved.birthYear)
    assertEquals(phone, retrieved.phoneNumber)

    val exportJson = storageManager.exportFarmerJson()
    assert(exportJson.contains("Mallikarjun Patil"))
    assert(exportJson.contains("Dharwad Rural"))
    assert(exportJson.contains("1974"))
  }

  @Test
  fun `verify karnataka mandis in agri map screen are valid coordinates`() {
    val mandis = com.example.ui.screens.KARNATAKA_MANDIS
    assert(mandis.isNotEmpty())
    assert(mandis.size >= 8)

    for (mandi in mandis) {
      assert(mandi.latitude in 11.0..19.0) { "Latitude ${mandi.latitude} out of Karnataka bounds for ${mandi.nameEn}" }
      assert(mandi.longitude in 73.5..79.0) { "Longitude ${mandi.longitude} out of Karnataka bounds for ${mandi.nameEn}" }
      assert(mandi.livePricePerQtl > 0)
    }
  }

  @Test
  fun `verify pathology report share formatting across regional languages`() {
    val sampleDiagnosis = com.example.data.model.CropDiagnosisResult(
        diagnosis = "Zinc Deficiency (White Bud / Patchy White Leaves)",
        severity = "Moderate",
        confidence = 94,
        summary = "White bleaching between leaf midrib and margin",
        immediateActions = listOf("Foliar spray Zinc Sulphate 0.5%"),
        weeds = emptyList(),
        selectiveHerbicides = emptyList(),
        organicFertilizers = listOf("Panchagavya spray"),
        chemicalFertilizers = listOf("Zinc Sulphate Heptahydrate 21%"),
        safety = listOf("Wear protective gloves"),
        cropName = "Maize / ಜೋಳ",
        timestamp = System.currentTimeMillis()
    )

    // Test Kannada format
    val knText = com.example.util.ShareManager.buildCropDiagnosisShareText(
        sampleDiagnosis,
        com.example.data.model.AppLanguage.KANNADA,
        "21 Sep 2026"
    )
    assert(knText.contains("ರೈತ ದೃಷ್ಟಿ"))
    assert(knText.contains("Maize / ಜೋಳ"))
    assert(knText.contains("Zinc Deficiency"))

    // Test Hindi format
    val hiText = com.example.util.ShareManager.buildCropDiagnosisShareText(
        sampleDiagnosis,
        com.example.data.model.AppLanguage.HINDI,
        "21 Sep 2026"
    )
    assert(hiText.contains("रैत दृष्टि"))

    // Test English format
    val enText = com.example.util.ShareManager.buildCropDiagnosisShareText(
        sampleDiagnosis,
        com.example.data.model.AppLanguage.ENGLISH,
        "21 Sep 2026"
    )
    assert(enText.contains("Raitha Drishti"))
    assert(enText.contains("Crop Pathology Diagnosis Report"))
  }

  @Test
  fun `verify seasonal growth tips engine adapts advice based on Karnataka weather`() {
    val dryWeather = com.example.data.model.WeatherData(
        location = "Dharwad",
        latitude = 15.4589,
        longitude = 75.0078,
        temperature = 36.5,
        humidity = 35.0,
        precipitation = 0.0,
        windSpeed = 12.0,
        weatherCode = 0,
        agriAdvice = "Dry and hot conditions"
    )

    val maizeDryTip = com.example.util.KarnatakaGrowthTipsEngine.generateTipsForCrop(
        cropId = "maize",
        weather = dryWeather,
        language = com.example.data.model.AppLanguage.KANNADA
    )
    assert(maizeDryTip.weatherAdaptedAdviceKn.isNotBlank())
    assert(maizeDryTip.weatherRiskSeverity in listOf("High", "Medium", "Low"))
    assertEquals("Maize (ಮೆಕ್ಕೆಜೋಳ)", maizeDryTip.cropNameEn)

    val humidWeather = com.example.data.model.WeatherData(
        location = "Shivamogga",
        latitude = 13.9299,
        longitude = 75.5681,
        temperature = 26.0,
        humidity = 88.0,
        precipitation = 14.0,
        windSpeed = 18.0,
        weatherCode = 61,
        agriAdvice = "High humidity and continuous rain"
    )

    val arecanutRainTip = com.example.util.KarnatakaGrowthTipsEngine.generateTipsForCrop(
        cropId = "arecanut",
        weather = humidWeather,
        language = com.example.data.model.AppLanguage.KANNADA
    )
    assert(arecanutRainTip.pestPreventionKn.contains("ಕೊಳೆರೋಗ") || arecanutRainTip.weatherAdaptedAdviceKn.contains("ಬೋರ್ಡೋ") || arecanutRainTip.pestPreventionEn.contains("Koleroga"))
  }

  @Test
  fun `verify local Room database stores market cache and weather data`() = kotlinx.coroutines.runBlocking {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val db = androidx.room.Room.inMemoryDatabaseBuilder(
        context,
        com.example.data.local.AppDatabase::class.java
    ).allowMainThreadQueries().build()

    val marketDao = db.marketPriceCacheDao()
    val weatherDao = db.weatherCacheDao()
    val alertDao = db.priceAlertDao()

    // Test inserting market price cache
    val cachedPrice = com.example.data.local.MarketPriceCacheEntity(
        commodity = "Tomato",
        marketAnalyticsJson = "{\"commodity\":\"Tomato\",\"bestMandi\":\"Kolar\"}",
        weeklyAnalysisJson = "{\"weeklyAveragePrice\":3200.0}",
        lastSyncedAt = System.currentTimeMillis(),
        syncedDateString = "21 Sep 2026"
    )
    marketDao.insertOrUpdate(cachedPrice)
    val retrievedMarket = marketDao.getCacheSync("Tomato")
    assert(retrievedMarket != null)
    assertEquals("Tomato", retrievedMarket?.commodity)

    // Test inserting weather cache
    val cachedWeather = com.example.data.local.WeatherCacheEntity(
        locationKey = "Belagavi APMC",
        weatherDataJson = "{\"location\":\"Belagavi APMC\",\"temperature\":27.5}",
        weatherAdvisoryJson = "{\"advisory\":\"Moderate weather\"}",
        lastSyncedAt = System.currentTimeMillis()
    )
    weatherDao.insertOrUpdate(cachedWeather)
    val retrievedWeather = weatherDao.getWeatherCacheSync("Belagavi APMC")
    assert(retrievedWeather != null)
    assertEquals("Belagavi APMC", retrievedWeather?.locationKey)

    // Test price alerts
    val alert = com.example.data.local.PriceAlertEntity(
        commodity = "Tomato",
        mandiName = "Kolar",
        targetMinPrice = 2800.0,
        targetMaxPrice = 3600.0,
        isEnabled = true,
        createdAt = System.currentTimeMillis()
    )
    alertDao.insertAlert(alert)
    val activeAlerts = alertDao.getActiveAlerts()
    assertEquals(1, activeAlerts.size)
    assertEquals("Tomato", activeAlerts[0].commodity)

    db.close()
  }
}
