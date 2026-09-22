package com.example.data.model

import androidx.compose.ui.graphics.Color

data class CropDiagnosisResult(
    val diagnosis: String,
    val severity: String, // "Low", "Moderate", "High", "Severe"
    val confidence: Int,  // 0 - 100
    val summary: String,
    val immediateActions: List<String>,
    val weeds: List<String>,
    val selectiveHerbicides: List<String>,
    val organicFertilizers: List<String>,
    val chemicalFertilizers: List<String>,
    val safety: List<String>,
    val cropName: String = "",
    val timestamp: Long = System.currentTimeMillis()
) {
    fun getSeverityColor(): Color {
        return when (severity.lowercase()) {
            "low" -> Color(0xFF2E7D32)      // Green
            "moderate" -> Color(0xFFE65100) // Orange
            "high" -> Color(0xFFC62828)     // Red
            "severe" -> Color(0xFF880E4F)   // Deep Crimson/Purple
            else -> Color(0xFF455A64)
        }
    }
}

data class WeatherData(
    val location: String,
    val latitude: Double,
    val longitude: Double,
    val temperature: Double,
    val humidity: Double,
    val precipitation: Double,
    val windSpeed: Double,
    val weatherCode: Int,
    val agriAdvice: String,
    val isFungalRisk: Boolean = humidity > 75.0,
    val isSprayingFavorable: Boolean = precipitation == 0.0 && windSpeed < 15.0
)

data class MandiPriceInfo(
    val mandiName: String,
    val modalPrice: Double,
    val minPrice: Double,
    val maxPrice: Double,
    val dailyChangePercent: Double
)

data class PriceTrendPoint(
    val displayDate: String,
    val bengaluruPrice: Double,
    val mysuruPrice: Double,
    val chikkamagaluruPrice: Double
)

data class MarketAnalytics(
    val commodity: String,
    val trendHistory: List<PriceTrendPoint>,
    val mandiPrices: List<MandiPriceInfo>,
    val priceSpread: Double,
    val bestMandi: String,
    val arbitrageGain: Double,
    val recommendation: String
)

enum class SellDecision(val labelEn: String, val labelKn: String, val labelHi: String) {
    SELL_NOW_PEAK("Sell Now (Peak Price)", "ಇಂದೇ ಮಾರಾಟ ಮಾಡಿ (ಗರಿಷ್ಠ ದರ)", "आज ही बेचें (सर्वोत्तम भाव)"),
    HOLD_FOR_HIGHER("Hold & Wait 2-3 Days (Rising Trend)", "2-3 ದಿನ ನಿರೀಕ್ಷಿಸಿ (ಬೆಲೆ ಏರುತ್ತಿದೆ)", "2-3 दिन रुकें (भाव बढ़ रहा है)"),
    HARVEST_AND_SELL("Regular Sale (Stable Trend)", "ಸಾಮಾನ್ಯ ಮಾರಾಟ (ಸ್ಥಿರ ಬೆಲೆ)", "नियमित बिक्री (स्थिर भाव)"),
    SELL_IMMEDIATELY_DROPPING("Sell Fast (Supply Flooding)", "ತಕ್ಷಣ ಮಾರಾಟ ಮಾಡಿ (ಬೆಲೆ ಇಳಿಯುವ ಮುನ್ನ)", "जल्दी बेचें (भाव गिरने से पहले)")
}

data class DailyPriceRecord(
    val dateString: String,
    val dayOfWeekEn: String,
    val dayOfWeekKn: String,
    val dayOfWeekHi: String,
    val modalPrice: Double,
    val minPrice: Double,
    val maxPrice: Double,
    val dailyChange: Double,
    val dailyChangePercent: Double,
    val arrivalVolumeQtl: Int,
    val marketSentiment: String
)

data class WeeklyMarketAnalysis(
    val commodity: String,
    val reportDate: String,
    val formattedDateLongEn: String,
    val formattedDateLongKn: String,
    val formattedDateLongHi: String,
    val dailyRecords: List<DailyPriceRecord>,
    val weeklyAveragePrice: Double,
    val weeklyHighPrice: Double,
    val weeklyHighDate: String,
    val weeklyLowPrice: Double,
    val weeklyLowDate: String,
    val weeklyPriceChange: Double,
    val weeklyPriceChangePercent: Double,
    val sellDecision: SellDecision,
    val advisoryKn: String,
    val advisoryHi: String,
    val advisoryEn: String,
    val projectedPriceNextDays: String,
    val marketVolumeSummaryKn: String,
    val marketVolumeSummaryHi: String,
    val marketVolumeSummaryEn: String
)

data class KarnatakaDistrict(
    val name: String,
    val kannadaName: String,
    val hindiName: String = "",
    val lat: Double,
    val lon: Double
)

val KARNATAKA_DISTRICTS = listOf(
    KarnatakaDistrict("Chikkamagaluru", "ಚಿಕ್ಕಮಗಳೂರು", "चिकमगलूर", 13.3161, 75.7720),
    KarnatakaDistrict("Bengaluru Urban", "ಬೆಂಗಳೂರು ನಗರ", "बेंगलुरु शहरी", 12.9716, 77.5946),
    KarnatakaDistrict("Bengaluru Rural", "ಬೆಂಗಳೂರು ಗ್ರಾಮಾಂತರ", "बेंगलुरु ग्रामीण", 13.0980, 77.3910),
    KarnatakaDistrict("Hassan", "ಹಾಸನ", "हासन", 13.0033, 76.1004),
    KarnatakaDistrict("Shivamogga", "ಶಿವಮೊಗ್ಗ", "शिमोगा", 13.9299, 75.5681),
    KarnatakaDistrict("Mysuru", "ಮೈಸೂರು", "मैसूर", 12.2958, 76.6394),
    KarnatakaDistrict("Mandya", "ಮಂಡ್ಯ", "मंड्या", 12.5218, 76.8951),
    KarnatakaDistrict("Tumakuru", "ತುಮಕೂರು", "तुमकुरु", 13.3422, 77.1017),
    KarnatakaDistrict("Kolar", "ಕೋಲಾರ", "कोलार", 13.1367, 78.1291),
    KarnatakaDistrict("Chikkaballapura", "ಚಿಕ್ಕಬಳ್ಳಾಪುರ", "चिक्काबल्लापुर", 13.4325, 77.7275),
    KarnatakaDistrict("Chitradurga", "ಚಿತ್ರದುರ್ಗ", "चित्रदुर्ग", 14.2251, 76.3980),
    KarnatakaDistrict("Davanagere", "ದಾವಣಗೆರೆ", "दावणगेरे", 14.4644, 75.9218),
    KarnatakaDistrict("Ramanagara", "ರಾಮನಗರ", "रामनगर", 12.7150, 77.2810),
    KarnatakaDistrict("Kodagu", "ಕೊಡಗು", "कोडागु", 12.4244, 75.7382),
    KarnatakaDistrict("Dakshina Kannada", "ದಕ್ಷಿಣ ಕನ್ನಡ (ಮಂಗಳೂರು)", "दक्षिण कन्नड़ (मंगलुरु)", 12.9141, 74.8560),
    KarnatakaDistrict("Udupi", "ಉಡುಪಿ", "उडुपी", 13.3409, 74.7421),
    KarnatakaDistrict("Uttara Kannada", "ಉತ್ತರ ಕನ್ನಡ (ಕಾರವಾರ)", "उत्तर कन्नड़ (कारवार)", 14.8136, 74.1297),
    KarnatakaDistrict("Belagavi", "ಬೆಳಗಾವಿ", "बेलगावी", 15.8497, 74.4977),
    KarnatakaDistrict("Dharwad / Hubballi", "ಧಾರವಾಡ / ಹುಬ್ಬಳ್ಳಿ", "धारवाड़ / हुबली", 15.3647, 75.1240),
    KarnatakaDistrict("Gadag", "ಗದಗ", "गदग", 15.4167, 75.6333),
    KarnatakaDistrict("Haveri", "ಹಾವೇರಿ", "हावेरी", 14.7955, 75.3991),
    KarnatakaDistrict("Bagalkot", "ಬಾಗಲಕೋಟೆ", "बागलकोट", 16.1691, 75.6615),
    KarnatakaDistrict("Vijayapura", "ವಿಜಯಪುರ", "विजयपुर", 16.8302, 75.7100),
    KarnatakaDistrict("Kalaburagi", "ಕಲಬುರಗಿ", "कलबुर्गी", 17.3297, 76.8343),
    KarnatakaDistrict("Bidar", "ಬೀದರ್", "बीदर", 17.9104, 77.5199),
    KarnatakaDistrict("Raichur", "ರಾಯಚೂರು", "रायचूर", 16.2120, 77.3439),
    KarnatakaDistrict("Koppal", "ಕೊಪ್ಪಳ", "कोप्पल", 15.3444, 76.1558),
    KarnatakaDistrict("Ballari", "ಬಳ್ಳಾರಿ", "बल्लारी", 15.1394, 76.9214),
    KarnatakaDistrict("Vijayanagara", "ವಿಜಯನಗರ (ಹೊಸಪೇಟೆ)", "विजयनगर (होसपेटे)", 15.2689, 76.3909),
    KarnatakaDistrict("Yadgir", "ಯಾದಗಿರಿ", "यादगीर", 16.7700, 77.1400),
    KarnatakaDistrict("Chamarajanagar", "ಚಾಮರಾಜನಗರ", "चामराजनगर", 11.9261, 76.9437)
)

