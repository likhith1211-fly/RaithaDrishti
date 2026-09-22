package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppLanguage
import com.example.data.model.KARNATAKA_DISTRICTS
import com.example.data.model.KarnatakaDistrict
import com.example.data.model.WeatherData
import com.example.ui.theme.ForestGreenPrimary

@Composable
fun MicroWeatherWidget(
    weatherData: WeatherData?,
    selectedDistrict: KarnatakaDistrict,
    onSelectDistrict: (KarnatakaDistrict) -> Unit,
    isLoading: Boolean,
    language: AppLanguage = AppLanguage.KANNADA,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("micro_weather_widget"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, com.example.ui.theme.LightBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header with District Title
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = when (language) {
                                AppLanguage.KANNADA -> "ಲೈವ್ ಕೃಷಿ ಹವಾಮಾನ"
                                AppLanguage.HINDI -> "लाइव कृषि मौसम"
                                AppLanguage.ENGLISH -> "Live Micro-Weather"
                            },
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 19.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Surface(
                            color = ForestGreenPrimary.copy(alpha = 0.12f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "Open-Meteo",
                                color = ForestGreenPrimary,
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = when (language) {
                            AppLanguage.KANNADA -> "${selectedDistrict.kannadaName} (${selectedDistrict.name})"
                            AppLanguage.HINDI -> "${selectedDistrict.hindiName.ifBlank { selectedDistrict.name }} (${selectedDistrict.name})"
                            AppLanguage.ENGLISH -> "${selectedDistrict.name} (${selectedDistrict.kannadaName})"
                        },
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 14.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.5.dp,
                        color = ForestGreenPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // District Filter Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                KARNATAKA_DISTRICTS.forEach { district ->
                    val isSelected = district.name == selectedDistrict.name
                    val districtLabel = when (language) {
                        AppLanguage.KANNADA -> district.kannadaName
                        AppLanguage.HINDI -> district.hindiName.ifBlank { district.name }
                        AppLanguage.ENGLISH -> district.name
                    }
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = if (isSelected) ForestGreenPrimary else MaterialTheme.colorScheme.surfaceVariant,
                        border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, com.example.ui.theme.LightBorder),
                        modifier = Modifier
                            .clickable { onSelectDistrict(district) }
                            .testTag("district_chip_${district.name.lowercase().replace(" ", "_")}")
                    ) {
                        Text(
                            text = districtLabel,
                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.labelLarge,
                            fontSize = 14.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (weatherData != null) {
                // Weather Metrics Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    WeatherMetricItem(
                        icon = Icons.Default.Thermostat,
                        iconColor = Color(0xFFE65100),
                        label = when (language) {
                            AppLanguage.KANNADA -> "ತಾಪಮಾನ"
                            AppLanguage.HINDI -> "तापमान"
                            AppLanguage.ENGLISH -> "Temp"
                        },
                        value = "${weatherData.temperature.toInt()}°C"
                    )
                    WeatherMetricItem(
                        icon = Icons.Default.WaterDrop,
                        iconColor = Color(0xFF0288D1),
                        label = when (language) {
                            AppLanguage.KANNADA -> "ತೇವಾಂಶ"
                            AppLanguage.HINDI -> "आर्द्रता"
                            AppLanguage.ENGLISH -> "Humidity"
                        },
                        value = "${weatherData.humidity.toInt()}%",
                        highlight = weatherData.humidity > 75.0
                    )
                    WeatherMetricItem(
                        icon = Icons.Default.WbSunny,
                        iconColor = Color(0xFF00796B),
                        label = when (language) {
                            AppLanguage.KANNADA -> "ಮಳೆ"
                            AppLanguage.HINDI -> "वर्षा"
                            AppLanguage.ENGLISH -> "Rain"
                        },
                        value = "${weatherData.precipitation} mm"
                    )
                    WeatherMetricItem(
                        icon = Icons.Default.Air,
                        iconColor = Color(0xFF5D4037),
                        label = when (language) {
                            AppLanguage.KANNADA -> "ಗಾಳಿ ವೇಗ"
                            AppLanguage.HINDI -> "हवा गति"
                            AppLanguage.ENGLISH -> "Wind"
                        },
                        value = "${weatherData.windSpeed.toInt()} km/h"
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Fungal / Humidity Risk Alert Banner
                val alertBg = if (weatherData.isFungalRisk) Color(0xFFFFF3E0) else Color(0xFFE8F5E9)
                val alertBorder = if (weatherData.isFungalRisk) Color(0xFFFFB74D) else Color(0xFFA5D6A7)
                val alertText = if (weatherData.isFungalRisk) Color(0xFFE65100) else Color(0xFF1B5E20)

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = alertBg,
                    border = androidx.compose.foundation.BorderStroke(1.dp, alertBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Alert",
                            tint = alertText,
                            modifier = Modifier.size(22.dp).padding(top = 2.dp)
                        )
                        Column {
                            Text(
                                text = if (weatherData.isFungalRisk) {
                                    when (language) {
                                        AppLanguage.KANNADA -> "ಶಿಲೀಂಧ್ರ ರೋಗಾಣು ಎಚ್ಚರಿಕೆ (ಹೆಚ್ಚಿನ ತೇವಾಂಶ)"
                                        AppLanguage.HINDI -> "फंगल रोग चेतावनी (अधिक आर्द्रता)"
                                        AppLanguage.ENGLISH -> "FUNGAL PATHOGEN ALERT"
                                    }
                                } else {
                                    when (language) {
                                        AppLanguage.KANNADA -> "ಹವಾಮಾನ ಸ್ಥಿತಿ (ಸಾಮಾನ್ಯ)"
                                        AppLanguage.HINDI -> "मौसम स्थिति (सामान्य)"
                                        AppLanguage.ENGLISH -> "AGRI-WEATHER STATUS"
                                    }
                                },
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.5.sp
                                ),
                                color = alertText
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = weatherData.agriAdvice,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontSize = 14.sp,
                                    lineHeight = 20.sp
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun WeatherMetricItem(
    icon: ImageVector,
    iconColor: Color,
    label: String,
    value: String,
    highlight: Boolean = false
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .background(iconColor.copy(alpha = 0.12f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = iconColor,
                modifier = Modifier.size(22.dp)
            )
        }
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp
            ),
            color = if (highlight) Color(0xFFD32F2F) else MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            fontSize = 12.5.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
