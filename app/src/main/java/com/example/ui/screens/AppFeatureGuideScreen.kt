package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppLanguage
import com.example.ui.theme.AmberLight
import com.example.ui.theme.ForestGreenDark
import com.example.ui.theme.ForestGreenPrimary
import com.example.ui.viewmodel.RaithaDrishtiViewModel

data class FeatureGuideItem(
    val id: String,
    val icon: ImageVector,
    val targetTab: Int,
    val titleKn: String,
    val titleHi: String,
    val titleEn: String,
    val purposeKn: String,
    val purposeHi: String,
    val purposeEn: String,
    val howToUseKn: String,
    val howToUseHi: String,
    val howToUseEn: String,
    val benefitKn: String,
    val benefitHi: String,
    val benefitEn: String
)

val APP_FEATURES_CATALOG = listOf(
    FeatureGuideItem(
        id = "crop_doctor",
        icon = Icons.Default.CameraAlt,
        targetTab = 0,
        titleKn = "ಬೆಳೆ ವೈದ್ಯ AI (ಕ್ಯಾಮೆರಾ ರೋಗ ಪತ್ತೆ)",
        titleHi = "क्रॉप डॉक्टर AI (कैमरा रोग निदान)",
        titleEn = "Crop Doctor AI (Camera Pathology)",
        purposeKn = "ಬೆಳೆಗಳ ಎಲೆ, ಕಾಂಡ ಅಥವಾ ಹಣ್ಣಿನ ಫೋಟೋ ತೆಗೆದು ತಕ್ಷಣವೇ ನಿಖರ ರೋಗ ಮತ್ತು ಕೀಟ ಬಾಧೆ ಪತ್ತೆ ಹಚ್ಚುವುದು.",
        purposeHi = "फसल के पत्तों, तने या फलों की फोटो लेकर तुरंत सटीक रोग और कीट प्रकोप की पहचान करना।",
        purposeEn = "Instantly detect crop diseases, nutrient deficiencies, and pest infestations using CameraX leaf scanning.",
        howToUseKn = "ಕ್ಯಾಮೆರಾ ಬಟನ್ ಒತ್ತಿ ಎಲೆಯ ಫೋಟೋ ತೆಗೆಯಿರಿ ಅಥವಾ ಗ್ಯಾಲರಿಯಿಂದ ಆಯ್ಕೆ ಮಾಡಿ 'ರೋಗ ಪತ್ತೆ ಮಾಡಿ' ಒತ್ತಿ.",
        howToUseHi = "कैमरा बटन दबाकर पत्ते की फोटो लें या गैलरी से चुनें, फिर 'जांच करें' पर टैप करें।",
        howToUseEn = "Point camera at foliage or upload an image, then tap 'Diagnose with AI' for instant prescription.",
        benefitKn = "ಖಚಿತ ಔಷಧ ಮತ್ತು ಕೀಟನಾಶಕಗಳ ನಿಖರ ಪ್ರಮಾಣ ತಿಳಿಯುವುದರಿಂದ ಅನಗತ್ಯ ಖರ್ಚು ಉಳಿತಾಯ ಮತ್ತು ಇಳುವರಿ ರಕ್ಷಣೆ.",
        benefitHi = "सटीक दवा व खुराक की जानकारी मिलने से अनावश्यक खर्च की बचत और फसल सुरक्षा।",
        benefitEn = "Reduces chemical spraying costs and prevents catastrophic harvest losses with scientific dosages."
    ),
    FeatureGuideItem(
        id = "crop_nutrition",
        icon = Icons.Default.Science,
        targetTab = 1,
        titleKn = "ಗೊಬ್ಬರ ಮತ್ತು ಪೋಷಕಾಂಶ ಕ್ಯಾಲ್ಕುಲೇಟರ್",
        titleHi = "उर्वरक और पोषण कैलकुलेटर",
        titleEn = "Fertilizer & Nutrition Calculator",
        purposeKn = "ಕರ್ನಾಟಕದ ಪ್ರತಿ ಬೆಳೆ ಮತ್ತು ಜಮೀನಿನ ವಿಸ್ತೀರ್ಣಕ್ಕೆ ತಕ್ಕಂತೆ N-P-K ಗೊಬ್ಬರ, ಸೂಕ್ಷ್ಮ ಪೋಷಕಾಂಶ ಹಾಗೂ ಕಳೆನಾಶಕ ಲೆಕ್ಕಾಚಾರ.",
        purposeHi = "कर्नाटक की फसलों और खेत के रकबे के अनुसार N-P-K खाद, सूक्ष्म पोषक तत्व और खरपतवारनाशी की सही गणना।",
        purposeEn = "Calculate exact scientific N-P-K nutrient doses, micronutrients, and selective herbicides per acre.",
        howToUseKn = "ನಿಮ್ಮ ಬೆಳೆ ಮತ್ತು ಎಕರೆ ಸಂಖ್ಯೆಯನ್ನು ಆಯ್ಕೆ ಮಾಡಿ; ತಕ್ಷಣವೇ ಗೊಬ್ಬರ ಪ್ರಮಾಣ ಮತ್ತು ಸಿಂಪಡಣೆ ವೇಳಾಪಟ್ಟಿ ನೋಡಿ.",
        howToUseHi = "अपनी फसल और एकड़ चुनें; तुरंत खाद की सटीक मात्रा और छिड़काव का समय देखें।",
        howToUseEn = "Select crop and enter farm acreage to view instant recommended kg/bag dosage schedule.",
        benefitKn = "ಅತಿಯಾದ ಗೊಬ್ಬರ ಬಳಕೆಯ ವೆಚ್ಚ ತಗ್ಗಿಸುತ್ತದೆ ಮತ್ತು ಮಣ್ಣಿನ ಫಲವತ್ತತೆ ಕಾಪಾಡುತ್ತದೆ.",
        benefitHi = "अत्यधिक खाद के खर्च को कम करता है और मिट्टी की उपजाऊ क्षमता बनाए रखता है।",
        benefitEn = "Eliminates excessive fertilizer expense while optimizing crop vegetative growth and soil health."
    ),
    FeatureGuideItem(
        id = "market_arbitrage",
        icon = Icons.Default.Storefront,
        targetTab = 2,
        titleKn = "ಎಪಿಎಂಸಿ ಮಂಡಿ ದರಗಳು & ಕ್ವಿಂಟಾಲ್ ಲೆಕ್ಕ",
        titleHi = "APMC मंडी भाव और क्विंटल दर",
        titleEn = "APMC Mandi Rates & Quintal Arbitrage",
        purposeKn = "ಕರ್ನಾಟಕದ ಪ್ರಮುಖ ಮಾರುಕಟ್ಟೆಗಳಲ್ಲಿ (ಚಿಕ್ಕಮಗಳೂರು, ಯಶವಂತಪುರ, ಮೈಸೂರು, ಹುಬ್ಬಳ್ಳಿ) ಕಾಫಿ, ಅಡಿಕೆ ಹಾಗೂ ತರಕಾರಿಗಳ ಅಧಿಕೃತ ದಿನನಿತ್ಯದ ಕ್ವಿಂಟಾಲ್ ಹಾಗೂ ಕಿಲೋ ದರ ಪ್ರದರ್ಶನ.",
        purposeHi = "कर्नाटक की प्रमुख मंडियों (चिकमगलूर, यशवंतपुर, मैसूर, हुबली) में कॉफी, सुपारी व सब्जियों के आधिकारिक दैनिक क्विंटल व प्रति किलो भाव।",
        purposeEn = "Real-time official daily APMC market prices across Chikkamagaluru (primary), Bengaluru, Mysuru, and Hubballi per Quintal (100 kg) & Kg.",
        howToUseKn = "ಉತ್ಪನ್ನ ಆಯ್ಕೆ ಮಾಡಿ; ಚಿಕ್ಕಮಗಳೂರು ಮತ್ತು ಇತರ ಮಂಡಿಗಳ ಇಂದಿನ ಹರಾಜು ದರ, ಆವಕ ಕ್ವಿಂಟಾಲ್‌ನಲ್ಲಿ ಮತ್ತು ಹೆಚ್ಚಿನ ಲಾಭ ನೀಡುವ ಮಂಡಿ ಪರಿಶೀಲಿಸಿ.",
        howToUseHi = "उपज चुनें; चिकमगलूर व अन्य मंडियों के भाव, आवक और सर्वोत्तम लाभ वाली मंडी देखें।",
        howToUseEn = "Pick a commodity to view Chikkamagaluru & other APMC modal rates, arrival volumes, and arbitrage gains.",
        benefitKn = "ದಲ್ಲಾಳಿಗಳ ಸುಲಿಗೆಯಿಂದ ತಪ್ಪಿಸಿ, ಉತ್ತಮ ಬೆಲೆ ಸಿಗುವ ಮಾರುಕಟ್ಟೆಯಲ್ಲಿ ಕ್ವಿಂಟಾಲ್‌ಗೆ ₹200-₹500 ಹೆಚ್ಚಿನ ಲಾಭ ಪಡೆಯಲು ಸಹಾಯಕ.",
        benefitHi = "बिचौलियों से बचाव और उचित मंडी चुनकर प्रति क्विंटल ₹200-₹500 का अतिरिक्त लाभ।",
        benefitEn = "Empowers farmers with transparent auction rates, securing ₹200-₹500 higher net profit per quintal."
    ),
    FeatureGuideItem(
        id = "weather_advisory",
        icon = Icons.Default.Cloud,
        targetTab = 4,
        titleKn = "ನಿಖರ ಹವಾಮಾನ & ಗೂಗಲ್ ಮ್ಯಾಪ್ಸ್ ಸ್ಥಳ",
        titleHi = "सटीक मौसम व गूगल मैप्स लोकेशन",
        titleEn = "Hyper-Local Weather & Google Maps",
        purposeKn = "ನಿಮ್ಮ ನೈಜ ಜಿಪಿಎಸ್ ಹೊಲದ ಸ್ಥಳ ಅಥವಾ ಯಾವುದೇ ಕರ್ನಾಟಕ ಜಿಲ್ಲೆಯ ತಾಪಮಾನ, ಗಾಳಿ ವೇಗ, ಆರ್ದ್ರತೆ ಮತ್ತು ಸಂವಾದಾತ್ಮಕ ಗೂಗಲ್ ಮ್ಯಾಪ್ಸ್ ವೀಕ್ಷಣೆ.",
        purposeHi = "आपके वास्तविक जीपीएस खेत या कर्नाटक के किसी भी जिले का तापमान, हवा की गति, आर्द्रता और इंटरैक्टिव गूगल मैप्स देखने की सुविधा।",
        purposeEn = "Interactive Google Maps & live GPS weather telemetry for your exact farm or any Karnataka district with precision spray windows.",
        howToUseKn = "ಲೈವ್ ಜಿಪಿಎಸ್ ಬಟನ್ ಟ್ಯಾಪ್ ಮಾಡಿ ಅಥವಾ ನಕ್ಷೆಯಲ್ಲಿ ಸ್ಥಳ ಹುಡುಕಿ, ಉಪಗ್ರಹ ನಕ್ಷೆ ವೀಕ್ಷಿಸಿ ಮತ್ತು AI ಗೂಗಲ್ ಮ್ಯಾಪ್ಸ್ ವರದಿ ಪಡೆಯಿರಿ.",
        howToUseHi = "लाइव जीपीएस बटन दबाएं या मैप पर स्थान खोजें, सैटेलाइट मैप देखें और AI गूगल मैप्स रिपोर्ट पाएं।",
        howToUseEn = "Tap 'Detect My Live GPS' or search any town on Google Maps, toggle satellite view, and view AI Google Maps telemetry.",
        benefitKn = "ಮಳೆ ಅಥವಾ ಗಾಳಿಯಲ್ಲಿ ಕೀಟನಾಶಕ ಕೊಚ್ಚಿಹೋಗುವುದು ತಪ್ಪುತ್ತದೆ ಮತ್ತು ಶಿಲೀಂಧ್ರ ರೋಗಗಳ ಮುನ್ಸೂಚನೆ ಮೊದಲೇ ಸಿಗುತ್ತದೆ.",
        benefitHi = "बारिश में दवा धुलने का नुकसान बचता है और फंगल रोगों की पूर्व सूचना मिलती है।",
        benefitEn = "Prevents pesticide wash-off by rain, avoiding wasted chemical sprays and containing fungal blight."
    ),
    FeatureGuideItem(
        id = "voice_assistant",
        icon = Icons.Default.Mic,
        targetTab = 0,
        titleKn = "ಧ್ವನಿ ಕೃಷಿ ಸಹಾಯಕ (ಕನ್ನಡ / ಹಿಂದಿ / ಇಂಗ್ಲಿಷ್)",
        titleHi = "आवाज सहायक (कन्नड़ / हिंदी / अंग्रेजी)",
        titleEn = "Multilingual Voice AI Assistant",
        purposeKn = "ಟೈಪ್ ಮಾಡುವ ಅಗತ್ಯವಿಲ್ಲದೆ, ನೇರವಾಗಿ ಮಾತನಾಡಿ ಯಾವುದೇ ಬೆಳೆ, ರೋಗ, ಗೊಬ್ಬರ ಅಥವಾ ಮಾರುಕಟ್ಟೆ ಬೆಲೆಯ ಉತ್ತರ ಪಡೆಯುವುದು.",
        purposeHi = "टाइप करने की जरूरत नहीं, सीधे बोलकर किसी भी फसल रोग, खाद या मंडी भाव की जानकारी पाना।",
        purposeEn = "Hands-free voice recognition in Kannada, Hindi, and English for immediate agricultural answers.",
        howToUseKn = "ಮೈಕ್ ಐಕಾನ್ ಒತ್ತಿ ನಿಮ್ಮ ಪ್ರಶ್ನೆ ಕೇಳಿ (ಉದಾ: 'ಮೆಕ್ಕೆಜೋಳದ ಬಿಳಿ ಎಲೆಗೆ ಏನು ಮಾಡಬೇಕು?' ಅಥವಾ 'ಇಂದಿನ ಟೊಮೇಟೊ ದರ ಎಷ್ಟು?').",
        howToUseHi = "माइक बटन दबाकर पूछें (जैसे: 'मक्के में सफेद पत्ती का इलाज' या 'आज टमाटर का क्या भाव है?').",
        howToUseEn = "Tap the Mic button and speak naturally about symptoms, market queries, or fertilizer schedules.",
        benefitKn = "ಎಲ್ಲಾ ರೈತರಿಗೂ ಸುಲಭ ಬಳಕೆ; ಬೆರಳಚ್ಚು ಅಥವಾ ಬರವಣಿಗೆಯ ಅಗತ್ಯವಿಲ್ಲದೆ ಕ್ಷಣಾರ್ಧದಲ್ಲಿ ಮಾಹಿತಿ ಲಭ್ಯ.",
        benefitHi = "सभी किसान भाइयों के लिए उपयोग में आसान; बिना टाइप किए सीधे अपनी भाषा में उत्तर।",
        benefitEn = "100% accessible to every farmer regardless of literacy, offering rapid spoken agronomic guidance."
    ),
    FeatureGuideItem(
        id = "farmer_profile",
        icon = Icons.Default.History,
        targetTab = 5,
        titleKn = "ರೈತರ ಪ್ರೊಫೈಲ್ & ಡಿಜಿಟಲ್ ಚೀಟಿ ಸಂಗ್ರಹ",
        titleHi = "किसान प्रोफ़ाइल और डिजिटल पर्ची",
        titleEn = "Farmer Profile & Prescription Records",
        purposeKn = "ರೈತರು ತಮ್ಮ ಹೆಸರು, ಗ್ರಾಮ ಮತ್ತು ಬೆಳೆಗಳ ವಿವರಗಳನ್ನು ನಮೂದಿಸಿ ಹಿಂದಿನ ಎಲ್ಲಾ ರೋಗ ತಪಾಸಣೆಗಳನ್ನು ಡಿಜಿಟಲ್ ಆಗಿ ಸಂಗ್ರಹಿಸಿಡುವುದು.",
        purposeHi = "किसान अपना नाम, गांव और फसल दर्ज कर पुरानी सभी रोग जांच पर्चियों को सुरक्षित रख सकते हैं।",
        purposeEn = "Maintain farm identity under farmer's name and village, storing offline history and printable slips.",
        howToUseKn = "'ಪ್ರೊಫೈಲ್' ಟ್ಯಾಬ್‌ನಲ್ಲಿ ನಿಮ್ಮ ಹೆಸರು ಮತ್ತು ವಿವರ ನಮೂದಿಸಿ; ಯಾವುದೇ ಹಿಂದಿನ ವರದಿಯನ್ನು ಶೇರ್ ಅಥವಾ ಪ್ರಿಂಟ್ ಮಾಡಿ.",
        howToUseHi = "'प्रोफ़ाइल' में अपना विवरण भरें; पुरानी पर्ची को कभी भी देखें, साझा करें या प्रिंट करें।",
        howToUseEn = "Enter your profile in the Profile tab; review past diagnoses, share on WhatsApp, or export slips.",
        benefitKn = "ಪ್ರತಿ ವರ್ಷ ಬಂದ ರೋಗಗಳು ಮತ್ತು ನೀಡಿದ ಔಷಧಿಗಳ ಇತಿಹಾಸ ಭದ್ರವಾಗಿ ನಿಮ್ಮ ಮೊಬೈಲ್‌ನಲ್ಲೇ ಉಳಿಯುತ್ತದೆ.",
        benefitHi = "हर मौसम में हुए रोगों और दी गई दवाओं का पूरा रिकॉर्ड आपके पास सुरक्षित रहता है।",
        benefitEn = "Enables long-term agronomic record-keeping, avoiding repeating ineffective chemicals season after season."
    ),
    FeatureGuideItem(
        id = "offline_database",
        icon = Icons.Default.Storage,
        targetTab = 0,
        titleKn = "ಆಫ್‌ಲೈನ್ ಬೆಂಬಲ & ವೇಗದ ಕಾರ್ಯಕ್ಷಮತೆ",
        titleHi = "ऑफलाइन मोड और सुपरफास्ट स्पीड",
        titleEn = "Offline Resilience & 1-Second Launch",
        purposeKn = "ಗ್ರಾಮೀಣ ಭಾಗಗಳಲ್ಲಿ ಇಂಟರ್ನೆಟ್ ನೆಟ್‌ವರ್ಕ್ ಇಲ್ಲದಿದ್ದರೂ ಆಪ್ ತಕ್ಷಣವೇ ತೆರೆದು ಉಳಿಸಿದ ಮಾಹಿತಿಗಳನ್ನು ತೋರಿಸುವುದು.",
        purposeHi = "ग्रामीण इलाकों में नेटवर्क न होने पर भी ऐप 1 सेकंड में खुले और सुरक्षित डेटा तुरंत दिखाए।",
        purposeEn = "Room Database offline caching ensuring the app loads within 2 seconds with cached rates and guides.",
        howToUseKn = "ಆಪ್ ನೇರವಾಗಿ ತೆರೆದು ಬಳಸಬಹುದು; ನೆಟ್‌ವರ್ಕ್ ಬಂದಾಗ ಸ್ವಯಂಚಾಲಿತವಾಗಿ ಹೊಸ ದರಗಳು ನವೀಕರಣಗೊಳ್ಳುತ್ತವೆ.",
        howToUseHi = "ऐप को कभी भी खोलें; नेटवर्क मिलने पर नए भाव अपने आप अपडेट हो जाएंगे।",
        howToUseEn = "Works automatically offline without hanging; syncs fresh telemetry whenever connectivity is restored.",
        benefitKn = "ಹೊಲದಲ್ಲಿ ನೆಟ್‌ವರ್ಕ್ ಇಲ್ಲದಿದ್ದರೂ ಅಗತ್ಯ ಕೃಷಿ ಮಾಹಿತಿ ಮತ್ತು ಇತಿಹಾಸ ನಿರಂತರ ಲಭ್ಯ.",
        benefitHi = "खेत में टावर न होने पर भी काम नहीं रुकता, पूरी जानकारी हमेशा साथ रहती है।",
        benefitEn = "Zero frustrating delays or loading screens when standing in remote rural farm plots."
    )
)

