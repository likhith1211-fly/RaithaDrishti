package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.CropDiagnosisResult
import org.json.JSONArray

@Entity(tableName = "diagnoses")
data class DiagnosisEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val farmerId: String = "unregistered_farmer",
    val farmerName: String = "",
    val village: String = "",
    val birthYear: Int = 0,
    val farmerPhone: String = "",
    val cropName: String,
    val farmerNotes: String,
    val weatherContext: String,
    val imageUri: String? = null,
    val diagnosis: String,
    val severity: String,
    val confidence: Int,
    val summary: String,
    val immediateActionsJson: String,
    val weedsJson: String,
    val selectiveHerbicidesJson: String,
    val organicFertilizersJson: String,
    val chemicalFertilizersJson: String,
    val safetyJson: String,
    val createdAt: Long = System.currentTimeMillis()
) {
    fun toDomain(): CropDiagnosisResult {
        return CropDiagnosisResult(
            diagnosis = diagnosis,
            severity = severity,
            confidence = confidence,
            summary = summary,
            immediateActions = jsonToList(immediateActionsJson),
            weeds = jsonToList(weedsJson),
            selectiveHerbicides = jsonToList(selectiveHerbicidesJson),
            organicFertilizers = jsonToList(organicFertilizersJson),
            chemicalFertilizers = jsonToList(chemicalFertilizersJson),
            safety = jsonToList(safetyJson),
            cropName = cropName,
            timestamp = createdAt
        )
    }

    companion object {
        fun fromDomain(
            domain: CropDiagnosisResult,
            farmerNotes: String,
            weatherContext: String,
            imageUri: String?,
            farmerId: String = "unregistered_farmer",
            farmerName: String = "",
            village: String = "",
            birthYear: Int = 0,
            farmerPhone: String = ""
        ): DiagnosisEntity {
            return DiagnosisEntity(
                farmerId = farmerId,
                farmerName = farmerName,
                village = village,
                birthYear = birthYear,
                farmerPhone = farmerPhone,
                cropName = domain.cropName,
                farmerNotes = farmerNotes,
                weatherContext = weatherContext,
                imageUri = imageUri,
                diagnosis = domain.diagnosis,
                severity = domain.severity,
                confidence = domain.confidence,
                summary = domain.summary,
                immediateActionsJson = listToJson(domain.immediateActions),
                weedsJson = listToJson(domain.weeds),
                selectiveHerbicidesJson = listToJson(domain.selectiveHerbicides),
                organicFertilizersJson = listToJson(domain.organicFertilizers),
                chemicalFertilizersJson = listToJson(domain.chemicalFertilizers),
                safetyJson = listToJson(domain.safety),
                createdAt = domain.timestamp
            )
        }

        fun listToJson(list: List<String>): String {
            val jsonArray = JSONArray()
            list.forEach { jsonArray.put(it) }
            return jsonArray.toString()
        }

        fun jsonToList(json: String): List<String> {
            val list = mutableListOf<String>()
            try {
                if (json.isNotEmpty()) {
                    val jsonArray = JSONArray(json)
                    for (i in 0 until jsonArray.length()) {
                        list.add(jsonArray.getString(i))
                    }
                }
            } catch (e: Exception) {
                // fallback
            }
            return list
        }
    }
}

@Entity(tableName = "farmer_accounts")
data class FarmerAccountEntity(
    @PrimaryKey
    val farmerId: String,
    val fullName: String,
    val village: String,
    val birthYear: Int,
    val phoneNumber: String = "",
    val district: String = "Bengaluru Rural",
    val state: String = "Karnataka",
    val primaryCrops: String = "Maize, Tomato, Arecanut",
    val landSizeAcres: Double = 4.0,
    val latitude: Double = 13.098,
    val longitude: Double = 77.391,
    val lastSignedIn: Long = System.currentTimeMillis()
)

@Entity(tableName = "profiles")
data class ProfileEntity(
    @PrimaryKey
    val id: Int = 1,
    val fullName: String = "",
    val village: String = "",
    val birthYear: Int = 0,
    val email: String = "",
    val district: String = "Bengaluru Rural",
    val state: String = "Karnataka",
    val primaryCrops: String = "",
    val landSizeAcres: Double = 0.0
)

@Entity(tableName = "market_price_cache")
data class MarketPriceCacheEntity(
    @PrimaryKey
    val commodity: String,
    val marketAnalyticsJson: String,
    val weeklyAnalysisJson: String,
    val lastSyncedAt: Long = System.currentTimeMillis(),
    val syncedDateString: String = ""
)

@Entity(tableName = "weather_cache")
data class WeatherCacheEntity(
    @PrimaryKey
    val locationKey: String,
    val weatherDataJson: String,
    val weatherAdvisoryJson: String,
    val lastSyncedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "price_alerts")
data class PriceAlertEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val commodity: String,
    val mandiName: String = "All Mandis",
    val targetMinPrice: Double, // Alert if modal price drops below this
    val targetMaxPrice: Double, // Alert if modal price exceeds this
    val isEnabled: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val lastTriggeredAt: Long? = null,
    val lastTriggeredMessage: String? = null
)

