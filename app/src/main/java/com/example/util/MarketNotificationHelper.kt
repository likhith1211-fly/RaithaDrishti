package com.example.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.MainActivity
import com.example.data.local.PriceAlertEntity
import com.example.data.model.AppLanguage
import com.example.data.model.MarketAnalytics

object MarketNotificationHelper {

    const val CHANNEL_ID = "apmc_market_price_alerts"
    const val CHANNEL_NAME = "APMC Price Alerts / ಬೆಲೆ ಎಚ್ಚರಿಕೆಗಳು"

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                importance
            ).apply {
                description = "Alerts when APMC mandi prices cross farmer-set thresholds"
                enableVibration(true)
                enableLights(true)
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    data class AlertTriggerResult(
        val alertId: Long,
        val commodity: String,
        val mandiName: String,
        val currentPrice: Double,
        val thresholdPrice: Double,
        val isExceeded: Boolean, // true = price went above max, false = dropped below min
        val title: String,
        val message: String
    )

    fun evaluateAlerts(
        context: Context,
        alerts: List<PriceAlertEntity>,
        analytics: MarketAnalytics,
        language: AppLanguage
    ): List<AlertTriggerResult> {
        createNotificationChannel(context)
        val triggered = mutableListOf<AlertTriggerResult>()

        val matchingAlerts = alerts.filter { alert ->
            alert.isEnabled && (
                alert.commodity.equals(analytics.commodity, ignoreCase = true) ||
                alert.commodity.contains(analytics.commodity, ignoreCase = true) ||
                analytics.commodity.contains(alert.commodity, ignoreCase = true)
            )
        }

        for (alert in matchingAlerts) {
            // Check specific mandi or best mandi/overall average
            val relevantPrice = if (alert.mandiName != "All Mandis") {
                analytics.mandiPrices.find { it.mandiName.equals(alert.mandiName, ignoreCase = true) }?.modalPrice
                    ?: analytics.mandiPrices.firstOrNull()?.modalPrice ?: 0.0
            } else {
                analytics.mandiPrices.maxOfOrNull { it.modalPrice } ?: 0.0
            }

            if (relevantPrice <= 0.0) continue

            var trigger: AlertTriggerResult? = null

            if (alert.targetMaxPrice > 0 && relevantPrice >= alert.targetMaxPrice) {
                // High price threshold reached!
                val title = when (language) {
                    AppLanguage.KANNADA -> "ಗರಿಷ್ಠ ಬೆಲೆ ಎಚ್ಚರಿಕೆ: ${analytics.commodity}!"
                    AppLanguage.HINDI -> "अधिकतम भाव अलर्ट: ${analytics.commodity}!"
                    AppLanguage.ENGLISH -> "High Price Target Met: ${analytics.commodity}!"
                }
                val mandiDisplay = if (alert.mandiName != "All Mandis") alert.mandiName else analytics.bestMandi
                val msg = when (language) {
                    AppLanguage.KANNADA -> "${analytics.commodity} ದರ ₹${relevantPrice.toInt()}/ಕ್ವಿಂಟಾಲ್ ತಲುಪಿದೆ ($mandiDisplay). ನಿಮ್ಮ ಗುರಿ ₹${alert.targetMaxPrice.toInt()} ಮೀರಿದೆ. ಮಾರಾಟ ಮಾಡಲು ಸಕಾಲ!"
                    AppLanguage.HINDI -> "${analytics.commodity} का भाव ₹${relevantPrice.toInt()}/क्विंटल ($mandiDisplay) पहुंच गया है। आपका लक्ष्य ₹${alert.targetMaxPrice.toInt()} पार हो गया। बेचने का सही समय!"
                    AppLanguage.ENGLISH -> "${analytics.commodity} price hit ₹${relevantPrice.toInt()}/Qtl ($mandiDisplay). Exceeded your target ₹${alert.targetMaxPrice.toInt()}. Great time to sell!"
                }
                trigger = AlertTriggerResult(
                    alertId = alert.id,
                    commodity = analytics.commodity,
                    mandiName = mandiDisplay,
                    currentPrice = relevantPrice,
                    thresholdPrice = alert.targetMaxPrice,
                    isExceeded = true,
                    title = title,
                    message = msg
                )
            } else if (alert.targetMinPrice > 0 && relevantPrice <= alert.targetMinPrice) {
                // Low price drop threshold reached!
                val title = when (language) {
                    AppLanguage.KANNADA -> "ಬೆಲೆ ಕುಸಿತ ಎಚ್ಚರಿಕೆ: ${analytics.commodity}!"
                    AppLanguage.HINDI -> "भाव गिरावट अलर्ट: ${analytics.commodity}!"
                    AppLanguage.ENGLISH -> "Price Drop Alert: ${analytics.commodity}!"
                }
                val mandiDisplay = if (alert.mandiName != "All Mandis") alert.mandiName else analytics.bestMandi
                val msg = when (language) {
                    AppLanguage.KANNADA -> "${analytics.commodity} ದರ ₹${relevantPrice.toInt()}/ಕ್ವಿಂಟಾಲ್ ಗೆ ಕುಸಿದಿದೆ ($mandiDisplay). ನಿಮ್ಮ ಕನಿಷ್ಠ ಮಿತಿ ₹${alert.targetMinPrice.toInt()} ಕ್ಕಿಂತ ಕಡಿಮೆಯಾಗಿದೆ!"
                    AppLanguage.HINDI -> "${analytics.commodity} का भाव ₹${relevantPrice.toInt()}/क्विंटल ($mandiDisplay) पर गिर गया है। आपकी न्यूनतम सीमा ₹${alert.targetMinPrice.toInt()} से नीचे है!"
                    AppLanguage.ENGLISH -> "${analytics.commodity} price dropped to ₹${relevantPrice.toInt()}/Qtl ($mandiDisplay). Below your minimum ₹${alert.targetMinPrice.toInt()} threshold!"
                }
                trigger = AlertTriggerResult(
                    alertId = alert.id,
                    commodity = analytics.commodity,
                    mandiName = mandiDisplay,
                    currentPrice = relevantPrice,
                    thresholdPrice = alert.targetMinPrice,
                    isExceeded = false,
                    title = title,
                    message = msg
                )
            }

            if (trigger != null) {
                triggered.add(trigger)
                sendSystemNotification(context, trigger)
            }
        }

        return triggered
    }

    fun sendSystemNotification(context: Context, alertResult: AlertTriggerResult) {
        try {
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            val pendingIntent = PendingIntent.getActivity(
                context,
                alertResult.alertId.toInt(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val builder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(alertResult.title)
                .setContentText(alertResult.message)
                .setStyle(NotificationCompat.BigTextStyle().bigText(alertResult.message))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)

            val notificationManager = NotificationManagerCompat.from(context)
            notificationManager.notify((alertResult.alertId + 1000).toInt(), builder.build())
        } catch (e: SecurityException) {
            // POST_NOTIFICATIONS permission not yet granted by user; in-app banner will still alert farmer
        } catch (e: Exception) {
            // Safe fallback
        }
    }
}
