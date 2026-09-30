package com.example.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EditLocation
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.PinDrop
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.CachedWeatherReportEntity
import com.example.data.model.AppLanguage
import com.example.data.model.KARNATAKA_DISTRICTS
import com.example.data.model.KarnatakaDistrict
import com.example.ui.components.MicroWeatherWidget
import com.example.ui.components.RaithaBlueButton
import com.example.ui.components.RaithaCardActionButton
import com.example.ui.components.RaithaGoldButton
import com.example.ui.components.RaithaHighlightedButton
import com.example.ui.components.RaithaOutlinedHighlightedButton
import com.example.ui.theme.AmberLight
import com.example.ui.theme.ForestGreenDark
import com.example.ui.theme.ForestGreenLight
import com.example.ui.theme.ForestGreenPrimary
import com.example.ui.theme.SovereignGold
import com.example.ui.viewmodel.RaithaDrishtiViewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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

    val cachedWeatherReports by viewModel.allCachedWeatherReports.collectAsState()

    var showGoogleMapsPinDialog by remember { mutableStateOf(false) }

    // Active coordinates to use
    val activeLat = currentWeather?.latitude ?: selectedDistrict.lat
    val activeLon = currentWeather?.longitude ?: selectedDistrict.lon
    val activeLocName = currentWeather?.location ?: selectedDistrict.name

    // Location Permission Launcher with high-accuracy live GPS query & reverse geocoding
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

        if (granted) {
            try {
                val fusedClient = LocationServices.getFusedLocationProviderClient(context)
                fusedClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                    .addOnSuccessListener { loc: Location? ->
                        if (loc != null) {
                            viewModel.updateLocationFromGps(context, loc.latitude, loc.longitude)
                            Toast.makeText(context, "ನಿಮ್ಮ ನೈಜ ಜಿಪಿಎಸ್ ಸ್ಥಳ ಸಂಪರ್ಕಗೊಂಡಿದೆ / Live GPS Connected!", Toast.LENGTH_SHORT).show()
                        } else {
                            fusedClient.lastLocation.addOnSuccessListener { lastLoc ->
                                if (lastLoc != null) {
                                    viewModel.updateLocationFromGps(context, lastLoc.latitude, lastLoc.longitude)
                                    Toast.makeText(context, "GPS Location Updated", Toast.LENGTH_SHORT).show()
                                } else {
                                    val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                                    val lastKnown = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                                        ?: lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                                    if (lastKnown != null) {
                                        viewModel.updateLocationFromGps(context, lastKnown.latitude, lastKnown.longitude)
                                    } else {
                                        Toast.makeText(context, "GPS ಸಿಗ್ನಲ್ ಹುಡುಕಲಾಗುತ್ತಿದೆ. ದಯವಿಟ್ಟು ಜಿಲ್ಲೆಯನ್ನು ಆರಿಸಿ.", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        }
                    }
                    .addOnFailureListener {
                        Toast.makeText(context, "GPS Error. Please select your district.", Toast.LENGTH_SHORT).show()
                    }
            } catch (e: Exception) {
                Toast.makeText(context, "Location detection error. Please select your district.", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "Location permission denied. Showing selected district weather.", Toast.LENGTH_SHORT).show()
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
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
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
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (isExactLocationActive && exactLocationLabel != null) {
                                    exactLocationLabel!!
                                } else {
                                    "${selectedDistrict.name} (${selectedDistrict.kannadaName} • Lat: ${String.format(Locale.US, "%.3f", activeLat)}, Lon: ${String.format(Locale.US, "%.3f", activeLon)})"
                                },
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = ForestGreenPrimary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    RaithaGoldButton(
                        text = when (currentLang) {
                            AppLanguage.KANNADA -> "ಜಿಪಿಎಸ್"
                            AppLanguage.HINDI -> "जीपीएस"
                            AppLanguage.ENGLISH -> "Live GPS"
                        },
                        icon = Icons.Default.MyLocation,
                        height = 44.dp,
                        fontSize = 13.5.sp,
                        onClick = {
                            locationPermissionLauncher.launch(
                                arrayOf(
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION
                                )
                            )
                        },
                        modifier = Modifier.testTag("get_gps_button")
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Highlighted Action Row: Open in Maps & Change Location
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // 1. Open Exact Location in Google Maps (Vibrant Blue Highlighted Button)
                    RaithaBlueButton(
                        text = when (currentLang) {
                            AppLanguage.KANNADA -> "ಗೂಗಲ್ ಮ್ಯಾಪ್ಸ್"
                            AppLanguage.HINDI -> "Google Maps"
                            AppLanguage.ENGLISH -> "Google Maps"
                        },
                        icon = Icons.Default.Map,
                        height = 46.dp,
                        fontSize = 12.5.sp,
                        onClick = {
                            val encodedName = Uri.encode(activeLocName)
                            val geoUri = Uri.parse("geo:0,0?q=$activeLat,$activeLon($encodedName)")
                            val mapIntent = Intent(Intent.ACTION_VIEW, geoUri).apply {
                                setPackage("com.google.android.apps.maps")
                            }
                            try {
                                context.startActivity(mapIntent)
                            } catch (e: Exception) {
                                val webIntent = Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse("https://www.google.com/maps/search/?api=1&query=$activeLat,$activeLon")
                                )
                                context.startActivity(webIntent)
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("open_in_google_maps_button")
                    )

                    // 2. Select Location Dialog (Highlighted Outlined Button)
                    RaithaOutlinedHighlightedButton(
                        text = when (currentLang) {
                            AppLanguage.KANNADA -> "ಸ್ಥಳ ಬದಲಿಸಿ"
                            AppLanguage.HINDI -> "स्थान बदलें"
                            AppLanguage.ENGLISH -> "Change Location"
                        },
                        icon = Icons.Default.PinDrop,
                        height = 46.dp,
                        onClick = { showGoogleMapsPinDialog = true },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("pick_coordinates_button")
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

        // Section: Offline Cached Weather Reports (Local Room Database)
        if (cachedWeatherReports.isNotEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("cached_weather_reports_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, ForestGreenLight.copy(alpha = 0.4f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Cloud,
                                contentDescription = null,
                                tint = ForestGreenPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = when (currentLang) {
                                    AppLanguage.KANNADA -> "ಸಂಗ್ರಹಿಸಲಾದ ಹವಾಮಾನ ವರದಿಗಳು (Offline Room)"
                                    AppLanguage.HINDI -> "ऑफलाइन मौसम रिपोर्ट्स (Room DB)"
                                    AppLanguage.ENGLISH -> "Offline Cached Reports (Room DB)"
                                },
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Surface(
                            color = ForestGreenPrimary.copy(alpha = 0.12f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "${cachedWeatherReports.size} ವರದಿಗಳು",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = ForestGreenPrimary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = when (currentLang) {
                            AppLanguage.KANNADA -> "ಇಂಟರ್ನೆಟ್ ಇಲ್ಲದಿದ್ದಾಗಲೂ ಈ ಹಿಂದೆ ಸಂಗ್ರಹಿಸಲಾದ ಹವಾಮಾನ ಡೇಟಾ ಲಭ್ಯವಿದೆ:"
                            AppLanguage.HINDI -> "इंटरनेट न होने पर भी पुराना सहेजा गया मौसम डेटा उपलब्ध रहता है:"
                            AppLanguage.ENGLISH -> "Access previously cached telemetry and spray windows without internet:"
                        },
                        fontSize = 12.5.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        cachedWeatherReports.take(5).forEach { report ->
                            val dateFormat = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
                            val dateStr = dateFormat.format(Date(report.cachedAt))

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        viewModel.updateExactCoordinates(
                                            lat = report.latitude,
                                            lon = report.longitude,
                                            customLabel = report.locationName
                                        )
                                        Toast.makeText(context, "Loaded cached weather for ${report.locationName}", Toast.LENGTH_SHORT).show()
                                    }
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = report.locationName,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "${report.temperature}°C",
                                                fontWeight = FontWeight.ExtraBold,
                                                fontSize = 13.sp,
                                                color = ForestGreenPrimary
                                            )
                                            Text(
                                                text = "RH: ${report.humidity.toInt()}%",
                                                fontSize = 12.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                            Text(
                                                text = "Rain: ${report.precipitation}mm",
                                                fontSize = 12.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                        Text(
                                            text = "Saved: $dateStr • ${report.sprayWindowStatus}",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.outline
                                        )
                                    }

                                    IconButton(
                                        onClick = { viewModel.deleteCachedWeatherReport(report.id) },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Delete Cached Report",
                                            tint = Color(0xFFEF4444),
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Prominent Highlighted Refresh Action Button
        RaithaHighlightedButton(
            text = when (currentLang) {
                AppLanguage.KANNADA -> "ಹವಾಮಾನ ಮಾಹಿತಿ ನವೀಕರಿಸಿ (Refresh)"
                AppLanguage.HINDI -> "मौसम अपडेट करें (Refresh)"
                AppLanguage.ENGLISH -> "Refresh Live Micro-Weather"
            },
            icon = Icons.Default.Refresh,
            height = 52.dp,
            fontSize = 15.5.sp,
            onClick = {
                if (isExactLocationActive && currentWeather != null) {
                    viewModel.updateExactCoordinates(currentWeather!!.latitude, currentWeather!!.longitude, exactLocationLabel)
                } else {
                    viewModel.fetchWeatherForDistrict(selectedDistrict)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("refresh_weather_button")
        )

        Spacer(modifier = Modifier.height(24.dp))
    }

    if (showGoogleMapsPinDialog) {
        DynamicLocationSelectorDialog(
            currentLat = activeLat,
            currentLon = activeLon,
            currentLang = currentLang,
            onDismiss = { showGoogleMapsPinDialog = false },
            onApplyDistrict = { district ->
                viewModel.setDistrict(district)
                showGoogleMapsPinDialog = false
                Toast.makeText(context, "Selected: ${district.name}", Toast.LENGTH_SHORT).show()
            },
            onApplyCoordinates = { lat, lon, label ->
                viewModel.updateExactCoordinates(lat, lon, label)
                showGoogleMapsPinDialog = false
                Toast.makeText(context, "Location updated: $label", Toast.LENGTH_SHORT).show()
            },
            onUseGps = {
                locationPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
                showGoogleMapsPinDialog = false
            },
            onSearchPlace = { query ->
                viewModel.searchAndSetCustomLocation(context, query) { success, msg ->
                    if (success) {
                        showGoogleMapsPinDialog = false
                        Toast.makeText(context, "Found location: $msg", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        )
    }
}

/**
 * Dynamic, Non-plotted Location Selector Dialog.
 * Allows searching ANY town/taluk in Karnataka, 1-tap live GPS auto-detect,
 * quick district selection across all 31 Karnataka Agro-Climatic Districts,
 * or direct coordinates input. Eliminates any pre-fixed or plotted location locks.
 */
@Composable
fun DynamicLocationSelectorDialog(
    currentLat: Double,
    currentLon: Double,
    currentLang: AppLanguage,
    onDismiss: () -> Unit,
    onApplyDistrict: (KarnatakaDistrict) -> Unit,
    onApplyCoordinates: (Double, Double, String) -> Unit,
    onUseGps: () -> Unit,
    onSearchPlace: (String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var latText by remember { mutableStateOf(String.format(Locale.US, "%.5f", currentLat)) }
    var lonText by remember { mutableStateOf(String.format(Locale.US, "%.5f", currentLon)) }
    var customFarmLabel by remember { mutableStateOf("") }
    var showManualCoords by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(ForestGreenPrimary.copy(alpha = 0.12f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Explore,
                        contentDescription = null,
                        tint = ForestGreenPrimary,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Text(
                    text = when (currentLang) {
                        AppLanguage.KANNADA -> "ನೈಜ ಹೊಲದ ಸ್ಥಳ & ಜಿಲ್ಲೆ ಆಯ್ಕೆ"
                        AppLanguage.HINDI -> "खेत का वास्तविक स्थान व जिला"
                        AppLanguage.ENGLISH -> "Farm Location & Agro-District"
                    },
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.5.sp
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // 1. One-tap Live Device GPS (Highlighted Golden Button)
                RaithaGoldButton(
                    text = when (currentLang) {
                        AppLanguage.KANNADA -> "ನನ್ನ ನೈಜ ಸಾಧನ ಜಿಪಿಎಸ್ ಬಳಸಿ (Live GPS)"
                        AppLanguage.HINDI -> "डिवाइस का सटीक GPS उपयोग करें"
                        AppLanguage.ENGLISH -> "Detect My Live GPS Location"
                    },
                    icon = Icons.Default.MyLocation,
                    height = 46.dp,
                    fontSize = 13.5.sp,
                    onClick = onUseGps,
                    modifier = Modifier.fillMaxWidth()
                )

                // 2. Search any Karnataka Village / Taluk / Town
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text(
                            when (currentLang) {
                                AppLanguage.KANNADA -> "ಗ್ರಾಮ, ತಾಲ್ಲೂಕು ಅಥವಾ ಊರು ಹುಡುಕಿ (उदा. ಹಾಸನ, ಸಿರಸಿ...)"
                                AppLanguage.HINDI -> "गांव, कस्बा या तालुका खोजें..."
                                AppLanguage.ENGLISH -> "Search Village, Taluk, or Town..."
                            },
                            fontSize = 13.sp
                        )
                    },
                    trailingIcon = {
                        IconButton(
                            onClick = { onSearchPlace(searchQuery) }
                        ) {
                            Icon(Icons.Default.Search, contentDescription = "Search Place", tint = ForestGreenPrimary)
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                // 3. Quick Agro-District Picker (Covers all Karnataka regions)
                Text(
                    text = when (currentLang) {
                        AppLanguage.KANNADA -> "ಕರ್ನಾಟಕದ ಪ್ರಮುಖ ಕೃಷಿ ಜಿಲ್ಲೆಗಳು (1-ಟ್ಯಾಪ್ ಆಯ್ಕೆ):"
                        AppLanguage.HINDI -> "कर्नाटक के कृषि जिले (तुरंत चुनें):"
                        AppLanguage.ENGLISH -> "Karnataka Agricultural Districts:"
                    },
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = ForestGreenPrimary
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    KARNATAKA_DISTRICTS.forEach { district ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)),
                            modifier = Modifier.clickable { onApplyDistrict(district) }
                        ) {
                            Column(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = district.name,
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = district.kannadaName,
                                    fontSize = 11.sp,
                                    color = ForestGreenPrimary
                                )
                            }
                        }
                    }
                }

                // Toggle for Manual Latitude / Longitude entry
                TextButton(
                    onClick = { showManualCoords = !showManualCoords },
                    modifier = Modifier.align(Alignment.Start)
                ) {
                    Icon(
                        imageVector = if (showManualCoords) Icons.Default.CheckCircle else Icons.Default.PinDrop,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = ForestGreenPrimary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (showManualCoords) "ಕಕ್ಷೆ ವಿವರ ಮರೆಮಾಡಿ (Hide Coordinates)" else "ಅಕ್ಷಾಂಶ/ರೇಖಾಂಶ ನೇರ ನಮೂದು (Custom Lat/Lon)",
                        fontSize = 12.5.sp,
                        color = ForestGreenPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                if (showManualCoords) {
                    OutlinedTextField(
                        value = customFarmLabel,
                        onValueChange = { customFarmLabel = it },
                        label = { Text("ಹೊಲದ ಹೆಸರು / Farm Label") },
                        placeholder = { Text("e.g. My North Field, Plot 4") },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = latText,
                            onValueChange = { latText = it },
                            label = { Text("Latitude") },
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = lonText,
                            onValueChange = { lonText = it },
                            label = { Text("Longitude") },
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    RaithaHighlightedButton(
                        text = when (currentLang) {
                            AppLanguage.KANNADA -> "ಈ ಕಕ್ಷೆಗಳನ್ನು ಅನ್ವಯಿಸಿ"
                            AppLanguage.HINDI -> "निर्देशांक लागू करें"
                            AppLanguage.ENGLISH -> "Apply Coordinates"
                        },
                        height = 44.dp,
                        fontSize = 13.5.sp,
                        onClick = {
                            val latVal = latText.toDoubleOrNull() ?: currentLat
                            val lonVal = lonText.toDoubleOrNull() ?: currentLon
                            val lbl = customFarmLabel.ifBlank { "Farm (${String.format(Locale.US, "%.3f", latVal)}, ${String.format(Locale.US, "%.3f", lonVal)})" }
                            onApplyCoordinates(latVal, lonVal, lbl)
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = when (currentLang) {
                        AppLanguage.KANNADA -> "ಮುಚ್ಚಿ (Close)"
                        AppLanguage.HINDI -> "बंद करें"
                        AppLanguage.ENGLISH -> "Close"
                    },
                    fontWeight = FontWeight.Bold
                )
            }
        }
    )
}