enum class AppLanguage(val code: String, val displayName: String, val nativeName: String, val localeCode: String) {
    KANNADA("kn", "Kannada", "ಕನ್ನಡ", "kn-IN"),
    HINDI("hi", "Hindi", "हिंदी", "hi-IN"),
    ENGLISH("en", "English", "English", "en-IN")
}

data class FertilizerRecommendation(
    val id: String,
    val name: String,
    val kannadaName: String,
    val hindiName: String,
    val category: String, // "Fast Growth Booster", "Chlorosis & White Leaf Cure", "Organic Vitalizer", "Wanted Crop Chemical"
    val targetCrops: List<String>,
    val dosagePerAcre: String,
    val dilutionPerLiter: String,
    val applicationMethod: String, // "Foliar Spray", "Soil Fertigation", "Basal Dressing"
    val optimalGrowthStage: String,
    val primaryBenefit: String,
    val precautions: String,
    val isFastGrowthBooster: Boolean = false,
    val curesWhiteLeaves: Boolean = false
)

data class VoiceChatMessage(
    val id: String = System.currentTimeMillis().toString(),
    val sender: String, // "user" or "ai"
    val text: String,
    val language: AppLanguage,
    val timestamp: Long = System.currentTimeMillis()
)

data class GeminiWeatherAdvisory(
    val summary: String,
    val sporeRiskLevel: String,
    val sprayWindow: String,
    val irrigationAdvice: String,
    val generatedAt: Long = System.currentTimeMillis()
)

