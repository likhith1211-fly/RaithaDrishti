package com.example.util

import com.example.data.model.AppLanguage
import com.example.data.model.WeatherData

data class SeasonalCropTip(
    val cropId: String,
    val cropNameEn: String,
    val cropNameKn: String,
    val cropNameHi: String,
    val currentSeasonStageEn: String,
    val currentSeasonStageKn: String,
    val currentSeasonStageHi: String,
    val weatherRiskBadgeEn: String,
    val weatherRiskBadgeKn: String,
    val weatherRiskBadgeHi: String,
    val weatherRiskSeverity: String, // "Low", "Medium", "High"
    val weatherAdaptedAdviceEn: String,
    val weatherAdaptedAdviceKn: String,
    val weatherAdaptedAdviceHi: String,
    val nutrientActionEn: String,
    val nutrientActionKn: String,
    val nutrientActionHi: String,
    val irrigationAdviceEn: String,
    val irrigationAdviceKn: String,
    val irrigationAdviceHi: String,
    val pestPreventionEn: String,
    val pestPreventionKn: String,
    val pestPreventionHi: String
)

object KarnatakaGrowthTipsEngine {

    fun generateTipsForCrop(
        cropId: String,
        weather: WeatherData?,
        language: AppLanguage
    ): SeasonalCropTip {
        val temp = weather?.temperature ?: 28.0
        val humidity = weather?.humidity ?: 65.0
        val rain = weather?.precipitation ?: 0.0
        val wind = weather?.windSpeed ?: 8.0

        val isRaining = rain > 0.5
        val isHighHumidity = humidity > 75.0
        val isHotDry = temp > 31.0 && rain == 0.0
        val isHighWind = wind > 16.0

        return when (cropId.lowercase()) {
            "maize", "corn", "ಮೆಕ್ಕೆಜೋಳ", "मक्का" -> {
                val riskSeverity = if (isHighHumidity || isRaining) "High" else "Low"
                SeasonalCropTip(
                    cropId = "maize",
                    cropNameEn = "Maize (ಮೆಕ್ಕೆಜೋಳ)",
                    cropNameKn = "ಮೆಕ್ಕೆಜೋಳ",
                    cropNameHi = "मक्का",
                    currentSeasonStageEn = "Tasseling & Cob Filling Stage",
                    currentSeasonStageKn = "ಹೂವಾಡುವ ಹಾಗೂ ತೆನೆ ತುಂಬುವ ಹಂತ",
                    currentSeasonStageHi = "भुट्टा भरने और परागण की अवस्था",
                    weatherRiskBadgeEn = if (isRaining) "Heavy Rain & Waterlogging Risk" else if (isHighHumidity) "Turcicum Leaf Blight & Stem Rot Risk" else "Optimal Weather",
                    weatherRiskBadgeKn = if (isRaining) "ನೀರು ನಿಲ್ಲುವ ಅಪಾಯ - ಬಸಿಗಾಲುವೆ ತೋಡಿ" else if (isHighHumidity) "ಎಲೆ ತಿನ್ನುವ ಕೀಟ ಹಾಗೂ ಬ್ಲೈಟ್ ಶಿಲೀಂಧ್ರ ಅಪಾಯ" else "ಅನುಕೂಲಕರ ಹವಾಮಾನ",
                    weatherRiskBadgeHi = if (isRaining) "जलजमाव का खतरा" else if (isHighHumidity) "पत्ती झुलसा व तना गलन खतरा" else "अनुकूल मौसम",
                    weatherRiskSeverity = riskSeverity,
                    weatherAdaptedAdviceEn = if (isRaining) {
                        "Drain stagnant water from furrows immediately; maize roots rot quickly in standing water. Postpone urea top dressing."
                    } else if (isHighHumidity) {
                        "Cloudy humid weather favors Fall Armyworm (ಕತ್ತರಿ ಹುಳು) egg hatching and Turcicum blight. Monitor central whorls closely."
                    } else {
                        "Favorable weather. Maintain light soil moisture. Perfect timing for inter-row earthing up."
                    },
                    weatherAdaptedAdviceKn = if (isRaining) {
                        "ಸಾಲುಗಳಲ್ಲಿ ನೀರು ನಿಲ್ಲದಂತೆ ತಕ್ಷಣ ಬಸಿಗಾಲುವೆ ಮಾಡಿ. ನೀರು ನಿಂತರೆ ಬೇರು ಕೊಳೆತು ಗಿಡ ಹಳದಿಯಾಗುತ್ತದೆ. ಮಳೆ ನಿಲ್ಲುವವರೆಗೂ ಯೂರಿಯಾ ಹಾಕಬೇಡಿ."
                    } else if (isHighHumidity) {
                        "ಮೋಡ ಕವಿದ ತೇವಾಂಶದ ವಾತಾವರಣವು ಕತ್ತರಿ ಹುಳು (Fall Armyworm) ಬಾಧೆಯನ್ನು ಹೆಚ್ಚಿಸುತ್ತದೆ. ಸುಳಿಗಳಲ್ಲಿ ಎಲೆ ತಿನ್ನುವ ಕೀಟಗಳ ಲಕ್ಷಣಗಳನ್ನು ಗಮನಿಸಿ."
                    } else {
                        "ಹವಾಮಾನವು ಉತ್ತಮವಾಗಿದೆ. ತೆನೆಗೆ ತೇವಾಂಶದ ಕೊರತೆಯಾಗದಂತೆ ಲಘು ನೀರಾವರಿ ಒದಗಿಸಿ."
                    },
                    weatherAdaptedAdviceHi = if (isRaining) {
                        "खेत से जल निकासी तुरंत करें। मक्का की जड़ें पानी भरने से सड़ जाती हैं। यूरिया डालना टालें।"
                    } else if (isHighHumidity) {
                        "नमी वाले मौसम में फॉल आर्मीवर्म और पत्ती झुलसा का प्रकोप बढ़ सकता है। तने और पत्तियों की नियमित जांच करें।"
                    } else {
                        "मौसम अनुकूल है। भुट्टा बनने के लिए खेत में हल्की नमी बनाए रखें।"
                    },
                    nutrientActionEn = if (isRaining) "Avoid foliar spray. Apply 0:0:50 (SOP) once soil dries to strengthen cob formation." else "Foliar spray of 19:19:19 (5g/L) + Zinc 1g/L for bold grain development.",
                    nutrientActionKn = if (isRaining) "ಮಳೆ ಕಡಿಮೆಯಾದ ನಂತರ 0:0:50 (ಪೊಟ್ಯಾಷ್) ಕೊಟ್ಟು ತೆನೆ ಗಟ್ಟಿಗೊಳಿಸಿ." else "19:19:19 (5 ಗ್ರಾಂ/ಲೀ) ಜೊತೆಗೆ ಸತುವಿನ (Zinc) ದ್ರಾವಣ ಸಿಂಪಡಿಸಿ ಕಾಳು ಚೆನ್ನಾಗಿ ತುಂಬಲು ಸಹಾಯ ಮಾಡಿ.",
                    nutrientActionHi = if (isRaining) "बारिश थमने पर 0:0:50 पोटाश दें।" else "19:19:19 (5 ग्राम/लीटर) और जिंक का छिड़काव करें।",
                    irrigationAdviceEn = if (isRaining) "No irrigation needed. Ensure drain outlets are open." else "Irrigate every 4-6 days; cob filling requires critical moisture.",
                    irrigationAdviceKn = if (isRaining) "ನೀರಾವರಿ ಅಗತ್ಯವಿಲ್ಲ. ಹೆಚ್ಚುವರಿ ನೀರನ್ನು ಹೊರಹಾಕಿ." else "ಪ್ರತಿ 4-6 ದಿನಗಳಿಗೊಮ್ಮೆ ಲಘು ನೀರು ಕೊಡಿ; ಕಾಳು ಕಟ್ಟುವಾಗ ನೀರಿನ ಕೊರತೆಯಾಗಬಾರದು.",
                    irrigationAdviceHi = if (isRaining) "सिंचाई न करें। पानी निकासी सुनिश्चित करें।" else "हर 4-6 दिन में हल्की सिंचाई करें।",
                    pestPreventionEn = "For Fall Armyworm, apply Coragen (Chlorantraniliprole 18.5% SC) @ 0.4 ml/L or Emamectin Benzoate 5% SG @ 0.5 g/L into whorls.",
                    pestPreventionKn = "ಕತ್ತರಿ ಹುಳು ನಿಯಂತ್ರಣಕ್ಕೆ ಕೊರಾಜನ್ (0.4 ಮಿ.ಲೀ/ಲೀ) ಅಥವಾ ಇಮಾಮೆಕ್ಟಿನ್ ಬೆಂಜೊಯೇಟ್ (0.5 ಗ್ರಾಂ/ಲೀ) ಸುಳಿಯೊಳಗೆ ಬೀಳುವಂತೆ ಸಿಂಪಡಿಸಿ.",
                    pestPreventionHi = "आर्मीवर्म नियंत्रण के लिए कोराजन 0.4 मिली/लीटर या इमामेक्टिन 0.5 ग्राम/लीटर का छिड़काव करें।"
                )
            }
            "tomato", "ಟೊಮೆಟೊ", "टमाटर" -> {
                val riskSeverity = if (isHighHumidity || isRaining) "High" else "Medium"
                SeasonalCropTip(
                    cropId = "tomato",
                    cropNameEn = "Tomato (ಟೊಮೆಟೊ)",
                    cropNameKn = "ಟೊಮೆಟೊ",
                    cropNameHi = "टमाटर",
                    currentSeasonStageEn = "Flowering & Fruit Development",
                    currentSeasonStageKn = "ಹೂವು ಮತ್ತು ಕಾಯಿ ಕಟ್ಟುವ ಹಂತ",
                    currentSeasonStageHi = "फूल व फल विकास अवस्था",
                    weatherRiskBadgeEn = if (isHighHumidity) "Late Blight & Fruit Borer Alert" else "Favorable Harvesting Window",
                    weatherRiskBadgeKn = if (isHighHumidity) "ಅಂಗಮಾರಿ ರೋಗ ಹಾಗೂ ಕಾಯಿ ಕೊರಕ ಹುಳು ಎಚ್ಚರಿಕೆ" else "ಉತ್ತಮ ಬೆಳವಣಿಗೆಯ ವಾತಾವರಣ",
                    weatherRiskBadgeHi = if (isHighHumidity) "पछेती झुलसा व फल छेदक कीट अलर्ट" else "अनुकूल मौसम",
                    weatherRiskSeverity = riskSeverity,
                    weatherAdaptedAdviceEn = if (isHighHumidity || isRaining) {
                        "High humidity triggers Late Blight (ಫೈಟೋಫ್ತೋರಾ). Spray preventative Mancozeb or Cymoxanil immediately after rain stops."
                    } else {
                        "Dry sunny weather aids good fruit set. Stake plants to prevent fruits touching soil."
                    },
                    weatherAdaptedAdviceKn = if (isHighHumidity || isRaining) {
                        "ಅತಿಯಾದ ತೇವಾಂಶದಿಂದ ಲೇಟ್ ಬ್ಲೈಟ್ (ಅಂಗಮಾರಿ ರೋಗ) ಹರಡಬಹುದು. ಮಳೆ ನಿಂತ ತಕ್ಷಣ ಮ್ಯಾಂಕೋಜೆಬ್ ಅಥವಾ ಸಿಮೊಕ್ಸಾನಿಲ್ ಸಿಂಪಡಿಸಿ."
                    } else {
                        "ಬಿಸಿಲು ಹವಾಮಾನವು ಹೂವು ಕಾಯಿಯಾಗಲು ಉತ್ತಮ. ಹಣ್ಣುಗಳು ಮಣ್ಣಿಗೆ ತಾಗದಂತೆ ಕೋಲುಗಳಿಂದ ಕಟ್ಟಿ ನಿಲ್ಲಿಸಿ."
                    },
                    weatherAdaptedAdviceHi = if (isHighHumidity || isRaining) {
                        "अधिक नमी से झुलसा रोग तेजी से फैलता है। बारिश रुकते ही मैंकोजेब का छिड़काव करें।"
                    } else {
                        "धूप वाला मौसम फल विकास के लिए उत्तम है। पौधों को सहारा दें।"
                    },
                    nutrientActionEn = "Spray Calcium Nitrate (3g/L) + Boron (1g/L) to prevent Blossom End Rot and fruit cracking.",
                    nutrientActionKn = "ಕಾಯಿ ಸೀಳುವಿಕೆ ಹಾಗೂ ಕಪ್ಪು ಕಲೆ ತಡೆಯಲು ಕ್ಯಾಲ್ಸಿಯಂ ನೈಟ್ರೇಟ್ (3 ಗ್ರಾಂ/ಲೀ) + ಬೋರಾನ್ (1 ಗ್ರಾಂ/ಲೀ) ಸಿಂಪಡಿಸಿ.",
                    nutrientActionHi = "कैल्शियम नाइट्रेट (3 ग्रा/ली) + बोरॉन (1 ग्रा/ली) का छिड़काव करें।",
                    irrigationAdviceEn = "Avoid fluctuating moisture. Drip fertigation in morning hours.",
                    irrigationAdviceKn = "ಒಮ್ಮೆಲೇ ಹೆಚ್ಚು ನೀರು ಕೊಡಬೇಡಿ; ಹನಿ ನೀರಾವರಿ ಮೂಲಕ ಬೆಳಿಗ್ಗೆ ಸಮಪ್ರಮಾಣದಲ್ಲಿ ನೀರು ಕೊಡಿ.",
                    irrigationAdviceHi = "ड्रिप से सुबह के समय नियमित पानी दें।",
                    pestPreventionEn = "Install yellow sticky traps for Whiteflies; spray Proclaim (0.5g/L) for fruit borer.",
                    pestPreventionKn = "ಬಿಳಿ ನೊಣಕ್ಕೆ ಹಳದಿ ಜಿಗುಟು ಬಲೆ ಹಾಕಿ; ಕಾಯಿ ಕೊರಕಕ್ಕೆ ಪ್ರೊಕ್ಲೈಮ್ (0.5 ಗ್ರಾಂ/ಲೀ) ಸಿಂಪಡಿಸಿ.",
                    pestPreventionHi = "सफेद मक्खी के लिए पीले ट्रैप लगाएं; छेदक कीट के लिए प्रोक्लेम का छिड़काव करें।"
                )
            }
            "arecanut", "ಅಡಿಕೆ", "सुपारी" -> {
                val riskSeverity = if (isRaining || isHighHumidity) "High" else "Low"
                SeasonalCropTip(
                    cropId = "arecanut",
                    cropNameEn = "Arecanut (ಅಡಿಕೆ)",
                    cropNameKn = "ಅಡಿಕೆ",
                    cropNameHi = "सुपारी",
                    currentSeasonStageEn = "Bunch Maturity & Green Nut Picking",
                    currentSeasonStageKn = "ಗೊನೆ ಮಾಗುವಿಕೆ ಮತ್ತು ಹಸಿ ಅಡಿಕೆ ಕೊಯ್ಲು",
                    currentSeasonStageHi = "फल परिपक्वता व तुड़ाई अवस्था",
                    weatherRiskBadgeEn = if (isHighHumidity || isRaining) "Koleroga (Fruit Rot) Warning" else "Stable Harvest Climate",
                    weatherRiskBadgeKn = if (isHighHumidity || isRaining) "ಕೊಳೆರೋಗ (Koleroga) ಶಿಲೀಂಧ್ರ ಎಚ್ಚರಿಕೆ" else "ಸ್ಥಿರ ಕೊಯ್ಲು ಹವಾಮಾನ",
                    weatherRiskBadgeHi = if (isHighHumidity || isRaining) "कोलेरोगा (फल सड़न) चेतावनी" else "अनुकूल तुड़ाई मौसम",
                    weatherRiskSeverity = riskSeverity,
                    weatherAdaptedAdviceEn = if (isHighHumidity || isRaining) {
                        "Heavy moisture encourages Phytophthora Koleroga. Apply 1% Bordeaux mixture spray or Metalaxyl to bunches on rain break."
                    } else {
                        "Dry spell is ideal for sun-drying harvested tender nuts for Rashi quality."
                    },
                    weatherAdaptedAdviceKn = if (isHighHumidity || isRaining) {
                        "ನಿರಂತರ ತೇವಾಂಶದಿಂದ ಗೊನೆಗೆ ಕೊಳೆರೋಗ (ಮಹಾಳಿ) ಬಾಧಿಸುವ ಸಂಭವ ಹೆಚ್ಚು. ಮಳೆ ವಿರಾಮ ಸಿಕ್ಕಾಗ 1% ಬೋರ್ಡೋ ದ್ರಾವಣವನ್ನು ಗೊನೆಗಳಿಗೆ ಸಿಂಪಡಿಸಿ."
                    } else {
                        "ಬಿಸಿಲು ಚೆನ್ನಾಗಿದ್ದು ಕೊಯ್ದ ಅಡಿಕೆಯನ್ನು ಒಣಗಿಸಿ ಉತ್ತಮ ರಾಶಿ ಗುಣಮಟ್ಟ ಪಡೆಯಲು ಸಕಾಲ."
                    },
                    weatherAdaptedAdviceHi = if (isHighHumidity || isRaining) {
                        "अधिक बारिश से फल सड़न (कोलेरोगा) का खतरा रहता है। 1% बोर्डो मिश्रण का छिड़काव करें।"
                    } else {
                        "धूप में सुपारी सुखाने और प्रसंस्करण के लिए अच्छा समय है।"
                    },
                    nutrientActionEn = "Apply 100g MOP + 150g Urea + 200g Rock Phosphate per palm with organic neem cake.",
                    nutrientActionKn = "ಮರದ ಬುಡಕ್ಕೆ ಬೇವಿನ ಹಿಂಡಿ, 100 ಗ್ರಾಂ ಪೊಟ್ಯಾಷ್ ಹಾಗೂ 150 ಗ್ರಾಂ ಯೂರಿಯಾ ಸಮತೋಲನದಲ್ಲಿ ನೀಡಿ.",
                    nutrientActionHi = "पेड़ की जड़ में नीम की खली व संतुलित एनपीके उर्वरक दें।",
                    irrigationAdviceEn = "Drain excess water in monsoon; mulch basin with dry leaves.",
                    irrigationAdviceKn = "ತೋಟದಲ್ಲಿ ನೀರು ನಿಲ್ಲದಂತೆ ನೋಡಿಕೊಳ್ಳಿ; ಬುಡಕ್ಕೆ ಅಡಿಕೆ ಸಿಪ್ಪೆ ಅಥವಾ ಒಣ ಎಲೆಗಳಿಂದ ಹೊದಿಕೆ ಹಾಕಿ.",
                    irrigationAdviceHi = "बगीचे में पानी रुकने न दें।",
                    pestPreventionEn = "Tie polythene bags to protect bunches or spray Copper Oxychloride (3g/L).",
                    pestPreventionKn = "ಗೊನೆಗಳಿಗೆ ಕಳೆ ತಡೆಯಲು ಪ್ಲಾಸ್ಟಿಕ್ ಕವಚ ಕಟ್ಟಿ ಅಥವಾ ಬ್ಲೈಟಾಕ್ಸ್ (3 ಗ್ರಾಂ/ಲೀ) ಸಿಂಪಡಿಸಿ.",
                    pestPreventionHi = "गुच्छों पर कॉपर ऑक्सीक्लोराइड का छिड़काव करें।"
                )
            }
            "pumpkin", "ಕುಂಬಳಕಾಯಿ", "कद्दू" -> {
                val riskSeverity = if (isHighHumidity) "High" else "Low"
                SeasonalCropTip(
                    cropId = "pumpkin",
                    cropNameEn = "Pumpkin (ಕುಂಬಳಕಾಯಿ)",
                    cropNameKn = "ಕುಂಬಳಕಾಯಿ",
                    cropNameHi = "कद्दू",
                    currentSeasonStageEn = "Vigorous Vine Growth & Fruit Sizing",
                    currentSeasonStageKn = "ಬಳ್ಳಿ ಹಬ್ಬುವಿಕೆ ಮತ್ತು ಕಾಯಿ ಗಾತ್ರ ಹಿಗ್ಗುವ ಹಂತ",
                    currentSeasonStageHi = "बेल फैलाव और फल बड़ा होने की अवस्था",
                    weatherRiskBadgeEn = if (isHighHumidity) "Powdery / Downy Mildew Risk" else "Optimal Sizing Window",
                    weatherRiskBadgeKn = if (isHighHumidity) "ಬೂದಿ ರೋಗ ಹಾಗೂ ಎಲೆ ಚುಕ್ಕೆ ಎಚ್ಚರಿಕೆ" else "ಉತ್ತಮ ಕಾಯಿ ಬೆಳವಣಿಗೆಯ ಸಮಯ",
                    weatherRiskBadgeHi = if (isHighHumidity) "चूर्णिल आसिता (फफूंद) चेतावनी" else "फल बढ़वार का अनुकूल समय",
                    weatherRiskSeverity = riskSeverity,
                    weatherAdaptedAdviceEn = if (isHighHumidity) {
                        "High morning dew causes Downy Mildew (white-yellow patches on vine leaves). Spray Redomil MZ (2g/L) on leaf undersides."
                    } else {
                        "Elevate growing pumpkins on dry straw or tiles so direct soil moisture doesn't rot base rind."
                    },
                    weatherAdaptedAdviceKn = if (isHighHumidity) {
                        "ಬೆಳಗಿನ ಮಂಜು ಮತ್ತು ತೇವಾಂಶದಿಂದ ಎಲೆಗಳ ಮೇಲೆ ಬೂದಿ ರೋಗ ಕಾಣಿಸಿಕೊಳ್ಳುತ್ತದೆ. ಎಲೆಯ ಕೆಳಭಾಗಕ್ಕೆ ರೆಡೋಮಿಲ್ (2 ಗ್ರಾಂ/ಲೀ) ಸಿಂಪಡಿಸಿ."
                    } else {
                        "ಬೆಳೆಯುತ್ತಿರುವ ಕುಂಬಳಕಾಯಿಯ ಕೆಳಗೆ ಒಣ ಹುಲ್ಲು ಅಥವಾ ಹಂಚು ಇಡಿ; ಇದರಿಂದ ಮಣ್ಣಿನ ತೇವಾಂಶ ತಾಗಿ ಕಾಯಿ ಕೊಳೆಯುವುದಿಲ್ಲ."
                    },
                    weatherAdaptedAdviceHi = if (isHighHumidity) {
                        "पत्तियों पर फफूंदी से बचाव के लिए रेडोमिल का छिड़काव करें।"
                    } else {
                        "फलों को सड़न से बचाने के लिए उनके नीचे सूखा भूसा रखें।"
                    },
                    nutrientActionEn = "Drench with 13:0:45 (Potassium Nitrate) @ 5g/L for thick skin and maximum fruit weight.",
                    nutrientActionKn = "ಕಾಯಿಯ ತೂಕ ಮತ್ತು ಗಟ್ಟಿ ಸಿಪ್ಪೆಗಾಗಿ 13:0:45 ಪೊಟ್ಯಾಷಿಯಂ ನೈಟ್ರೇಟ್ (5 ಗ್ರಾಂ/ಲೀ) ಬುಡಕ್ಕೆ ಕೊಡಿ.",
                    nutrientActionHi = "13:0:45 पोटाश घोल जड़ों में दें जिससे वजन व चमक बढ़े।",
                    irrigationAdviceEn = "Irrigate vines in alternate furrows; avoid flooding vine crowns.",
                    irrigationAdviceKn = "ಬಳ್ಳಿಯ ಬುಡಕ್ಕೆ ನೇರವಾಗಿ ನೀರು ಹಾಯಿಸದೆ ಸಾಲುಗಳ ಮೂಲಕ ಲಘು ತೇವಾಂಶ ಕಾಪಾಡಿ.",
                    irrigationAdviceHi = "बेल की जड़ों में हल्का पानी दें।",
                    pestPreventionEn = "Install cue-lure traps for melon fruit fly (ದುಂಬಿ ನೊಣ); spray Neem oil 5ml/L weekly.",
                    pestPreventionKn = "ಹಣ್ಣಿನ ನೊಣಕ್ಕೆ ಕ್ಯು-ಲ್ಯೂರ್ ಬಲೆ ಹಾಕಿ; ವಾರಕ್ಕೊಮ್ಮೆ ಬೇವಿನ ಎಣ್ಣೆ (5 ಮಿ.ಲೀ/ಲೀ) ಸಿಂಪಡಿಸಿ.",
                    pestPreventionHi = "फल मक्खी के लिए ट्रैप लगाएं व नीम तेल का छिड़काव करें।"
                )
            }
            "coffee", "ಕಾಫಿ", "कॉफ़ी" -> {
                val riskSeverity = if (isHighHumidity || isRaining) "High" else "Low"
                SeasonalCropTip(
                    cropId = "coffee",
                    cropNameEn = "Coffee (ಕಾಫಿ - Arabica/Robusta)",
                    cropNameKn = "ಕಾಫಿ",
                    cropNameHi = "कॉफ़ी",
                    currentSeasonStageEn = "Berry Development & Shade Regulation",
                    currentSeasonStageKn = "ಬೆರ್ರಿ (ಹಣ್ಣು) ಕಟ್ಟುವಿಕೆ ಮತ್ತು ನೆರಳು ನಿರ್ವಹಣೆ",
                    currentSeasonStageHi = "कॉफ़ी फल विकास व छाया प्रबंधन",
                    weatherRiskBadgeEn = if (isHighHumidity) "Coffee Leaf Rust & Black Rot Risk" else "Optimal Berry Swelling",
                    weatherRiskBadgeKn = if (isHighHumidity) "ಕಾಫಿ ತುಕ್ಕು ರೋಗ (Leaf Rust) ಎಚ್ಚರಿಕೆ" else "ಅನುಕೂಲಕರ ಹಣ್ಣು ಬೆಳವಣಿಗೆ",
                    weatherRiskBadgeHi = if (isHighHumidity) "कॉफ़ी रतुआ व सड़ांध रोग का खतरा" else "अनुकूल विकास",
                    weatherRiskSeverity = riskSeverity,
                    weatherAdaptedAdviceEn = if (isHighHumidity || isRaining) {
                        "Persistent drizzle in Western Ghats promotes Koleroga and Hemileia Leaf Rust. Thin shade trees for 40% sunlight penetration."
                    } else {
                        "Ideal conditions for berry maturation. Clear weed circles around bush bases."
                    },
                    weatherAdaptedAdviceKn = if (isHighHumidity || isRaining) {
                        "ಪಶ್ಚಿಮ ಘಟ್ಟಗಳಲ್ಲಿ ನಿರಂತರ ಮಳೆಯಿಂದ ಎಲೆ ತುಕ್ಕು ರೋಗ ಹಾಗೂ ಕಪ್ಪು ಕೊಳೆ ಕಾಣಿಸಿಕೊಳ್ಳಬಹುದು. ತೋಟದಲ್ಲಿ ಬೆಳಕು ಬೀಳುವಂತೆ ನೆರಳಿನ ಮರಗಳ ಕೊಂಬೆ ಸವರಿ."
                    } else {
                        "ಬೆರ್ರಿ ಬೆಳವಣಿಗೆಗೆ ಉತ್ತಮ ಸಮಯ. ಗಿಡಗಳ ಬುಡದಲ್ಲಿ ಕಳೆ ಕೀಳಿಸಿ ಬಸಿಗಾಲುವೆ ಸರಿಪಡಿಸಿ."
                    },
                    weatherAdaptedAdviceHi = if (isHighHumidity || isRaining) {
                        "पत्तियों में जंग और सड़न रोकने के लिए छायादार पेड़ों की छंटाई करें ताकि धूप पहुंच सके।"
                    } else {
                        "पौधों के आसपास की खरपतवार साफ करें।"
                    },
                    nutrientActionEn = "Post-monsoon application of 40kg N, 30kg P2O5, 40kg K2O per acre.",
                    nutrientActionKn = "ಮಳೆಯ ನಂತರ ಎಕರೆಗೆ ಶಿಫಾರಸು ಮಾಡಿದ ಎನ್.ಪಿ.ಕೆ ರಸಗೊಬ್ಬರ ಮತ್ತು ಜಿಪ್ಸಂ ನೀಡಿ.",
                    nutrientActionHi = "संतुलित एनपीके खाद का प्रयोग करें।",
                    irrigationAdviceEn = "Ensure hillside contour drainage to prevent soil erosion.",
                    irrigationAdviceKn = "ತೋಟದ ಇಳಿಜಾರಿನಲ್ಲಿ ಮಣ್ಣು ಕೊಚ್ಚಿ ಹೋಗದಂತೆ ಸಮಪಾತಳಿ ಬಸಿಗಾಲುವೆ ನಿರ್ವಹಿಸಿ.",
                    irrigationAdviceHi = "ढलान पर मिट्टी के कटाव को रोकने के उपाय करें।",
                    pestPreventionEn = "Set broca pheromone traps for Coffee Berry Borer (ಬೆರ್ರಿ ಬೋರರ್); spray Beauveria bassiana.",
                    pestPreventionKn = "ಬೆರ್ರಿ ಕೊರೆಯುವ ಕೀಟಕ್ಕೆ (Berry Borer) ಮೋಹಕ ಬಲೆ ಹಾಕಿ; ಜೈವಿಕ ಬುವೇರಿಯಾ ಶಿಲೀಂಧ್ರ ಸಿಂಪಡಿಸಿ.",
                    pestPreventionHi = "कॉफ़ी बेरी बोरर के लिए फेरोमोन ट्रैप लगाएं।"
                )
            }
            else -> {
                // Default Karnataka crop advice (Paddy / Ragi / General)
                val riskSeverity = if (isHighHumidity) "Medium" else "Low"
                SeasonalCropTip(
                    cropId = "general",
                    cropNameEn = "Paddy & Millets (ಭತ್ತ ಮತ್ತು ಸಿರಿಧಾನ್ಯ)",
                    cropNameKn = "ಭತ್ತ ಮತ್ತು ಸಿರಿಧಾನ್ಯ",
                    cropNameHi = "धान व मोटा अनाज",
                    currentSeasonStageEn = "Tillering & Panicle Initiation",
                    currentSeasonStageKn = "ಕವಲೊಡೆಯುವ ಹಾಗೂ ತೆನೆ ಆರಂಭದ ಹಂತ",
                    currentSeasonStageHi = "कल्ले फूटने और बाली निकलने की अवस्था",
                    weatherRiskBadgeEn = if (isHighHumidity) "Blast & Stem Borer Vigilance" else "Favorable Weather Window",
                    weatherRiskBadgeKn = if (isHighHumidity) "ಬೆಂಕಿ ರೋಗ (Blast) ಹಾಗೂ ಕಾಂಡ ಕೊರಕ ಎಚ್ಚರಿಕೆ" else "ಅನುಕೂಲಕರ ಹವಾಮಾನ",
                    weatherRiskBadgeHi = if (isHighHumidity) "ब्लास्ट रोग व तना छेदक अलर्ट" else "अनुकूल मौसम",
                    weatherRiskSeverity = riskSeverity,
                    weatherAdaptedAdviceEn = if (isHighHumidity) {
                        "Blast fungus spreads rapidly in humid cloudy spells. Avoid excess nitrogen and maintain shallow water level."
                    } else {
                        "Good weather for secondary tillering. Maintain 2-3 cm shallow standing water."
                    },
                    weatherAdaptedAdviceKn = if (isHighHumidity) {
                        "ಮೋಡ ಕವಿದ ವಾತಾವರಣದಲ್ಲಿ ಬೆಂಕಿ ರೋಗ (Blast) ವೇಗವಾಗಿ ಹರಡುತ್ತದೆ. ಯೂರಿಯಾ ಅತಿಯಾಗಿ ಹಾಕಬೇಡಿ ಹಾಗೂ ಗದ್ದೆಯಲ್ಲಿ ನೀರು ನಿಲ್ಲಿಸಿ ಬದಲಾಯಿಸುತ್ತಿರಿ."
                    } else {
                        "ತೆನೆ ಹೊರಬರಲು ಹವಾಮಾನ ಅನುಕೂಲವಾಗಿದೆ. ಗದ್ದೆಯಲ್ಲಿ 2-3 ಸೆಂ.ಮೀ ನೀರು ನಿಲ್ಲಿಸಿ."
                    },
                    weatherAdaptedAdviceHi = if (isHighHumidity) {
                        "ब्लास्ट रोग से बचाव के लिए अधिक नाइट्रोजन न दें।"
                    } else {
                        "खेत में 2-3 सेमी पानी बनाए रखें।"
                    },
                    nutrientActionEn = "Top dress with Urea + MOP before panicle emergence.",
                    nutrientActionKn = "ತೆನೆ ಬರುವ ಮುನ್ನ ಯೂರಿಯಾ ಹಾಗೂ ಪೊಟ್ಯಾಷ್ ಸಮಪ್ರಮಾಣದಲ್ಲಿ ಮೇಲುಗೊಬ್ಬರವಾಗಿ ನೀಡಿ.",
                    nutrientActionHi = "यूरिया और पोटाश का छिड़काव करें।",
                    irrigationAdviceEn = "Alternate wetting and drying (AWD) saves water and strengthens roots.",
                    irrigationAdviceKn = "ಗದ್ದೆಯನ್ನು ಆಗಾಗ ಆರಿಸಿ ನೀರು ಕಟ್ಟುವುದು ಬೇರುಗಳನ್ನು ಬಲಪಡಿಸುತ್ತದೆ.",
                    irrigationAdviceHi = "समय-समय पर पानी बदलते रहें।",
                    pestPreventionEn = "For stem borer, spray Cartap Hydrochloride 50% SP @ 2g/L.",
                    pestPreventionKn = "ಕಾಂಡ ಕೊರಕ ನಿಯಂತ್ರಣಕ್ಕೆ ಕಾರ್ಟಾಪ್ ಹೈಡ್ರೋಕ್ಲೋರೈಡ್ (2 ಗ್ರಾಂ/ಲೀ) ಸಿಂಪಡಿಸಿ.",
                    pestPreventionHi = "तना छेदक के लिए कार्टाप हाइड्रोक्लोराइड का छिड़काव करें।"
                )
            }
        }
    }
}
