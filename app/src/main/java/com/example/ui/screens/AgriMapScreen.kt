package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EditLocation
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import com.example.data.model.KARNATAKA_DISTRICTS
import com.example.data.model.KarnatakaDistrict
import com.example.data.model.AppLanguage
import com.example.ui.theme.ForestGreenDark
import com.example.ui.theme.ForestGreenPrimary
import com.example.ui.viewmodel.RaithaDrishtiViewModel
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt

data class MandiMapPin(
    val id: String,
    val nameEn: String,
    val nameKn: String,
    val nameHi: String,
    val district: String,
    val latitude: Double,
    val longitude: Double,
    val primaryCommodity: String,
    val livePricePerQtl: Int,
    val priceTrend: String, // "UP", "STABLE", "DOWN"
    val highwayRoute: String,
    val description: String
)

val KARNATAKA_MANDIS = listOf(
    MandiMapPin(
        id = "chikkamagaluru_apmc",
        nameEn = "Chikkamagaluru Coffee & Spice APMC",
        nameKn = "ಚಿಕ್ಕಮಗಳೂರು ಕಾಫಿ & ಸಾಂಬಾರ ಮಾರುಕಟ್ಟೆ",
        nameHi = "चिक्कमगलुरु कॉफी व मसाला मंडी",
        district = "Chikkamagaluru",
        latitude = 13.316,
        longitude = 75.772,
        primaryCommodity = "Coffee & Black Pepper & Ginger",
        livePricePerQtl = 34500,
        priceTrend = "UP",
        highwayRoute = "NH-173 via Mudigere / Kadur",
        description = "Premier Malnad plantation market for Arabica/Robusta coffee and whole black pepper."
    ),
    MandiMapPin(
        id = "bengaluru_apmc",
        nameEn = "Bengaluru Yeshwanthpur APMC",
        nameKn = "ಬೆಂಗಳೂರು ಯಶವಂತಪುರ ಎಪಿಎಂಸಿ",
        nameHi = "बेंगलुरु यशवंतपुर एपीएमसी",
        district = "Bengaluru Urban",
        latitude = 13.028,
        longitude = 77.540,
        primaryCommodity = "Tomato & Onion",
        livePricePerQtl = 3800,
        priceTrend = "UP",
        highwayRoute = "NH-48 / Tumakuru Road",
        description = "Karnataka's largest consumption hub with highest liquidity and daily cash turnover."
    ),
    MandiMapPin(
        id = "kolar_apmc",
        nameEn = "Kolar Tomato APMC",
        nameKn = "ಕೋಲಾರ ಟೊಮೇಟೊ ಮಾರುಕಟ್ಟೆ",
        nameHi = "कोलार टमाटर मंडी",
        district = "Kolar",
        latitude = 13.136,
        longitude = 78.129,
        primaryCommodity = "Tomato",
        livePricePerQtl = 3450,
        priceTrend = "UP",
        highwayRoute = "NH-75 via Hosakote",
        description = "Asia's 2nd largest tomato market with buyers from 14 Indian states."
    ),
    MandiMapPin(
        id = "nelamangala_local",
        nameEn = "Nelamangala Local Market",
        nameKn = "ನೆಲಮಂಗಲ ಸ್ಥಳೀಯ ಮಾರುಕಟ್ಟೆ",
        nameHi = "नेलमंगला स्थानीय बाजार",
        district = "Bengaluru Rural",
        latitude = 13.098,
        longitude = 77.391,
        primaryCommodity = "Maize & Vegetables",
        livePricePerQtl = 2950,
        priceTrend = "STABLE",
        highwayRoute = "Local Grama Road",
        description = "Farmer's closest local village mandi with minimal freight cost."
    ),
    MandiMapPin(
        id = "mysuru_apmc",
        nameEn = "Mysuru Bandipalya APMC",
        nameKn = "ಮೈಸೂರು ಬಂಡಿಪಾಳ್ಯ ಎಪಿಎಂಸಿ",
        nameHi = "मैसूर बंडीपाल्या एपीएमसी",
        district = "Mysuru",
        latitude = 12.295,
        longitude = 76.639,
        primaryCommodity = "Paddy & Jaggery",
        livePricePerQtl = 3100,
        priceTrend = "UP",
        highwayRoute = "Bengaluru-Mysuru Expressway",
        description = "Direct procurement market for Cauvery basin paddy, jaggery, and ginger."
    ),
    MandiMapPin(
        id = "davanagere_apmc",
        nameEn = "Davanagere Maize Mandi",
        nameKn = "ದಾವಣಗೆರೆ ಮೆಕ್ಕೆಜೋಳ ಮಾರುಕಟ್ಟೆ",
        nameHi = "दावणगेरे मक्का मंडी",
        district = "Davanagere",
        latitude = 14.464,
        longitude = 75.921,
        primaryCommodity = "Maize & Cotton",
        livePricePerQtl = 2480,
        priceTrend = "UP",
        highwayRoute = "NH-48 Pune-Bengaluru Hwy",
        description = "Premier grain terminal for hybrid yellow maize feed manufacturers."
    ),
    MandiMapPin(
        id = "shivamogga_apmc",
        nameEn = "Shivamogga Arecanut APMC",
        nameKn = "ಶಿವಮೊಗ್ಗ ಅಡಿಕೆ ಎಪಿಎಂಸಿ",
        nameHi = "शिवमोग्गा सुपारी एपीएमसी",
        district = "Shivamogga",
        latitude = 13.929,
        longitude = 75.568,
        primaryCommodity = "Arecanut (Chali/Rashi)",
        livePricePerQtl = 52000,
        priceTrend = "UP",
        highwayRoute = "NH-69 via Tumakuru-Arasikere",
        description = "World famous Rashi idi arecanut trading auction platform."
    ),
    MandiMapPin(
        id = "hassan_apmc",
        nameEn = "Hassan Potato & Maize APMC",
        nameKn = "ಹಾಸನ ಆಲೂಗಡ್ಡೆ & ಮೆಕ್ಕೆಜೋಳ ಎಪಿಎಂಸಿ",
        nameHi = "हासन आलू व मक्का मंडी",
        district = "Hassan",
        latitude = 13.003,
        longitude = 76.100,
        primaryCommodity = "Potato & Ginger",
        livePricePerQtl = 2300,
        priceTrend = "DOWN",
        highwayRoute = "NH-75 Kunigal-Channarayapatna",
        description = "Specialized cold storage and potato auction hub."
    ),
    MandiMapPin(
        id = "hubballi_apmc",
        nameEn = "Hubballi Amaragol APMC",
        nameKn = "ಹುಬ್ಬಳ್ಳಿ ಅಮರಗೋಳ ಎಪಿಎಂಸಿ",
        nameHi = "हुबली अमरागोल मंडी",
        district = "Dharwad",
        latitude = 15.364,
        longitude = 75.124,
        primaryCommodity = "Byadagi Chilli & Pulses",
        livePricePerQtl = 18500,
        priceTrend = "UP",
        highwayRoute = "NH-48 North Corridor",
        description = "Key transit terminal connecting North Karnataka and Maharashtra markets."
    ),
    MandiMapPin(
        id = "mandya_apmc",
        nameEn = "Mandya Sugar & Jaggery APMC",
        nameKn = "ಮಂಡ್ಯ ಬೆಲ್ಲ & ಕಬ್ಬು ಮಾರುಕಟ್ಟೆ",
        nameHi = "मंड्या गुड़ व गन्ना मंडी",
        district = "Mandya",
        latitude = 12.522,
        longitude = 76.898,
        primaryCommodity = "Jaggery & Vegetables",
        livePricePerQtl = 3900,
        priceTrend = "STABLE",
        highwayRoute = "NH-275 Highway",
        description = "Centenary jaggery auction center with high demand from Tamil Nadu."
    ),
    MandiMapPin(
        id = "chikkamagaluru_apmc",
        nameEn = "Chikkamagaluru Spices APMC",
        nameKn = "ಚಿಕ್ಕಮಗಳೂರು ಕಾಫಿ & ಸಾಂಬಾರ ಮಾರುಕಟ್ಟೆ",
        nameHi = "चिकमगलूर कॉफी व मसाला मंडी",
        district = "Chikkamagaluru",
        latitude = 13.316,
        longitude = 75.772,
        primaryCommodity = "Coffee, Pepper & Areca",
        livePricePerQtl = 32000,
        priceTrend = "UP",
        highwayRoute = "NH-173 via Hassan",
        description = "Direct platform for Arabica coffee beans and black pepper."
    ),
    MandiMapPin(
        id = "belagavi_apmc",
        nameEn = "Belagavi Central Vegetable Mandi",
        nameKn = "ಬೆಳಗಾವಿ ತರಕಾರಿ ಎಪಿಎಂಸಿ",
        nameHi = "बेलगावी सब्जी मंडी",
        district = "Belagavi",
        latitude = 15.849,
        longitude = 74.497,
        primaryCommodity = "Vegetables & Onion",
        livePricePerQtl = 2700,
        priceTrend = "STABLE",
        highwayRoute = "NH-48 Border Highway",
        description = "Primary vegetable exporter hub to Goa and Western Maharashtra."
    ),
    MandiMapPin(
        id = "raichur_apmc",
        nameEn = "Raichur Cotton & Rice Mandi",
        nameKn = "ರಾಯಚೂರು ಹತ್ತಿ & ಸೋನಾ ಮಸೂರಿ ಎಪಿಎಂಸಿ",
        nameHi = "रायचूर कपास व चावल मंडी",
        district = "Raichur",
        latitude = 16.212,
        longitude = 77.343,
        primaryCommodity = "Cotton & Sona Masoori",
        livePricePerQtl = 7100,
        priceTrend = "UP",
        highwayRoute = "SH-19 / NH-167",
        description = "India's highest quality Sona Masoori raw rice and BT cotton trading floor."
    )
)

