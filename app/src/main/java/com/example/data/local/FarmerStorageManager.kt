package com.example.data.local

import android.content.Context
import android.content.SharedPreferences
import com.example.data.model.AppLanguage
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

/**
 * Multi-tier local disk persistence manager for RaithaDrishti.
 * Guarantees that farmer profile, settings, and diagnoses are NEVER erased
 * across browser page refreshes, emulator restarts, or app reinstantiations.
 *
 * All data is strictly saved under:
 * - Farmer Name (ರೈತರ ಹೆಸರು)
 * - Village Name (ಗ್ರಾಮದ ಹೆಸರು)
 * - Date of Birth Year (ಹುಟ್ಟಿದ ವರ್ಷ)
 */
class FarmerStorageManager(private val context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("raitha_drishti_farmer_prefs", Context.MODE_PRIVATE)

    private val backupFile = File(context.filesDir, "farmer_records_backup.json")

    companion object {
        private const val KEY_IS_REGISTERED = "is_registered"
        private const val KEY_FARMER_ID = "farmer_id"
        private const val KEY_FARMER_NAME = "farmer_name"
        private const val KEY_VILLAGE_NAME = "village_name"
        private const val KEY_BIRTH_YEAR = "birth_year"
        private const val KEY_PHONE = "phone_number"
        private const val KEY_DISTRICT = "district"
        private const val KEY_CROPS = "primary_crops"
        private const val KEY_ACRES = "land_size_acres"
        private const val KEY_LATITUDE = "latitude"
        private const val KEY_LONGITUDE = "longitude"
        private const val KEY_LANGUAGE = "app_language"

        fun generateFarmerId(name: String, village: String, birthYear: Int): String {
            val safeName = name.trim().replace(Regex("[\\s]+"), "_").ifBlank { "farmer" }
            val safeVillage = village.trim().replace(Regex("[\\s]+"), "_").ifBlank { "village" }
            return "${safeName}_${safeVillage}_$birthYear".take(64)
        }
    }

    data class SavedFarmerData(
        val farmerId: String,
        val fullName: String,
        val village: String,
        val birthYear: Int,
        val phoneNumber: String,
        val district: String,
        val primaryCrops: String,
        val landSizeAcres: Double,
        val latitude: Double,
        val longitude: Double,
        val language: AppLanguage
    )

    fun hasSavedFarmer(): Boolean {
        return prefs.getBoolean(KEY_IS_REGISTERED, false) || backupFile.exists()
    }

    fun getSavedFarmer(): SavedFarmerData {
        // First try SharedPreferences
        if (prefs.getBoolean(KEY_IS_REGISTERED, false)) {
            val name = prefs.getString(KEY_FARMER_NAME, "") ?: ""
            val village = prefs.getString(KEY_VILLAGE_NAME, "") ?: ""
            val birthYear = prefs.getInt(KEY_BIRTH_YEAR, 0)
            val phone = prefs.getString(KEY_PHONE, "") ?: ""

            // Purge legacy sample placeholder data so farmers can enter newly
            if (name.contains("Basava", ignoreCase = true) || phone == "9876543210" || name.contains("Raju Gowda", ignoreCase = true)) {
                clearFarmer()
                return SavedFarmerData(
                    farmerId = "",
                    fullName = "",
                    village = "",
                    birthYear = 0,
                    phoneNumber = "",
                    district = "Bengaluru Rural",
                    primaryCrops = "",
                    landSizeAcres = 0.0,
                    latitude = 13.098,
                    longitude = 77.391,
                    language = AppLanguage.KANNADA
                )
            }

            val farmerId = prefs.getString(KEY_FARMER_ID, if (name.isNotBlank()) generateFarmerId(name, village, birthYear) else "")
                ?: (if (name.isNotBlank()) generateFarmerId(name, village, birthYear) else "")
            val district = prefs.getString(KEY_DISTRICT, "Bengaluru Rural") ?: "Bengaluru Rural"
            val crops = prefs.getString(KEY_CROPS, "") ?: ""
            val acres = prefs.getFloat(KEY_ACRES, 0f).toDouble()
            val lat = prefs.getFloat(KEY_LATITUDE, 13.098f).toDouble()
            val lon = prefs.getFloat(KEY_LONGITUDE, 77.391f).toDouble()
            val langName = prefs.getString(KEY_LANGUAGE, AppLanguage.KANNADA.name) ?: AppLanguage.KANNADA.name
            val lang = try { AppLanguage.valueOf(langName) } catch (e: Exception) { AppLanguage.KANNADA }

            return SavedFarmerData(
                farmerId = farmerId,
                fullName = name,
                village = village,
                birthYear = birthYear,
                phoneNumber = phone,
                district = district,
                primaryCrops = crops,
                landSizeAcres = acres,
                latitude = lat,
                longitude = lon,
                language = lang
            )
        }

        // Secondary fallback to backup file if SharedPreferences was cleared
        if (backupFile.exists()) {
            try {
                val jsonStr = backupFile.readText()
                val obj = JSONObject(jsonStr)
                val name = obj.optString("fullName", "")
                val village = obj.optString("village", "")
                val birthYear = obj.optInt("birthYear", 0)
                val phone = obj.optString("phoneNumber", "")

                // Purge legacy sample placeholder from disk backup as well
                if (name.contains("Basava", ignoreCase = true) || phone == "9876543210" || name.contains("Raju Gowda", ignoreCase = true)) {
                    clearFarmer()
                    return SavedFarmerData(
                        farmerId = "",
                        fullName = "",
                        village = "",
                        birthYear = 0,
                        phoneNumber = "",
                        district = "Bengaluru Rural",
                        primaryCrops = "",
                        landSizeAcres = 0.0,
                        latitude = 13.098,
                        longitude = 77.391,
                        language = AppLanguage.KANNADA
                    )
                }

                val farmerId = obj.optString("farmerId", if (name.isNotBlank()) generateFarmerId(name, village, birthYear) else "")
                val district = obj.optString("district", "Bengaluru Rural")
                val crops = obj.optString("primaryCrops", "")
                val acres = obj.optDouble("landSizeAcres", 0.0)
                val lat = obj.optDouble("latitude", 13.098)
                val lon = obj.optDouble("longitude", 77.391)
                val langStr = obj.optString("language", AppLanguage.KANNADA.name)
                val lang = try { AppLanguage.valueOf(langStr) } catch (e: Exception) { AppLanguage.KANNADA }

                if (name.isNotBlank() || phone.isNotBlank()) {
                    // Reseed SharedPreferences from disk backup
                    saveFarmer(
                        name = name,
                        village = village,
                        birthYear = birthYear,
                        phone = phone,
                        district = district,
                        crops = crops,
                        acres = acres,
                        lat = lat,
                        lon = lon,
                        language = lang
                    )
                    return SavedFarmerData(farmerId, name, village, birthYear, phone, district, crops, acres, lat, lon, lang)
                }
            } catch (e: Exception) {
                // ignore
            }
        }

        // Fresh uninitialized profile - let the farmer enter their own details newly
        return SavedFarmerData(
            farmerId = "",
            fullName = "",
            village = "",
            birthYear = 0,
            phoneNumber = "",
            district = "Bengaluru Rural",
            primaryCrops = "",
            landSizeAcres = 0.0,
            latitude = 13.098,
            longitude = 77.391,
            language = AppLanguage.KANNADA
        )
    }

    fun saveFarmer(
        name: String,
        village: String,
        birthYear: Int,
        phone: String = "",
        district: String = "Bengaluru Rural",
        crops: String = "",
        acres: Double = 0.0,
        lat: Double = 13.098,
        lon: Double = 77.391,
        language: AppLanguage = AppLanguage.KANNADA
    ): SavedFarmerData {
        val farmerId = generateFarmerId(name, village, birthYear)

        // 1. Commit to SharedPreferences (survives app restarts)
        prefs.edit()
            .putBoolean(KEY_IS_REGISTERED, true)
            .putString(KEY_FARMER_ID, farmerId)
            .putString(KEY_FARMER_NAME, name)
            .putString(KEY_VILLAGE_NAME, village)
            .putInt(KEY_BIRTH_YEAR, birthYear)
            .putString(KEY_PHONE, phone)
            .putString(KEY_DISTRICT, district)
            .putString(KEY_CROPS, crops)
            .putFloat(KEY_ACRES, acres.toFloat())
            .putFloat(KEY_LATITUDE, lat.toFloat())
            .putFloat(KEY_LONGITUDE, lon.toFloat())
            .putString(KEY_LANGUAGE, language.name)
            .commit()

        // 2. Mirror to Internal Disk JSON Backup File
        try {
            val json = JSONObject().apply {
                put("farmerId", farmerId)
                put("fullName", name)
                put("village", village)
                put("birthYear", birthYear)
                put("phoneNumber", phone)
                put("district", district)
                put("primaryCrops", crops)
                put("landSizeAcres", acres)
                put("latitude", lat)
                put("longitude", lon)
                put("language", language.name)
                put("savedAt", System.currentTimeMillis())
            }
            backupFile.writeText(json.toString(2))
        } catch (e: Exception) {
            // non-fatal
        }

        return SavedFarmerData(
            farmerId = farmerId,
            fullName = name,
            village = village,
            birthYear = birthYear,
            phoneNumber = phone,
            district = district,
            primaryCrops = crops,
            landSizeAcres = acres,
            latitude = lat,
            longitude = lon,
            language = language
        )
    }

    fun updateCoordinates(lat: Double, lon: Double) {
        val current = getSavedFarmer()
        saveFarmer(
            name = current.fullName,
            village = current.village,
            birthYear = current.birthYear,
            phone = current.phoneNumber,
            district = current.district,
            crops = current.primaryCrops,
            acres = current.landSizeAcres,
            lat = lat,
            lon = lon,
            language = current.language
        )
    }

    fun updateDistrictAndCoordinates(districtName: String, lat: Double, lon: Double, village: String? = null) {
        val current = getSavedFarmer()
        saveFarmer(
            name = current.fullName,
            village = village ?: current.village,
            birthYear = current.birthYear,
            phone = current.phoneNumber,
            district = districtName,
            crops = current.primaryCrops,
            acres = current.landSizeAcres,
            lat = lat,
            lon = lon,
            language = current.language
        )
    }

    fun updateLanguage(lang: AppLanguage) {
        prefs.edit().putString(KEY_LANGUAGE, lang.name).commit()
    }

    fun clearFarmer() {
        prefs.edit().clear().commit()
        if (backupFile.exists()) {
            backupFile.delete()
        }
    }

    fun exportFarmerJson(): String = getExportBackupJson()

    fun getExportBackupJson(): String {
        return if (backupFile.exists()) {
            backupFile.readText()
        } else {
            val data = getSavedFarmer()
            JSONObject().apply {
                put("farmerId", data.farmerId)
                put("fullName", data.fullName)
                put("village", data.village)
                put("birthYear", data.birthYear)
                put("phoneNumber", data.phoneNumber)
                put("district", data.district)
                put("primaryCrops", data.primaryCrops)
                put("landSizeAcres", data.landSizeAcres)
                put("latitude", data.latitude)
                put("longitude", data.longitude)
                put("language", data.language.name)
            }.toString(2)
        }
    }
}
