package com.example.ui.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.DiagnosisEntity
import com.example.data.local.FarmerAccountEntity
import com.example.data.local.ProfileEntity
import com.example.data.local.PriceAlertEntity
import com.example.data.local.FarmerStorageManager
import com.example.data.model.AppLanguage
import com.example.data.model.CropDiagnosisResult
import com.example.data.model.FertilizerRecommendation
import com.example.data.model.GeminiWeatherAdvisory
import com.example.data.model.KARNATAKA_DISTRICTS
import com.example.data.model.KarnatakaDistrict
import com.example.data.model.MarketAnalytics
import com.example.data.model.RECOMMENDED_FERTILIZERS
import com.example.data.model.VoiceChatMessage
import com.example.data.model.WeeklyMarketAnalysis
import com.example.data.model.WeatherData
import com.example.data.remote.ApmcKarnatakaMarketService
import com.example.data.remote.GeminiDiagnosisService
import com.example.data.remote.WeatherApiService
import com.example.data.repository.RaithaDrishtiRepository
import com.example.util.KarnatakaGrowthTipsEngine
import com.example.util.MarketNotificationHelper
import com.example.util.SeasonalCropTip
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class RaithaDrishtiViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: RaithaDrishtiRepository

    init {
        val db = AppDatabase.getDatabase(application)
        repository = RaithaDrishtiRepository(
            diagnosisDao = db.diagnosisDao(),
            profileDao = db.profileDao(),
            farmerAccountDao = db.farmerAccountDao(),
            marketPriceCacheDao = db.marketPriceCacheDao(),
            weatherCacheDao = db.weatherCacheDao(),
            priceAlertDao = db.priceAlertDao(),
            weatherService = WeatherApiService(),
            geminiService = GeminiDiagnosisService(),
            marketService = ApmcKarnatakaMarketService()
        )
    }

    private val storageManager = FarmerStorageManager(application.applicationContext)
    private val savedInitialFarmer = storageManager.getSavedFarmer()

    // --- App Language State (Kannada, Hindi, English) ---
    private val _currentLanguage = MutableStateFlow(savedInitialFarmer.language)
    val currentLanguage: StateFlow<AppLanguage> = _currentLanguage.asStateFlow()

    fun setLanguage(language: AppLanguage) {
        _currentLanguage.value = language
        storageManager.updateLanguage(language)
        // Trigger weather advisory update in new language if weather is present
        _currentWeather.value?.let { weather ->
            fetchAiWeatherAdvisory(weather, language)
        }
    }

    // --- Farmer Identity strictly saved under Name, Village, and Date of Birth Year ---
    private val _activeFarmerId = MutableStateFlow(savedInitialFarmer.farmerId)
    val activeFarmerId: StateFlow<String> = _activeFarmerId.asStateFlow()

    private val _activeFarmerName = MutableStateFlow(savedInitialFarmer.fullName)
    val activeFarmerName: StateFlow<String> = _activeFarmerName.asStateFlow()

    private val _activeVillageName = MutableStateFlow(savedInitialFarmer.village)
    val activeVillageName: StateFlow<String> = _activeVillageName.asStateFlow()

    private val _activeBirthYear = MutableStateFlow(savedInitialFarmer.birthYear)
    val activeBirthYear: StateFlow<Int> = _activeBirthYear.asStateFlow()

    private val _activeFarmerPhone = MutableStateFlow(savedInitialFarmer.phoneNumber)
    val activeFarmerPhone: StateFlow<String> = _activeFarmerPhone.asStateFlow()

    private val _currentAccount = MutableStateFlow<FarmerAccountEntity?>(
        if (savedInitialFarmer.fullName.isNotBlank() || savedInitialFarmer.phoneNumber.isNotBlank()) {
            FarmerAccountEntity(
                farmerId = savedInitialFarmer.farmerId,
                fullName = savedInitialFarmer.fullName,
                village = savedInitialFarmer.village,
                birthYear = savedInitialFarmer.birthYear,
                phoneNumber = savedInitialFarmer.phoneNumber,
                district = savedInitialFarmer.district,
                state = "Karnataka",
                primaryCrops = savedInitialFarmer.primaryCrops,
                landSizeAcres = savedInitialFarmer.landSizeAcres,
                latitude = savedInitialFarmer.latitude,
                longitude = savedInitialFarmer.longitude
            )
        } else null
    )
    val currentAccount: StateFlow<FarmerAccountEntity?> = _currentAccount.asStateFlow()

    val allFarmerAccounts: StateFlow<List<FarmerAccountEntity>> = repository.allFarmerAccounts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filter diagnoses by active farmer's unique composite ID (strictly saved under Name + Village + Birth Year)
    val diagnosesHistory: StateFlow<List<DiagnosisEntity>> = _activeFarmerId
        .flatMapLatest { id -> repository.getDiagnosesForFarmerId(id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Resolve initial district from saved farmer persistence
    private val initialDistrict: KarnatakaDistrict = run {
        val savedDistrictName = savedInitialFarmer.district
        KARNATAKA_DISTRICTS.find {
            it.name.equals(savedDistrictName, ignoreCase = true) ||
            it.kannadaName.equals(savedDistrictName, ignoreCase = true) ||
            savedDistrictName.contains(it.name, ignoreCase = true) ||
            savedDistrictName.contains(it.kannadaName)
        } ?: KARNATAKA_DISTRICTS[0]
    }

    // Map Geolocation State
    private val _farmerCoordinates = MutableStateFlow(
        if (savedInitialFarmer.latitude != 0.0 && savedInitialFarmer.longitude != 0.0) {
            Pair(savedInitialFarmer.latitude, savedInitialFarmer.longitude)
        } else {
            Pair(initialDistrict.lat, initialDistrict.lon)
        }
    )
    val farmerCoordinates: StateFlow<Pair<Double, Double>> = _farmerCoordinates.asStateFlow()

    private val _selectedMapMandiId = MutableStateFlow<String?>("bengaluru_apmc")
    val selectedMapMandiId: StateFlow<String?> = _selectedMapMandiId.asStateFlow()

    private val _mapLayer = MutableStateFlow("MANDI_NETWORK")
    val mapLayer: StateFlow<String> = _mapLayer.asStateFlow()

    // --- Weather State & Exact GPS Location ---
    private val _selectedDistrict = MutableStateFlow(initialDistrict)
    val selectedDistrict: StateFlow<KarnatakaDistrict> = _selectedDistrict.asStateFlow()

    private val _currentWeather = MutableStateFlow<WeatherData?>(null)
    val currentWeather: StateFlow<WeatherData?> = _currentWeather.asStateFlow()

    private val _isWeatherLoading = MutableStateFlow(false)
    val isWeatherLoading: StateFlow<Boolean> = _isWeatherLoading.asStateFlow()

    private val _isExactLocationActive = MutableStateFlow(false)
    val isExactLocationActive: StateFlow<Boolean> = _isExactLocationActive.asStateFlow()

    private val _exactLocationLabel = MutableStateFlow<String?>(null)
    val exactLocationLabel: StateFlow<String?> = _exactLocationLabel.asStateFlow()

    private val _aiWeatherAdvisory = MutableStateFlow<GeminiWeatherAdvisory?>(null)
    val aiWeatherAdvisory: StateFlow<GeminiWeatherAdvisory?> = _aiWeatherAdvisory.asStateFlow()

    private val _isGeneratingAdvisory = MutableStateFlow(false)
    val isGeneratingAdvisory: StateFlow<Boolean> = _isGeneratingAdvisory.asStateFlow()

    // --- Mandi Market State ---
    private val defaultMarketService = ApmcKarnatakaMarketService()
    private val _selectedCommodity = MutableStateFlow("Tomato")
    val selectedCommodity: StateFlow<String> = _selectedCommodity.asStateFlow()

    private val _marketAnalytics = MutableStateFlow(defaultMarketService.getMarketAnalytics("Tomato"))
    val marketAnalytics: StateFlow<MarketAnalytics> = _marketAnalytics.asStateFlow()

    private val _weeklyMarketAnalysis = MutableStateFlow(defaultMarketService.getWeeklyMarketAnalysis("Tomato"))
    val weeklyMarketAnalysis: StateFlow<WeeklyMarketAnalysis> = _weeklyMarketAnalysis.asStateFlow()

    val supportedCommodities: List<String> = defaultMarketService.getSupportedCommodities()

    // --- Price Threshold Alerts ---
    val allPriceAlerts: StateFlow<List<PriceAlertEntity>> = repository.allPriceAlerts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _triggeredAlerts = MutableStateFlow<List<MarketNotificationHelper.AlertTriggerResult>>(emptyList())
    val triggeredAlerts: StateFlow<List<MarketNotificationHelper.AlertTriggerResult>> = _triggeredAlerts.asStateFlow()

    // --- Hands-Free Voice Command Feedback in Crop Diagnostics ---
    private val _voiceFeedbackMessage = MutableStateFlow<String?>(null)
    val voiceFeedbackMessage: StateFlow<String?> = _voiceFeedbackMessage.asStateFlow()

    fun dismissVoiceFeedback() {
        _voiceFeedbackMessage.value = null
    }

    fun clearVoiceFeedback() {
        _voiceFeedbackMessage.value = null
    }

    // --- Crop Diagnostics State ---
    private val _targetCrop = MutableStateFlow("Maize")
    val targetCrop: StateFlow<String> = _targetCrop.asStateFlow()

    private val _farmerNotes = MutableStateFlow("Patchy white leaves observed on younger maize leaves with chlorotic striping.")
    val farmerNotes: StateFlow<String> = _farmerNotes.asStateFlow()

    private val _selectedImageBitmap = MutableStateFlow<Bitmap?>(null)
    val selectedImageBitmap: StateFlow<Bitmap?> = _selectedImageBitmap.asStateFlow()

    private val _isDiagnosing = MutableStateFlow(false)
    val isDiagnosing: StateFlow<Boolean> = _isDiagnosing.asStateFlow()

    private val _latestDiagnosis = MutableStateFlow<CropDiagnosisResult?>(null)
    val latestDiagnosis: StateFlow<CropDiagnosisResult?> = _latestDiagnosis.asStateFlow()

    private val _diagnosisError = MutableStateFlow<String?>(null)
    val diagnosisError: StateFlow<String?> = _diagnosisError.asStateFlow()

    // --- Multilingual Voice Assistant State ---
    private val _voiceChatMessages = MutableStateFlow<List<VoiceChatMessage>>(
        listOf(
            VoiceChatMessage(
                sender = "ai",
                text = "ನಮಸ್ಕಾರ! ನಾನು ನಿಮ್ಮ ಕೃಷಿ ಧ್ವನಿ ಸಹಾಯಕ (RaithaDrishti AI). ಬೆಳೆ ರೋಗ, ಮೆಕ್ಕೆಜೋಳದ ಬಿಳಿ ಎಲೆ, ರಸಗೊಬ್ಬರಗಳು, ಕೀಟನಾಶಕಗಳು ಅಥವಾ ಮಾರುಕಟ್ಟೆ ದರಗಳ ಬಗ್ಗೆ ಕೇಳಿ!",
                language = AppLanguage.KANNADA
            )
        )
    )
    val voiceChatMessages: StateFlow<List<VoiceChatMessage>> = _voiceChatMessages.asStateFlow()

    private val _isVoiceProcessing = MutableStateFlow(false)
    val isVoiceProcessing: StateFlow<Boolean> = _isVoiceProcessing.asStateFlow()

    // --- Fertilizers & Wanted Chemicals State ---
    private val _fertilizerSearch = MutableStateFlow("")
    val fertilizerSearch: StateFlow<String> = _fertilizerSearch.asStateFlow()

    private val _selectedFertilizerCategory = MutableStateFlow("All")
    val selectedFertilizerCategory: StateFlow<String> = _selectedFertilizerCategory.asStateFlow()

    private val _selectedFertilizerCrop = MutableStateFlow("All")
    val selectedFertilizerCrop: StateFlow<String> = _selectedFertilizerCrop.asStateFlow()

    val allFertilizers: List<FertilizerRecommendation> = RECOMMENDED_FERTILIZERS

    val farmerProfile: StateFlow<ProfileEntity?> = repository.farmerProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    init {
        fetchWeatherForDistrict(_selectedDistrict.value)
        // Only write to Room DB if farmer has actually entered their details
        if (savedInitialFarmer.fullName.isNotBlank() || savedInitialFarmer.phoneNumber.isNotBlank()) {
            viewModelScope.launch {
                val account = FarmerAccountEntity(
                    farmerId = savedInitialFarmer.farmerId,
                    fullName = savedInitialFarmer.fullName,
                    village = savedInitialFarmer.village,
                    birthYear = savedInitialFarmer.birthYear,
                    phoneNumber = savedInitialFarmer.phoneNumber,
                    district = savedInitialFarmer.district,
                    state = "Karnataka",
                    primaryCrops = savedInitialFarmer.primaryCrops,
                    landSizeAcres = savedInitialFarmer.landSizeAcres,
                    latitude = savedInitialFarmer.latitude,
                    longitude = savedInitialFarmer.longitude,
                    lastSignedIn = System.currentTimeMillis()
                )
                repository.saveFarmerAccount(account)
                repository.saveProfile(
                    ProfileEntity(
                        id = 1,
                        fullName = savedInitialFarmer.fullName,
                        village = savedInitialFarmer.village,
                        birthYear = savedInitialFarmer.birthYear,
                        email = "${savedInitialFarmer.farmerId}@raithadrishti.in",
                        district = savedInitialFarmer.district,
                        state = "Karnataka",
                        primaryCrops = savedInitialFarmer.primaryCrops,
                        landSizeAcres = savedInitialFarmer.landSizeAcres
                    )
                )
            }
        }
    }

    // --- Farmer Persistence by Name, Village, and Birth Year ---
    fun saveOrAlterFarmerProfile(
        name: String,
        village: String,
        birthYear: Int,
        district: String = "Bengaluru Rural",
        crops: String = "",
        acres: Double = 0.0,
        phoneNumber: String = "",
        lat: Double = 13.098,
        lon: Double = 77.391
    ) {
        val sanitizedName = name.trim()
        val sanitizedVillage = village.trim()
        val sanitizedYear = if (birthYear in 1920..2026) birthYear else 0

        val updatedRecord = storageManager.saveFarmer(
            name = sanitizedName,
            village = sanitizedVillage,
            birthYear = sanitizedYear,
            phone = phoneNumber,
            district = district,
            crops = crops,
            acres = acres,
            lat = lat,
            lon = lon,
            language = _currentLanguage.value
        )

        _activeFarmerId.value = updatedRecord.farmerId
        _activeFarmerName.value = updatedRecord.fullName
        _activeVillageName.value = updatedRecord.village
        _activeBirthYear.value = updatedRecord.birthYear
        _activeFarmerPhone.value = updatedRecord.phoneNumber
        _farmerCoordinates.value = Pair(updatedRecord.latitude, updatedRecord.longitude)

        val account = FarmerAccountEntity(
            farmerId = updatedRecord.farmerId,
            fullName = updatedRecord.fullName,
            village = updatedRecord.village,
            birthYear = updatedRecord.birthYear,
            phoneNumber = updatedRecord.phoneNumber,
            district = updatedRecord.district,
            state = "Karnataka",
            primaryCrops = updatedRecord.primaryCrops,
            landSizeAcres = updatedRecord.landSizeAcres,
            latitude = updatedRecord.latitude,
            longitude = updatedRecord.longitude,
            lastSignedIn = System.currentTimeMillis()
        )
        _currentAccount.value = account

        viewModelScope.launch {
            repository.saveFarmerAccount(account)
            repository.saveProfile(
                ProfileEntity(
                    id = 1,
                    fullName = updatedRecord.fullName,
                    village = updatedRecord.village,
                    birthYear = updatedRecord.birthYear,
                    email = "${updatedRecord.farmerId}@raithadrishti.in",
                    district = updatedRecord.district,
                    state = "Karnataka",
                    primaryCrops = updatedRecord.primaryCrops,
                    landSizeAcres = updatedRecord.landSizeAcres
                )
            )
        }
    }

    fun signInOrRegisterFarmer(
        phone: String,
        name: String,
        village: String,
        district: String,
        crops: String,
        acres: Double,
        birthYear: Int = 0
    ) {
        saveOrAlterFarmerProfile(
            name = name,
            village = village,
            birthYear = birthYear,
            district = district,
            crops = crops,
            acres = acres,
            phoneNumber = phone
        )
    }

    fun resetFarmerProfile() {
        storageManager.clearFarmer()
        _activeFarmerId.value = ""
        _activeFarmerName.value = ""
        _activeVillageName.value = ""
        _activeBirthYear.value = 0
        _activeFarmerPhone.value = ""
        _currentAccount.value = null
    }

    fun switchFarmerAccount(farmerId: String) {
        viewModelScope.launch {
            val account = repository.getFarmerAccountById(farmerId)
            if (account != null) {
                _activeFarmerId.value = account.farmerId
                _activeFarmerName.value = account.fullName
                _activeVillageName.value = account.village
                _activeBirthYear.value = account.birthYear
                _activeFarmerPhone.value = account.phoneNumber
                _currentAccount.value = account
                _farmerCoordinates.value = Pair(account.latitude, account.longitude)
                storageManager.saveFarmer(
                    name = account.fullName,
                    village = account.village,
                    birthYear = account.birthYear,
                    phone = account.phoneNumber,
                    district = account.district,
                    crops = account.primaryCrops,
                    acres = account.landSizeAcres,
                    lat = account.latitude,
                    lon = account.longitude,
                    language = _currentLanguage.value
                )
            }
        }
    }

    fun getFarmerExportJson(): String {
        return storageManager.exportFarmerJson()
    }

    // --- Map Operations ---
    fun updateFarmerCoordinates(lat: Double, lon: Double) {
        _farmerCoordinates.value = Pair(lat, lon)
        storageManager.updateCoordinates(lat, lon)
    }

    fun setSelectedMapMandi(mandiId: String?) {
        _selectedMapMandiId.value = mandiId
    }

    fun setMapLayer(layer: String) {
        _mapLayer.value = layer
    }

    // --- Location Weather Methods ---
    fun setDistrict(district: KarnatakaDistrict) {
        _isExactLocationActive.value = false
        _exactLocationLabel.value = null
        _selectedDistrict.value = district
        _farmerCoordinates.value = Pair(district.lat, district.lon)
        storageManager.updateDistrictAndCoordinates(
            districtName = district.name,
            lat = district.lat,
            lon = district.lon
        )
        fetchWeatherForDistrict(district)
    }

    fun setFarmerFieldLocation(district: KarnatakaDistrict, customVillage: String? = null) {
        _isExactLocationActive.value = false
        _exactLocationLabel.value = null
        _selectedDistrict.value = district
        _farmerCoordinates.value = Pair(district.lat, district.lon)
        val villageName = customVillage ?: district.name
        _activeVillageName.value = villageName
        storageManager.updateDistrictAndCoordinates(
            districtName = district.name,
            lat = district.lat,
            lon = district.lon,
            village = villageName
        )
        fetchWeatherForDistrict(district)
    }

    fun fetchWeatherForDistrict(district: KarnatakaDistrict) {
        viewModelScope.launch {
            _isWeatherLoading.value = true
            try {
                val data = repository.fetchWeather(district.lat, district.lon, district.name)
                _currentWeather.value = data
                fetchAiWeatherAdvisory(data, _currentLanguage.value)
            } catch (e: Exception) {
                // handle error
            } finally {
                _isWeatherLoading.value = false
            }
        }
    }

    fun updateExactCoordinates(lat: Double, lon: Double, customLabel: String? = null) {
        viewModelScope.launch {
            _isWeatherLoading.value = true
            _isExactLocationActive.value = true
            val label = customLabel ?: "GPS Exact Farm (${String.format("%.3f", lat)}, ${String.format("%.3f", lon)})"
            _exactLocationLabel.value = label
            try {
                val data = repository.fetchWeather(lat, lon, label)
                _currentWeather.value = data
                fetchAiWeatherAdvisory(data, _currentLanguage.value)
            } catch (e: Exception) {
                // handle error
            } finally {
                _isWeatherLoading.value = false
            }
        }
    }

    private fun fetchAiWeatherAdvisory(weather: WeatherData, language: AppLanguage) {
        viewModelScope.launch {
            _isGeneratingAdvisory.value = true
            try {
                val advisory = repository.generateWeatherAdvisory(weather, language)
                _aiWeatherAdvisory.value = advisory
            } catch (e: Exception) {
                // keep previous
            } finally {
                _isGeneratingAdvisory.value = false
            }
        }
    }

    // --- Voice Assistant Methods ---
    fun sendVoiceQuery(query: String) {
        val trimmed = query.trim()
        if (trimmed.isBlank()) return

        val userMessage = VoiceChatMessage(
            sender = "user",
            text = trimmed,
            language = _currentLanguage.value
        )
        _voiceChatMessages.value = _voiceChatMessages.value + userMessage

        viewModelScope.launch {
            _isVoiceProcessing.value = true
            try {
                val weather = _currentWeather.value
                val weatherContext = weather?.let {
                    "${it.location}, Temp: ${it.temperature}°C, Humidity: ${it.humidity}%, Rain: ${it.precipitation}mm"
                }

                val aiResponseText = repository.queryVoiceAssistant(
                    userQuery = trimmed,
                    language = _currentLanguage.value,
                    weatherContext = weatherContext
                )

                val aiMessage = VoiceChatMessage(
                    sender = "ai",
                    text = aiResponseText,
                    language = _currentLanguage.value
                )
                _voiceChatMessages.value = _voiceChatMessages.value + aiMessage
            } catch (e: Exception) {
                val fallbackText = when (_currentLanguage.value) {
                    AppLanguage.KANNADA -> "ಕ್ಷಮಿಸಿ, ಧ್ವನಿ ಸಂಪರ್ಕದಲ್ಲಿ ಸಮಸ್ಯೆಯಾಗಿದೆ. ಮೆಕ್ಕೆಜೋಳದ ಬಿಳಿ ಎಲೆಗೆ ಜಿಂಕ್ ಸಲ್ಫೇಟ್ ಮತ್ತು ರಿಡೋಮಿಲ್ ಗೋಲ್ಡ್ ಸಿಂಪಡಿಸಿ."
                    AppLanguage.HINDI -> "क्षमा करें, तकनीकी समस्या है। मक्के की सफेद पत्ती के लिए जिंक और रिडोमिल का छिड़काव करें।"
                    AppLanguage.ENGLISH -> "Network issue occurred. For maize white leaves, apply foliar Zinc Sulphate and Ridomil Gold immediately."
                }
                _voiceChatMessages.value = _voiceChatMessages.value + VoiceChatMessage(
                    sender = "ai",
                    text = fallbackText,
                    language = _currentLanguage.value
                )
            } finally {
                _isVoiceProcessing.value = false
            }
        }
    }

    fun clearVoiceChat() {
        val welcomeText = when (_currentLanguage.value) {
            AppLanguage.KANNADA -> "ನಮಸ್ಕಾರ! ನಿಮ್ಮ ಬೆಳೆ ಅಥವಾ ಗೊಬ್ಬರದ ಪ್ರಶ್ನೆಯನ್ನು ಮಾತನಾಡಿ ಕೇಳಿ."
            AppLanguage.HINDI -> "नमस्ते! अपनी फसल, खाद या मौसम के बारे में बोलकर पूछें।"
            AppLanguage.ENGLISH -> "Hello! Ask any question about crops, fertilizers, or weather via voice."
        }
        _voiceChatMessages.value = listOf(
            VoiceChatMessage(sender = "ai", text = welcomeText, language = _currentLanguage.value)
        )
    }

    // --- Fertilizer & Chemicals Methods ---
    fun setFertilizerSearch(search: String) {
        _fertilizerSearch.value = search
    }

    fun setFertilizerCategory(category: String) {
        _selectedFertilizerCategory.value = category
    }

    fun setFertilizerCrop(crop: String) {
        _selectedFertilizerCrop.value = crop
    }

    fun setCommodity(commodity: String) {
        _selectedCommodity.value = commodity
        viewModelScope.launch {
            val analytics = repository.getMarketAnalytics(commodity)
            val weekly = repository.getWeeklyMarketAnalysis(commodity)
            _marketAnalytics.value = analytics
            _weeklyMarketAnalysis.value = weekly
            evaluatePriceAlerts(analytics)
        }
    }

    private fun evaluatePriceAlerts(analytics: MarketAnalytics) {
        viewModelScope.launch {
            val activeAlerts = repository.getActivePriceAlerts()
            if (activeAlerts.isNotEmpty()) {
                val context = getApplication<Application>().applicationContext
                val triggered = MarketNotificationHelper.evaluateAlerts(
                    context = context,
                    alerts = activeAlerts,
                    analytics = analytics,
                    language = _currentLanguage.value
                )
                if (triggered.isNotEmpty()) {
                    _triggeredAlerts.value = (_triggeredAlerts.value + triggered).distinctBy { it.alertId }
                }
            }
        }
    }

    fun addPriceAlert(
        commodity: String,
        mandiName: String = "All Mandis",
        minPrice: Double,
        maxPrice: Double
    ) {
        viewModelScope.launch {
            val newAlert = PriceAlertEntity(
                commodity = commodity,
                mandiName = mandiName,
                targetMinPrice = minPrice,
                targetMaxPrice = maxPrice,
                isEnabled = true
            )
            repository.savePriceAlert(newAlert)
            evaluatePriceAlerts(_marketAnalytics.value)
        }
    }

    fun togglePriceAlert(alert: PriceAlertEntity) {
        viewModelScope.launch {
            repository.updatePriceAlert(alert.copy(isEnabled = !alert.isEnabled))
            if (!alert.isEnabled) {
                evaluatePriceAlerts(_marketAnalytics.value)
            }
        }
    }

    fun deletePriceAlert(id: Long) {
        viewModelScope.launch {
            repository.deletePriceAlert(id)
            _triggeredAlerts.value = _triggeredAlerts.value.filter { it.alertId != id }
        }
    }

    fun dismissTriggeredAlert(alertId: Long) {
        _triggeredAlerts.value = _triggeredAlerts.value.filter { it.alertId != alertId }
    }

    fun dismissAllTriggeredAlerts() {
        _triggeredAlerts.value = emptyList()
    }

    // --- Karnataka Seasonal Growth Tips ---
    fun getSeasonalGrowthTip(cropName: String = _targetCrop.value): SeasonalCropTip {
        return KarnatakaGrowthTipsEngine.generateTipsForCrop(
            cropId = cropName,
            weather = _currentWeather.value,
            language = _currentLanguage.value
        )
    }

    // --- Voice Command Support for Hands-Free Crop Diagnostics ---
    fun processDiagnosticVoiceCommand(
        rawCommand: String,
        onNavigateToCamera: () -> Unit,
        onOpenGallery: () -> Unit,
        onStartDiagnosis: () -> Unit
    ) {
        val lower = rawCommand.lowercase().trim()
        val lang = _currentLanguage.value

        when {
            // Camera trigger
            lower.contains("camera") || lower.contains("ಕ್ಯಾಮೆರಾ") || lower.contains("ಫೋಟೋ") ||
            lower.contains("photo") || lower.contains("take photo") || lower.contains("ಕ್ಲಿಕ್") ||
            lower.contains("कैमरा") || lower.contains("फोटो") -> {
                val feedback = when (lang) {
                    AppLanguage.KANNADA -> "ಕ್ಯಾಮೆರಾ ತೆರೆಯಲಾಗುತ್ತಿದೆ..."
                    AppLanguage.HINDI -> "कैमरा खोला जा रहा है..."
                    AppLanguage.ENGLISH -> "Opening camera..."
                }
                _voiceFeedbackMessage.value = feedback
                onNavigateToCamera()
            }

            // Gallery trigger
            lower.contains("gallery") || lower.contains("ಗ್ಯಾಲರಿ") || lower.contains("ಚಿತ್ರ") ||
            lower.contains("ಗ್ಯಾಲರಿಯಿಂದ") || lower.contains("गैलरी") || lower.contains("image") -> {
                val feedback = when (lang) {
                    AppLanguage.KANNADA -> "ಗ್ಯಾಲರಿ ತೆರೆಯಲಾಗುತ್ತಿದೆ..."
                    AppLanguage.HINDI -> "गैलरी खोली जा रही है..."
                    AppLanguage.ENGLISH -> "Opening photo gallery..."
                }
                _voiceFeedbackMessage.value = feedback
                onOpenGallery()
            }

            // Run diagnosis
            lower.contains("diagnose") || lower.contains("ಪರೀಕ್ಷಿಸು") || lower.contains("ರೋಗ") ||
            lower.contains("ಪರೀಕ್ಷೆ") || lower.contains("जांच") || lower.contains("check") ||
            lower.contains("ರೋಗ ಪರೀಕ್ಷೆ") -> {
                val feedback = when (lang) {
                    AppLanguage.KANNADA -> "ಬೆಳೆ ರೋಗ ಪರೀಕ್ಷೆ ಆರಂಭಿಸಲಾಗುತ್ತಿದೆ..."
                    AppLanguage.HINDI -> "फसल रोग जांच शुरू हो रही है..."
                    AppLanguage.ENGLISH -> "Starting crop pathology diagnosis..."
                }
                _voiceFeedbackMessage.value = feedback
                onStartDiagnosis()
            }

            // Crop switching via voice
            lower.contains("maize") || lower.contains("ಮೆಕ್ಕೆಜೋಳ") || lower.contains("मक्का") -> {
                setTargetCrop("Maize")
                _voiceFeedbackMessage.value = when (lang) {
                    AppLanguage.KANNADA -> "ಬೆಳೆಯನ್ನು ಮೆಕ್ಕೆಜೋಳಕ್ಕೆ ಬದಲಾಯಿಸಲಾಗಿದೆ"
                    AppLanguage.HINDI -> "फसल को मक्का चुना गया"
                    AppLanguage.ENGLISH -> "Crop switched to Maize"
                }
            }
            lower.contains("tomato") || lower.contains("ಟೊಮೆಟೊ") || lower.contains("टमाटर") -> {
                setTargetCrop("Tomato")
                _voiceFeedbackMessage.value = when (lang) {
                    AppLanguage.KANNADA -> "ಬೆಳೆಯನ್ನು ಟೊಮೆಟೊಗೆ ಬದಲಾಯಿಸಲಾಗಿದೆ"
                    AppLanguage.HINDI -> "फसल को टमाटर चुना गया"
                    AppLanguage.ENGLISH -> "Crop switched to Tomato"
                }
            }
            lower.contains("pumpkin") || lower.contains("ಕುಂಬಳಕಾಯಿ") || lower.contains("कद्दू") -> {
                setTargetCrop("Pumpkin")
                _voiceFeedbackMessage.value = when (lang) {
                    AppLanguage.KANNADA -> "ಬೆಳೆಯನ್ನು ಕುಂಬಳಕಾಯಿಗೆ ಬದಲಾಯಿಸಲಾಗಿದೆ"
                    AppLanguage.HINDI -> "फसल को कद्दू चुना गया"
                    AppLanguage.ENGLISH -> "Crop switched to Pumpkin"
                }
            }
            lower.contains("arecanut") || lower.contains("ಅಡಿಕೆ") || lower.contains("सुपारी") -> {
                setTargetCrop("Arecanut")
                _voiceFeedbackMessage.value = when (lang) {
                    AppLanguage.KANNADA -> "ಬೆಳೆಯನ್ನು ಅಡಿಕೆಗೆ ಬದಲಾಯಿಸಲಾಗಿದೆ"
                    AppLanguage.HINDI -> "फसल को सुपारी चुना गया"
                    AppLanguage.ENGLISH -> "Crop switched to Arecanut"
                }
            }

            // Price query
            lower.contains("price") || lower.contains("ಬೆಲೆ") || lower.contains("ದರ") || lower.contains("भाव") || lower.contains("rate") -> {
                val crop = if (lower.contains("pumpkin") || lower.contains("ಕುಂಬಳಕಾಯಿ") || lower.contains("कद्दू")) "Pumpkin"
                else if (lower.contains("maize") || lower.contains("ಮೆಕ್ಕೆಜೋಳ") || lower.contains("मक्का")) "Maize"
                else if (lower.contains("arecanut") || lower.contains("ಅಡಿಕೆ") || lower.contains("सुಪಾರಿ")) "Arecanut (Rashi)"
                else "Tomato"

                setCommodity(crop)
                val currentP = _marketAnalytics.value.mandiPrices.firstOrNull()?.modalPrice?.toInt() ?: 1600
                _voiceFeedbackMessage.value = when (lang) {
                    AppLanguage.KANNADA -> "$crop ಇಂದಿನ ಎಪಿಎಂಸಿ ದರ: ₹$currentP/ಕ್ವಿಂಟಾಲ್"
                    AppLanguage.HINDI -> "$crop का आज का भाव: ₹$currentP/क्विंटल"
                    AppLanguage.ENGLISH -> "$crop today's APMC modal price: ₹$currentP/Qtl"
                }
            }

            else -> {
                _voiceFeedbackMessage.value = when (lang) {
                    AppLanguage.KANNADA -> "ಸ್ವೀಕರಿಸಿದ ಆದೇಶ: \"$rawCommand\". (ಕ್ಯಾಮೆರಾ, ಗ್ಯಾಲರಿ, ಅಥವಾ ರೋಗ ಪರೀಕ್ಷೆ ಎಂದು ಹೇಳಿ)"
                    AppLanguage.HINDI -> "आदेश मिला: \"$rawCommand\"। (कैमरा, गैलरी, या जांच कहें)"
                    AppLanguage.ENGLISH -> "Recognized: \"$rawCommand\". (Try saying 'Camera', 'Gallery', or 'Diagnose')"
                }
            }
        }
    }

    fun setTargetCrop(crop: String) {
        _targetCrop.value = crop
    }

    fun setFarmerNotes(notes: String) {
        _farmerNotes.value = notes
    }

    fun setSelectedImage(bitmap: Bitmap?) {
        _selectedImageBitmap.value = bitmap
    }

    fun setImageFromUri(uri: Uri) {
        try {
            val context = getApplication<Application>().applicationContext
            val inputStream = context.contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            _selectedImageBitmap.value = bitmap
        } catch (e: Exception) {
            _diagnosisError.value = "Failed to load image: ${e.message}"
        }
    }

    fun runCropDiagnosis() {
        viewModelScope.launch {
            _isDiagnosing.value = true
            _diagnosisError.value = null
            try {
                val crop = _targetCrop.value
                val notes = _farmerNotes.value
                val weather = _currentWeather.value
                val bitmap = _selectedImageBitmap.value

                val result = repository.diagnoseCrop(crop, notes, weather, bitmap)
                _latestDiagnosis.value = result

                // Auto save to Room database scoped to active farmer's account
                val weatherSummary = weather?.let {
                    "Temp: ${it.temperature}°C, RH: ${it.humidity}%, Rain: ${it.precipitation}mm in ${it.location}"
                } ?: "Standard Karnataka agricultural context"

                repository.saveDiagnosis(
                    result = result,
                    farmerNotes = notes,
                    weatherContext = weatherSummary,
                    imageUri = null,
                    farmerId = _activeFarmerId.value,
                    farmerName = _activeFarmerName.value,
                    village = _activeVillageName.value,
                    birthYear = _activeBirthYear.value,
                    farmerPhone = _activeFarmerPhone.value
                )
            } catch (e: Exception) {
                _diagnosisError.value = "Diagnostic error: ${e.message}"
            } finally {
                _isDiagnosing.value = false
            }
        }
    }

    fun deleteHistoryItem(id: Long) {
        viewModelScope.launch {
            repository.deleteDiagnosis(id)
        }
    }

    fun updateProfile(
        name: String,
        district: String,
        crops: String,
        acres: Double,
        village: String = _activeVillageName.value,
        birthYear: Int = _activeBirthYear.value
    ) {
        saveOrAlterFarmerProfile(
            name = name,
            village = village,
            birthYear = birthYear,
            district = district,
            crops = crops,
            acres = acres,
            phoneNumber = _activeFarmerPhone.value,
            lat = _farmerCoordinates.value.first,
            lon = _farmerCoordinates.value.second
        )
    }
}
