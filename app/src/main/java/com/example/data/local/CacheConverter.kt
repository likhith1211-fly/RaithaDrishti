package com.example.data.local

import com.example.data.model.*
import org.json.JSONArray
import org.json.JSONObject

object CacheConverter {

    fun marketAnalyticsToJson(analytics: MarketAnalytics): String {
        val obj = JSONObject()
        obj.put("commodity", analytics.commodity)
        obj.put("priceSpread", analytics.priceSpread)
        obj.put("bestMandi", analytics.bestMandi)
        obj.put("arbitrageGain", analytics.arbitrageGain)
        obj.put("recommendation", analytics.recommendation)

        val mandiArray = JSONArray()
        for (m in analytics.mandiPrices) {
            val mObj = JSONObject()
            mObj.put("mandiName", m.mandiName)
            mObj.put("modalPrice", m.modalPrice)
            mObj.put("minPrice", m.minPrice)
            mObj.put("maxPrice", m.maxPrice)
            mObj.put("dailyChangePercent", m.dailyChangePercent)
            mandiArray.put(mObj)
        }
        obj.put("mandiPrices", mandiArray)

        val trendArray = JSONArray()
        for (t in analytics.trendHistory) {
            val tObj = JSONObject()
            tObj.put("displayDate", t.displayDate)
            tObj.put("bengaluruPrice", t.bengaluruPrice)
            tObj.put("mysuruPrice", t.mysuruPrice)
            tObj.put("chikkamagaluruPrice", t.chikkamagaluruPrice)
            trendArray.put(tObj)
        }
        obj.put("trendHistory", trendArray)

        return obj.toString()
    }

    fun marketAnalyticsFromJson(jsonStr: String): MarketAnalytics? {
        return try {
            val obj = JSONObject(jsonStr)
            val commodity = obj.getString("commodity")
            val priceSpread = obj.optDouble("priceSpread", 0.0)
            val bestMandi = obj.optString("bestMandi", "")
            val arbitrageGain = obj.optDouble("arbitrageGain", 0.0)
            val recommendation = obj.optString("recommendation", "")

            val mandiList = mutableListOf<MandiPriceInfo>()
            val mandiArray = obj.optJSONArray("mandiPrices")
            if (mandiArray != null) {
                for (i in 0 until mandiArray.length()) {
                    val m = mandiArray.getJSONObject(i)
                    mandiList.add(
                        MandiPriceInfo(
                            mandiName = m.getString("mandiName"),
                            modalPrice = m.getDouble("modalPrice"),
                            minPrice = m.getDouble("minPrice"),
                            maxPrice = m.getDouble("maxPrice"),
                            dailyChangePercent = m.getDouble("dailyChangePercent")
                        )
                    )
                }
            }

            val trendList = mutableListOf<PriceTrendPoint>()
            val trendArray = obj.optJSONArray("trendHistory")
            if (trendArray != null) {
                for (i in 0 until trendArray.length()) {
                    val t = trendArray.getJSONObject(i)
                    trendList.add(
                        PriceTrendPoint(
                            displayDate = t.getString("displayDate"),
                            bengaluruPrice = t.getDouble("bengaluruPrice"),
                            mysuruPrice = t.getDouble("mysuruPrice"),
                            chikkamagaluruPrice = t.getDouble("chikkamagaluruPrice")
                        )
                    )
                }
            }

            MarketAnalytics(
                commodity = commodity,
                trendHistory = trendList,
                mandiPrices = mandiList,
                priceSpread = priceSpread,
                bestMandi = bestMandi,
                arbitrageGain = arbitrageGain,
                recommendation = recommendation
            )
        } catch (e: Exception) {
            null
        }
    }