@Composable
fun AgriMapScreen(
    viewModel: RaithaDrishtiViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val currentLang by viewModel.currentLanguage.collectAsState()
    val farmerCoords by viewModel.farmerCoordinates.collectAsState()
    val currentAccount by viewModel.currentAccount.collectAsState()
    val activeFarmerName by viewModel.activeFarmerName.collectAsState()
    val activeVillage by viewModel.activeVillageName.collectAsState()
    val activeBirthYear by viewModel.activeBirthYear.collectAsState()
    val selectedDistrict by viewModel.selectedDistrict.collectAsState()

    var showFieldSetterDialog by remember { mutableStateOf(false) }
    var dialogDistrict by remember(selectedDistrict) { mutableStateOf(selectedDistrict) }
    var dialogVillageName by remember(activeVillage) { mutableStateOf(activeVillage) }

    var selectedLayer by remember { mutableStateOf("MANDI") } // "MANDI", "WEATHER", "PARCEL"
    var selectedMandiId by remember { mutableStateOf<String?>("bengaluru_apmc") }
    var zoomLevel by remember { mutableFloatStateOf(1.0f) }
    var panOffsetX by remember { mutableFloatStateOf(0f) }
    var panOffsetY by remember { mutableFloatStateOf(0f) }

    val selectedMandi = KARNATAKA_MANDIS.find { it.id == selectedMandiId } ?: KARNATAKA_MANDIS[0]

    // Calculate road distance using Haversine formula with a 1.25x road curvature factor
    val distanceKm = calculateRoadDistanceKm(
        lat1 = farmerCoords.first,
        lon1 = farmerCoords.second,
        lat2 = selectedMandi.latitude,
        lon2 = selectedMandi.longitude
    )

    val freightPerQtl = calculateFreight(distanceKm)
    val localPrice = 2950 // local baseline
    val netArbitrage = (selectedMandi.livePricePerQtl - localPrice) - freightPerQtl

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // --- Header with Farmer Persistent Identity Bar ---
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = when (currentLang) {
                                AppLanguage.KANNADA -> "ಕರ್ನಾಟಕ ಕೃಷಿ & ಎಪಿಎಂಸಿ ಜಿಐಎಸ್ ಭೂಪಟ"
                                AppLanguage.HINDI -> "कर्नाटक कृषि व मंडी जीआईएस नक्शा"
                                AppLanguage.ENGLISH -> "Karnataka Agri & Mandi GIS Map"
                            },
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (activeFarmerName.isNotBlank()) {
                                when (currentLang) {
                                    AppLanguage.KANNADA -> "ಖಾತೆ: $activeFarmerName • $activeVillage (ಜನನ: $activeBirthYear)"
                                    AppLanguage.HINDI -> "खाता: $activeFarmerName • $activeVillage (जन्म: $activeBirthYear)"
                                    AppLanguage.ENGLISH -> "Identity: $activeFarmerName • $activeVillage (DOB: $activeBirthYear)"
                                }
                            } else {
                                when (currentLang) {
                                    AppLanguage.KANNADA -> "ರೈತರ ಜಮೀನು ಮತ್ತು ಮಂಡಿ ನಕ್ಷೆ"
                                    AppLanguage.HINDI -> "किसान का खेत और मंडी मानचित्र"
                                    AppLanguage.ENGLISH -> "Farmer Field & Mandi Network"
                                }
                            },
                            style = MaterialTheme.typography.labelSmall,
                            color = ForestGreenPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    // Persistent storage confirmation pill
                    Surface(
                        color = ForestGreenPrimary.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = when (currentLang) {
                                AppLanguage.KANNADA -> "ಖಚಿತ ಉಳಿತಾಯ"
                                AppLanguage.HINDI -> "सुरक्षित डेटा"
                                AppLanguage.ENGLISH -> "Saved & Locked"
                            },
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = ForestGreenPrimary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Layer selection chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = selectedLayer == "MANDI",
                        onClick = { selectedLayer = "MANDI" },
                        label = {
                            Text(
                                text = when (currentLang) {
                                    AppLanguage.KANNADA -> "ಎಪಿಎಂಸಿ ಮಾರ್ಗಗಳು"
                                    AppLanguage.HINDI -> "मंडी व रास्ते"
                                    AppLanguage.ENGLISH -> "APMC Mandis & Routes"
                                },
                                fontSize = 11.sp
                            )
                        },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.Storefront, contentDescription = null, modifier = Modifier.size(14.dp))
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ForestGreenPrimary,
                            selectedLabelColor = Color.White,
                            selectedLeadingIconColor = Color.White
                        )
                    )

                    FilterChip(
                        selected = selectedLayer == "WEATHER",
                        onClick = { selectedLayer = "WEATHER" },
                        label = {
                            Text(
                                text = when (currentLang) {
                                    AppLanguage.KANNADA -> "ಮಳೆ ರೇಡಾರ್"
                                    AppLanguage.HINDI -> "मौसम रडार"
                                    AppLanguage.ENGLISH -> "Weather Radar"
                                },
                                fontSize = 11.sp
                            )
                        },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.Cloud, contentDescription = null, modifier = Modifier.size(14.dp))
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF1976D2),
                            selectedLabelColor = Color.White,
                            selectedLeadingIconColor = Color.White
                        )
                    )

                    FilterChip(
                        selected = selectedLayer == "PARCEL",
                        onClick = { selectedLayer = "PARCEL" },
                        label = {
                            Text(
                                text = when (currentLang) {
                                    AppLanguage.KANNADA -> "ನನ್ನ ಜಮೀನು"
                                    AppLanguage.HINDI -> "मेरा खेत"
                                    AppLanguage.ENGLISH -> "Farm Parcel"
                                },
                                fontSize = 11.sp
                            )
                        },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.Layers, contentDescription = null, modifier = Modifier.size(14.dp))
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFFE65100),
                            selectedLabelColor = Color.White,
                            selectedLeadingIconColor = Color.White
                        )
                    )
                }

                // Farmer Fixed Field Location Status Bar (Guaranteed Persistence)
                Surface(
                    color = ForestGreenPrimary.copy(alpha = 0.08f),
                    border = BorderStroke(1.dp, ForestGreenPrimary.copy(alpha = 0.35f)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                        .testTag("farmer_field_place_banner")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .background(ForestGreenPrimary, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = "Farmer Field",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = when (currentLang) {
                                        AppLanguage.KANNADA -> "ನಿಮ್ಮ ಕಾಯಂ ಹೊಲದ ಸ್ಥಳ (ಮರುಪ್ರಾರಂಭದಲ್ಲೂ ಉಳಿಯುತ್ತದೆ):"
                                        AppLanguage.HINDI -> "आपका स्थायी खेत स्थान (ऐप बंद होने पर भी सुरक्षित):"
                                        AppLanguage.ENGLISH -> "Permanent Farm Field Location (Saved):"
                                    },
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = when (currentLang) {
                                        AppLanguage.KANNADA -> "${selectedDistrict.kannadaName} ($activeVillage)"
                                        AppLanguage.HINDI -> "${selectedDistrict.hindiName.ifBlank { selectedDistrict.name }} ($activeVillage)"
                                        AppLanguage.ENGLISH -> "${selectedDistrict.name} ($activeVillage)"
                                    },
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ForestGreenDark
                                )
                            }
                        }

                        Button(
                            onClick = {
                                dialogDistrict = selectedDistrict
                                dialogVillageName = activeVillage
                                showFieldSetterDialog = true
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.testTag("set_farmer_field_btn")
                        ) {
                            Icon(imageVector = Icons.Default.EditLocation, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = when (currentLang) {
                                    AppLanguage.KANNADA -> "ಸ್ಥಳ ಬದಲಿಸಿ"
                                    AppLanguage.HINDI -> "स्थान बदलें"
                                    AppLanguage.ENGLISH -> "Set Field"
                                },
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }

        // Dialog for setting permanent field location
        if (showFieldSetterDialog) {
            AlertDialog(
                onDismissRequest = { showFieldSetterDialog = false },
                title = {
                    Text(
                        text = when (currentLang) {
                            AppLanguage.KANNADA -> "ನಿಮ್ಮ ಹೊಲದ ಸ್ಥಳ ನಿಗದಿಪಡಿಸಿ"
                            AppLanguage.HINDI -> "अपने खेत का स्थान स्थायी रूप से सेट करें"
                            AppLanguage.ENGLISH -> "Set Your Permanent Farm Field"
                        },
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                },
                text = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(380.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = when (currentLang) {
                                AppLanguage.KANNADA -> "ಒಮ್ಮೆ ಇಲ್ಲಿ ನಿಮ್ಮ ಜಿಲ್ಲೆ ಮತ್ತು ಗ್ರಾಮವನ್ನು ಆರಿಸಿದರೆ, ಆ್ಯಪ್ ಮುಚ್ಚಿ ಪುನಃ ತೆರೆದಾಗಲೂ ಅದೇ ಸ್ಥಳದ ಹವಾಮಾನ ಮತ್ತು ಮಾರುಕಟ್ಟೆ ವರದಿ ಕಾಣಿಸುತ್ತದೆ (ಬೆಂಗಳೂರಿಗೆ ಬದಲಾಗುವುದಿಲ್ಲ)."
                                AppLanguage.HINDI -> "एक बार अपना जिला व गांव सेट करें। ऐप दोबारा खोलने पर भी यही स्थान रहेगा (बेंगलुरु पर रिसेट नहीं होगा)।"
                                AppLanguage.ENGLISH -> "Once set, your chosen district and village will remain permanent across app restarts (will never reset to Bengaluru)."
                            },
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        OutlinedTextField(
                            value = dialogVillageName,
                            onValueChange = { dialogVillageName = it },
                            label = {
                                Text(
                                    when (currentLang) {
                                        AppLanguage.KANNADA -> "ಗ್ರಾಮ / ಹೊಲದ ಹೆಸರು"
                                        AppLanguage.HINDI -> "गांव / खेत का नाम"
                                        AppLanguage.ENGLISH -> "Village / Farm Name"
                                    }
                                )
                            },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Text(
                            text = when (currentLang) {
                                AppLanguage.KANNADA -> "ಕರ್ನಾಟಕದ ಜಿಲ್ಲೆಯನ್ನು ಆಯ್ಕೆಮಾಡಿ (31 ಜಿಲ್ಲೆಗಳು):"
                                AppLanguage.HINDI -> "कर्नाटक का जिला चुनें (31 जिले):"
                                AppLanguage.ENGLISH -> "Select Karnataka District (31 Districts):"
                            },
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = ForestGreenDark
                        )

                        // List of districts with Chikkamagaluru, Hassan, Shivamogga, etc.
                        LazyColumn(
                            modifier = Modifier
                                .weight(1f)
                                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(8.dp))
                        ) {
                            items(KARNATAKA_DISTRICTS) { dist ->
                                val isChosen = dist.name == dialogDistrict.name
                                val distLabel = when (currentLang) {
                                    AppLanguage.KANNADA -> dist.kannadaName
                                    AppLanguage.HINDI -> dist.hindiName.ifBlank { dist.name }
                                    AppLanguage.ENGLISH -> dist.name
                                }
                                Surface(
                                    color = if (isChosen) ForestGreenPrimary.copy(alpha = 0.15f) else Color.Transparent,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { dialogDistrict = dist }
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 12.dp, vertical = 10.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(
                                                text = distLabel,
                                                fontWeight = if (isChosen) FontWeight.Bold else FontWeight.Normal,
                                                color = if (isChosen) ForestGreenDark else MaterialTheme.colorScheme.onSurface
                                            )
                                            Text(
                                                text = "${dist.name} • ${String.format("%.2f", dist.lat)}°N, ${String.format("%.2f", dist.lon)}°E",
                                                fontSize = 10.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                        if (isChosen) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = "Selected",
                                                tint = ForestGreenPrimary,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }
                                }
                                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val cleanedVillage = dialogVillageName.trim().ifEmpty { activeVillage }
                            viewModel.setFarmerFieldLocation(dialogDistrict, cleanedVillage)
                            showFieldSetterDialog = false
                            Toast.makeText(
                                context,
                                when (currentLang) {
                                    AppLanguage.KANNADA -> "ಹೊಲದ ಸ್ಥಳ ಕಾಯಂ ಆಗಿ ನಿಗದಿಪಡಿಸಲಾಗಿದೆ: ${dialogDistrict.kannadaName} ($cleanedVillage)!"
                                    AppLanguage.HINDI -> "खेत का स्थान स्थायी रूप से सेट किया गया: ${dialogDistrict.hindiName.ifBlank { dialogDistrict.name }} ($cleanedVillage)!"
                                    AppLanguage.ENGLISH -> "Permanent Farm Field locked: ${dialogDistrict.name} ($cleanedVillage)!"
                                },
                                Toast.LENGTH_LONG
                            ).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary)
                    ) {
                        Text(
                            when (currentLang) {
                                AppLanguage.KANNADA -> "ಉಳಿಸಿ ಮತ್ತು ಕಾಯಂ ನಿಗದಿಪಡಿಸಿ"
                                AppLanguage.HINDI -> "सुरक्षित व स्थायी सेट करें"
                                AppLanguage.ENGLISH -> "Save & Lock Field"
                            }
                        )
                    }
                },
                dismissButton = {
                    OutlinedButton(onClick = { showFieldSetterDialog = false }) {
                        Text(
                            when (currentLang) {
                                AppLanguage.KANNADA -> "ರದ್ದು"
                                AppLanguage.HINDI -> "रद्द करें"
                                AppLanguage.ENGLISH -> "Cancel"
                            }
                        )
                    }
                }
            )
        }

        // --- Interactive Map Canvas with Pan & Zoom ---
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(Color(0xFFE8F5E9))
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        panOffsetX += dragAmount.x
                        panOffsetY += dragAmount.y
                    }
                }
                .testTag("karnataka_map_canvas")
        ) {
            BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                val canvasWidth = constraints.maxWidth.toFloat()
                val canvasHeight = constraints.maxHeight.toFloat()

                // Coordinate bounds for Karnataka:
                // Lat: 11.5°N (South) to 18.5°N (North) -> Span = 7.0°
                // Lon: 74.0°E (West) to 78.8°E (East) -> Span = 4.8°
                val minLat = 11.4
                val maxLat = 18.6
                val minLon = 73.8
                val maxLon = 78.8

                fun geoToScreen(lat: Double, lon: Double): Offset {
                    val normX = ((lon - minLon) / (maxLon - minLon)).toFloat()
                    // Invert Y because latitude goes South -> North, but screen Y goes Top -> Bottom
                    val normY = (1.0f - ((lat - minLat) / (maxLat - minLat)).toFloat())

                    val baseMargin = 32f
                    val usableW = canvasWidth - (baseMargin * 2)
                    val usableH = canvasHeight - (baseMargin * 2)

                    val cx = canvasWidth / 2f
                    val cy = canvasHeight / 2f

                    val rawX = baseMargin + normX * usableW
                    val rawY = baseMargin + normY * usableH

                    val zoomedX = cx + (rawX - cx) * zoomLevel + panOffsetX
                    val zoomedY = cy + (rawY - cy) * zoomLevel + panOffsetY

                    return Offset(zoomedX, zoomedY)
                }

                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(zoomLevel, panOffsetX, panOffsetY) {
                            detectTapGestures { tapOffset ->
                                // Hit test against Mandi Pins
                                for (mandi in KARNATAKA_MANDIS) {
                                    val pos = geoToScreen(mandi.latitude, mandi.longitude)
                                    val dist = (pos - tapOffset).getDistance()
                                    if (dist < 40f) {
                                        selectedMandiId = mandi.id
                                        break
                                    }
                                }
                            }
                        }
                ) {
                    // 1. Draw Map Background & Karnataka State Schematic Contour
                    drawKarnatakaStateBoundaries(
                        geoToScreen = { lat, lon -> geoToScreen(lat, lon) },
                        selectedLayer = selectedLayer
                    )

                    // 2. If Weather Layer is Active: Draw Doppler Clouds & Rain Zones
                    if (selectedLayer == "WEATHER") {
                        drawWeatherRadarLayer(geoToScreen = { lat, lon -> geoToScreen(lat, lon) })
                    }

                    // 3. Draw Connecting Route between Farmer's Village and Selected Mandi
                    val farmerPos = geoToScreen(farmerCoords.first, farmerCoords.second)
                    val mandiPos = geoToScreen(selectedMandi.latitude, selectedMandi.longitude)

                    // Animated dashed road line
                    val routePath = Path().apply {
                        moveTo(farmerPos.x, farmerPos.y)
                        // Add smooth bezier curve representing state highway
                        val midX = (farmerPos.x + mandiPos.x) / 2f
                        val midY = (farmerPos.y + mandiPos.y) / 2f - 25f
                        quadraticBezierTo(midX, midY, mandiPos.x, mandiPos.y)
                    }

                    drawPath(
                        path = routePath,
                        color = Color(0xFFE65100),
                        style = Stroke(
                            width = 5f,
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 10f), 0f),
                            cap = StrokeCap.Round
                        )
                    )

                    // 4. Draw Mandi Pins across Karnataka
                    for (mandi in KARNATAKA_MANDIS) {
                        val pos = geoToScreen(mandi.latitude, mandi.longitude)
                        val isSelected = mandi.id == selectedMandiId

                        // Pulsing outer halo for selected mandi
                        if (isSelected) {
                            drawCircle(
                                color = Color(0xFFFFB300).copy(alpha = 0.4f),
                                radius = 22f,
                                center = pos
                            )
                        }

                        // Base Mandi Pin circle
                        drawCircle(
                            color = if (isSelected) Color(0xFFE65100) else ForestGreenPrimary,
                            radius = if (isSelected) 14f else 9f,
                            center = pos
                        )
                        drawCircle(
                            color = Color.White,
                            radius = if (isSelected) 6f else 4f,
                            center = pos
                        )
                    }

                    // 5. Draw Farmer's Farm Pin at Village Coordinates
                    // Glowing beacon for farmer's farm
                    drawCircle(
                        color = Color(0xFF2E7D32).copy(alpha = 0.35f),
                        radius = 28f,
                        center = farmerPos
                    )
                    drawCircle(
                        color = Color(0xFF1B5E20),
                        radius = 16f,
                        center = farmerPos
                    )
                    drawCircle(
                        color = Color(0xFFFFD54F),
                        radius = 8f,
                        center = farmerPos
                    )

                    // 6. If Farm Parcel layer is active, draw agricultural boundary polygon around farm
                    if (selectedLayer == "PARCEL") {
                        drawFarmParcelGeometry(center = farmerPos)
                    }
                }
            }

            // Floating Map Controls (Zoom In, Zoom Out, Reset, My Farm GPS)
            Column(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 4.dp,
                    shadowElevation = 3.dp
                ) {
                    IconButton(
                        onClick = { if (zoomLevel < 3.5f) zoomLevel += 0.4f },
                        modifier = Modifier.size(42.dp)
                    ) {
                        Icon(imageVector = Icons.Default.ZoomIn, contentDescription = "Zoom In", tint = ForestGreenDark)
                    }
                }

                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 4.dp,
                    shadowElevation = 3.dp
                ) {
                    IconButton(
                        onClick = { if (zoomLevel > 0.8f) zoomLevel -= 0.4f },
                        modifier = Modifier.size(42.dp)
                    ) {
                        Icon(imageVector = Icons.Default.ZoomOut, contentDescription = "Zoom Out", tint = ForestGreenDark)
                    }
                }

                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 4.dp,
                    shadowElevation = 3.dp
                ) {
                    IconButton(
                        onClick = {
                            zoomLevel = 1.0f
                            panOffsetX = 0f
                            panOffsetY = 0f
                        },
                        modifier = Modifier.size(42.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = "Reset View", tint = ForestGreenDark)
                    }
                }

                Surface(
                    shape = CircleShape,
                    color = ForestGreenPrimary,
                    shadowElevation = 4.dp
                ) {
                    IconButton(
                        onClick = {
                            // Center immediately onto farmer's farm
                            zoomLevel = 1.6f
                            panOffsetX = 0f
                            panOffsetY = 0f
                            val farmLabel = if (activeFarmerName.isNotBlank()) "$activeFarmerName's Farm" else "Farmer's Field"
                            val locLabel = if (activeVillage.isNotBlank()) " in $activeVillage" else ""
                            Toast.makeText(
                                context,
                                "Centered on $farmLabel$locLabel",
                                Toast.LENGTH_SHORT
                            ).show()
                        },
                        modifier = Modifier.size(42.dp)
                    ) {
                        Icon(imageVector = Icons.Default.MyLocation, contentDescription = "Locate Farm", tint = Color.White)
                    }
                }
            }

            // Legend / Coordinates pill in bottom left of map
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f),
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(Color(0xFF1B5E20), CircleShape)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "$activeVillage Farm: ${String.format("%.3f", farmerCoords.first)}°N, ${String.format("%.3f", farmerCoords.second)}°E",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        // --- Bottom Sheet / Selected APMC Mandi Route & Profit Card ---
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("selected_mandi_route_card"),
            shape = RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                // Mandi Title & Commodity
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        val mandiTitle = when (currentLang) {
                            AppLanguage.KANNADA -> selectedMandi.nameKn
                            AppLanguage.HINDI -> selectedMandi.nameHi
                            AppLanguage.ENGLISH -> selectedMandi.nameEn
                        }
                        Text(
                            text = mandiTitle,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${selectedMandi.district} • ${selectedMandi.primaryCommodity}",
                            style = MaterialTheme.typography.bodySmall,
                            color = ForestGreenPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    // Price Tag
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "₹${selectedMandi.livePricePerQtl}",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = Color(0xFF2E7D32)
                        )
                        Text(
                            text = "/ Quintal (ಕ್ವಿಂಟಾಲ್)",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = MaterialTheme.colorScheme.surfaceVariant)

                // Key Route & Logistics Metrics
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Distance
                    Column {
                        Text(
                            text = when (currentLang) {
                                AppLanguage.KANNADA -> "ಹೆದ್ದಾರಿ ದೂರ"
                                AppLanguage.HINDI -> "दूरी"
                                AppLanguage.ENGLISH -> "Road Distance"
                            },
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "$distanceKm km",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = selectedMandi.highwayRoute,
                            fontSize = 9.sp,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }

                    // Freight
                    Column {
                        Text(
                            text = when (currentLang) {
                                AppLanguage.KANNADA -> "ಸಾರಿಗೆ ವೆಚ್ಚ"
                                AppLanguage.HINDI -> "भाड़ा खर्च"
                                AppLanguage.ENGLISH -> "Freight Cost"
                            },
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "₹$freightPerQtl / qtl",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color(0xFFE65100)
                        )
                        Text(
                            text = "Max 5T Vehicle",
                            fontSize = 9.sp,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }

                    // Net Profit Spread
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = when (currentLang) {
                                AppLanguage.KANNADA -> "ನಿವ್ವಳ ಲಾಭ"
                                AppLanguage.HINDI -> "शुद्ध मुनाफा"
                                AppLanguage.ENGLISH -> "Net Profit Gain"
                            },
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = if (netArbitrage > 0) "+₹$netArbitrage / qtl" else "₹0 (Local Better)",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = if (netArbitrage > 0) Color(0xFF2E7D32) else Color(0xFF757575)
                        )
                        Text(
                            text = if (netArbitrage > 0) "Profitable Route" else "Sell locally",
                            fontSize = 9.sp,
                            color = if (netArbitrage > 0) Color(0xFF2E7D32) else Color(0xFF757575)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Action Buttons: Open in Google Maps & Set Target Hub
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            openGoogleMapsNavigation(
                                context = context,
                                destLat = selectedMandi.latitude,
                                destLon = selectedMandi.longitude,
                                destName = selectedMandi.nameEn
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("open_google_maps_button")
                    ) {
                        Icon(imageVector = Icons.Default.Navigation, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = when (currentLang) {
                                AppLanguage.KANNADA -> "ಗೂಗಲ್ ಮ್ಯಾಪ್ಸ್‌ನಲ್ಲಿ ನೋಡಿ (ನ್ಯಾವಿಗೇಟ್)"
                                AppLanguage.HINDI -> "गूगल मैप्स में खोलें"
                                AppLanguage.ENGLISH -> "Navigate in Google Maps"
                            },
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }

                    OutlinedButton(
                        onClick = {
                            val forFarmer = if (activeFarmerName.isNotBlank()) " for $activeFarmerName" else ""
                            Toast.makeText(
                                context,
                                "Target Mandi set to ${selectedMandi.nameEn}$forFarmer",
                                Toast.LENGTH_SHORT
                            ).show()
                        },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = when (currentLang) {
                                AppLanguage.KANNADA -> "ಗುರಿ ಆಯ್ಕೆ"
                                AppLanguage.HINDI -> "लक्ष्य चुनें"
                                AppLanguage.ENGLISH -> "Set Target"
                            },
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}

/**
 * Custom Drawing: Karnataka Boundary & Agro-Climatic Zones
 */
private fun DrawScope.drawKarnatakaStateBoundaries(
    geoToScreen: (Double, Double) -> Offset,
    selectedLayer: String
) {
    // Karnataka Key Perimeter Geo Coordinates (Coastal, Malnad, Northern Plains, Southern Plateau)
    val statePolygonGeo = listOf(
        Pair(15.849, 74.200), // Karwar/Belagavi border
        Pair(17.500, 76.800), // Bidar / Kalaburagi North tip
        Pair(16.500, 77.400), // Raichur Krishna border
        Pair(15.200, 76.900), // Ballari eastern bulge
        Pair(13.800, 77.600), // Chikkaballapura
        Pair(13.136, 78.400), // Kolar Eastern tip
        Pair(11.900, 77.100), // Chamarajanagar southern tip
        Pair(12.200, 75.700), // Kodagu / Madikeri
        Pair(12.800, 74.800), // Mangaluru Coastal belt
        Pair(14.300, 74.400), // Bhatkal / Kumta Coast
        Pair(15.000, 74.100)  // Goa border
    )

    val boundaryPath = Path().apply {
        statePolygonGeo.forEachIndexed { index, (lat, lon) ->
            val pt = geoToScreen(lat, lon)
            if (index == 0) moveTo(pt.x, pt.y) else lineTo(pt.x, pt.y)
        }
        close()
    }

    // Fill Karnataka territory with gentle topographical gradient
    val baseGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFE8F5E9), // North Karnataka
            Color(0xFFC8E6C9), // Central Malnad
            Color(0xFFA5D6A7)  // Southern Plains
        )
    )
    drawPath(path = boundaryPath, brush = baseGradient)
    drawPath(
        path = boundaryPath,
        color = Color(0xFF2E7D32).copy(alpha = 0.45f),
        style = Stroke(width = 3f)
    )

    // Draw Major National Highway Corridors (NH-48, NH-75, Bengaluru Expressway)
    val nh48Geo = listOf(
        Pair(15.849, 74.497), // Belagavi
        Pair(15.364, 75.124), // Hubballi
        Pair(14.464, 75.921), // Davanagere
        Pair(13.340, 77.100), // Tumakuru
        Pair(13.098, 77.391), // Nelamangala (Farmer)
        Pair(12.971, 77.594)  // Bengaluru
    )

    val nh48Path = Path().apply {
        nh48Geo.forEachIndexed { i, (lat, lon) ->
            val pt = geoToScreen(lat, lon)
            if (i == 0) moveTo(pt.x, pt.y) else lineTo(pt.x, pt.y)
        }
    }
    drawPath(
        path = nh48Path,
        color = Color(0xFF78909C),
        style = Stroke(width = 3.5f, cap = StrokeCap.Round)
    )

    // NH-75 (Bengaluru -> Kolar and Bengaluru -> Hassan -> Mangaluru)
    val nh75Geo = listOf(
        Pair(12.800, 74.800), // Mangaluru
        Pair(13.003, 76.100), // Hassan
        Pair(13.098, 77.391), // Nelamangala
        Pair(12.971, 77.594), // Bengaluru
        Pair(13.136, 78.129)  // Kolar
    )
    val nh75Path = Path().apply {
        nh75Geo.forEachIndexed { i, (lat, lon) ->
            val pt = geoToScreen(lat, lon)
            if (i == 0) moveTo(pt.x, pt.y) else lineTo(pt.x, pt.y)
        }
    }
    drawPath(
        path = nh75Path,
        color = Color(0xFF90A4AE),
        style = Stroke(width = 2.5f, cap = StrokeCap.Round)
    )
}

