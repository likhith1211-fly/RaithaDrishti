package com.example.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppLanguage
import com.example.ui.components.MicroWeatherWidget
import com.example.ui.theme.ForestGreenPrimary
import com.example.ui.viewmodel.RaithaDrishtiViewModel
import com.google.android.gms.location.LocationServices

@SuppressLint("MissingPermission")
@Composable
fun WeatherAdvisoryScreen(
    viewModel: RaithaDrishtiViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val currentLang by viewModel.currentLanguage.collectAsState()
    val currentWeather by viewModel.currentWeather.collectAsState()
    val selectedDistrict by viewModel.selectedDistrict.collectAsState()
    val isLoading by viewModel.isWeatherLoading.collectAsState()
    val isExactLocationActive by viewModel.isExactLocationActive.collectAsState()
    val exactLocationLabel by viewModel.exactLocationLabel.collectAsState()
    val aiAdvisory by viewModel.aiWeatherAdvisory.collectAsState()
    val isGeneratingAdvisory by viewModel.isGeneratingAdvisory.collectAsState()

    // Location Permission Launcher
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

        if (granted) {
            try {
                val fusedClient = LocationServices.getFusedLocationProviderClient(context)
                fusedClient.lastLocation.addOnSuccessListener { loc: Location? ->
                    if (loc != null) {
                        viewModel.updateExactCoordinates(
                            lat = loc.latitude,
                            lon = loc.longitude,
                            customLabel = "ನನ್ನ ಹೊಲ / My Farm (${String.format("%.3f", loc.latitude)}, ${String.format("%.3f", loc.longitude)})"
                        )
                        Toast.makeText(context, "Exact Farm Location Connected!", Toast.LENGTH_SHORT).show()
                    } else {
                        // Fallback to LocationManager
                        val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                        val lastKnown = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                            ?: lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                        if (lastKnown != null) {
                            viewModel.updateExactCoordinates(
                                lat = lastKnown.latitude,
                                lon = lastKnown.longitude,
                                customLabel = "ನನ್ನ ಹೊಲ / My Farm (${String.format("%.3f", lastKnown.latitude)}, ${String.format("%.3f", lastKnown.longitude)})"
                            )
                        } else {
                            // Fallback to farmer's selected field district coordinates (e.g. Chikkamagaluru)
                            val currentDist = viewModel.selectedDistrict.value
                            viewModel.updateExactCoordinates(
                                currentDist.lat,
                                currentDist.lon,
                                "ನನ್ನ ಹೊಲ / ${currentDist.name}"
                            )
                        }
                    }
                }.addOnFailureListener {
                    val currentDist = viewModel.selectedDistrict.value
                    viewModel.updateExactCoordinates(
                        currentDist.lat,
                        currentDist.lon,
                        "ನನ್ನ ಹೊಲ / ${currentDist.name}"
                    )
                }
            } catch (e: Exception) {
                val currentDist = viewModel.selectedDistrict.value
                viewModel.updateExactCoordinates(
                    currentDist.lat,
                    currentDist.lon,
                    "ನನ್ನ ಹೊಲ / ${currentDist.name}"
                )
            }
        } else {
            Toast.makeText(context, "Location permission denied. Showing district weather.", Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Header Card - Royal Agricultural Emerald & Gold
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("weather_advisory_hero_card"),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
            shape = RoundedCornerShape(18.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, com.example.ui.theme.SovereignGold.copy(alpha = 0.5f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        androidx.compose.ui.graphics.Brush.linearGradient(
                            listOf(com.example.ui.theme.ForestGreenPrimary, Color(0xFF0F3026))
                        )
                    )
                    .padding(20.dp)
            ) {
                Column {
                    Surface(
                        color = com.example.ui.theme.SovereignGold.copy(alpha = 0.25f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = when (currentLang) {
                                AppLanguage.KANNADA -> "★ ಹವಾಮಾನ ಸಲಹೆ • ನಿಖರ ಉಪಗ್ರಹ ವರದಿ"
                                AppLanguage.HINDI -> "★ मौसम सलाह • सटीक उपग्रह रिपोर्ट"
                                AppLanguage.ENGLISH -> "★ Micro-Weather Hub • Open-Meteo"
                            },
                            color = com.example.ui.theme.AmberLight,
                            style = MaterialTheme.typography.labelLarge,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = when (currentLang) {
                            AppLanguage.KANNADA -> "ಕೃಷಿ ಹವಾಮಾನ ಕೇಂದ್ರ"
                            AppLanguage.HINDI -> "कृषि मौसम परामर्श केंद्र"
                            AppLanguage.ENGLISH -> "Agricultural Weather Advisory"
                        },
                        color = Color.White,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = when (currentLang) {
                            AppLanguage.KANNADA -> "ಜಿಪಿಎಸ್ ಆಧಾರಿತ ನಿಖರ ಸ್ಥಳದ ಮಳೆ, ತೇವಾಂಶ, ಗಾಳಿಯ ವೇಗ ಮತ್ತು ಶಿಲೀಂಧ್ರ ರೋಗ ಹರಡುವ ಅಪಾಯದ ಮುನ್ಸೂಚನೆ."
                            AppLanguage.HINDI -> "जीपीएस द्वारा आपके सटीक खेत का मौसम, आर्द्रता, फंगल जोखिम व दवा छिड़काव का अनुकूल समय।"
                            AppLanguage.ENGLISH -> "Real-time metrics correlated directly with fungal spore germination risks, pesticide drift windows, and irrigation scheduling."
                        },
                        color = Color(0xFFF1F5F2),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 14.5.sp,
                            lineHeight = 22.sp
                        )
                    )
                }
            }
        }

        // Exact GPS Location Action Button & Indicator
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isExactLocationActive) Color(0xFFF4F9F5) else MaterialTheme.colorScheme.surface
            ),
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                if (isExactLocationActive) com.example.ui.theme.SovereignGold else com.example.ui.theme.LightBorder
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(
                                (if (isExactLocationActive) com.example.ui.theme.ForestGreenPrimary else MaterialTheme.colorScheme.primary).copy(alpha = 0.12f),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isExactLocationActive) Icons.Default.LocationOn else Icons.Default.MyLocation,
                            contentDescription = null,
                            tint = if (isExactLocationActive) com.example.ui.theme.ForestGreenPrimary else MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = if (isExactLocationActive) {
                                when (currentLang) {
                                    AppLanguage.KANNADA -> "ನಿಖರ ಜಿಪಿಎಸ್ ಸ್ಥಳ ಸಕ್ರಿಯವಾಗಿದೆ"
                                    AppLanguage.HINDI -> "सटीक जीपीएस सक्रिय है"
                                    AppLanguage.ENGLISH -> "Exact GPS Location Active"
                                }
                            } else {
                                when (currentLang) {
                                    AppLanguage.KANNADA -> "ನಿಮ್ಮ ನಿಖರ ಹೊಲದ ಹವಾಮಾನ ಪಡೆಯಿರಿ"
                                    AppLanguage.HINDI -> "अपने सटीक खेत का मौसम देखें"
                                    AppLanguage.ENGLISH -> "Use Exact GPS Farm Location"
                                }
                            },
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = if (isExactLocationActive) com.example.ui.theme.ForestGreenPrimary else MaterialTheme.colorScheme.onSurface
                        )
                        if (isExactLocationActive && exactLocationLabel != null) {
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = exactLocationLabel!!,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = com.example.ui.theme.ForestGreenPrimary
                            )
                        }
                    }
                }

                Button(
                    onClick = {
                        locationPermissionLauncher.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                        )
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isExactLocationActive) com.example.ui.theme.ForestGreenPrimary else com.example.ui.theme.ForestGreenPrimary
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.height(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MyLocation,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = when (currentLang) {
                            AppLanguage.KANNADA -> "ಜಿಪಿಎಸ್ ಪಡೆಯಿರಿ"
                            AppLanguage.HINDI -> "जीपीएस लें"
                            AppLanguage.ENGLISH -> "Get GPS"
                        },
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Gemini AI Weather Advisory Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = androidx.compose.foundation.BorderStroke(1.dp, com.example.ui.theme.SovereignGold.copy(alpha = 0.5f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(com.example.ui.theme.SovereignGold.copy(alpha = 0.15f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = com.example.ui.theme.AmberDark,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = when (currentLang) {
                                AppLanguage.KANNADA -> "ಜೆಮಿನಿ ಕೃಷಿ-ಹವಾಮಾನ ತಜ್ಞರ ಸಲಹೆ"
                                AppLanguage.HINDI -> "जेमिनी एआई मौसम परामर्श"
                                AppLanguage.ENGLISH -> "Gemini Agronomic Advisory"
                            },
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.5.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    if (isGeneratingAdvisory) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(22.dp),
                            strokeWidth = 2.5.dp,
                            color = com.example.ui.theme.ForestGreenPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                aiAdvisory?.let { adv ->
                    Text(
                        text = adv.summary,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 15.sp,
                            lineHeight = 23.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            color = if (adv.sporeRiskLevel.contains("HIGH")) Color(0xFFFFEBEE) else Color(0xFFE8F5E9),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (adv.sporeRiskLevel.contains("HIGH")) Color(0xFFFFCDD2) else Color(0xFFA5D6A7)
                            )
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = "ಶಿಲೀಂಧ್ರ ಅಪಾಯ / Spores",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.outline
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = adv.sporeRiskLevel,
                                    fontSize = 13.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (adv.sporeRiskLevel.contains("HIGH")) Color(0xFFC62828) else Color(0xFF2E7D32)
                                )
                            }
                        }

                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFE3F2FD),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBBDEFB))
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = "ಸಿಂಪಡಣಾ ಸಮಯ / Spray",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.outline
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = adv.sprayWindow,
                                    fontSize = 13.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1565C0)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Live Micro-Weather Widget with District Chips
        MicroWeatherWidget(
            weatherData = currentWeather,
            selectedDistrict = selectedDistrict,
            onSelectDistrict = { viewModel.setDistrict(it) },
            isLoading = isLoading,
            language = currentLang
        )

        // Fungal Spore Propagation Risk Index
        currentWeather?.let { weather ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(18.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, com.example.ui.theme.LightBorder),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = when (currentLang) {
                            AppLanguage.KANNADA -> "ಶಿಲೀಂಧ್ರ ಮತ್ತು ರೋಗಾಣು ಅಪಾಯ ವಿಶ್ಲೇಷಣೆ"
                            AppLanguage.HINDI -> "फंगल रोग व कीट जोखिम विश्लेषण"
                            AppLanguage.ENGLISH -> "Pathogen & Fungal Risk Analysis"
                        },
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.5.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    val humidity = weather.humidity
                    val riskLevel = when {
                        humidity > 80 -> when (currentLang) {
                            AppLanguage.KANNADA -> "ತೀವ್ರ ಅಪಾಯ (ಶಿಲೀಂಧ್ರ ಬೀಜಾಣು ಬಿಡುಗಡೆ ಸಾಧ್ಯತೆ)"
                            AppLanguage.HINDI -> "गंभीर जोखिम (फंगल बीजाणु फैलाव)"
                            AppLanguage.ENGLISH -> "Critical Risk (Severe Spore Release)"
                        }
                        humidity > 70 -> when (currentLang) {
                            AppLanguage.KANNADA -> "ಹೆಚ್ಚಿನ ಅಪಾಯ (ಅಂಗಮಾರಿ, ಬೂದಿ ರೋಗಕ್ಕೆ ಅನುಕೂಲ)"
                            AppLanguage.HINDI -> "उच्च जोखिम (झुलसा व पाउडरी मिल्ड्यू अनुकूल)"
                            AppLanguage.ENGLISH -> "High Risk (Favorable for Blight & Mildew)"
                        }
                        humidity > 60 -> when (currentLang) {
                            AppLanguage.KANNADA -> "ಮಧ್ಯಮ ಅಪಾಯ"
                            AppLanguage.HINDI -> "मध्यम जोखिम"
                            AppLanguage.ENGLISH -> "Moderate Risk"
                        }
                        else -> when (currentLang) {
                            AppLanguage.KANNADA -> "ಕಡಿಮೆ ಅಪಾಯ (ಒಣ ವಾತಾವರಣ)"
                            AppLanguage.HINDI -> "कम जोखिम (शुष्क मौसम)"
                            AppLanguage.ENGLISH -> "Low Risk (Dry Canopy)"
                        }
                    }
                    val riskColor = when {
                        humidity > 80 -> Color(0xFFB71C1C)
                        humidity > 70 -> Color(0xFFE65100)
                        humidity > 60 -> Color(0xFFF57F17)
                        else -> Color(0xFF2E7D32)
                    }

                    Surface(
                        color = riskColor.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, riskColor.copy(alpha = 0.3f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .background(riskColor, CircleShape)
                            )
                            Column {
                                Text(
                                    text = riskLevel,
                                    fontWeight = FontWeight.Bold,
                                    color = riskColor,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontSize = 16.sp
                                    )
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = when (currentLang) {
                                        AppLanguage.KANNADA -> "ಶೇಕಡಾ ${humidity.toInt()}% ತೇವಾಂಶದಲ್ಲಿ ಎಲೆಗಳಲ್ಲಿ ನೀರು ನಿಂತು ಬೂದಿ ರೋಗ ಹಾಗೂ ಮುಂಗಾರು ಕಾಯಿ ಕೊಳೆ ರೋಗದ ಶಿಲೀಂಧ್ರಗಳು 6-12 ಗಂಟೆಗಳಲ್ಲಿ ಹರಡುತ್ತವೆ."
                                        AppLanguage.HINDI -> "${humidity.toInt()}% आर्द्रता में पत्तियों पर नमी रहने से फफूंद रोग व सड़न 6-12 घंटों में तेजी से फैलती है।"
                                        AppLanguage.ENGLISH -> "At ${humidity.toInt()}% humidity, fungal sporulation accelerates rapidly within 6-12 hours of leaf wetness."
                                    },
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontSize = 14.sp,
                                        lineHeight = 20.sp
                                    ),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Spraying Window Recommendation
                    Text(
                        text = when (currentLang) {
                            AppLanguage.KANNADA -> "ಕೀಟನಾಶಕ / ಪೋಷಕಾಂಶ ಸಿಂಪಡಣೆಯ ಸೂಕ್ತ ಸಮಯ"
                            AppLanguage.HINDI -> "दवा व कीटनाशक छिड़काव की अनुकूल खिड़की"
                            AppLanguage.ENGLISH -> "Field Spraying Feasibility Window"
                        },
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    val isSprayingAllowed = weather.isSprayingFavorable
                    Surface(
                        color = if (isSprayingAllowed) Color(0xFFE8F5E9) else Color(0xFFFFEBEE),
                        shape = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSprayingAllowed) Color(0xFFA5D6A7) else Color(0xFFFFCDD2)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                imageVector = if (isSprayingAllowed) Icons.Default.CheckCircle else Icons.Default.Warning,
                                contentDescription = "Spraying Status",
                                tint = if (isSprayingAllowed) Color(0xFF2E7D32) else Color(0xFFC62828),
                                modifier = Modifier.size(24.dp)
                            )
                            Column {
                                Text(
                                    text = if (isSprayingAllowed) {
                                        when (currentLang) {
                                            AppLanguage.KANNADA -> "ಸಿಂಪಡಣೆಗೆ ಅತ್ಯಂತ ಅನುಕೂಲಕರ ಸಮಯ"
                                            AppLanguage.HINDI -> "छिड़काव के लिए बहुत अनुकूल समय"
                                            AppLanguage.ENGLISH -> "Favorable Spraying Conditions"
                                        }
                                    } else {
                                        when (currentLang) {
                                            AppLanguage.KANNADA -> "ಸಿಂಪಡಿಸಬೇಡಿ / ಹವಾಮಾನ ಸರಿಯಿಲ್ಲ"
                                            AppLanguage.HINDI -> "छिड़काव स्थगित करें / मौसम प्रतिकूल"
                                            AppLanguage.ENGLISH -> "Unfavorable / Defer Spraying"
                                        }
                                    },
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSprayingAllowed) Color(0xFF2E7D32) else Color(0xFFC62828),
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontSize = 15.5.sp
                                    )
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (isSprayingAllowed) {
                                        when (currentLang) {
                                            AppLanguage.KANNADA -> "ಗಾಳಿಯ ವೇಗ ${weather.windSpeed.toInt()} ಕಿಮೀ/ಗಂಟೆ (< 15 ಕಿಮೀ) ಮತ್ತು ಮಳೆಯಿಲ್ಲ. ಬೆಳಗಿನ ಜಾವ ಸಿಂಪಡಣೆ ಮಾಡಿ."
                                            AppLanguage.HINDI -> "हवा गति ${weather.windSpeed.toInt()} किमी/घंटा (< 15 किमी) और बारिश नहीं है। सुबह छिड़काव करें।"
                                            AppLanguage.ENGLISH -> "Wind speed is ${weather.windSpeed.toInt()} km/h (< 15 km/h) and no rain detected. Spray in early morning."
                                        }
                                    } else {
                                        when (currentLang) {
                                            AppLanguage.KANNADA -> "ಗಾಳಿ ಅಥವಾ ಮಳೆಯ ಅಪಾಯವಿದೆ. ಔಷಧಿ ಪೋಲಾಗುವುದನ್ನು ತಡೆಯಲು ನಿಲ್ಲಿಸಿ."
                                            AppLanguage.HINDI -> "हवा या बारिश का खतरा है। दवा बहने से बचाने के लिए रुकें।"
                                            AppLanguage.ENGLISH -> "Pesticide drift or rain wash-off risk is active. Wait for dry, calm conditions."
                                        }
                                    },
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

        // Refresh Action Button
        Button(
            onClick = {
                if (isExactLocationActive && currentWeather != null) {
                    viewModel.updateExactCoordinates(currentWeather!!.latitude, currentWeather!!.longitude, exactLocationLabel)
                } else {
                    viewModel.fetchWeatherForDistrict(selectedDistrict)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("refresh_weather_button"),
            colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary),
            shape = RoundedCornerShape(14.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = "Refresh",
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = when (currentLang) {
                    AppLanguage.KANNADA -> "ಹವಾಮಾನ ಮಾಹಿತಿ ನವೀಕರಿಸಿ"
                    AppLanguage.HINDI -> "मौसम अपडेट करें"
                    AppLanguage.ENGLISH -> "Refresh Live Micro-Weather"
                },
                fontSize = 15.5.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