    fun weeklyAnalysisToJson(weekly: WeeklyMarketAnalysis): String {
        val obj = JSONObject()
        obj.put("commodity", weekly.commodity)
        obj.put("reportDate", weekly.reportDate)
        obj.put("formattedDateLongEn", weekly.formattedDateLongEn)
        obj.put("formattedDateLongKn", weekly.formattedDateLongKn)
        obj.put("formattedDateLongHi", weekly.formattedDateLongHi)
        obj.put("weeklyAveragePrice", weekly.weeklyAveragePrice)
        obj.put("weeklyHighPrice", weekly.weeklyHighPrice)
        obj.put("weeklyHighDate", weekly.weeklyHighDate)
        obj.put("weeklyLowPrice", weekly.weeklyLowPrice)
        obj.put("weeklyLowDate", weekly.weeklyLowDate)
        obj.put("weeklyPriceChange", weekly.weeklyPriceChange)
        obj.put("weeklyPriceChangePercent", weekly.weeklyPriceChangePercent)
        obj.put("sellDecision", weekly.sellDecision.name)
        obj.put("advisoryKn", weekly.advisoryKn)
        obj.put("advisoryHi", weekly.advisoryHi)
        obj.put("advisoryEn", weekly.advisoryEn)
        obj.put("projectedPriceNextDays", weekly.projectedPriceNextDays)
        obj.put("marketVolumeSummaryKn", weekly.marketVolumeSummaryKn)
        obj.put("marketVolumeSummaryHi", weekly.marketVolumeSummaryHi)
        obj.put("marketVolumeSummaryEn", weekly.marketVolumeSummaryEn)

        val dailyArray = JSONArray()
        for (d in weekly.dailyRecords) {
            val dObj = JSONObject()
            dObj.put("dateString", d.dateString)
            dObj.put("dayOfWeekEn", d.dayOfWeekEn)
            dObj.put("dayOfWeekKn", d.dayOfWeekKn)
            dObj.put("dayOfWeekHi", d.dayOfWeekHi)
            dObj.put("modalPrice", d.modalPrice)
            dObj.put("minPrice", d.minPrice)
            dObj.put("maxPrice", d.maxPrice)
            dObj.put("dailyChange", d.dailyChange)
            dObj.put("dailyChangePercent", d.dailyChangePercent)
            dObj.put("arrivalVolumeQtl", d.arrivalVolumeQtl)
            dObj.put("marketSentiment", d.marketSentiment)
            dailyArray.put(dObj)
        }
        obj.put("dailyRecords", dailyArray)

        return obj.toString()
    }

    fun weeklyAnalysisFromJson(jsonStr: String): WeeklyMarketAnalysis? {
        return try {
            val obj = JSONObject(jsonStr)
            val dailyList = mutableListOf<DailyPriceRecord>()
            val dailyArray = obj.optJSONArray("dailyRecords")
            if (dailyArray != null) {
                for (i in 0 until dailyArray.length()) {
                    val d = dailyArray.getJSONObject(i)
                    dailyList.add(
                        DailyPriceRecord(
                            dateString = d.getString("dateString"),
                            dayOfWeekEn = d.getString("dayOfWeekEn"),
                            dayOfWeekKn = d.getString("dayOfWeekKn"),
                            dayOfWeekHi = d.getString("dayOfWeekHi"),
                            modalPrice = d.getDouble("modalPrice"),
                            minPrice = d.getDouble("minPrice"),
                            maxPrice = d.getDouble("maxPrice"),
                            dailyChange = d.getDouble("dailyChange"),
                            dailyChangePercent = d.getDouble("dailyChangePercent"),
                            arrivalVolumeQtl = d.getInt("arrivalVolumeQtl"),
                            marketSentiment = d.getString("marketSentiment")
                        )
                    )
                }
            }

            val sellDecision = try {
                SellDecision.valueOf(obj.optString("sellDecision", SellDecision.SELL_NOW_PEAK.name))
            } catch (e: Exception) {
                SellDecision.SELL_NOW_PEAK
            }

            WeeklyMarketAnalysis(
                commodity = obj.getString("commodity"),
                reportDate = obj.optString("reportDate", ""),
                formattedDateLongEn = obj.optString("formattedDateLongEn", ""),
                formattedDateLongKn = obj.optString("formattedDateLongKn", ""),
                formattedDateLongHi = obj.optString("formattedDateLongHi", ""),
                dailyRecords = dailyList,
                weeklyAveragePrice = obj.optDouble("weeklyAveragePrice", 0.0),
                weeklyHighPrice = obj.optDouble("weeklyHighPrice", 0.0),
                weeklyHighDate = obj.optString("weeklyHighDate", ""),
                weeklyLowPrice = obj.optDouble("weeklyLowPrice", 0.0),
                weeklyLowDate = obj.optString("weeklyLowDate", ""),
                weeklyPriceChange = obj.optDouble("weeklyPriceChange", 0.0),
                weeklyPriceChangePercent = obj.optDouble("weeklyPriceChangePercent", 0.0),
                sellDecision = sellDecision,
                advisoryKn = obj.optString("advisoryKn", ""),
                advisoryHi = obj.optString("advisoryHi", ""),
                advisoryEn = obj.optString("advisoryEn", ""),
                projectedPriceNextDays = obj.optString("projectedPriceNextDays", ""),
                marketVolumeSummaryKn = obj.optString("marketVolumeSummaryKn", ""),
                marketVolumeSummaryHi = obj.optString("marketVolumeSummaryHi", ""),
                marketVolumeSummaryEn = obj.optString("marketVolumeSummaryEn", "")
            )
        } catch (e: Exception) {
            null
        }
    }