/**
 * Custom Drawing: Weather Radar Cloud Overlay
 */
private fun DrawScope.drawWeatherRadarLayer(geoToScreen: (Double, Double) -> Offset) {
    // Malnad & Coastal Ghats Rain Belt
    val malnadCloudCenter = geoToScreen(13.6, 75.2)
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(Color(0x992196F3), Color(0x5564B5F6), Color.Transparent),
            center = malnadCloudCenter,
            radius = 120f
        ),
        center = malnadCloudCenter,
        radius = 120f
    )

    // Bengaluru / Kolar Light Shower Zone
    val kolarCloudCenter = geoToScreen(13.1, 77.8)
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(Color(0x884CAF50), Color(0x3381C784), Color.Transparent),
            center = kolarCloudCenter,
            radius = 90f
        ),
        center = kolarCloudCenter,
        radius = 90f
    )
}

/**
 * Custom Drawing: Farm Parcel Geometry with NDVI health zone
 */
private fun DrawScope.drawFarmParcelGeometry(center: Offset) {
    val parcelW = 90f
    val parcelH = 75f

    // 4.5 acre parcel rectangle
    drawRoundRect(
        color = Color(0xFF4CAF50).copy(alpha = 0.5f),
        topLeft = Offset(center.x - parcelW / 2, center.y - parcelH / 2),
        size = Size(parcelW, parcelH),
        cornerRadius = CornerRadius(8f, 8f)
    )

    drawRoundRect(
        color = Color(0xFF1B5E20),
        topLeft = Offset(center.x - parcelW / 2, center.y - parcelH / 2),
        size = Size(parcelW, parcelH),
        cornerRadius = CornerRadius(8f, 8f),
        style = Stroke(width = 2.5f)
    )
}

