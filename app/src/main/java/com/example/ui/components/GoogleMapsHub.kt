package com.example.ui.components

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.data.model.AppLanguage
import com.example.data.model.KARNATAKA_DISTRICTS
import com.example.data.model.KarnatakaDistrict
import com.example.ui.theme.AmberLight
import com.example.ui.theme.AmberSecondary
import com.example.ui.theme.ForestGreenDark
import com.example.ui.theme.ForestGreenLight
import com.example.ui.theme.ForestGreenPrimary
import com.example.ui.theme.SovereignGold
import java.util.Locale

/**
 * Interactive, high-performance Google Maps component for RaithaDrishti.
 * Fully dynamic: loads the user's live GPS coordinates, allows panning, zooming,
 * toggling between Roadmap and Satellite view, searching any village/taluk in Karnataka,
 * and direct launch into Google Maps app.
 */
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun InteractiveGoogleMapCard(
    latitude: Double,
    longitude: Double,
    locationLabel: String,
    currentLanguage: AppLanguage,
    isGpsActive: Boolean,
    onDetectGps: () -> Unit,
    onSelectDistrict: (KarnatakaDistrict) -> Unit,
    onSearchPlace: (String) -> Unit,
    mapsGroundingSummary: String?,
    isGroundingLoading: Boolean,
    onRefreshGrounding: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isSatelliteMode by remember { mutableStateOf(false) }
    var zoomLevel by remember { mutableIntStateOf(13) }
    var searchQuery by remember { mutableStateOf("") }

    // Dynamic Google Maps URL based on mode, coordinates and zoom
    val mapEmbedUrl = remember(latitude, longitude, isSatelliteMode, zoomLevel) {
        val modeParam = if (isSatelliteMode) "&t=k" else ""
        "https://maps.google.com/maps?q=$latitude,$longitude$modeParam&z=$zoomLevel&output=embed"
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("interactive_google_map_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.2.dp, SovereignGold.copy(alpha = 0.55f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Title & Map Mode Switcher
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .background(Color(0xFF1E88E5).copy(alpha = 0.15f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Map,
                            contentDescription = "Google Maps",
                            tint = Color(0xFF1976D2),
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Column {
                        Text(
                            text = when (currentLanguage) {
                                AppLanguage.KANNADA -> "ಗೂಗಲ್ ನಕ್ಷೆ & ಲೈವ್ ಸ್ಥಳ"
                                AppLanguage.HINDI -> "गूगल मैप्स और लाइव स्थान"
                                AppLanguage.ENGLISH -> "Google Maps & Live Location"
                            },
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.5.sp
                            )
                        )
                        Text(
                            text = if (isGpsActive) "📍 Live Device GPS Active" else "📍 Dynamic Agri Pin",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = if (isGpsActive) Color(0xFF2E7D32) else SovereignGold,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp
                            )
                        )
                    }
                }

                // Satellite vs Roadmap Switcher Badge
                Surface(
                    onClick = { isSatelliteMode = !isSatelliteMode },
                    shape = RoundedCornerShape(12.dp),
                    color = if (isSatelliteMode) ForestGreenDark else MaterialTheme.colorScheme.surfaceVariant,
                    border = BorderStroke(1.dp, SovereignGold.copy(alpha = 0.4f)),
                    modifier = Modifier.testTag("toggle_satellite_map_button")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Layers,
                            contentDescription = null,
                            tint = if (isSatelliteMode) AmberLight else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = if (isSatelliteMode) "Satellite" else "Roadmap",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSatelliteMode) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Search Bar: Search any village/town across Karnataka
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = {
                    Text(
                        text = when (currentLanguage) {
                            AppLanguage.KANNADA -> "ಗ್ರಾಮ, ತಾಲ್ಲೂಕು ಅಥವಾ ಜಿಲ್ಲೆ ಹುಡುಕಿ (ಉದಾ. ಸಿರಸಿ, ಹಾಸನ...)"
                            AppLanguage.HINDI -> "गांव, कस्बा या जिला खोजें (जैसे सिरसी, हासन...)"
                            AppLanguage.ENGLISH -> "Search village, taluk, town in Karnataka..."
                        },
                        fontSize = 12.5.sp
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = ForestGreenPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotBlank()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear", modifier = Modifier.size(18.dp))
                        }
                    } else {
                        IconButton(onClick = { onSearchPlace(searchQuery) }) {
                            Icon(Icons.Default.Explore, contentDescription = "Go", tint = ForestGreenPrimary)
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ForestGreenPrimary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("google_map_search_field")
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Embedded Interactive Google Maps WebView
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFE0E0E0))
                    .testTag("google_maps_webview_container")
            ) {
                AndroidView(
                    factory = { ctx ->
                        WebView(ctx).apply {
                            layoutParams = ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT
                            )
                            settings.apply {
                                javaScriptEnabled = true
                                domStorageEnabled = true
                                loadWithOverviewMode = true
                                useWideViewPort = true
                                setSupportZoom(true)
                                builtInZoomControls = true
                                displayZoomControls = false
                                cacheMode = WebSettings.LOAD_DEFAULT
                            }
                            webViewClient = WebViewClient()
                            webChromeClient = WebChromeClient()
                            loadUrl(mapEmbedUrl)
                        }
                    },
                    update = { webView ->
                        if (webView.url != mapEmbedUrl) {
                            webView.loadUrl(mapEmbedUrl)
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )

                // Overlay Controls: Zoom In / Zoom Out and Open in Google Maps App
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Zoom In
                    Surface(
                        onClick = { if (zoomLevel < 18) zoomLevel++ },
                        shape = CircleShape,
                        color = Color.White.copy(alpha = 0.92f),
                        shadowElevation = 3.dp,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.ZoomIn,
                                contentDescription = "Zoom In",
                                tint = Color.Black,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    // Zoom Out
                    Surface(
                        onClick = { if (zoomLevel > 5) zoomLevel-- },
                        shape = CircleShape,
                        color = Color.White.copy(alpha = 0.92f),
                        shadowElevation = 3.dp,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.ZoomOut,
                                contentDescription = "Zoom Out",
                                tint = Color.Black,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    // Open Full in Google Maps App / Web
                    Surface(
                        onClick = { launchGoogleMapsIntent(context, latitude, longitude, locationLabel) },
                        shape = CircleShape,
                        color = Color(0xFF1976D2),
                        shadowElevation = 3.dp,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.OpenInNew,
                                contentDescription = "Open in Google Maps",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action Row: Detect My Live GPS & Open Google Maps Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                RaithaGoldButton(
                    text = when (currentLanguage) {
                        AppLanguage.KANNADA -> "ನನ್ನ ನೈಜ ಜಿಪಿಎಸ್ ಪತ್ತೆಹಚ್ಚಿ"
                        AppLanguage.HINDI -> "मेरा सटीक GPS खोजें"
                        AppLanguage.ENGLISH -> "Detect My Live GPS"
                    },
                    icon = Icons.Default.MyLocation,
                    height = 46.dp,
                    fontSize = 12.5.sp,
                    onClick = onDetectGps,
                    modifier = Modifier
                        .weight(1.1f)
                        .testTag("detect_live_gps_hub_button")
                )

                RaithaBlueButton(
                    text = when (currentLanguage) {
                        AppLanguage.KANNADA -> "ಗೂಗಲ್ ಮ್ಯಾಪ್ಸ್‌ನಲ್ಲಿ ನೋಡಿ"
                        AppLanguage.HINDI -> "गूगल मैप्स में खोलें"
                        AppLanguage.ENGLISH -> "Open Google Maps"
                    },
                    icon = Icons.Default.Map,
                    height = 46.dp,
                    fontSize = 12.5.sp,
                    onClick = { launchGoogleMapsIntent(context, latitude, longitude, locationLabel) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("open_in_google_maps_hub_button")
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Location Coordinates Telemetry
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Place,
                            contentDescription = null,
                            tint = Color(0xFFE53935),
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = locationLabel,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                    }

                    Text(
                        text = "${String.format(Locale.US, "%.4f", latitude)}, ${String.format(Locale.US, "%.4f", longitude)}",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 1-Tap Karnataka Agro-District Quick Switcher
            Text(
                text = when (currentLanguage) {
                    AppLanguage.KANNADA -> "ಕರ್ನಾಟಕದ ಜಿಲ್ಲೆಗಳು (1-ಟ್ಯಾಪ್ ಆಯ್ಕೆ):"
                    AppLanguage.HINDI -> "कर्नाटक के जिले (त्वरित चयन):"
                    AppLanguage.ENGLISH -> "Karnataka Agricultural Districts (1-Tap):"
                },
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = ForestGreenPrimary
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                KARNATAKA_DISTRICTS.forEach { district ->
                    val isCurrent = district.name.equals(locationLabel, ignoreCase = true) ||
                            (Math.abs(district.lat - latitude) < 0.1 && Math.abs(district.lon - longitude) < 0.1)

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (isCurrent) ForestGreenPrimary else MaterialTheme.colorScheme.surfaceVariant,
                        border = BorderStroke(1.dp, if (isCurrent) SovereignGold else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                        modifier = Modifier
                            .clickable { onSelectDistrict(district) }
                            .testTag("quick_district_${district.name.lowercase().replace(" ", "_")}")
                    ) {
                        Text(
                            text = when (currentLanguage) {
                                AppLanguage.KANNADA -> district.kannadaName
                                AppLanguage.HINDI -> district.hindiName
                                AppLanguage.ENGLISH -> district.name
                            },
                            fontSize = 12.sp,
                            fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
                            color = if (isCurrent) Color.White else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Gemini 3.5 Flash Google Maps Grounding Advisory Card
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color(0xFFF1F8E9),
                border = BorderStroke(1.dp, Color(0xFF81C784).copy(alpha = 0.6f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = ForestGreenPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = when (currentLanguage) {
                                    AppLanguage.KANNADA -> "ಗೂಗಲ್ ಮ್ಯಾಪ್ಸ್ AI ಕೃಷಿ ವರದಿ"
                                    AppLanguage.HINDI -> "गूगल मैप्स AI कृषि सलाह"
                                    AppLanguage.ENGLISH -> "Google Maps AI Agri-Advisory"
                                },
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.5.sp,
                                color = ForestGreenPrimary
                            )
                        }

                        IconButton(
                            onClick = onRefreshGrounding,
                            modifier = Modifier.size(28.dp)
                        ) {
                            if (isGroundingLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp,
                                    color = ForestGreenPrimary
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Refresh",
                                    tint = ForestGreenPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    if (isGroundingLoading && mapsGroundingSummary == null) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(vertical = 6.dp)
                        ) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                            Text(
                                text = "ಗೂಗಲ್ ಮ್ಯಾಪ್ಸ್ ಡೇಟಾ ವಿಶ್ಲೇಷಿಸಲಾಗುತ್ತಿದೆ... / Analyzing Google Maps data...",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                    } else {
                        Text(
                            text = mapsGroundingSummary ?: when (currentLanguage) {
                                AppLanguage.KANNADA -> "$locationLabel ಸ್ಥಳಕ್ಕೆ ಹತ್ತಿರದ APMC ಮಾರುಕಟ್ಟೆಗಳು ಹಾಗೂ ಕೃಷಿ ಸೇವಾ ಕೇಂದ್ರಗಳು ಲಭ್ಯವಿವೆ. ಹವಾಮಾನಕ್ಕೆ ತಕ್ಕಂತೆ ಮುಂಜಾನೆ ಕೀಟನಾಶಕ ಸಿಂಪಡಣೆ ಸೂಕ್ತ."
                                AppLanguage.HINDI -> "$locationLabel स्थान के नजदीकी APMC मंडी और कृषि सेवा केंद्र सक्रिय हैं। उचित जल प्रबंधन से अच्छी उपज प्राप्त करें।"
                                AppLanguage.ENGLISH -> "Nearest APMC market yards and local Krishi Vigyan Kendra for $locationLabel are active. Balanced nutrition and morning spray windows recommended."
                            },
                            fontSize = 13.sp,
                            lineHeight = 18.sp,
                            color = Color(0xFF1B5E20)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Bulletproof Google Maps Launcher:
 * Attempts native Google Maps app intent first, and cleanly falls back
 * to mobile browser view if native app is not installed.
 */
fun launchGoogleMapsIntent(context: Context, lat: Double, lon: Double, label: String) {
    val encoded = Uri.encode(label)
    val geoUri = Uri.parse("geo:$lat,$lon?q=$lat,$lon($encoded)")
    val appIntent = Intent(Intent.ACTION_VIEW, geoUri).apply {
        setPackage("com.google.android.apps.maps")
    }

    try {
        context.startActivity(appIntent)
    } catch (e: Exception) {
        val webUri = Uri.parse("https://www.google.com/maps/search/?api=1&query=$lat,$lon")
        val browserIntent = Intent(Intent.ACTION_VIEW, webUri)
        try {
            context.startActivity(browserIntent)
        } catch (ignored: Exception) {}
    }
}