val RECOMMENDED_FERTILIZERS = listOf(
    FertilizerRecommendation(
        id = "npk_191919",
        name = "19:19:19 (Water Soluble NPK)",
        kannadaName = "19:19:19 ನೀರಿನಲ್ಲಿ ಕರಗುವ ಗೊಬ್ಬರ",
        hindiName = "19:19:19 घुलनशील उर्वरक",
        category = "Fast Growth Booster",
        targetCrops = listOf("Maize", "Tomato", "Potato", "Chilli", "Vegetables"),
        dosagePerAcre = "1.0 - 1.5 kg / acre",
        dilutionPerLiter = "5.0 g / Litre of water",
        applicationMethod = "Foliar Spray / Drip Fertigation",
        optimalGrowthStage = "Early Vegetative stage (15 - 35 days after sowing)",
        primaryBenefit = "Provides balanced Nitrogen, Phosphorus & Potassium directly to foliage for explosive green leaf expansion and vigorous stem girth.",
        precautions = "Spray during cool morning or evening hours; avoid mixing with calcium nitrate.",
        isFastGrowthBooster = true,
        curesWhiteLeaves = false
    ),
    FertilizerRecommendation(
        id = "zinc_sulphate",
        name = "Zinc Sulphate (ZnSO4 21% / Chelated Zinc EDTA 12%)",
        kannadaName = "ಜಿಂಕ್ ಸಲ್ಫೇಟ್ (ಬಿಳಿ ಎಲೆ ರೋಗ & ಬಿಳಿ ಮೊಗ್ಗು ನಿವಾರಕ)",
        hindiName = "जिंक सल्फेट (सफेद पत्ती और सफेद कली का इलाज)",
        category = "Chlorosis & White Leaf Cure",
        targetCrops = listOf("Maize", "Paddy", "Tomato", "Arecanut"),
        dosagePerAcre = "ZnSO4 21%: 5.0 kg/acre soil or 1.0 kg/acre foliar; Zinc EDTA: 250 g/acre",
        dilutionPerLiter = "Zinc EDTA 12%: 1.0 - 1.5 g / L; ZnSO4 21%: 3.0 g / L (with 1.5 g slaked lime)",
        applicationMethod = "Foliar Spray & Soil Basal",
        optimalGrowthStage = "Vegetative stage when patchy white leaf / bleached bands appear",
        primaryBenefit = "Cures Maize White Bud (ಬಿಳಿ ಮೊಗ್ಗು) and patchy white chlorotic leaf stripping; restores enzyme synthesis and dense chlorophyll production.",
        precautions = "Never mix inorganic zinc sulphate directly with phosphatic fertilizers like DAP.",
        isFastGrowthBooster = true,
        curesWhiteLeaves = true
    ),
    FertilizerRecommendation(
        id = "urea_nano",
        name = "Nano Urea / Agricultural Urea (46% N)",
        kannadaName = "ನ್ಯಾನೋ ಯೂರಿಯಾ / ಸಾರಜನಕ ರಸಗೊಬ್ಬರ",
        hindiName = "नैनो यूरिया / नाइट्रोजन उर्वरक",
        category = "Fast Growth Booster",
        targetCrops = listOf("Maize", "Paddy", "Tomato", "Potato", "Coffee"),
        dosagePerAcre = "Nano Urea: 250-500 ml/acre; Conventional: 25-30 kg/acre top dressing",
        dilutionPerLiter = "Nano Urea: 2.0 - 4.0 ml / Litre",
        applicationMethod = "Foliar Spray (Nano) / Soil Top-dress (Conventional)",
        optimalGrowthStage = "Peak vegetative tillering and knee-high stage",
        primaryBenefit = "Rapid surge of amino acids, protein synthesis, dark green foliar canopy, and height growth.",
        precautions = "Avoid excess nitrogen during flowering as it may promote succulent tissue vulnerable to blight.",
        isFastGrowthBooster = true,
        curesWhiteLeaves = false
    ),
    FertilizerRecommendation(
        id = "seaweed_humic",
        name = "Seaweed Extract + Humic Acid 12% (Bio-Stimulant)",
        kannadaName = "ಸಮುದ್ರ ಪಾಚಿ ಸಾರ ಮತ್ತು ಹ್ಯೂಮಿಕ್ ಆಮ್ಲ (ಬೇರು & ಚಿಗುರು ವರ್ಧಕ)",
        hindiName = "सीवीड एक्सट्रैक्ट और ह्यूमिक एसिड",
        category = "Organic Vitalizer",
        targetCrops = listOf("Maize", "Tomato", "Arecanut", "Potato", "Coffee", "Chilli"),
        dosagePerAcre = "500 ml / acre foliar or 1.0 L / acre drenching",
        dilutionPerLiter = "2.0 - 3.0 ml / Litre of water",
        applicationMethod = "Foliar Spray & Root Drenching",
        optimalGrowthStage = "Active vegetative growth and after any pest shock",
        primaryBenefit = "Stimulates explosive white feeder root formation, unlocks tied-up soil nutrients, and speeds plant recovery.",
        precautions = "Compatible with organic and chemical sprays; store away from direct sunlight.",
        isFastGrowthBooster = true,
        curesWhiteLeaves = false
    ),
    FertilizerRecommendation(
        id = "ga3_booster",
        name = "Gibberellic Acid (GA3 0.001% L)",
        kannadaName = "ಜಿಬ್ರೆಲಿಕ್ ಆಮ್ಲ (ಗಿಡದ ತ್ವರಿತ ಎತ್ತರ & ಹೂವು-ಕಾಯಿ ವೃದ್ಧಿ)",
        hindiName = "जिबरेलिक एसिड (पौधे की तीव्र वृद्धि)",
        category = "Fast Growth Booster",
        targetCrops = listOf("Maize", "Tomato", "Potato", "Chilli"),
        dosagePerAcre = "250 ml / acre",
        dilutionPerLiter = "1.0 - 1.5 ml / Litre",
        applicationMethod = "Foliar Spray",
        optimalGrowthStage = "Vegetative to early pre-flowering stage",
        primaryBenefit = "Dramatically accelerates cellular elongation, internode lengthening, and plant vigor.",
        precautions = "Ensure adequate soil moisture and base NPK fertilization before application.",
        isFastGrowthBooster = true,
        curesWhiteLeaves = false
    ),
    FertilizerRecommendation(
        id = "panchagavya",
        name = "Panchagavya (Organic Vedic Growth Tonic)",
        kannadaName = "ಪಂಚಗವ್ಯ ಸಾವಯವ ಬೆಳವಣಿಗೆ ದ್ರಾವಣ",
        hindiName = "पंचगव्य जैविक टॉनिक",
        category = "Organic Vitalizer",
        targetCrops = listOf("All Crops", "Maize", "Tomato", "Arecanut", "Paddy"),
        dosagePerAcre = "3.0 - 5.0 Litres / acre",
        dilutionPerLiter = "30 ml / Litre (3% foliar solution)",
        applicationMethod = "Foliar Spray & Soil Fertigation",
        optimalGrowthStage = "Every 15-20 days throughout crop lifecycle",
        primaryBenefit = "Contains natural auxins, cytokinins, and lactobacillus bacteria; triggers natural systemic resistance (SAR) against fungal spots.",
        precautions = "Filter thoroughly before pouring into knapsack sprayer to avoid nozzle choking.",
        isFastGrowthBooster = true,
        curesWhiteLeaves = false
    ),
    FertilizerRecommendation(
        id = "coragen_chemical",
        name = "Coragen (Chlorantraniliprole 18.5% SC)",
        kannadaName = "ಕೋರಾಜನ್ ಕೀಟನಾಶಕ (ಮೆಕ್ಕೆಜೋಳದ ಸೈನಿಕ ಹುಳು ನಿಯಂತ್ರಣ)",
        hindiName = "कोराजन (फॉल आर्मीवॉर्म व इल्ली नियंत्रण)",
        category = "Wanted Crop Chemical",
        targetCrops = listOf("Maize", "Tomato", "Paddy", "Chilli"),
        dosagePerAcre = "60 ml / acre",
        dilutionPerLiter = "0.4 ml / Litre of water",
        applicationMethod = "Directed Foliar Spray into plant whorls",
        optimalGrowthStage = "First sighting of Fall Armyworm (FAW) or tomato fruit borer",
        primaryBenefit = "Unrivaled control against destructive Fall Armyworm (Spodoptera frugiperda) and borers; safeguards young cobs and foliage.",
        precautions = "Wear protective chemical mask and goggles; avoid spraying when honeybees are active.",
        isFastGrowthBooster = false,
        curesWhiteLeaves = false
    ),
    FertilizerRecommendation(
        id = "ridomil_metalaxyl",
        name = "Ridomil Gold (Metalaxyl-M 4% + Mancozeb 64% WP)",
        kannadaName = "ರಿಡೋಮಿಲ್ ಗೋಲ್ಡ್ (ಡೌನಿ ಮಿಲ್ಡ್ಯೂ ಮತ್ತು ಎಲೆ ರೋಗ ನಿವಾರಕ)",
        hindiName = "रिडोमिल गोल्ड (डाउनी फफूंद व झुलसा नाशक)",
        category = "Wanted Crop Chemical",
        targetCrops = listOf("Maize", "Potato", "Tomato", "Grapes", "Vegetables"),
        dosagePerAcre = "500 g / acre",
        dilutionPerLiter = "2.0 - 2.5 g / Litre of water",
        applicationMethod = "Foliar Spray & Root Collar Drench",
        optimalGrowthStage = "High humidity or initial appearance of patchy white downy spots / blights",
        primaryBenefit = "Dual systemic and contact fungicidal action; halts spore multiplication inside vascular tissues within 2 hours.",
        precautions = "14 days pre-harvest safety interval; alternate with copper fungicides to prevent resistance.",
        isFastGrowthBooster = false,
        curesWhiteLeaves = true
    ),
    FertilizerRecommendation(
        id = "atrazine_herbicide",
        name = "Atrazine 50% WP (Selective Maize Herbicide)",
        kannadaName = "ಅಟ್ರಾಜಿನ್ ಕಳೆನಾಶಕ (ಮೆಕ್ಕೆಜೋಳಕ್ಕೆ ಅನುಮೋದಿತ ಕಳೆ ನಿಯಂತ್ರಕ)",
        hindiName = "एट्राजिन 50% डब्ल्यूपी (मक्का खरपतवार नाशक)",
        category = "Wanted Crop Chemical",
        targetCrops = listOf("Maize"),
        dosagePerAcre = "800 g - 1.0 kg / acre",
        dilutionPerLiter = "4.0 - 5.0 g / Litre of water",
        applicationMethod = "Pre-emergence spray on moist soil within 2-3 days of sowing",
        optimalGrowthStage = "Pre-emergence or early post-emergence (before weeds cross 2-leaf stage)",
        primaryBenefit = "Selective weed destruction (broadleaf and annual grasses) without harming maize seedlings, allowing fast unchecked maize establishment.",
        precautions = "Ensure uniform soil moisture; use flat-fan nozzle for even ground curtain.",
        isFastGrowthBooster = false,
        curesWhiteLeaves = false
    )
)

enum class KarnatakaCropCategory(val labelEn: String, val labelKn: String, val labelHi: String) {
    ALL("All Crops", "ಎಲ್ಲಾ ಬೆಳೆಗಳು", "सभी फसलें"),
    CEREALS_MILLETS("Cereals & Millets", "ಧಾನ್ಯಗಳು & ಸಿರಿಧಾನ್ಯ", "अनाज व बाजरा"),
    COMMERCIAL_CASH("Commercial & Cash", "ವಾಣಿಜ್ಯ ಬೆಳೆಗಳು", "वाणिज्यिक फसलें"),
    PULSES_OILSEEDS("Pulses & Oilseeds", "ದ್ವಿದಳ ಧಾನ್ಯ & ಎಣ್ಣೆಕಾಳು", "दालें व तिलहन"),
    PLANTATION_SPICES("Plantation & Spices", "ತೋಟಗಾರಿಕೆ & ಮಸಾಲೆ", "बागवानी व मसाले"),
    VEGETABLES("Vegetables", "ತರಕಾರಿಗಳು", "सब्जियां"),
    FRUITS("Fruits", "ಹಣ್ಣಿನ ಬೆಳೆಗಳು", "फल"),
    FLORICULTURE_SILK("Flowers & Silk", "ಹೂವು & ರೇಷ್ಮೆ", "फूल व रेशम")
}

data class KarnatakaCropItem(
    val id: String,
    val nameEn: String,
    val nameKn: String,
    val nameHi: String,
    val category: KarnatakaCropCategory,
    val primaryDiseases: List<String>,
    val commonSymptoms: String,
    val iconEmoji: String,
    val majorDistricts: String,
    val sampleDiseaseTitle: String = "",
    val sampleSymptomNotes: String = ""
)