    fun weatherDataToJson(weather: WeatherData): String {
        val obj = JSONObject()
        obj.put("location", weather.location)
        obj.put("latitude", weather.latitude)
        obj.put("longitude", weather.longitude)
        obj.put("temperature", weather.temperature)
        obj.put("humidity", weather.humidity)
        obj.put("precipitation", weather.precipitation)
        obj.put("windSpeed", weather.windSpeed)
        obj.put("weatherCode", weather.weatherCode)
        obj.put("agriAdvice", weather.agriAdvice)
        obj.put("isFungalRisk", weather.isFungalRisk)
        obj.put("isSprayingFavorable", weather.isSprayingFavorable)
        return obj.toString()
    }

    fun weatherDataFromJson(jsonStr: String): WeatherData? {
        return try {
            val obj = JSONObject(jsonStr)
            WeatherData(
                location = obj.getString("location"),
                latitude = obj.getDouble("latitude"),
                longitude = obj.getDouble("longitude"),
                temperature = obj.getDouble("temperature"),
                humidity = obj.getDouble("humidity"),
                precipitation = obj.getDouble("precipitation"),
                windSpeed = obj.getDouble("windSpeed"),
                weatherCode = obj.getInt("weatherCode"),
                agriAdvice = obj.optString("agriAdvice", ""),
                isFungalRisk = obj.optBoolean("isFungalRisk", false),
                isSprayingFavorable = obj.optBoolean("isSprayingFavorable", true)
            )
        } catch (e: Exception) {
            null
        }
    }

    fun weatherAdvisoryToJson(advisory: GeminiWeatherAdvisory): String {
        val obj = JSONObject()
        obj.put("summary", advisory.summary)
        obj.put("sporeRiskLevel", advisory.sporeRiskLevel)
        obj.put("sprayWindow", advisory.sprayWindow)
        obj.put("irrigationAdvice", advisory.irrigationAdvice)
        obj.put("generatedAt", advisory.generatedAt)
        return obj.toString()
    }

    fun weatherAdvisoryFromJson(jsonStr: String): GeminiWeatherAdvisory? {
        return try {
            val obj = JSONObject(jsonStr)
            GeminiWeatherAdvisory(
                summary = obj.getString("summary"),
                sporeRiskLevel = obj.optString("sporeRiskLevel", "Low"),
                sprayWindow = obj.optString("sprayWindow", ""),
                irrigationAdvice = obj.optString("irrigationAdvice", ""),
                generatedAt = obj.optLong("generatedAt", System.currentTimeMillis())
            )
        } catch (e: Exception) {
            null
        }
    }
}