enum class GuideDisplayMode {
    SELECTED_LANGUAGE,
    COMPARE_ALL_THREE
}

@Composable
fun AppFeatureGuideScreen(
    viewModel: RaithaDrishtiViewModel,
    onNavigateToTab: (Int) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val currentLang by viewModel.currentLanguage.collectAsState()
    var displayMode by remember { mutableStateOf(GuideDisplayMode.SELECTED_LANGUAGE) }
    var activeLangFilter by remember { mutableStateOf(currentLang) }
    var expandedFeatureId by remember { mutableStateOf<String?>("crop_doctor") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Header Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("feature_guide_hero_card"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = ForestGreenDark),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, AmberLight.copy(alpha = 0.6f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .background(Color.White.copy(alpha = 0.15f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.TableChart,
                                contentDescription = "Feature Guide",
                                tint = AmberLight,
                                modifier = Modifier.size(26.dp)
                            )
                        }

                        Column {
                            Text(
                                text = when (activeLangFilter) {
                                    AppLanguage.KANNADA -> "ರೈತ ದೃಷ್ಟಿ • ವೈಶಿಷ್ಟ್ಯಗಳ ಸಮಗ್ರ ಮಾರ್ಗದರ್ಶಿ"
                                    AppLanguage.HINDI -> "रैत दृष्टि • सभी सुविधाओं की सारणी"
                                    AppLanguage.ENGLISH -> "RaithaDrishti • Complete Feature Matrix"
                                },
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 17.sp
                                ),
                                color = Color.White
                            )
                            Text(
                                text = when (activeLangFilter) {
                                    AppLanguage.KANNADA -> "3 ಭಾಷೆಗಳಲ್ಲಿ ಪ್ರತಿಯೊಂದು ಸೌಲಭ್ಯದ ಸ್ಪಷ್ಟ ವಿವರಣೆ"
                                    AppLanguage.HINDI -> "3 भाषाओं में हर फीचर का संक्षिप्त विवरण"
                                    AppLanguage.ENGLISH -> "Tabular breakdown of features in 3 languages"
                                },
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.5.sp),
                                color = AmberLight
                            )
                        }
                    }

                    // Speed Pill
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF047857),
                        border = androidx.compose.foundation.BorderStroke(1.dp, AmberLight.copy(alpha = 0.7f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Speed,
                                contentDescription = "Fast Speed",
                                tint = AmberLight,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "< 2s Ready",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = Color.White.copy(alpha = 0.2f))
                Spacer(modifier = Modifier.height(14.dp))

                // Language View Filter Chips (Kannada, Hindi, English, 3-Language Comparison)
                Text(
                    text = when (activeLangFilter) {
                        AppLanguage.KANNADA -> "ಭಾಷೆ ಆಯ್ಕೆ ಮಾಡಿ ಅಥವಾ 3 ಭಾಷೆಗಳ ತುಲನೆ ನೋಡಿ:"
                        AppLanguage.HINDI -> "भाषा चुनें या 3 भाषाओं की तुलना देखें:"
                        AppLanguage.ENGLISH -> "Select Language or Compare All 3 in Table:"
                    },
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.9f),
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Kannada Chip
                    FilterChip(
                        selected = displayMode == GuideDisplayMode.SELECTED_LANGUAGE && activeLangFilter == AppLanguage.KANNADA,
                        onClick = {
                            displayMode = GuideDisplayMode.SELECTED_LANGUAGE
                            activeLangFilter = AppLanguage.KANNADA
                        },
                        label = { Text("ಕನ್ನಡ (Kannada)", fontWeight = FontWeight.Bold, fontSize = 12.5.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AmberLight,
                            selectedLabelColor = Color(0xFF1E1500),
                            containerColor = Color.White.copy(alpha = 0.12f),
                            labelColor = Color.White
                        )
                    )

                    // Hindi Chip
                    FilterChip(
                        selected = displayMode == GuideDisplayMode.SELECTED_LANGUAGE && activeLangFilter == AppLanguage.HINDI,
                        onClick = {
                            displayMode = GuideDisplayMode.SELECTED_LANGUAGE
                            activeLangFilter = AppLanguage.HINDI
                        },
                        label = { Text("हिंदी (Hindi)", fontWeight = FontWeight.Bold, fontSize = 12.5.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AmberLight,
                            selectedLabelColor = Color(0xFF1E1500),
                            containerColor = Color.White.copy(alpha = 0.12f),
                            labelColor = Color.White
                        )
                    )

                    // English Chip
                    FilterChip(
                        selected = displayMode == GuideDisplayMode.SELECTED_LANGUAGE && activeLangFilter == AppLanguage.ENGLISH,
                        onClick = {
                            displayMode = GuideDisplayMode.SELECTED_LANGUAGE
                            activeLangFilter = AppLanguage.ENGLISH
                        },
                        label = { Text("English", fontWeight = FontWeight.Bold, fontSize = 12.5.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AmberLight,
                            selectedLabelColor = Color(0xFF1E1500),
                            containerColor = Color.White.copy(alpha = 0.12f),
                            labelColor = Color.White
                        )
                    )

                    // Compare All 3
                    FilterChip(
                        selected = displayMode == GuideDisplayMode.COMPARE_ALL_THREE,
                        onClick = {
                            displayMode = GuideDisplayMode.COMPARE_ALL_THREE
                        },
                        label = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.Language, contentDescription = null, modifier = Modifier.size(15.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("3 ಭಾಷೆಗಳ ತುಲನೆ (All 3)", fontWeight = FontWeight.Bold, fontSize = 12.5.sp)
                            }
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFFFBBF24),
                            selectedLabelColor = Color(0xFF1E1500),
                            containerColor = Color.White.copy(alpha = 0.12f),
                            labelColor = Color.White
                        )
                    )
                }
            }
        }

        // Summary Stats Banner
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("7+", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = ForestGreenPrimary)
                    Text(
                        text = when (activeLangFilter) {
                            AppLanguage.KANNADA -> "ಮುಖ್ಯ ಸೌಲಭ್ಯಗಳು"
                            AppLanguage.HINDI -> "प्रमुख सुविधाएं"
                            AppLanguage.ENGLISH -> "Core Features"
                        },
                        fontSize = 11.5.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Box(modifier = Modifier.width(1.dp).height(30.dp).background(MaterialTheme.colorScheme.outlineVariant))

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("3", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = Color(0xFFD97706))
                    Text(
                        text = when (activeLangFilter) {
                            AppLanguage.KANNADA -> "ರಾಜ್ಯ ಭಾಷೆಗಳು"
                            AppLanguage.HINDI -> "भाषा विकल्प"
                            AppLanguage.ENGLISH -> "Languages"
                        },
                        fontSize = 11.5.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Box(modifier = Modifier.width(1.dp).height(30.dp).background(MaterialTheme.colorScheme.outlineVariant))

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("100%", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = Color(0xFF2563EB))
                    Text(
                        text = when (activeLangFilter) {
                            AppLanguage.KANNADA -> "ಆಫ್‌ಲೈನ್ ಸಂಗ್ರಹ"
                            AppLanguage.HINDI -> "ऑफलाइन डेटा"
                            AppLanguage.ENGLISH -> "Offline Safe"
                        },
                        fontSize = 11.5.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Tabular Column Presentation of Every App Feature
        Text(
            text = when (activeLangFilter) {
                AppLanguage.KANNADA -> "ಅಪ್ಲಿಕೇಶನ್ ವೈಶಿಷ್ಟ್ಯಗಳ ವಿವರವಾದ ಕೋಷ್ಟಕ (Tabular Columns)"
                AppLanguage.HINDI -> "ऐप की विशेषताओं की विस्तृत तालिका (Tabular Columns)"
                AppLanguage.ENGLISH -> "Detailed Application Feature Columns (Matrix)"
            },
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            ),
            color = MaterialTheme.colorScheme.onSurface
        )

        // List of Tabular Feature Cards
        APP_FEATURES_CATALOG.forEachIndexed { index, item ->
            val isExpanded = expandedFeatureId == item.id

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("feature_table_card_${item.id}"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isExpanded) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surface.copy(alpha = 0.85f)
                ),
                border = androidx.compose.foundation.BorderStroke(
                    width = if (isExpanded) 1.5.dp else 1.dp,
                    color = if (isExpanded) ForestGreenPrimary else MaterialTheme.colorScheme.outlineVariant
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = if (isExpanded) 3.dp else 1.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    // Row 1: Header (Number + Icon + Title + Action Button)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                expandedFeatureId = if (isExpanded) null else item.id
                            },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            // Badge number & icon
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .background(ForestGreenPrimary.copy(alpha = 0.12f), RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = null,
                                    tint = ForestGreenPrimary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Column {
                                Text(
                                    text = "0${index + 1}. " + when (activeLangFilter) {
                                        AppLanguage.KANNADA -> item.titleKn
                                        AppLanguage.HINDI -> item.titleHi
                                        AppLanguage.ENGLISH -> item.titleEn
                                    },
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.5.sp
                                    ),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = when (activeLangFilter) {
                                        AppLanguage.KANNADA -> "ಟ್ಯಾಬ್ ತೆರೆಯಲು ಇಲ್ಲಿ ಒತ್ತಿರಿ"
                                        AppLanguage.HINDI -> "विवरण देखने के लिए टैप करें"
                                        AppLanguage.ENGLISH -> "Tap to toggle details & launch"
                                    },
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        // Direct Launch Button
                        Button(
                            onClick = { onNavigateToTab(item.targetTab) },
                            colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.testTag("launch_feature_button_${item.id}")
                        ) {
                            Text(
                                text = when (activeLangFilter) {
                                    AppLanguage.KANNADA -> "ತೆರೆಯಿರಿ"
                                    AppLanguage.HINDI -> "खोलें"
                                    AppLanguage.ENGLISH -> "Open"
                                },
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(imageVector = Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(14.dp))
                        }
                    }

                    // Expandable Tabular Content
                    AnimatedVisibility(
                        visible = isExpanded,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.7f))

                            if (displayMode == GuideDisplayMode.COMPARE_ALL_THREE) {
                                // 3-Language Comparison Table Rows
                                Text(
                                    text = "3-Language Tabular Comparison (ಮೂರು ಭಾಷೆಗಳ ತುಲನೆ):",
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ForestGreenPrimary
                                )

                                // Column Table 1: Kannada
                                TabularLanguageBox(
                                    langTitle = "ಕನ್ನಡ (Kannada)",
                                    purpose = item.purposeKn,
                                    howTo = item.howToUseKn,
                                    benefit = item.benefitKn,
                                    accentColor = ForestGreenPrimary
                                )

                                // Column Table 2: Hindi
                                TabularLanguageBox(
                                    langTitle = "हिंदी (Hindi)",
                                    purpose = item.purposeHi,
                                    howTo = item.howToUseHi,
                                    benefit = item.benefitHi,
                                    accentColor = Color(0xFFD97706)
                                )

                                // Column Table 3: English
                                TabularLanguageBox(
                                    langTitle = "English",
                                    purpose = item.purposeEn,
                                    howTo = item.howToUseEn,
                                    benefit = item.benefitEn,
                                    accentColor = Color(0xFF2563EB)
                                )
                            } else {
                                // Single Selected Language Tabular Matrix
                                val purposeText = when (activeLangFilter) {
                                    AppLanguage.KANNADA -> item.purposeKn
                                    AppLanguage.HINDI -> item.purposeHi
                                    AppLanguage.ENGLISH -> item.purposeEn
                                }
                                val howToText = when (activeLangFilter) {
                                    AppLanguage.KANNADA -> item.howToUseKn
                                    AppLanguage.HINDI -> item.howToUseHi
                                    AppLanguage.ENGLISH -> item.howToUseEn
                                }
                                val benefitText = when (activeLangFilter) {
                                    AppLanguage.KANNADA -> item.benefitKn
                                    AppLanguage.HINDI -> item.benefitHi
                                    AppLanguage.ENGLISH -> item.benefitEn
                                }

                                // Table Row 1: Purpose
                                TableRowCell(
                                    header = when (activeLangFilter) {
                                        AppLanguage.KANNADA -> "ಮುಖ್ಯ ಉದ್ದೇಶ"
                                        AppLanguage.HINDI -> "मुख्य उद्देश्य"
                                        AppLanguage.ENGLISH -> "Purpose & Objective"
                                    },
                                    value = purposeText,
                                    badgeColor = Color(0xFF047857)
                                )

                                // Table Row 2: How to Use
                                TableRowCell(
                                    header = when (activeLangFilter) {
                                        AppLanguage.KANNADA -> "ಬಳಸುವ ವಿಧಾನ"
                                        AppLanguage.HINDI -> "उपयोग कैसे करें"
                                        AppLanguage.ENGLISH -> "How to Use"
                                    },
                                    value = howToText,
                                    badgeColor = Color(0xFFD97706)
                                )

                                // Table Row 3: Key Benefit
                                TableRowCell(
                                    header = when (activeLangFilter) {
                                        AppLanguage.KANNADA -> "ರೈತರಿಗೆ ಲಾಭ"
                                        AppLanguage.HINDI -> "किसान लाभ"
                                        AppLanguage.ENGLISH -> "Farmer Advantage"
                                    },
                                    value = benefitText,
                                    badgeColor = Color(0xFF2563EB)
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun TableRowCell(
    header: String,
    value: String,
    badgeColor: Color
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(badgeColor, CircleShape)
                )
                Text(
                    text = header,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.5.sp
                    ),
                    color = badgeColor
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.5.sp),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun TabularLanguageBox(
    langTitle: String,
    purpose: String,
    howTo: String,
    benefit: String,
    accentColor: Color
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = accentColor.copy(alpha = 0.05f),
        border = androidx.compose.foundation.BorderStroke(1.dp, accentColor.copy(alpha = 0.35f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = langTitle,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 13.sp,
                color = accentColor
            )
            HorizontalDivider(color = accentColor.copy(alpha = 0.2f))
            Text(
                text = "• ಉದ್ದೇಶ/उद्देश्य: $purpose",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "• ವಿಧಾನ/प्रक्रिया: $howTo",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "• ಲಾಭ/लाभ: $benefit",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = accentColor
            )
        }
    }
}