val KARNATAKA_CROPS_CATALOG: List<KarnatakaCropItem> = listOf(
    // 1. CEREALS & MILLETS
    KarnatakaCropItem(
        id = "ragi",
        nameEn = "Ragi / Finger Millet",
        nameKn = "ರಾಗಿ",
        nameHi = "रागी / मड़ुआ",
        category = KarnatakaCropCategory.CEREALS_MILLETS,
        primaryDiseases = listOf("Neck & Finger Blast (ಬೆಂಕಿ ರೋಗ)", "Foot Rot / Seedling Blight", "Brown Leaf Spot"),
        commonSymptoms = "Diamond-shaped spindle lesions on leaves, blackened neck nodes leading to earhead lodging.",
        iconEmoji = "🥣",
        majorDistricts = "Mandya, Mysuru, Tumakuru, Hassan, Kolar, Ramanagara",
        sampleDiseaseTitle = "Ragi Finger & Neck Blast (Magnaporthe grisea)",
        sampleSymptomNotes = "Diamond-shaped spindle lesions with grey center on leaves and black neck node lesion preventing grain filling."
    ),
    KarnatakaCropItem(
        id = "paddy",
        nameEn = "Paddy / Rice",
        nameKn = "ಭತ್ತ",
        nameHi = "धान / चावल",
        category = KarnatakaCropCategory.CEREALS_MILLETS,
        primaryDiseases = listOf("Paddy Blast (ಪೈರಿಕ್ಯುಲೇರಿಯಾ)", "Bacterial Leaf Blight (BLB)", "Sheath Blight", "Brown Spot"),
        commonSymptoms = "Spindle eye-shaped lesions, water-soaked wavy leaf margin yellowing, reddish brown leaf spots.",
        iconEmoji = "🌾",
        majorDistricts = "Mandya, Shimoga, Davanagere, Raichur, Bellary, Karavali",
        sampleDiseaseTitle = "Paddy Blast & Bacterial Leaf Blight",
        sampleSymptomNotes = "Spindle-shaped brown lesions on leaves and yellowish wavy margins running downward along veins."
    ),
    KarnatakaCropItem(
        id = "maize",
        nameEn = "Maize / Corn",
        nameKn = "ಮೆಕ್ಕೆಜೋಳ",
        nameHi = "मक्का",
        category = KarnatakaCropCategory.CEREALS_MILLETS,
        primaryDiseases = listOf("Downy Mildew (ಡೌನಿ ಮಿಲ್ಡ್ಯೂ)", "Zinc Deficiency White Bud (ಬಿಳಿ ಮೊಗ್ಗು)", "Northern Corn Leaf Blight", "Fall Armyworm"),
        commonSymptoms = "Patchy white bleached leaf stripes, downy mycelium under leaf, leaf windowing and ragged whorl feeding.",
        iconEmoji = "🌽",
        majorDistricts = "Davanagere, Haveri, Belagavi, Bagalkote, Chitradurga",
        sampleDiseaseTitle = "Maize Patchy White Leaf & Zinc Deficiency",
        sampleSymptomNotes = "Young leaves showing patchy white bleached bands between midrib and margins, chlorotic stripes, and downy mold under leaf."
    ),
    KarnatakaCropItem(
        id = "jowar",
        nameEn = "Jowar / Sorghum",
        nameKn = "ಜೋಳ",
        nameHi = "ज्वार",
        category = KarnatakaCropCategory.CEREALS_MILLETS,
        primaryDiseases = listOf("Anthracnose & Red Rot", "Grain Mold", "Charcoal Rot", "Shoot Fly / Stem Borer"),
        commonSymptoms = "Small circular reddish-purple leaf spots with dark margins, premature stalk lodging, dead heart in seedlings.",
        iconEmoji = "🌾",
        majorDistricts = "Kalaburagi, Vijayapura, Bagalkote, Dharwad, Gadag",
        sampleDiseaseTitle = "Jowar Anthracnose & Shoot Fly Deadheart",
        sampleSymptomNotes = "Elliptical reddish-purple spots on midrib and leaf blades with center deadheart in young seedling stage."
    ),
    KarnatakaCropItem(
        id = "bajra",
        nameEn = "Bajra / Pearl Millet",
        nameKn = "ಸಜ್ಜೆ",
        nameHi = "बाजरा",
        category = KarnatakaCropCategory.CEREALS_MILLETS,
        primaryDiseases = listOf("Downy Mildew / Green Ear (ಹಸಿರು ತೆನೆ ರೋಗ)", "Rust (ತುಕ್ಕು ರೋಗ)", "Ergot"),
        commonSymptoms = "Transformation of earhead floral parts into leafy structures, honey-dew secretion, reddish-brown pustules.",
        iconEmoji = "🌾",
        majorDistricts = "Raichur, Koppal, Yadgir, Bellary, Bagalkote",
        sampleDiseaseTitle = "Bajra Green Ear & Downy Mildew",
        sampleSymptomNotes = "Earhead converted into twisted mass of green leafy structures with chlorotic yellow striping on foliage."
    ),
    KarnatakaCropItem(
        id = "navane",
        nameEn = "Foxtail Millet / Navane",
        nameKn = "ನವಣೆ / ಸಿರಿಧಾನ್ಯ",
        nameHi = "कंगनी बाजरा",
        category = KarnatakaCropCategory.CEREALS_MILLETS,
        primaryDiseases = listOf("Blast (ಬೆಂಕಿ ರೋಗ)", "Smut", "Rust"),
        commonSymptoms = "Brown eye-shaped foliar spots and smut sori destroying spikelets.",
        iconEmoji = "🥣",
        majorDistricts = "Haveri, Chitradurga, Tumakuru, Bellary",
        sampleDiseaseTitle = "Foxtail Millet Blast & Smut",
        sampleSymptomNotes = "Small brownish spindle spots on leaves and dark powdery smut sori in earheads."
    ),

    // 2. COMMERCIAL & CASH CROPS
    KarnatakaCropItem(
        id = "sugarcane",
        nameEn = "Sugarcane",
        nameKn = "ಕಬ್ಬು",
        nameHi = "गन्ना",
        category = KarnatakaCropCategory.COMMERCIAL_CASH,
        primaryDiseases = listOf("Red Rot (ಕೆಂಪು ಕೊಳೆ ರೋಗ)", "Smut (ಕಾಡಿಗೆ ರೋಗ)", "Grassy Shoot Disease", "Woolly Aphid"),
        commonSymptoms = "Red internal stem discoloration with white horizontal cross-bands, whip-like black dusty shoot, profuse tillering.",
        iconEmoji = "🪴",
        majorDistricts = "Mandya, Belagavi, Bagalkote, Davanagere, Mysuru",
        sampleDiseaseTitle = "Sugarcane Red Rot (Colletotrichum falcatum)",
        sampleSymptomNotes = "Third and fourth leaf drooping with longitudinal red lesions on midrib; split cane reveals sour alcohol smell and white patches."
    ),
    KarnatakaCropItem(
        id = "cotton",
        nameEn = "Cotton",
        nameKn = "ಹತ್ತಿ",
        nameHi = "कपास",
        category = KarnatakaCropCategory.COMMERCIAL_CASH,
        primaryDiseases = listOf("Bacterial Blight / Angular Leaf Spot", "Alternaria Leaf Spot", "Pink Bollworm", "Leaf Curl Virus"),
        commonSymptoms = "Angular water-soaked spots bounded by veinlets, dark lesions on bolls, curling and thickening of leaf veins.",
        iconEmoji = "☁️",
        majorDistricts = "Haveri, Dharwad, Raichur, Bellary, Kalaburagi",
        sampleDiseaseTitle = "Cotton Bacterial Angular Leaf Spot & Bollworm",
        sampleSymptomNotes = "Water-soaked angular vein-delimited leaf spots turning blackish brown and bore holes in young bolls."
    ),
    KarnatakaCropItem(
        id = "tobacco",
        nameEn = "Tobacco",
        nameKn = "ತಂಬಾಕು",
        nameHi = "तंबाकू",
        category = KarnatakaCropCategory.COMMERCIAL_CASH,
        primaryDiseases = listOf("Black Shank (ಕಪ್ಪು ಕಾಂಡ ರೋಗ)", "Frog Eye Leaf Spot", "Tobacco Mosaic Virus (TMV)", "Root Knot Nematode"),
        commonSymptoms = "Blackening of collar and basal stem, circular brown leaf spots with ash-grey papery center, mosaic mottling.",
        iconEmoji = "🍂",
        majorDistricts = "Mysuru (Hunsur, Periyapatna), Hassan, Shivamogga",
        sampleDiseaseTitle = "Tobacco Black Shank & Frog Eye Spot",
        sampleSymptomNotes = "Base of stem turning dark black and discing inside pith, circular parchment-like frog-eye leaf lesions."
    ),

    // 3. PULSES & OILSEEDS
    KarnatakaCropItem(
        id = "tur",
        nameEn = "Red Gram / Tur / Pigeon Pea",
        nameKn = "ತೊಗರಿ",
        nameHi = "अरहर / तुअर",
        category = KarnatakaCropCategory.PULSES_OILSEEDS,
        primaryDiseases = listOf("Fusarium Wilt (ಸೊರಗು ರೋಗ)", "Sterility Mosaic (ಬಂಜೆ ರೋಗ)", "Phytophthora Blight", "Pod Borer / Helicoverpa"),
        commonSymptoms = "Gradual unilateral yellowing and wilting of plants, bushy pale green sterile foliage, holes in green pods.",
        iconEmoji = "🫘",
        majorDistricts = "Kalaburagi (Tur Bowl), Bidar, Yadgir, Vijayapura, Raichur",
        sampleDiseaseTitle = "Red Gram Fusarium Wilt & Sterility Mosaic",
        sampleSymptomNotes = "Sudden wilting of branches with brown vascular discoloration under bark; excessive bushy sterile green leaves without flowers."
    ),
    KarnatakaCropItem(
        id = "chickpea",
        nameEn = "Bengal Gram / Chickpea / Chana",
        nameKn = "ಕಡಲೆ",
        nameHi = "चना / छोले",
        category = KarnatakaCropCategory.PULSES_OILSEEDS,
        primaryDiseases = listOf("Dry Root Rot (ಒಣ ಬೇರು ಕೊಳೆ)", "Fusarium Wilt", "Ascochyta Blight", "Pod Borer"),
        commonSymptoms = "Drooping of petioles, straw-colored dried canopy, brittle taproot with shredded blackened xylem vessels.",
        iconEmoji = "🫘",
        majorDistricts = "Kalaburagi, Vijayapura, Dharwad, Gadag, Bidar",
        sampleDiseaseTitle = "Bengal Gram Dry Root Rot & Wilt",
        sampleSymptomNotes = "Canopy turns suddenly straw-yellow during flowering; taproot shows blackened brittle root tips and shredded bark."
    ),
    KarnatakaCropItem(
        id = "greengram",
        nameEn = "Green Gram / Moong",
        nameKn = "ಹೆಸರು ಕಾಳು",
        nameHi = "मूंग",
        category = KarnatakaCropCategory.PULSES_OILSEEDS,
        primaryDiseases = listOf("Yellow Mosaic Virus (MYMV)", "Powdery Mildew (ಬೂದಿ ರೋಗ)", "Cercospora Leaf Spot"),
        commonSymptoms = "Bright yellow diffuse patches alternating with green on trifoliate leaves, white talcum-like powdery coating.",
        iconEmoji = "🌱",
        majorDistricts = "Dharwad, Gadag, Belagavi, Bidar, Haveri",
        sampleDiseaseTitle = "Green Gram Yellow Mosaic Virus (MYMV)",
        sampleSymptomNotes = "Bright yellow mosaic patches on leaves spread by whitefly vector and powdery white mildew on lower foliage."
    ),
    KarnatakaCropItem(
        id = "blackgram",
        nameEn = "Black Gram / Urad",
        nameKn = "ಉದ್ದು",
        nameHi = "उड़द",
        category = KarnatakaCropCategory.PULSES_OILSEEDS,
        primaryDiseases = listOf("Yellow Mosaic Virus", "Powdery Mildew", "Root Rot", "Leaf Crinkle"),
        commonSymptoms = "Mosaic mottling, crinkling and curling of leaf laminae, root decay under moisture stress.",
        iconEmoji = "🌱",
        majorDistricts = "Bidar, Kalaburagi, Mysuru, Belagavi",
        sampleDiseaseTitle = "Black Gram Leaf Crinkle & Mosaic",
        sampleSymptomNotes = "Severe crinkling and thickening of leaves with stunted internodes and yellow mosaic mottling."
    ),
    KarnatakaCropItem(
        id = "groundnut",
        nameEn = "Groundnut / Peanut",
        nameKn = "ಕಡಲೆಕಾಯಿ / ನೆಲಗಡಲೆ",
        nameHi = "मूंगफली",
        category = KarnatakaCropCategory.PULSES_OILSEEDS,
        primaryDiseases = listOf("Tikka Leaf Spot (ತಿಕ್ಕಾ ರೋಗ)", "Collar Rot / Crown Rot", "Rust", "Stem Rot (Sclerotium)"),
        commonSymptoms = "Circular reddish-brown necrotic spots with bright yellow halos, white mycelial fan around collar, orange-brown rust pustules.",
        iconEmoji = "🥜",
        majorDistricts = "Tumakuru, Chitradurga, Chikkaballapura, Kolar, Bellary",
        sampleDiseaseTitle = "Groundnut Early & Late Tikka Leaf Spot",
        sampleSymptomNotes = "Circular dark brown foliar spots surrounded by prominent yellow chlorotic halos, severe premature defoliation."
    ),
    KarnatakaCropItem(
        id = "soybean",
        nameEn = "Soybean",
        nameKn = "ಸೋಯಾಬೀನ್",
        nameHi = "सोयाबीन",
        category = KarnatakaCropCategory.PULSES_OILSEEDS,
        primaryDiseases = listOf("Soybean Rust (ತುಕ್ಕು ರೋಗ)", "Charcoal Rot", "Yellow Mosaic Virus", "Anthracnose"),
        commonSymptoms = "Tan to dark reddish-brown polygonal lesions with uredinia on abaxial leaf, premature defoliation, black sclerotia inside pith.",
        iconEmoji = "🫘",
        majorDistricts = "Belagavi, Dharwad, Haveri, Bidar",
        sampleDiseaseTitle = "Soybean Asian Rust & Charcoal Rot",
        sampleSymptomNotes = "Tiny brown polygonal spots rapidly blistering on underside of leaves, sudden yellowing and premature leaf drop."
    ),
    KarnatakaCropItem(
        id = "sunflower",
        nameEn = "Sunflower",
        nameKn = "ಸೂರ್ಯಕಾಂತಿ",
        nameHi = "सूरजमुखी",
        category = KarnatakaCropCategory.PULSES_OILSEEDS,
        primaryDiseases = listOf("Alternaria Blight (ಎಲೆ ಕರಕಲು ರೋಗ)", "Head Rot / Rhizopus", "Necrosis Virus", "Powdery Mildew"),
        commonSymptoms = "Dark brown concentric lesions on leaves, rotting of spongy thalamus head, terminal bud necrosis.",
        iconEmoji = "🌻",
        majorDistricts = "Koppal, Raichur, Bellary, Bagalkote, Chitradurga",
        sampleDiseaseTitle = "Sunflower Alternaria Leaf Blight & Head Rot",
        sampleSymptomNotes = "Dark brown circular spots with concentric rings on leaves, brown rot on the back of developing flower head."
    ),

    // 4. PLANTATION & SPICES
    KarnatakaCropItem(
        id = "arecanut",
        nameEn = "Arecanut / Betel Nut",
        nameKn = "ಅಡಿಕೆ",
        nameHi = "सुपारी",
        category = KarnatakaCropCategory.PLANTATION_SPICES,
        primaryDiseases = listOf("Koleroga / Fruit Rot (ಕೊಳೆ ರೋಗ)", "Yellow Leaf Disease (YLD)", "Bud Rot (ಸುಳಿ ಕೊಳೆ)", "Anabe Roga (Foot Rot)"),
        commonSymptoms = "Water-soaked dark lesions on green nuts causing premature dropping, yellowing of inner whorl leaflets, crown rot.",
        iconEmoji = "🌴",
        majorDistricts = "Shimoga, Chikkamagaluru, Dakshina Kannada, Uttara Kannada, Davanagere, Tumakuru",
        sampleDiseaseTitle = "Arecanut Koleroga (Phytophthora meadii)",
        sampleSymptomNotes = "Water-soaked dark lesions at base of developing nuts, white fungal bloom on calyx, heavy dropping of green immature nuts."
    ),
    KarnatakaCropItem(
        id = "coffee",
        nameEn = "Coffee (Arabica & Robusta)",
        nameKn = "ಕಾಫಿ",
        nameHi = "कॉफ़ी",
        category = KarnatakaCropCategory.PLANTATION_SPICES,
        primaryDiseases = listOf("Coffee Leaf Rust (ಎಲೆ ತುಕ್ಕು ರೋಗ)", "Black Rot / Koleroga", "Berry Borer", "Root Rot"),
        commonSymptoms = "Orange-yellow powdery spots on lower leaf surface, hanging black dried leaves bound by fungal threads, bored pinholes in berries.",
        iconEmoji = "☕",
        majorDistricts = "Chikkamagaluru, Kodagu, Hassan, Shimoga",
        sampleDiseaseTitle = "Coffee Leaf Rust (Hemileia vastatrix)",
        sampleSymptomNotes = "Bright yellow to orange powdery spore masses on the lower surface of leaves with defoliation of fruit-bearing branches."
    ),
    KarnatakaCropItem(
        id = "coconut",
        nameEn = "Coconut",
        nameKn = "ತೆಂಗು",
        nameHi = "नारियल",
        category = KarnatakaCropCategory.PLANTATION_SPICES,
        primaryDiseases = listOf("Bud Rot (ಸುಳಿ ಕೊಳೆ ರೋಗ)", "Root (Wilt) Disease", "Stem Bleeding (ಕಾಂಡ ಸ್ರಾವ)", "Rhinoceros Beetle & Whitefly"),
        commonSymptoms = "Yellowing of central spindle leaf followed by foul-smelling rotting heart, reddish-brown liquid exuding from stem cracks.",
        iconEmoji = "🥥",
        majorDistricts = "Tumakuru, Hassan, Mandya, Dakshina Kannada, Mysuru, Udupi",
        sampleDiseaseTitle = "Coconut Bud Rot & Stem Bleeding",
        sampleSymptomNotes = "Central spear leaf turning pale brown and rotting with foul odor, rust-brown liquid oozing from lower trunk fissures."
    ),
    KarnatakaCropItem(
        id = "pepper",
        nameEn = "Black Pepper",
        nameKn = "ಕರಿಮೆಣಸು",
        nameHi = "काली मिर्च",
        category = KarnatakaCropCategory.PLANTATION_SPICES,
        primaryDiseases = listOf("Quick Wilt / Foot Rot (Phytophthora)", "Slow Decline (ಸೊರಗು ರೋಗ)", "Pollu Disease (Anthracnose)"),
        commonSymptoms = "Black lesions on runner shoots and collar, sudden flaccid wilting and defoliation of whole vine within days.",
        iconEmoji = "🌿",
        majorDistricts = "Kodagu, Uttara Kannada, Chikkamagaluru, Dakshina Kannada, Shimoga",
        sampleDiseaseTitle = "Black Pepper Quick Wilt (Phytophthora capsici)",
        sampleSymptomNotes = "Dark water-soaked lesions on collar zone at soil level, rapid drooping of leaves, and sudden vine collapse."
    ),
    KarnatakaCropItem(
        id = "cardamom",
        nameEn = "Cardamom",
        nameKn = "ಏಲಕ್ಕಿ",
        nameHi = "इलायची",
        category = KarnatakaCropCategory.PLANTATION_SPICES,
        primaryDiseases = listOf("Katte Disease / Mosaic (ಕಟ್ಟೆ ರೋಗ)", "Capsule Rot / Azhukal (ಕೊಳೆ ರೋಗ)", "Clump Rot / Rhizome Rot"),
        commonSymptoms = "Discontinuous chlorotic stripes on young leaves, water-soaked brown discoloration and rotting of immature capsules.",
        iconEmoji = "🌱",
        majorDistricts = "Kodagu, Hassan (Sakleshpur), Chikkamagaluru",
        sampleDiseaseTitle = "Cardamom Katte Mosaic & Capsule Rot",
        sampleSymptomNotes = "Pale green chlorotic streaks running parallel to veins and rotting of developing fruit capsules with foul smell."
    ),
    KarnatakaCropItem(
        id = "ginger",
        nameEn = "Ginger",
        nameKn = "ಶುಂಠಿ",
        nameHi = "अदरक",
        category = KarnatakaCropCategory.PLANTATION_SPICES,
        primaryDiseases = listOf("Rhizome Soft Rot (ಗೆಡ್ಡೆ ಕೊಳೆ ರೋಗ)", "Bacterial Wilt (ಬ್ಯಾಕ್ಟೀರಿಯಲ್ ಸೊರಗು)", "Leaf Blotch"),
        commonSymptoms = "Water-soaked collar zone, pseudostem easily pulls out with rotten base, honey-like slime exuding upon squeezing.",
        iconEmoji = "🫚",
        majorDistricts = "Shimoga, Hassan, Kodagu, Chamarajanagar, Belagavi",
        sampleDiseaseTitle = "Ginger Rhizome Soft Rot (Pythium aphanidermatum)",
        sampleSymptomNotes = "Water-soaked pseudostem base that slips off easily when pulled; underground rhizomes turning soft, spongy, and putrid."
    ),
    KarnatakaCropItem(
        id = "turmeric",
        nameEn = "Turmeric",
        nameKn = "ಅರಿಶಿನ",
        nameHi = "हल्दी",
        category = KarnatakaCropCategory.PLANTATION_SPICES,
        primaryDiseases = listOf("Leaf Blotch (Taphrina maculans)", "Rhizome Rot", "Leaf Spot (Colletotrichum)"),
        commonSymptoms = "Small, oval brownish-yellow spots on both leaf surfaces coalescing into dirty yellow blotches, decay of mother rhizomes.",
        iconEmoji = "🟡",
        majorDistricts = "Chamarajanagar, Belagavi, Bagalkote, Davanagere",
        sampleDiseaseTitle = "Turmeric Leaf Blotch & Rhizome Rot",
        sampleSymptomNotes = "Upper and lower leaf surfaces covered with numerous small yellow-brown blister-like blotches and drying tips."
    ),

    // 5. VEGETABLES
    KarnatakaCropItem(
        id = "tomato",
        nameEn = "Tomato",
        nameKn = "ಟೊಮೆಟೊ",
        nameHi = "टमाटर",
        category = KarnatakaCropCategory.VEGETABLES,
        primaryDiseases = listOf("Early Blight (ಅಲ್ಟರ್ನೇರಿಯಾ)", "Late Blight", "Tomato Leaf Curl Virus (ToLCV)", "Bacterial Wilt"),
        commonSymptoms = "Target-board concentric rings on lower leaves, curling and puckering of young leaves, sudden green wilt without yellowing.",
        iconEmoji = "🍅",
        majorDistricts = "Kolar (Tomato Mandi), Chikkaballapura, Belagavi, Ramanagara, Bengaluru Rural",
        sampleDiseaseTitle = "Tomato Early Blight & Leaf Curl Virus",
        sampleSymptomNotes = "Concentric dark brown circular rings on lower foliage and severe upward curling of young shoot tips with stunted growth."
    ),
    KarnatakaCropItem(
        id = "onion",
        nameEn = "Onion",
        nameKn = "ಈರುಳ್ಳಿ",
        nameHi = "प्याज",
        category = KarnatakaCropCategory.VEGETABLES,
        primaryDiseases = listOf("Purple Blotch (ನೇರಳೆ ಮಚ್ಚೆ ರೋಗ)", "Basal Rot / Damping Off", "Colletotrichum Twister", "Thrips"),
        commonSymptoms = "Small sunken purplish lesions on hollow leaf blades with yellowish border, rotting bulb base with white mold, silvery leaf streaks.",
        iconEmoji = "🧅",
        majorDistricts = "Chitradurga, Gadag, Bagalkote, Dharwad, Vijayapura, Bellary",
        sampleDiseaseTitle = "Onion Purple Blotch & Twister Disease",
        sampleSymptomNotes = "Elliptical purple spots with dark brown centers on leaves, leaf blades twisting and curling downward."
    ),
    KarnatakaCropItem(
        id = "potato",
        nameEn = "Potato",
        nameKn = "ಆಲೂಗಡ್ಡೆ",
        nameHi = "आलू",
        category = KarnatakaCropCategory.VEGETABLES,
        primaryDiseases = listOf("Late Blight (ಲೇಟ್ ಬ್ಲೈಟ್)", "Early Blight", "Bacterial Wilt / Brown Rot", "Common Scab"),
        commonSymptoms = "Water-soaked irregular dark necrotic leaf blotches with white mildew underneath in humid weather, vascular ring browning in tubers.",
        iconEmoji = "🥔",
        majorDistricts = "Hassan, Kolar, Chikkaballapura, Belagavi, Chikkamagaluru",
        sampleDiseaseTitle = "Potato Late Blight (Phytophthora infestans)",
        sampleSymptomNotes = "Dark water-soaked blotches on leaf margins expanding rapidly, white delicate fungal down on leaf undersides under high moisture."
    ),
    KarnatakaCropItem(
        id = "chilli",
        nameEn = "Green Chilli / Byadagi Chilli",
        nameKn = "ಹಸಿಮೆಣಸಿನಕಾಯಿ / ಬ್ಯಾಡಗಿ ಮೆಣಸಿನಕಾಯಿ",
        nameHi = "हरी मिर्च",
        category = KarnatakaCropCategory.VEGETABLES,
        primaryDiseases = listOf("Anthracnose / Fruit Rot / Dieback", "Murda / Leaf Curl Virus (ಮುರಡ ರೋಗ)", "Powdery Mildew", "Bacterial Spot"),
        commonSymptoms = "Circular sunken black spots with concentric rings on ripe fruits, upward/downward cup-shaped curling of leaves, shoot tip dieback.",
        iconEmoji = "🌶️",
        majorDistricts = "Haveri (Byadagi), Belagavi, Bellary, Raichur, Dharwad",
        sampleDiseaseTitle = "Chilli Anthracnose Fruit Rot & Murda Virus",
        sampleSymptomNotes = "Circular sunken blackish blemishes on pods with salmon-colored spore masses and severe upward leaf curling (thrips/mites)."
    ),
    KarnatakaCropItem(
        id = "brinjal",
        nameEn = "Brinjal / Eggplant",
        nameKn = "ಬದನೆಕಾಯಿ",
        nameHi = "बैंगन",
        category = KarnatakaCropCategory.VEGETABLES,
        primaryDiseases = listOf("Shoot and Fruit Borer", "Phomopsis Blight", "Bacterial Wilt", "Little Leaf of Brinjal"),
        commonSymptoms = "Drooping shoot tips with bore holes, circular brown leaf spots, severe reduction in leaf size giving bushy sterile broom appearance.",
        iconEmoji = "🍆",
        majorDistricts = "Belagavi, Mysuru, Haveri, Bengaluru Rural, Hassan",
        sampleDiseaseTitle = "Brinjal Shoot & Fruit Borer + Little Leaf",
        sampleSymptomNotes = "Wilted drooping shoot tips with frass, tiny stunted chlorotic leaves transformed into a sterile bush."
    ),
    KarnatakaCropItem(
        id = "cabbage",
        nameEn = "Cabbage & Cauliflower",
        nameKn = "ಎಲೆಕೋಸು & ಹೂಕೋಸು",
        nameHi = "पत्ता गोभी व फूल गोभी",
        category = KarnatakaCropCategory.VEGETABLES,
        primaryDiseases = listOf("Black Rot (ಕ್ಯಾಬೇಜ್ ಕಪ್ಪು ಕೊಳೆ)", "Diamondback Moth (DBM)", "Clubroot", "Downy Mildew"),
        commonSymptoms = "V-shaped yellow lesions originating from leaf margins with black veins, perforated windowed leaves from green caterpillars.",
        iconEmoji = "🥬",
        majorDistricts = "Belagavi, Kolar, Hassan, Bengaluru Rural, Chikkamagaluru",
        sampleDiseaseTitle = "Cabbage Black Rot & Diamondback Moth",
        sampleSymptomNotes = "V-shaped chlorotic chlorosis on leaf margins with blackened vascular veins and windowed feeding holes."
    ),
    KarnatakaCropItem(
        id = "beans",
        nameEn = "French Beans / Bush Beans",
        nameKn = "ಹುರುಳಿಕಾಯಿ / ಬೀನ್ಸ್",
        nameHi = "फ्रेंच बीन्स",
        category = KarnatakaCropCategory.VEGETABLES,
        primaryDiseases = listOf("Bean Anthracnose", "Rust (ತುಕ್ಕು ರೋಗ)", "Yellow Mosaic Virus", "Root Rot"),
        commonSymptoms = "Dark brown sunken eye-spots on pods and stems, reddish-brown powdery rust pustules, yellow leaf mosaic.",
        iconEmoji = "🫘",
        majorDistricts = "Kolar, Bengaluru, Chikkamagaluru, Belagavi, Hassan",
        sampleDiseaseTitle = "French Beans Anthracnose & Rust",
        sampleSymptomNotes = "Reddish-brown sunken cankers on pods with black margins and rust-colored powdery pustules on foliage."
    ),
    KarnatakaCropItem(
        id = "capsicum",
        nameEn = "Capsicum / Bell Pepper",
        nameKn = "ದಪ್ಪಮೆಣಸಿನಕಾಯಿ / ಕ್ಯಾಪ್ಸಿಕಂ",
        nameHi = "शिमला मिर्च",
        category = KarnatakaCropCategory.VEGETABLES,
        primaryDiseases = listOf("Powdery Mildew", "Bacterial Spot", "Anthracnose", "Broad Mite & Thrips"),
        commonSymptoms = "White flour-like patches on lower leaf surface, downward curling with bronze sheen on young leaves.",
        iconEmoji = "🫑",
        majorDistricts = "Bengaluru Rural, Kolar, Belagavi, Chikkaballapura",
        sampleDiseaseTitle = "Capsicum Powdery Mildew & Broad Mite Damage",
        sampleSymptomNotes = "White powdery fungal growth under leaves and downward cupping leaves with brittle brownish texture."
    ),

    // 6. FRUITS
    KarnatakaCropItem(
        id = "pomegranate",
        nameEn = "Pomegranate / Anar",
        nameKn = "ದಾಳಿಂಬೆ",
        nameHi = "अनार",
        category = KarnatakaCropCategory.FRUITS,
        primaryDiseases = listOf("Bacterial Blight / Telya (ತೆಲ್ಯ ರೋಗ)", "Fruit Borer / Anar Butterfly", "Cercospora Fruit Spot", "Wilt Complex"),
        commonSymptoms = "Dark oily water-soaked angular spots on leaves and stems, cracking of fruit rind with L/Y shaped lesions, bore holes with faecal pellets.",
        iconEmoji = "🔴",
        majorDistricts = "Bagalkote, Vijayapura, Koppal, Chitradurga, Bellary",
        sampleDiseaseTitle = "Pomegranate Bacterial Blight (Telya / Xanthomonas)",
        sampleSymptomNotes = "Oily dark brown angular spots with water-soaked halos on leaves, nodal stem cankers, and cracking fruit rind with dark greasy spots."
    ),
    KarnatakaCropItem(
        id = "banana",
        nameEn = "Banana / Yelakki Bale",
        nameKn = "ಬಾಳೆ (ಏಲಕ್ಕಿ ಬಾಳೆ, ಜಿ-9)",
        nameHi = "केला",
        category = KarnatakaCropCategory.FRUITS,
        primaryDiseases = listOf("Panama Wilt (ಪನಾಮಾ ಸೊರಗು ರೋಗ)", "Sigatoka Leaf Spot", "Bunchy Top Virus", "Rhizome Weevil"),
        commonSymptoms = "Yellowing of lower leaves along margins spreading inward, dark brown elongated spots with grey center, congested rosette bunchy top.",
        iconEmoji = "🍌",
        majorDistricts = "Mandya, Mysuru, Ramanagara, Chamarajanagar, Davanagere",
        sampleDiseaseTitle = "Banana Panama Wilt & Sigatoka Blight",
        sampleSymptomNotes = "Lower leaves turning bright yellow and collapsing at petiole base, leaf blades displaying brown spindle lesions with grey centers."
    ),
    KarnatakaCropItem(
        id = "mango",
        nameEn = "Mango (Alphonso, Totapuri)",
        nameKn = "ಮಾವು (ಅಲ್ಫಾನ್ಸೋ, ತೋತಾಪುರಿ)",
        nameHi = "आम",
        category = KarnatakaCropCategory.FRUITS,
        primaryDiseases = listOf("Anthracnose / Blossom Blight", "Powdery Mildew", "Mango Hopper", "Fruit Fly"),
        commonSymptoms = "Black sunken spots on leaves, blossom panicle withering, white powdery dusting on inflorescence, hopper honey-dew sooty mold.",
        iconEmoji = "🥭",
        majorDistricts = "Kolar (Mango Belt), Chikkaballapura, Ramanagara, Dharwad, Belagavi",
        sampleDiseaseTitle = "Mango Blossom Blight & Powdery Mildew",
        sampleSymptomNotes = "Black necrotic spots on flowering panicles causing complete blossom drop, white powdery coating on flower clusters."
    ),
    KarnatakaCropItem(
        id = "papaya",
        nameEn = "Papaya",
        nameKn = "ಪರಂಗಿ / ಪಪ್ಪಾಯಿ",
        nameHi = "पपीता",
        category = KarnatakaCropCategory.FRUITS,
        primaryDiseases = listOf("Papaya Ringspot Virus (PRSV)", "Anthracnose Fruit Spot", "Stem Rot / Foot Rot", "Mealybug"),
        commonSymptoms = "Yellow mosaic mottling, shoe-string narrow leaf blades, green oily ring-spots on fruit surface, collar rot at ground level.",
        iconEmoji = "🍈",
        majorDistricts = "Mandya, Bellary, Chitradurga, Bagalkote, Mysuru",
        sampleDiseaseTitle = "Papaya Ringspot Virus (PRSV) & Anthracnose",
        sampleSymptomNotes = "Shoe-string deformation of leaves with deep chlorotic vein clearing and dark green concentric rings on green fruit skin."
    ),
    KarnatakaCropItem(
        id = "grapes",
        nameEn = "Grapes (Bangalore Blue, Dilkush)",
        nameKn = "ದ್ರಾಕ್ಷಿ (ಬೆಂಗಳೂರು ಬ್ಲೂ)",
        nameHi = "अंगूर",
        category = KarnatakaCropCategory.FRUITS,
        primaryDiseases = listOf("Downy Mildew (ಡೌನಿ ಮಿಲ್ಡ್ಯೂ)", "Powdery Mildew", "Anthracnose / Bird's Eye Rot", "Bacterial Spot"),
        commonSymptoms = "Yellowish translucent oil spots on upper leaf surface with white downy growth below, bird-eye lesions on berries with red margin.",
        iconEmoji = "🍇",
        majorDistricts = "Vijayapura, Bengaluru Rural, Chikkaballapura, Bagalkote",
        sampleDiseaseTitle = "Grapes Downy Mildew & Bird's Eye Anthracnose",
        sampleSymptomNotes = "Translucent yellow oil spots on upper leaf surface with dense white cottony fungal down on lower surface; fruit berry scarring."
    ),
    KarnatakaCropItem(
        id = "watermelon",
        nameEn = "Watermelon",
        nameKn = "ಕಲ್ಲಂಗಡಿ",
        nameHi = "तरबूज",
        category = KarnatakaCropCategory.FRUITS,
        primaryDiseases = listOf("Gummy Stem Blight", "Downy Mildew", "Fusarium Wilt", "Fruit Fly"),
        commonSymptoms = "Brown water-soaked stem lesions exuding amber gum drops, angular yellow spots on foliage, vine collapse.",
        iconEmoji = "🍉",
        majorDistricts = "Kolar, Koppal, Bagalkote, Chitradurga, Haveri",
        sampleDiseaseTitle = "Watermelon Gummy Stem Blight & Downy Mildew",
        sampleSymptomNotes = "Amber gummy sap oozing from circular stem lesions near soil line, angular yellow foliar spots drying to brown."
    ),

    // 7. FLORICULTURE & SILK (SERICULTURE)
    KarnatakaCropItem(
        id = "mulberry",
        nameEn = "Mulberry / Silk Farming",
        nameKn = "ಹಿಪ್ಪುನೇರಳೆ / ರೇಷ್ಮೆ ಕೃಷಿ",
        nameHi = "शहतूत (रेशम कीट पालन)",
        category = KarnatakaCropCategory.FLORICULTURE_SILK,
        primaryDiseases = listOf("Mulberry Leaf Spot (Cercospora)", "Powdery Mildew", "Tukra (Mealybug Curling)", "Root Rot"),
        commonSymptoms = "Circular brownish spots on leaves turning yellow, curling and crinkling of shoot tips from mealybug sap sucking.",
        iconEmoji = "🐛",
        majorDistricts = "Ramanagara (Silk City), Kolar, Mandya, Chikkaballapura, Mysuru",
        sampleDiseaseTitle = "Mulberry Leaf Spot & Tukra Viral Curling",
        sampleSymptomNotes = "Circular brown spots on leaves unpalatable for silkworms, shoot tips curled into dark green crinkled rosettes."
    ),
    KarnatakaCropItem(
        id = "jasmine",
        nameEn = "Jasmine (Mysore & Udupi Mallige)",
        nameKn = "ಮಲ್ಲಿಗೆ (ಮೈಸೂರು & ಉಡುಪಿ ಮಲ್ಲಿಗೆ)",
        nameHi = "चमेली / मोगरा",
        category = KarnatakaCropCategory.FLORICULTURE_SILK,
        primaryDiseases = listOf("Blossom Blight / Alternaria", "Bud Worm / Webworm", "Rust", "Leaf Spot"),
        commonSymptoms = "Flower buds turning reddish-brown and rotting before opening, silk webbing with bored holes in bud clusters.",
        iconEmoji = "🌸",
        majorDistricts = "Mysuru, Udupi (Shankarapura), Bellary, Mandya",
        sampleDiseaseTitle = "Jasmine Blossom Blight & Bud Worm",
        sampleSymptomNotes = "Flower buds turning brown and decaying on the bush, caterpillar webbing locking floral petals together."
    ),
    KarnatakaCropItem(
        id = "marigold",
        nameEn = "Marigold / Chenduhoo",
        nameKn = "ಚೆಂಡುಹೂ",
        nameHi = "गेंदा",
        category = KarnatakaCropCategory.FLORICULTURE_SILK,
        primaryDiseases = listOf("Alternaria Leaf Spot & Inflorescence Blight", "Botrytis Blossom Blight", "Collar Rot"),
        commonSymptoms = "Minute circular brownish spots on petals and leaves rapidly spreading into blotches, petal rotting under rain.",
        iconEmoji = "🌼",
        majorDistricts = "Haveri, Davanagere, Chamarajanagar, Bengaluru Rural",
        sampleDiseaseTitle = "Marigold Alternaria Blight & Collar Rot",
        sampleSymptomNotes = "Brown water-soaked specks expanding over flower petals causing flower decay, brown lesions on lower stem."
    )
)

