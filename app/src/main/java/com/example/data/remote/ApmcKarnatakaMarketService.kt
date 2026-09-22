package com.example.data.remote

import com.example.data.model.DailyPriceRecord
import com.example.data.model.MandiPriceInfo
import com.example.data.model.MarketAnalytics
import com.example.data.model.PriceTrendPoint
import com.example.data.model.SellDecision
import com.example.data.model.WeeklyMarketAnalysis
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ApmcKarnatakaMarketService {

    private val baseCommodities = mapOf(
        "Pumpkin" to Triple(1850.0, 1650.0, 1550.0),     // Pumpkin (ಕುಂಬಳಕಾಯಿ / कद्दू) - Bengaluru, Mysuru, Chikkamagaluru
        "Tomato" to Triple(2450.0, 2180.0, 2320.0),      // Bengaluru, Mysuru, Chikkamagaluru (₹/quintal)
        "Potato" to Triple(1850.0, 1920.0, 1780.0),
        "Onion" to Triple(2900.0, 2650.0, 2750.0),
        "Green Chilli" to Triple(4200.0, 3950.0, 4400.0),
        "Ash Gourd" to Triple(1650.0, 1500.0, 1400.0),    // ಬೂದು ಕುಂಬಳ / पेठा
        "Drumstick" to Triple(4800.0, 4400.0, 4600.0),    // ನುಗ್ಗೆಕಾಯಿ / सहजन
        "Beans" to Triple(3800.0, 3500.0, 3650.0),        // ಹುರುಳಿಕಾಯಿ / बीन्स
        "Cabbage" to Triple(1450.0, 1300.0, 1380.0),      // ಎಲೆಕೋಸು / पत्तागोभी
        "Capsicum" to Triple(3600.0, 3300.0, 3450.0),     // ದಪ್ಪ ಮೆಣಸಿನಕಾಯಿ / शिमला मिर्च
        "Ginger" to Triple(8500.0, 8100.0, 8900.0),       // ಶುಂಠಿ / अदरक
        "Garlic" to Triple(14200.0, 13800.0, 14500.0),   // ಬೆಳ್ಳುಳ್ಳಿ / लहसुन
        "Arecanut (Rashi)" to Triple(48500.0, 47200.0, 49800.0),
        "Coffee (Arabica Parchment)" to Triple(15800.0, 15400.0, 16200.0),
        "Ragi" to Triple(3250.0, 3180.0, 3100.0),
        "Maize" to Triple(2150.0, 2080.0, 2120.0)
    )

    fun getMarketAnalytics(commodity: String): MarketAnalytics {
        val base = baseCommodities[commodity] ?: Triple(2400.0, 2200.0, 2300.0)
        val bengaluruBase = base.first
        val mysuruBase = base.second
        val chikkamagaluruBase = base.third

        val trendList = mutableListOf<PriceTrendPoint>()
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("dd MMM", Locale.ENGLISH)

        // Generate 14-day realistic trend curve
        calendar.add(Calendar.DAY_OF_YEAR, -13)
        for (i in 0 until 14) {
            val dateStr = dateFormat.format(calendar.time)
            val factor = 1.0 + Math.sin(i * 0.45) * 0.08 + (i * 0.005)
            val bPrice = Math.round(bengaluruBase * factor * (1.0 + (i % 3 - 1) * 0.015)).toDouble()
            val mPrice = Math.round(mysuruBase * factor * (1.0 + (i % 2) * 0.012)).toDouble()
            val cPrice = Math.round(chikkamagaluruBase * factor * (1.0 - (i % 3) * 0.01)).toDouble()

            trendList.add(
                PriceTrendPoint(
                    displayDate = dateStr,
                    bengaluruPrice = bPrice,
                    mysuruPrice = mPrice,
                    chikkamagaluruPrice = cPrice
                )
            )
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        val latest = trendList.last()
        val prev = trendList[trendList.size - 2]

        val bChange = Math.round(((latest.bengaluruPrice - prev.bengaluruPrice) / prev.bengaluruPrice) * 1000) / 10.0
        val mChange = Math.round(((latest.mysuruPrice - prev.mysuruPrice) / prev.mysuruPrice) * 1000) / 10.0
        val cChange = Math.round(((latest.chikkamagaluruPrice - prev.chikkamagaluruPrice) / prev.chikkamagaluruPrice) * 1000) / 10.0

        val mandiPrices = listOf(
            MandiPriceInfo(
                mandiName = "Bengaluru APMC (Yeshwanthpur)",
                modalPrice = latest.bengaluruPrice,
                minPrice = Math.round(latest.bengaluruPrice * 0.92).toDouble(),
                maxPrice = Math.round(latest.bengaluruPrice * 1.08).toDouble(),
                dailyChangePercent = bChange
            ),
            MandiPriceInfo(
                mandiName = "Mysuru APMC (Bandipalya)",
                modalPrice = latest.mysuruPrice,
                minPrice = Math.round(latest.mysuruPrice * 0.91).toDouble(),
                maxPrice = Math.round(latest.mysuruPrice * 1.07).toDouble(),
                dailyChangePercent = mChange
            ),
            MandiPriceInfo(
                mandiName = "Chikkamagaluru Mandi",
                modalPrice = latest.chikkamagaluruPrice,
                minPrice = Math.round(latest.chikkamagaluruPrice * 0.90).toDouble(),
                maxPrice = Math.round(latest.chikkamagaluruPrice * 1.06).toDouble(),
                dailyChangePercent = cChange
            )
        )

        // Calculate inter-mandi arbitrage
        val prices = listOf(
            Pair("Bengaluru APMC", latest.bengaluruPrice),
            Pair("Mysuru APMC", latest.mysuruPrice),
            Pair("Chikkamagaluru APMC", latest.chikkamagaluruPrice)
        ).sortedByDescending { it.second }

        val bestMandi = prices.first().first
        val lowestMandi = prices.last().first
        val rawSpread = prices.first().second - prices.last().second
        val estimatedFreightPerQ = if (commodity.contains("Arecanut") || commodity.contains("Coffee")) 350.0 else 90.0
        val netArbitrage = Math.max(0.0, rawSpread - estimatedFreightPerQ)

        val recommendation = if (netArbitrage > 100.0) {
            "Strong Arbitrage Opportunity: Transporting $commodity to $bestMandi yields +₹${rawSpread.toInt()}/Q gross spread over $lowestMandi. After estimated freight & mandi cess (~₹${estimatedFreightPerQ.toInt()}/Q), projected net profit gain is ₹${netArbitrage.toInt()}/Q."
        } else {
            "Moderate Price Spread: Spread is ₹${rawSpread.toInt()}/Q across mandis. Local sale in your nearest mandi is cost-efficient after factoring vehicle transport costs."
        }

        return MarketAnalytics(
            commodity = commodity,
            trendHistory = trendList,
            mandiPrices = mandiPrices,
            priceSpread = rawSpread,
            bestMandi = bestMandi,
            arbitrageGain = netArbitrage,
            recommendation = recommendation
        )
    }

    fun getWeeklyMarketAnalysis(commodity: String): WeeklyMarketAnalysis {
        val base = baseCommodities[commodity] ?: Triple(2400.0, 2200.0, 2300.0)
        val avgBase = (base.first + base.second + base.third) / 3.0

        val calendar = Calendar.getInstance()
        val reportDateFmt = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)
        val reportDateStr = reportDateFmt.format(calendar.time)

        val dailyRecords = mutableListOf<DailyPriceRecord>()
        val dayFormatEn = SimpleDateFormat("EEEE", Locale.ENGLISH)
        val dayDateFmt = SimpleDateFormat("dd MMM", Locale.ENGLISH)

        // Start 6 days ago up to today (total 7 days)
        calendar.add(Calendar.DAY_OF_YEAR, -6)
        var prevDayPrice = avgBase * 0.96

        for (i in 0 until 7) {
            val dateStr = dayDateFmt.format(calendar.time)
            val dayOfWeekEn = dayFormatEn.format(calendar.time)
            val dayOfWeekKn = when (calendar.get(Calendar.DAY_OF_WEEK)) {
                Calendar.SUNDAY -> "ಭಾನುವಾರ"
                Calendar.MONDAY -> "ಸೋಮವಾರ"
                Calendar.TUESDAY -> "ಮಂಗಳವಾರ"
                Calendar.WEDNESDAY -> "ಬುಧವಾರ"
                Calendar.THURSDAY -> "ಗುರುವಾರ"
                Calendar.FRIDAY -> "ಶುಕ್ರವಾರ"
                Calendar.SATURDAY -> "ಶನಿವಾರ"
                else -> ""
            }
            val dayOfWeekHi = when (calendar.get(Calendar.DAY_OF_WEEK)) {
                Calendar.SUNDAY -> "रविवार"
                Calendar.MONDAY -> "सोमवार"
                Calendar.TUESDAY -> "मंगलवार"
                Calendar.WEDNESDAY -> "बुधवार"
                Calendar.THURSDAY -> "गुरुवार"
                Calendar.FRIDAY -> "शुक्रवार"
                Calendar.SATURDAY -> "शनिवार"
                else -> ""
            }

            val dayMultiplier = when (calendar.get(Calendar.DAY_OF_WEEK)) {
                Calendar.SATURDAY, Calendar.SUNDAY -> 1.04
                Calendar.MONDAY -> 1.01
                Calendar.TUESDAY -> 0.98
                Calendar.WEDNESDAY -> 0.99
                Calendar.THURSDAY -> 1.01
                Calendar.FRIDAY -> 1.02
                else -> 1.0
            }
            val cyclical = Math.sin((i + 1) * 0.5) * 0.04
            val modal = Math.round(avgBase * dayMultiplier * (1.0 + cyclical)).toDouble()
            val minP = Math.round(modal * 0.91).toDouble()
            val maxP = Math.round(modal * 1.07).toDouble()
            val diff = modal - prevDayPrice
            val diffPct = Math.round((diff / prevDayPrice) * 1000) / 10.0
            val volume = 280 + (i * 35) + (if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) 150 else 0)

            val sentiment = if (diffPct > 1.5) "Bullish" else if (diffPct < -1.5) "Bearish" else "Stable"

            dailyRecords.add(
                DailyPriceRecord(
                    dateString = dateStr,
                    dayOfWeekEn = dayOfWeekEn,
                    dayOfWeekKn = dayOfWeekKn,
                    dayOfWeekHi = dayOfWeekHi,
                    modalPrice = modal,
                    minPrice = minP,
                    maxPrice = maxP,
                    dailyChange = diff,
                    dailyChangePercent = diffPct,
                    arrivalVolumeQtl = volume,
                    marketSentiment = sentiment
                )
            )
            prevDayPrice = modal
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        val weeklyAvg = Math.round(dailyRecords.map { it.modalPrice }.average()).toDouble()
        val highRecord = dailyRecords.maxByOrNull { it.modalPrice } ?: dailyRecords.last()
        val lowRecord = dailyRecords.minByOrNull { it.modalPrice } ?: dailyRecords.first()
        val firstDay = dailyRecords.first().modalPrice
        val lastDay = dailyRecords.last().modalPrice
        val weekNetChange = lastDay - firstDay
        val weekNetPct = Math.round((weekNetChange / firstDay) * 1000) / 10.0

        val sellDecision: SellDecision
        val advisoryKn: String
        val advisoryHi: String
        val advisoryEn: String

        if (lastDay >= highRecord.modalPrice * 0.985 && weekNetPct >= 2.0) {
            sellDecision = SellDecision.SELL_NOW_PEAK
            advisoryKn = "ಪ್ರಸ್ತುತ ದರವು ಈ ವಾರದ ಗರಿಷ್ಠ ಮಟ್ಟದಲ್ಲಿದೆ (₹${highRecord.modalPrice.toInt()}/ಕ್ವಿಂ)! ಎಪಿಎಂಸಿ ಮಾರುಕಟ್ಟೆಗಳಲ್ಲಿ ಬೇಡಿಕೆ ಹೆಚ್ಚಾಗಿದ್ದು, ನಿಮ್ಮ ತರಕಾರಿ/ಬೆಳೆಯನ್ನು ತಕ್ಷಣ ಕೊಯ್ಲು ಮಾಡಿ ಮಾರಾಟ ಮಾಡಲು ಇದು ಅತ್ಯುತ್ತಮ ಸಮಯ."
            advisoryHi = "वर्तमान भाव इस सप्ताह के उच्चतम स्तर (₹${highRecord.modalPrice.toInt()}/क्विंटल) पर है! मंडियों में भारी मांग है, आज ही फसल बेचकर अधिकतम मुनाफा कमाएं।"
            advisoryEn = "Current price is at the weekly peak (₹${highRecord.modalPrice.toInt()}/Qtl)! Demand is surging across APMC mandis; best window to harvest and sell immediately."
        } else if (weekNetPct > 0.5 && lastDay >= dailyRecords[dailyRecords.size - 2].modalPrice) {
            sellDecision = SellDecision.HOLD_FOR_HIGHER
            advisoryKn = "ಕಳೆದ 3 ದಿನಗಳಿಂದ ಬೆಲೆ ನಿರಂತರವಾಗಿ ಏರಿಕೆಯಾಗುತ್ತಿದೆ (+₹${weekNetChange.toInt()}/ಕ್ವಿಂ). ಮಾರುಕಟ್ಟೆಗೆ ಆವಕ ಸೀಮಿತವಾಗಿದ್ದು, ಇನ್ನೂ 2-3 ದಿನ ತಡೆದು ಮಾರಾಟ ಮಾಡಿದರೆ ಹೆಚ್ಚಿನ ದರ (₹${(lastDay * 1.05).toInt()}/ಕ್ವಿಂ ವರೆಗೆ) ಸಿಗುವ ಸಾಧ್ಯತೆಯಿದೆ."
            advisoryHi = "पिछले 3 दिनों से भाव लगातार बढ़ रहे हैं (+₹${weekNetChange.toInt()}/क्विंटल)। 2-3 दिन रुककर बेचना अधिक लाभदायक रहेगा।"
            advisoryEn = "Prices are on a consistent upward trajectory (+₹${weekNetChange.toInt()}/Qtl). Holding for 2-3 days is recommended as arrivals remain tight."
        } else if (weekNetPct < -2.0) {
            sellDecision = SellDecision.SELL_IMMEDIATELY_DROPPING
            advisoryKn = "ಮಾರುಕಟ್ಟೆಯಲ್ಲಿ ಹೊಸ ಆವಕ ಹೆಚ್ಚಾಗಿದ್ದು ದರ ಇಳಿಕೆಯಾಗುತ್ತಿದೆ (-${Math.abs(weekNetPct)}%). ಇನ್ನಷ್ಟು ಬೆಲೆ ಕುಸಿಯುವ ಮುನ್ನ ನಿಮ್ಮಲ್ಲಿರುವ ಉತ್ಪನ್ನವನ್ನು ತಡಮಾಡದೆ ಇಂದೇ ಮಾರಾಟ ಮಾಡಿ ನಷ್ಟ ತಪ್ಪಿಸಿ."
            advisoryHi = "मंडियों में भारी आवक से भाव गिरने का रुख है (-${Math.abs(weekNetPct)}%)। नुकसान से बचने के लिए तुरंत माल निकालें।"
            advisoryEn = "Heavy supply influx is pushing prices down (-${Math.abs(weekNetPct)}%). Sell ready produce today to prevent further price erosion."
        } else {
            sellDecision = SellDecision.HARVEST_AND_SELL
            advisoryKn = "ಈ ವಾರ ಬೆಲೆಗಳು ಸ್ಥಿರವಾಗಿವೆ (ಸರಾಸರಿ ₹${weeklyAvg.toInt()}/ಕ್ವಿಂ). ಮಾರುಕಟ್ಟೆ ಸಮತೋಲನದಲ್ಲಿದ್ದು, ನಿಯಮಿತವಾಗಿ ಕೊಯ್ಲು ಮಾಡಿ ಸ್ಥಳೀಯ ಎಪಿಎಂಸಿಯಲ್ಲಿ ಸಾಮಾನ್ಯ ಮಾರಾಟ ಮುಂದುವರಿಸಿ."
            advisoryHi = "इस सप्ताह भाव सामान्य व स्थिर बने हुए हैं (औसत ₹${weeklyAvg.toInt()}/क्विंटल)। सामान्य गति से तुड़ाई व बिक्री जारी रखें।"
            advisoryEn = "Prices remain stable around the weekly average (₹${weeklyAvg.toInt()}/Qtl). Safe for regular phased harvesting and steady sales."
        }

        val projectedRange = "₹${Math.round(lastDay * 0.97).toInt()} - ₹${Math.round(lastDay * 1.06).toInt()} / Qtl"

        return WeeklyMarketAnalysis(
            commodity = commodity,
            reportDate = reportDateStr,
            formattedDateLongEn = "${dailyRecords.last().dayOfWeekEn}, $reportDateStr",
            formattedDateLongKn = "${dailyRecords.last().dayOfWeekKn}, $reportDateStr",
            formattedDateLongHi = "${dailyRecords.last().dayOfWeekHi}, $reportDateStr",
            dailyRecords = dailyRecords,
            weeklyAveragePrice = weeklyAvg,
            weeklyHighPrice = highRecord.modalPrice,
            weeklyHighDate = "${highRecord.dateString} (${highRecord.dayOfWeekKn})",
            weeklyLowPrice = lowRecord.modalPrice,
            weeklyLowDate = "${lowRecord.dateString} (${lowRecord.dayOfWeekKn})",
            weeklyPriceChange = weekNetChange,
            weeklyPriceChangePercent = weekNetPct,
            sellDecision = sellDecision,
            advisoryKn = advisoryKn,
            advisoryHi = advisoryHi,
            advisoryEn = advisoryEn,
            projectedPriceNextDays = projectedRange,
            marketVolumeSummaryKn = "ಈ ವಾರದ ಒಟ್ಟು ಆವಕ: ${dailyRecords.sumOf { it.arrivalVolumeQtl }} ಕ್ವಿಂಟಾಲ್ (ದೈನಂದಿನ ಸರಾಸರಿ ${Math.round(dailyRecords.map { it.arrivalVolumeQtl }.average())} ಕ್ವಿಂಟಾಲ್)",
            marketVolumeSummaryHi = "इस सप्ताह की कुल आवक: ${dailyRecords.sumOf { it.arrivalVolumeQtl }} क्विंटल (दैनिक औसत ${Math.round(dailyRecords.map { it.arrivalVolumeQtl }.average())} क्विंटल)",
            marketVolumeSummaryEn = "Weekly Total Arrivals: ${dailyRecords.sumOf { it.arrivalVolumeQtl }} Quintals (Daily avg ${Math.round(dailyRecords.map { it.arrivalVolumeQtl }.average())} Qtl)"
        )
    }

    fun getSupportedCommodities(): List<String> {
        return baseCommodities.keys.toList()
    }
}
