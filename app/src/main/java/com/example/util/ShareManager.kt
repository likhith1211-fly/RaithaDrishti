package com.example.util

import android.content.Context
import android.content.Intent
import com.example.data.model.AppLanguage
import com.example.data.model.CropDiagnosisResult
import com.example.data.model.MarketAnalytics
import com.example.data.model.WeeklyMarketAnalysis
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ShareManager {

    fun buildCropDiagnosisShareText(
        result: CropDiagnosisResult,
        language: AppLanguage,
        dateStr: String
    ): String {
        return buildString {
            when (language) {
                AppLanguage.KANNADA -> {
                    append("🌾 *ರೈತ ದೃಷ್ಟಿ - ಬೆಳೆ ರೋಗ ಪರೀಕ್ಷಾ ವರದಿ* 🌾\n")
                    append("━━━━━━━━━━━━━━━━━━━━\n")
                    append("📅 ದಿನಾಂಕ: $dateStr\n")
                    if (result.cropName.isNotBlank()) append("🌱 ಬೆಳೆ: *${result.cropName}*\n")
                    append("🔍 ಪತ್ತೆಯಾದ ರೋಗ: *${result.diagnosis}*\n")
                    append("⚠️ ತೀವ್ರತೆ: *${result.severity}* (ಖಚಿತತೆ: ${result.confidence}%)\n\n")

                    append("📋 *ಸಂಕ್ಷಿಪ್ತ ವಿವರಣೆ:*\n")
                    append("${result.summary}\n\n")

                    if (result.immediateActions.isNotEmpty()) {
                        append("⚡ *ತಕ್ಷಣದ ಕ್ರಮಗಳು:*\n")
                        result.immediateActions.forEach { append("• $it\n") }
                        append("\n")
                    }

                    if (result.chemicalFertilizers.isNotEmpty() || result.selectiveHerbicides.isNotEmpty()) {
                        append("🧪 *ಔಷಧಿ / ಕೀಟನಾಶಕ ಸಿಂಪಡಣೆ:*\n")
                        (result.chemicalFertilizers + result.selectiveHerbicides).take(3).forEach { append("• $it\n") }
                        append("\n")
                    }

                    if (result.organicFertilizers.isNotEmpty()) {
                        append("🌿 *ಸಾವಯವ ಪರಿಹಾರಗಳು:*\n")
                        result.organicFertilizers.take(3).forEach { append("• $it\n") }
                        append("\n")
                    }

                    if (result.safety.isNotEmpty()) {
                        append("🛡️ *ರೈತರ ಸುರಕ್ಷತೆ:*\n")
                        result.safety.take(2).forEach { append("• $it\n") }
                        append("\n")
                    }

                    append("━━━━━━━━━━━━━━━━━━━━\n")
                    append("📱 *ರೈತ ದೃಷ್ಟಿ (Raitha Drishti)* ಕರ್ನಾಟಕ ರೈತರ ಕೃಷಿ ಸಂಗಾತಿ")
                }
                AppLanguage.HINDI -> {
                    append("🌾 *रैत दृष्टि - फसल रोग जांच रिपोर्ट* 🌾\n")
                    append("━━━━━━━━━━━━━━━━━━━━\n")
                    append("📅 दिनांक: $dateStr\n")
                    if (result.cropName.isNotBlank()) append("🌱 फसल: *${result.cropName}*\n")
                    append("🔍 पहचाना गया रोग: *${result.diagnosis}*\n")
                    append("⚠️ गंभीरता: *${result.severity}* (सटीकता: ${result.confidence}%)\n\n")

                    append("📋 *संक्षिप्त विवरण:*\n")
                    append("${result.summary}\n\n")

                    if (result.immediateActions.isNotEmpty()) {
                        append("⚡ *तत्काल आवश्यक कदम:*\n")
                        result.immediateActions.forEach { append("• $it\n") }
                        append("\n")
                    }

                    if (result.chemicalFertilizers.isNotEmpty() || result.selectiveHerbicides.isNotEmpty()) {
                        append("🧪 *अनुशंसित दवा / छिड़काव:*\n")
                        (result.chemicalFertilizers + result.selectiveHerbicides).take(3).forEach { append("• $it\n") }
                        append("\n")
                    }

                    if (result.organicFertilizers.isNotEmpty()) {
                        append("🌿 *जैविक उपचार:*\n")
                        result.organicFertilizers.take(3).forEach { append("• $it\n") }
                        append("\n")
                    }

                    append("━━━━━━━━━━━━━━━━━━━━\n")
                    append("📱 *रैत दृष्टि (Raitha Drishti)* किसान डिजिटल सहायक")
                }
                AppLanguage.ENGLISH -> {
                    append("🌾 *Raitha Drishti - Crop Pathology Diagnosis Report* 🌾\n")
                    append("━━━━━━━━━━━━━━━━━━━━\n")
                    append("📅 Date: $dateStr\n")
                    if (result.cropName.isNotBlank()) append("🌱 Crop: *${result.cropName}*\n")
                    append("🔍 Disease Identified: *${result.diagnosis}*\n")
                    append("⚠️ Severity: *${result.severity}* (Confidence: ${result.confidence}%)\n\n")

                    append("📋 *Summary:*\n")
                    append("${result.summary}\n\n")

                    if (result.immediateActions.isNotEmpty()) {
                        append("⚡ *Immediate Field Actions:*\n")
                        result.immediateActions.forEach { append("• $it\n") }
                        append("\n")
                    }

                    if (result.chemicalFertilizers.isNotEmpty() || result.selectiveHerbicides.isNotEmpty()) {
                        append("🧪 *Recommended Spray / Treatment:*\n")
                        (result.chemicalFertilizers + result.selectiveHerbicides).take(3).forEach { append("• $it\n") }
                        append("\n")
                    }

                    if (result.organicFertilizers.isNotEmpty()) {
                        append("🌿 *Organic Solutions:*\n")
                        result.organicFertilizers.take(3).forEach { append("• $it\n") }
                        append("\n")
                    }

                    append("━━━━━━━━━━━━━━━━━━━━\n")
                    append("📱 *Generated via Raitha Drishti AI Agriculture Assistant*")
                }
            }
        }
    }

    fun shareCropDiagnosisReport(
        context: Context,
        result: CropDiagnosisResult,
        language: AppLanguage,
        farmerName: String = "Farmer"
    ) {
        val dateFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
        val dateStr = dateFormat.format(Date(result.timestamp))
        val text = buildCropDiagnosisShareText(result, language, dateStr)

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "${result.cropName} Diagnosis Report - Raitha Drishti")
            putExtra(Intent.EXTRA_TEXT, text)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }

        val chooserTitle = when (language) {
            AppLanguage.KANNADA -> "ವರದಿಯನ್ನು WhatsApp / ಇತರ ಆ್ಯಪ್ ಮೂಲಕ ಹಂಚಿಕೊಳ್ಳಿ"
            AppLanguage.HINDI -> "रिपोर्ट WhatsApp या अन्य ऐप पर साझा करें"
            AppLanguage.ENGLISH -> "Share Pathology Report via WhatsApp"
        }

        val chooser = Intent.createChooser(shareIntent, chooserTitle).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(chooser)
    }

    fun shareCropDiagnosisWhatsApp(
        context: Context,
        result: CropDiagnosisResult,
        language: AppLanguage,
        farmerName: String = "Farmer"
    ) {
        val dateFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
        val dateStr = dateFormat.format(Date(result.timestamp))
        val text = buildCropDiagnosisShareText(result, language, dateStr)

        try {
            val whatsappIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                setPackage("com.whatsapp")
                putExtra(Intent.EXTRA_TEXT, text)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(whatsappIntent)
        } catch (e: Exception) {
            // Fallback to chooser if WhatsApp not installed
            shareCropDiagnosisGeneral(context, result, language, farmerName)
        }
    }

    fun shareCropDiagnosisGeneral(
        context: Context,
        result: CropDiagnosisResult,
        language: AppLanguage,
        farmerName: String = "Farmer"
    ) {
        shareCropDiagnosisReport(context, result, language, farmerName)
    }

    fun shareMarketPriceSnapshot(
        context: Context,
        analytics: MarketAnalytics,
        weekly: WeeklyMarketAnalysis?,
        language: AppLanguage
    ) {
        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        val todayStr = dateFormat.format(Date())

        val text = buildString {
            when (language) {
                AppLanguage.KANNADA -> {
                    append("📊 *ರೈತ ದೃಷ್ಟಿ - ಕರ್ನಾಟಕ ಎಪಿಎಂಸಿ ಮಾರುಕಟ್ಟೆ ದರ* 📊\n")
                    append("━━━━━━━━━━━━━━━━━━━━\n")
                    append("🌱 ಬೆಳೆ: *${analytics.commodity}*\n")
                    append("📅 ದಿನಾಂಕ: $todayStr\n\n")

                    append("🏛️ *ಇಂದಿನ ಮಂಡಿ ದರಗಳು (ಕ್ವಿಂಟಾಲ್‌ಗೆ):*\n")
                    analytics.mandiPrices.forEach { m ->
                        val changeSign = if (m.dailyChangePercent >= 0) "+${m.dailyChangePercent}%" else "${m.dailyChangePercent}%"
                        append("• *${m.mandiName}*: ₹${m.modalPrice.toInt()} ($changeSign)\n")
                    }
                    append("\n")

                    append("🏆 *ಗರಿಷ್ಠ ಬೆಲೆಯ ಮಂಡಿ:* ${analytics.bestMandi}\n")
                    append("💰 *ದರ ವ್ಯತ್ಯಾಸ (ಲಾಭದ ಅವಕಾಶ):* ₹${analytics.priceSpread.toInt()}/ಕ್ವಿಂಟಾಲ್\n\n")

                    if (weekly != null) {
                        append("📈 *7 ದಿನಗಳ ಸಾರಾಂಶ:*\n")
                        append("• ಗರಿಷ್ಠ ದರ: ₹${weekly.weeklyHighPrice.toInt()}\n")
                        append("• ಕನಿಷ್ಠ ದರ: ₹${weekly.weeklyLowPrice.toInt()}\n")
                        append("• ವಾರದ ಸರಾಸರಿ: ₹${weekly.weeklyAveragePrice.toInt()}\n")
                        append("• ನಿರ್ಧಾರ: *${weekly.sellDecision.labelKn}*\n\n")
                        append("💡 *ತಜ್ಞರ ಸಲಹೆ:* ${weekly.advisoryKn}\n\n")
                    }

                    append("━━━━━━━━━━━━━━━━━━━━\n")
                    append("📱 *ರೈತ ದೃಷ್ಟಿ (Raitha Drishti)* ಕರ್ನಾಟಕ ಕೃಷಿ ಮಾರುಕಟ್ಟೆ ಮಾಹಿತಿ")
                }
                AppLanguage.HINDI -> {
                    append("📊 *रैत दृष्टि - कर्नाटक एपीएमसी मंडी भाव* 📊\n")
                    append("━━━━━━━━━━━━━━━━━━━━\n")
                    append("🌱 फसल: *${analytics.commodity}*\n")
                    append("📅 दिनांक: $todayStr\n\n")

                    append("🏛️ *आज के मंडी भाव (प्रति क्विंटल):*\n")
                    analytics.mandiPrices.forEach { m ->
                        val changeSign = if (m.dailyChangePercent >= 0) "+${m.dailyChangePercent}%" else "${m.dailyChangePercent}%"
                        append("• *${m.mandiName}*: ₹${m.modalPrice.toInt()} ($changeSign)\n")
                    }
                    append("\n")

                    append("🏆 *सर्वोत्तम भाव मंडी:* ${analytics.bestMandi}\n")
                    append("💰 *भाव अंतर (मुनाफे का अवसर):* ₹${analytics.priceSpread.toInt()}/क्विंटल\n\n")

                    if (weekly != null) {
                        append("📈 *7 दिन का विश्लेषण:*\n")
                        append("• उच्चतम भाव: ₹${weekly.weeklyHighPrice.toInt()}\n")
                        append("• न्यूनतम भाव: ₹${weekly.weeklyLowPrice.toInt()}\n")
                        append("• साप्ताहिक औसत: ₹${weekly.weeklyAveragePrice.toInt()}\n")
                        append("• बिक्री सलाह: *${weekly.sellDecision.labelHi}*\n\n")
                        append("💡 *सलाह:* ${weekly.advisoryHi}\n\n")
                    }

                    append("━━━━━━━━━━━━━━━━━━━━\n")
                    append("📱 *रैत दृष्टि (Raitha Drishti)* डिजिटल मंडी साथी")
                }
                AppLanguage.ENGLISH -> {
                    append("📊 *Raitha Drishti - Karnataka APMC Market Price Snapshot* 📊\n")
                    append("━━━━━━━━━━━━━━━━━━━━\n")
                    append("🌱 Commodity: *${analytics.commodity}*\n")
                    append("📅 Date: $todayStr\n\n")

                    append("🏛️ *Today's Mandi Prices (Per Quintal):*\n")
                    analytics.mandiPrices.forEach { m ->
                        val changeSign = if (m.dailyChangePercent >= 0) "+${m.dailyChangePercent}%" else "${m.dailyChangePercent}%"
                        append("• *${m.mandiName}*: ₹${m.modalPrice.toInt()} ($changeSign)\n")
                    }
                    append("\n")

                    append("🏆 *Best Mandi:* ${analytics.bestMandi}\n")
                    append("💰 *Arbitrage Spread:* ₹${analytics.priceSpread.toInt()}/Qtl\n\n")

                    if (weekly != null) {
                        append("📈 *7-Day Price Trends:*\n")
                        append("• 7-Day High: ₹${weekly.weeklyHighPrice.toInt()}\n")
                        append("• 7-Day Low: ₹${weekly.weeklyLowPrice.toInt()}\n")
                        append("• Weekly Average: ₹${weekly.weeklyAveragePrice.toInt()}\n")
                        append("• Recommendation: *${weekly.sellDecision.labelEn}*\n\n")
                        append("💡 *Advisory:* ${weekly.advisoryEn}\n\n")
                    }

                    append("━━━━━━━━━━━━━━━━━━━━\n")
                    append("📱 *Generated via Raitha Drishti APMC Market Intelligence*")
                }
            }
        }

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "${analytics.commodity} Mandi Price Snapshot - Raitha Drishti")
            putExtra(Intent.EXTRA_TEXT, text)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }

        val chooserTitle = when (language) {
            AppLanguage.KANNADA -> "ಮಾರುಕಟ್ಟೆ ದರವನ್ನು WhatsApp / ಇತರ ಆ್ಯಪ್ ಮೂಲಕ ಹಂಚಿಕೊಳ್ಳಿ"
            AppLanguage.HINDI -> "मंडी भाव WhatsApp पर शेयर करें"
            AppLanguage.ENGLISH -> "Share APMC Price Snapshot via WhatsApp"
        }

        val chooser = Intent.createChooser(shareIntent, chooserTitle).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(chooser)
    }
}