/**
 * Road distance calculator between two coordinates (in km)
 */
private fun calculateRoadDistanceKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Int {
    val r = 6371.0 // Radius of earth in km
    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)
    val a = sin(dLat / 2) * sin(dLat / 2) +
            cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
            sin(dLon / 2) * sin(dLon / 2)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))
    val crowFlyKm = r * c
    // Road curvature factor in Indian road networks is typically 1.25x
    return (crowFlyKm * 1.25).roundToInt().coerceAtLeast(5)
}

/**
 * Logistics Freight calculation (₹/quintal)
 * Base handling = ₹40, variable = ₹1.25/km
 */
private fun calculateFreight(distanceKm: Int): Int {
    return (40 + distanceKm * 1.25).roundToInt()
}

/**
 * Open external Google Maps navigation intent
 */
private fun openGoogleMapsNavigation(
    context: Context,
    destLat: Double,
    destLon: Double,
    destName: String
) {
    try {
        val gmmIntentUri = Uri.parse("google.navigation:q=$destLat,$destLon&mode=d")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri).apply {
            setPackage("com.google.android.apps.maps")
        }
        context.startActivity(mapIntent)
    } catch (e: Exception) {
        // Fallback to standard web browser Google Maps directions
        try {
            val webUri = Uri.parse("https://www.google.com/maps/dir/?api=1&destination=$destLat,$destLon&destination_place_id=${Uri.encode(destName)}")
            val browserIntent = Intent(Intent.ACTION_VIEW, webUri)
            context.startActivity(browserIntent)
        } catch (ex: Exception) {
            Toast.makeText(context, "Cannot launch Google Maps: ${ex.message}", Toast.LENGTH_SHORT).show()
        }
    }
}
