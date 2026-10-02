package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Grass
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppLanguage
import com.example.ui.theme.AmberLight
import com.example.ui.theme.AmberSecondary
import com.example.ui.theme.ForestGreenDark
import com.example.ui.theme.ForestGreenLight
import com.example.ui.theme.ForestGreenPrimary
import com.example.ui.theme.RoyalGoldContainer
import com.example.ui.theme.SovereignGold
import com.example.ui.viewmodel.RaithaDrishtiViewModel
import java.text.NumberFormat
import java.util.Locale

/**
 * Data Model for Crop Calendar
 */
data class CropCalendarEntry(
    val id: String,
    val nameEn: String,
    val nameKn: String,
    val nameHi: String,
    val season: String, // Kharif, Rabi, Summer, All
    val sowingPeriodEn: String,
    val sowingPeriodKn: String,
    val harvestPeriodEn: String,
    val harvestPeriodKn: String,
    val seedRatePerAcreEn: String,
    val seedRatePerAcreKn: String,
    val spacingEn: String,
    val durationDays: String,
    val avgYieldPerAcreEn: String,
    val avgYieldPerAcreKn: String,
    val soilTypeEn: String,
    val soilTypeKn: String,
    val keyTipsEn: String,
    val keyTipsKn: String
)

/**
 * Data Model for Govt Scheme
 */
data class GovtScheme(
    val id: String,
    val titleEn: String,
    val titleKn: String,
    val titleHi: String,
    val benefitEn: String,
    val benefitKn: String,
    val eligibilityEn: String,
    val eligibilityKn: String,
    val documentsEn: String,
    val documentsKn: String,
    val officialLink: String,
    val helpline: String
)

@Composable
fun KisanPlannerScreen(
    viewModel: RaithaDrishtiViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val currentLang by viewModel.currentLanguage.collectAsState()
    val selectedDistrict by viewModel.selectedDistrict.collectAsState()
    val exactLocationLabel by viewModel.exactLocationLabel.collectAsState()

    var activeSubTab by remember { mutableIntStateOf(0) }

    val subTabTitles = when (currentLang) {
        AppLanguage.KANNADA -> listOf("ಬೆಳೆ ಕ್ಯಾಲೆಂಡರ್", "ವೆಚ್ಚ & ಲಾಭ", "ಯೋಜನೆಗಳು", "ಗೂಗಲ್ ಮ್ಯಾಪ್")
        AppLanguage.HINDI -> listOf("फसल कैलेंडर", "व्यय व लाभ", "योजनाएं", "गूगल मैप")
        AppLanguage.ENGLISH -> listOf("Crop Calendar", "Cost & Profit", "Govt Schemes", "Google Maps")
    }

    val subTabIcons = listOf(
        Icons.Default.CalendarMonth,
        Icons.Default.Calculate,
        Icons.Default.AccountBalance,
        Icons.Default.Map
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. High-Performance Offline Header Banner (Zero Network / Zero Data)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("kisan_planner_header_card"),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = ForestGreenDark),
            border = BorderStroke(1.2.dp, SovereignGold.copy(alpha = 0.6f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.linearGradient(
                            listOf(
                                ForestGreenDark,
                                ForestGreenPrimary.copy(alpha = 0.85f),
                                Color(0xFF143820)
                            )
                        )
                    )
                    .padding(18.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .background(AmberLight.copy(alpha = 0.2f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Agriculture,
                                    contentDescription = "Agri Hub",
                                    tint = AmberLight,
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = when (currentLang) {
                                        AppLanguage.KANNADA -> "ಕೃಷಿ ಯೋಜನೆ & ರೈತ ಕೇಂದ್ರ"
                                        AppLanguage.HINDI -> "किसान योजना व कृषि केंद्र"
                                        AppLanguage.ENGLISH -> "Kisan Planner & Agri Hub"
                                    },
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                )
                                Text(
                                    text = when (currentLang) {
                                        AppLanguage.KANNADA -> "೧೦೦% ಆಫ್‌ಲೈನ್ • ಶೂನ್ಯ ಡೇಟಾ ಬಳಕೆ"
                                        AppLanguage.HINDI -> "100% ऑफलाइन • जीरो डाटा खपत"
                                        AppLanguage.ENGLISH -> "100% Offline • Zero Mobile Data"
                                    },
                                    color = AmberLight,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        // Offline Speed Badge
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF22C55E).copy(alpha = 0.2f),
                            border = BorderStroke(1.dp, Color(0xFF22C55E))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Instant",
                                    tint = Color(0xFF4ADE80),
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = "FAST",
                                    color = Color(0xFF4ADE80),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }

                    Text(
                        text = when (currentLang) {
                            AppLanguage.KANNADA -> "ಕರ್ನಾಟಕದ ಪ್ರಮುಖ ಬೆಳೆಗಳ ಬಿತ್ತನೆ ವೇಳಾಪಟ್ಟಿ, ವೆಚ್ಚ-ಲಾಭ ಕ್ಯಾಲ್ಕುಲೇಟರ್, ಸರ್ಕಾರಿ ಸಬ್ಸಿಡಿಗಳು ಮತ್ತು ನೇರ ಗೂಗಲ್ ಮ್ಯಾಪ್ ಸಂಪರ್ಕ."
                            AppLanguage.HINDI -> "कर्नाटक की प्रमुख फसलों का बुवाई कैलेंडर, लागत-लाभ कैलकुलेटर, सरकारी योजनाएं और गूगल मैप्स।"
                            AppLanguage.ENGLISH -> "Karnataka crop sowing calendars, smart farm profit estimator, government welfare schemes & direct Google Maps navigation."
                        },
                        color = Color.White.copy(alpha = 0.88f),
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        // 2. Sub-tab Navigation Switcher
        TabRow(
            selectedTabIndex = activeSubTab,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = ForestGreenPrimary,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[activeSubTab]),
                    color = ForestGreenPrimary,
                    height = 3.dp
                )
            },
            modifier = Modifier
                .clip(RoundedCornerShape(14.dp))
                .testTag("kisan_planner_subtabs")
        ) {
            subTabTitles.forEachIndexed { index, title ->
                val isSelected = activeSubTab == index
                Tab(
                    selected = isSelected,
                    onClick = { activeSubTab = index },
                    icon = {
                        Icon(
                            imageVector = subTabIcons[index],
                            contentDescription = title,
                            tint = if (isSelected) ForestGreenPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    text = {
                        Text(
                            text = title,
                            fontSize = 11.5.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            maxLines = 1
                        )
                    },
                    modifier = Modifier.testTag("kisan_subtab_$index")
                )
            }
        }

        // 3. Tab Content
        when (activeSubTab) {
            0 -> CropCalendarSection(currentLang = currentLang)
            1 -> FarmCostCalculatorSection(currentLang = currentLang)
            2 -> GovtSchemesSection(currentLang = currentLang)
            3 -> GoogleMapsLocatorSection(
                currentLang = currentLang,
                currentDistrict = selectedDistrict.name,
                exactLocationLabel = exactLocationLabel,
                viewModel = viewModel
            )
        }
    }
}

// -------------------------------------------------------------
// SUB-TAB 0: CROP CALENDAR SECTION (18+ KARNATAKA CROPS)
// -------------------------------------------------------------
@Composable
private fun CropCalendarSection(currentLang: AppLanguage) {
    var selectedSeasonFilter by remember { mutableStateOf("All") }
    var searchQuery by remember { mutableStateOf("") }
    var expandedCropId by remember { mutableStateOf<String?>("ragi") }

    val cropCatalog = remember { getKarnatakaCropCatalog() }

    val filteredCrops = remember(cropCatalog, selectedSeasonFilter, searchQuery) {
        cropCatalog.filter { crop ->
            val matchesSeason = (selectedSeasonFilter == "All") || (crop.season.equals(selectedSeasonFilter, ignoreCase = true))
            val matchesQuery = searchQuery.isBlank() ||
                    crop.nameEn.contains(searchQuery, ignoreCase = true) ||
                    crop.nameKn.contains(searchQuery, ignoreCase = true) ||
                    crop.nameHi.contains(searchQuery, ignoreCase = true)
            matchesSeason && matchesQuery
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        // Search & Season Filter
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = {
                Text(
                    text = when (currentLang) {
                        AppLanguage.KANNADA -> "ಬೆಳೆ ಹುಡುಕಿ (ಉದಾ: ರಾಗಿ, ಭತ್ತ, ಹತ್ತಿ)..."
                        AppLanguage.HINDI -> "फसल खोजें (जैसे: रागी, धान, कपास)..."
                        AppLanguage.ENGLISH -> "Search crop (e.g. Ragi, Paddy, Cotton)..."
                    },
                    fontSize = 13.sp
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = ForestGreenPrimary
                )
            },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("crop_calendar_search_input"),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ForestGreenPrimary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
            ),
            singleLine = true
        )

        // Season Filter Chips
        val seasons = listOf("All", "Kharif", "Rabi", "Summer")
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(seasons) { season ->
                val label = when (season) {
                    "All" -> when (currentLang) {
                        AppLanguage.KANNADA -> "ಎಲ್ಲಾ ಋತುಗಳು"
                        AppLanguage.HINDI -> "सभी ऋतुएं"
                        AppLanguage.ENGLISH -> "All Seasons"
                    }
                    "Kharif" -> when (currentLang) {
                        AppLanguage.KANNADA -> "ಖಾರಿಫ್ (ಮಳೆಗಾಲ)"
                        AppLanguage.HINDI -> "खरीफ (मानसून)"
                        AppLanguage.ENGLISH -> "Kharif (Monsoon)"
                    }
                    "Rabi" -> when (currentLang) {
                        AppLanguage.KANNADA -> "ರಬಿ (ಚಳಿಗಾಲ)"
                        AppLanguage.HINDI -> "रबी (सर्दियों)"
                        AppLanguage.ENGLISH -> "Rabi (Winter)"
                    }
                    else -> when (currentLang) {
                        AppLanguage.KANNADA -> "ಬೇಸಿಗೆ (ಜಾಯಿದ್)"
                        AppLanguage.HINDI -> "जायद (गर्मी)"
                        AppLanguage.ENGLISH -> "Summer (Zaid)"
                    }
                }

                FilterChip(
                    selected = selectedSeasonFilter == season,
                    onClick = { selectedSeasonFilter = season },
                    label = { Text(label, fontSize = 12.sp, fontWeight = FontWeight.SemiBold) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = RoyalGoldContainer,
                        selectedLabelColor = ForestGreenDark
                    ),
                    modifier = Modifier.testTag("filter_chip_$season")
                )
            }
        }

        // Crops List
        filteredCrops.forEach { crop ->
            val isExpanded = expandedCropId == crop.id
            val localizedName = when (currentLang) {
                AppLanguage.KANNADA -> "${crop.nameKn} (${crop.nameEn})"
                AppLanguage.HINDI -> "${crop.nameHi} (${crop.nameEn})"
                AppLanguage.ENGLISH -> "${crop.nameEn} (${crop.nameKn})"
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        expandedCropId = if (isExpanded) null else crop.id
                    }
                    .testTag("crop_card_${crop.id}"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isExpanded) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                ),
                border = BorderStroke(
                    if (isExpanded) 1.5.dp else 1.dp,
                    if (isExpanded) ForestGreenPrimary else MaterialTheme.colorScheme.outlineVariant
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .background(ForestGreenLight.copy(alpha = 0.2f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Spa,
                                    contentDescription = "Crop",
                                    tint = ForestGreenPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = localizedName,
                                    fontSize = 15.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Season: ${crop.season} • Duration: ${crop.durationDays}",
                                    fontSize = 11.5.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        IconButton(
                            onClick = { expandedCropId = if (isExpanded) null else crop.id },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = "Toggle",
                                tint = ForestGreenPrimary
                            )
                        }
                    }

                    // Key Overview Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        CalendarQuickMetric(
                            icon = Icons.Default.CalendarMonth,
                            title = if (currentLang == AppLanguage.KANNADA) "ಬಿತ್ತನೆ" else "Sowing",
                            value = if (currentLang == AppLanguage.KANNADA) crop.sowingPeriodKn else crop.sowingPeriodEn
                        )
                        CalendarQuickMetric(
                            icon = Icons.Default.Grass,
                            title = if (currentLang == AppLanguage.KANNADA) "ಕೊಯ್ಲು" else "Harvest",
                            value = if (currentLang == AppLanguage.KANNADA) crop.harvestPeriodKn else crop.harvestPeriodEn
                        )
                        CalendarQuickMetric(
                            icon = Icons.Default.TrendingUp,
                            title = if (currentLang == AppLanguage.KANNADA) "ಇಳುವರಿ" else "Yield/Acre",
                            value = if (currentLang == AppLanguage.KANNADA) crop.avgYieldPerAcreKn else crop.avgYieldPerAcreEn
                        )
                    }

                    // Expanded Details
                    AnimatedVisibility(visible = isExpanded) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                            DetailKeyValue(
                                label = if (currentLang == AppLanguage.KANNADA) "ಬೀಜದ ಪ್ರಮಾಣ (ಪ್ರತಿ ಎಕರೆಗೆ):" else "Seed Rate per Acre:",
                                value = if (currentLang == AppLanguage.KANNADA) crop.seedRatePerAcreKn else crop.seedRatePerAcreEn
                            )
                            DetailKeyValue(
                                label = if (currentLang == AppLanguage.KANNADA) "ಸಾಲಿನ & ಗಿಡಗಳ ಅಂತರ:" else "Row & Plant Spacing:",
                                value = crop.spacingEn
                            )
                            DetailKeyValue(
                                label = if (currentLang == AppLanguage.KANNADA) "ಸೂಕ್ತ ಮಣ್ಣಿನ ವಿಧ:" else "Suitable Soil:",
                                value = if (currentLang == AppLanguage.KANNADA) crop.soilTypeKn else crop.soilTypeEn
                            )

                            // Pro Tip Box
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = AmberLight.copy(alpha = 0.15f),
                                border = BorderStroke(1.dp, AmberSecondary.copy(alpha = 0.5f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.HelpOutline,
                                        contentDescription = "Tip",
                                        tint = Color(0xFFB45309),
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Text(
                                        text = if (currentLang == AppLanguage.KANNADA) crop.keyTipsKn else crop.keyTipsEn,
                                        fontSize = 12.sp,
                                        color = Color(0xFF78350F),
                                        lineHeight = 16.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CalendarQuickMetric(
    icon: ImageVector,
    title: String,
    value: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = ForestGreenPrimary,
            modifier = Modifier.size(16.dp)
        )
        Column {
            Text(
                text = title,
                fontSize = 10.5.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun DetailKeyValue(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            fontSize = 12.5.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

// -------------------------------------------------------------
// SUB-TAB 1: FARM COST & PROFIT ESTIMATOR (OFFLINE CALCULATOR)
// -------------------------------------------------------------
@Composable
private fun FarmCostCalculatorSection(currentLang: AppLanguage) {
    var acresText by remember { mutableStateOf("2.0") }
    var seedCostText by remember { mutableStateOf("1800") }
    var fertilizerCostText by remember { mutableStateOf("4500") }
    var laborCostText by remember { mutableStateOf("9000") }
    var irrigationCostText by remember { mutableStateOf("2000") }
    var expectedYieldPerAcreText by remember { mutableStateOf("14") } // in Quintals
    var expectedPricePerQuintalText by remember { mutableStateOf("2800") } // in Rupees

    val acres = acresText.toDoubleOrNull() ?: 1.0
    val seedCost = seedCostText.toDoubleOrNull() ?: 0.0
    val fertilizerCost = fertilizerCostText.toDoubleOrNull() ?: 0.0
    val laborCost = laborCostText.toDoubleOrNull() ?: 0.0
    val irrigationCost = irrigationCostText.toDoubleOrNull() ?: 0.0

    val yieldPerAcre = expectedYieldPerAcreText.toDoubleOrNull() ?: 0.0
    val pricePerQuintal = expectedPricePerQuintalText.toDoubleOrNull() ?: 0.0

    // Calculations
    val costPerAcre = seedCost + fertilizerCost + laborCost + irrigationCost
    val totalCost = costPerAcre * acres
    val totalYieldQuintals = yieldPerAcre * acres
    val totalGrossRevenue = totalYieldQuintals * pricePerQuintal
    val netProfit = totalGrossRevenue - totalCost
    val profitPerAcre = if (acres > 0) netProfit / acres else 0.0
    val costPerQuintal = if (totalYieldQuintals > 0) totalCost / totalYieldQuintals else 0.0
    val roiPercentage = if (totalCost > 0) (netProfit / totalCost) * 100 else 0.0

    val currencyFormat = remember { NumberFormat.getCurrencyInstance(Locale("en", "IN")) }

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        // Quick Presets
        Text(
            text = when (currentLang) {
                AppLanguage.KANNADA -> "ತ್ವರಿತ ಬೆಳೆ ಮಾದರಿ ಆರಿಸಿ:"
                AppLanguage.HINDI -> "त्वरित फसल मॉडल चुनें:"
                AppLanguage.ENGLISH -> "Quick Crop Presets:"
            },
            fontWeight = FontWeight.Bold,
            fontSize = 13.5.sp
        )

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            val presets = listOf(
                Triple("Ragi (ರಾಗಿ)", Pair(1800, 4200), Pair(14.0, 2900.0)),
                Triple("Paddy (ಭತ್ತ)", Pair(2400, 6500), Pair(26.0, 2300.0)),
                Triple("Maize (ಮೆಕ್ಕೆಜೋಳ)", Pair(2800, 5800), Pair(28.0, 2150.0)),
                Triple("Tur Dal (ತೊಗರಿ)", Pair(1600, 3800), Pair(8.5, 7500.0)),
                Triple("Tomato (ಟೊಮೆಟೊ)", Pair(4500, 12000), Pair(120.0, 1400.0)),
                Triple("Cotton (ಹತ್ತಿ)", Pair(3200, 7500), Pair(12.0, 6800.0))
            )

            items(presets) { (title, costs, returns) ->
                OutlinedButton(
                    onClick = {
                        seedCostText = costs.first.toString()
                        fertilizerCostText = costs.second.toString()
                        expectedYieldPerAcreText = returns.first.toString()
                        expectedPricePerQuintalText = returns.second.toInt().toString()
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    border = BorderStroke(1.dp, ForestGreenPrimary)
                ) {
                    Text(title, fontSize = 12.sp, color = ForestGreenPrimary, fontWeight = FontWeight.SemiBold)
                }
            }
        }

        // Calculation Inputs
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = when (currentLang) {
                        AppLanguage.KANNADA -> "೧. ಕೃಷಿ ವೆಚ್ಚ ನಮೂದಿಸಿ (ಪ್ರತಿ ಎಕರೆಗೆ ₹)"
                        AppLanguage.HINDI -> "१. लागत इनपुट (प्रति एकड़ ₹)"
                        AppLanguage.ENGLISH -> "1. Cultivation Costs (per Acre in ₹)"
                    },
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = ForestGreenPrimary
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = acresText,
                        onValueChange = { acresText = it },
                        label = { Text("Land (Acres)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("input_calc_acres"),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = seedCostText,
                        onValueChange = { seedCostText = it },
                        label = { Text("Seed Cost (₹)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("input_calc_seed"),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = fertilizerCostText,
                        onValueChange = { fertilizerCostText = it },
                        label = { Text("Fertilizer/Manure (₹)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("input_calc_fertilizer"),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = laborCostText,
                        onValueChange = { laborCostText = it },
                        label = { Text("Labor/Tractor (₹)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("input_calc_labor"),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )
                }

                OutlinedTextField(
                    value = irrigationCostText,
                    onValueChange = { irrigationCostText = it },
                    label = { Text("Irrigation, Power & Protection (₹)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_calc_irrigation"),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true
                )

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                Text(
                    text = when (currentLang) {
                        AppLanguage.KANNADA -> "೨. ಇಳುವರಿ & ಮಂಡಿ ಬೆಲೆ ನಮೂದಿಸಿ"
                        AppLanguage.HINDI -> "२. उपज और मंडी भाव दर्ज करें"
                        AppLanguage.ENGLISH -> "2. Expected Harvest & Mandi Price"
                    },
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = ForestGreenPrimary
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = expectedYieldPerAcreText,
                        onValueChange = { expectedYieldPerAcreText = it },
                        label = { Text("Yield (Qtl/Acre)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("input_calc_yield"),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = expectedPricePerQuintalText,
                        onValueChange = { expectedPricePerQuintalText = it },
                        label = { Text("Mandi Price (₹/Qtl)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("input_calc_price"),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )
                }
            }
        }

        // Live Calculated Result Card
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (netProfit >= 0) ForestGreenDark else Color(0xFF7F1D1D)
            ),
            border = BorderStroke(
                1.5.dp,
                if (netProfit >= 0) AmberSecondary else Color(0xFFFCA5A5)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("calc_result_card")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = when (currentLang) {
                                AppLanguage.KANNADA -> "ನಿವ್ವಳ ಲಾಭ (ನೆಟ್ ಪ್ರಾಫಿಟ್)"
                                AppLanguage.HINDI -> "शुद्ध लाभ (मुनाफा)"
                                AppLanguage.ENGLISH -> "Estimated Net Profit"
                            },
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 13.sp
                        )
                        Text(
                            text = currencyFormat.format(netProfit),
                            color = if (netProfit >= 0) AmberLight else Color(0xFFFCA5A5),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = "ROI: ${String.format(Locale.US, "%.1f", roiPercentage)}%",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }

                HorizontalDivider(color = Color.White.copy(alpha = 0.2f))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    SummaryCol(
                        label = if (currentLang == AppLanguage.KANNADA) "ಒಟ್ಟು ವೆಚ್ಚ" else "Total Cost",
                        value = currencyFormat.format(totalCost)
                    )
                    SummaryCol(
                        label = if (currentLang == AppLanguage.KANNADA) "ಒಟ್ಟು ಆದಾಯ" else "Gross Revenue",
                        value = currencyFormat.format(totalGrossRevenue)
                    )
                    SummaryCol(
                        label = if (currentLang == AppLanguage.KANNADA) "ಲಾಭ/ಎಕರೆಗೆ" else "Profit/Acre",
                        value = currencyFormat.format(profitPerAcre)
                    )
                }

                Text(
                    text = if (currentLang == AppLanguage.KANNADA)
                        "ಪ್ರತಿ ಕ್ವಿಂಟಾಲ್‌ಗೆ ತಗಲುವ ಉತ್ಪಾದನಾ ವೆಚ್ಚ: ${currencyFormat.format(costPerQuintal)}"
                    else
                        "Cost of Production per Quintal: ${currencyFormat.format(costPerQuintal)}",
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun SummaryCol(label: String, value: String) {
    Column {
        Text(text = label, color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
        Text(text = value, color = Color.White, fontSize = 13.5.sp, fontWeight = FontWeight.Bold)
    }
}

// -------------------------------------------------------------
// SUB-TAB 2: GOVT SCHEMES & SUBSIDIES (OFFLINE DIRECTORY)
// -------------------------------------------------------------
@Composable
private fun GovtSchemesSection(currentLang: AppLanguage) {
    val context = LocalContext.current
    val schemes = remember { getKarnatakaGovtSchemes() }
    var expandedSchemeId by remember { mutableStateOf<String?>("pm_kisan") }

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        // Direct Toll-Free Helpline Strip
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = RoyalGoldContainer),
            border = BorderStroke(1.dp, SovereignGold)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Phone,
                        contentDescription = "Help",
                        tint = ForestGreenDark,
                        modifier = Modifier.size(24.dp)
                    )
                    Column {
                        Text(
                            text = when (currentLang) {
                                AppLanguage.KANNADA -> "ಕಿಸಾನ್ ಸಹಾಯವಾಣಿ (ಉಚಿತ ಕರೆ)"
                                AppLanguage.HINDI -> "किसान कॉल सेंटर (टोल-फ्री)"
                                AppLanguage.ENGLISH -> "Kisan Call Center (Toll-Free)"
                            },
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.5.sp,
                            color = ForestGreenDark
                        )
                        Text(
                            text = "1800-180-1551 (6 AM - 10 PM)",
                            fontSize = 12.sp,
                            color = ForestGreenDark.copy(alpha = 0.8f)
                        )
                    }
                }

                Button(
                    onClick = {
                        val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:18001801551"))
                        context.startActivity(dialIntent)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ForestGreenDark),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("dial_kisan_call_center_btn")
                ) {
                    Text("Call", fontSize = 12.5.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Schemes List
        schemes.forEach { scheme ->
            val isExpanded = expandedSchemeId == scheme.id
            val title = when (currentLang) {
                AppLanguage.KANNADA -> scheme.titleKn
                AppLanguage.HINDI -> scheme.titleHi
                AppLanguage.ENGLISH -> scheme.titleEn
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        expandedSchemeId = if (isExpanded) null else scheme.id
                    }
                    .testTag("scheme_card_${scheme.id}"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(
                    if (isExpanded) 1.5.dp else 1.dp,
                    if (isExpanded) ForestGreenPrimary else MaterialTheme.colorScheme.outlineVariant
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(AmberLight.copy(alpha = 0.2f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AccountBalance,
                                    contentDescription = "Scheme",
                                    tint = ForestGreenPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = title,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = if (currentLang == AppLanguage.KANNADA) scheme.benefitKn else scheme.benefitEn,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = ForestGreenPrimary
                                )
                            }
                        }

                        IconButton(
                            onClick = { expandedSchemeId = if (isExpanded) null else scheme.id },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = "Toggle",
                                tint = ForestGreenPrimary
                            )
                        }
                    }

                    AnimatedVisibility(visible = isExpanded) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                            DetailKeyValue(
                                label = if (currentLang == AppLanguage.KANNADA) "ಅರ್ಹತೆ:" else "Eligibility:",
                                value = if (currentLang == AppLanguage.KANNADA) scheme.eligibilityKn else scheme.eligibilityEn
                            )

                            DetailKeyValue(
                                label = if (currentLang == AppLanguage.KANNADA) "ಅಗತ್ಯ ದಾಖಲೆಗಳು:" else "Required Documents:",
                                value = if (currentLang == AppLanguage.KANNADA) scheme.documentsKn else scheme.documentsEn
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Button(
                                    onClick = {
                                        val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${scheme.helpline}"))
                                        context.startActivity(dialIntent)
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Helpline", fontSize = 12.sp)
                                }

                                OutlinedButton(
                                    onClick = {
                                        try {
                                            val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse(scheme.officialLink))
                                            context.startActivity(webIntent)
                                        } catch (e: Exception) {
                                            Toast.makeText(context, "Could not open browser", Toast.LENGTH_SHORT).show()
                                        }
                                    },
                                    shape = RoundedCornerShape(10.dp),
                                    border = BorderStroke(1.dp, ForestGreenPrimary),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.OpenInNew, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Portal", fontSize = 12.sp, color = ForestGreenPrimary)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// SUB-TAB 3: GENUINE GOOGLE MAPS & AGRI CENTERS LOCATOR
// (Runs 100% reliably with Android Intent to official Google Maps)
// -------------------------------------------------------------
@Composable
private fun GoogleMapsLocatorSection(
    currentLang: AppLanguage,
    currentDistrict: String,
    exactLocationLabel: String?,
    viewModel: RaithaDrishtiViewModel
) {
    val context = LocalContext.current
    var isDetecting by remember { mutableStateOf(false) }

    fun launchRealGoogleMaps(query: String) {
        try {
            // Intent to open directly in Google Maps application or browser fallback
            val gmmIntentUri = Uri.parse("geo:0,0?q=" + Uri.encode(query))
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri).apply {
                setPackage("com.google.android.apps.maps")
            }
            if (mapIntent.resolveActivity(context.packageManager) != null) {
                context.startActivity(mapIntent)
            } else {
                val browserUri = Uri.parse("https://www.google.com/maps/search/?api=1&query=" + Uri.encode(query))
                context.startActivity(Intent(Intent.ACTION_VIEW, browserUri))
            }
        } catch (e: Exception) {
            // fallback
            try {
                val webUri = Uri.parse("https://www.google.com/maps/search/?api=1&query=" + Uri.encode(query))
                context.startActivity(Intent(Intent.ACTION_VIEW, webUri))
            } catch (ex: Exception) {
                Toast.makeText(context, "Please install Google Maps or Browser", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        // Live GPS / Location Bar
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.2.dp, ForestGreenPrimary)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .background(ForestGreenLight.copy(alpha = 0.2f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Location",
                            tint = ForestGreenPrimary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Column {
                        Text(
                            text = when (currentLang) {
                                AppLanguage.KANNADA -> "ಪ್ರಸ್ತುತ ಸ್ಥಳ (ಕರ್ನಾಟಕ)"
                                AppLanguage.HINDI -> "वर्तमान स्थान (कर्नाटक)"
                                AppLanguage.ENGLISH -> "Current Location"
                            },
                            fontSize = 11.5.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = exactLocationLabel ?: currentDistrict,
                            fontSize = 14.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Button(
                    onClick = {
                        isDetecting = true
                        viewModel.detectLiveLocation(context) { success, msg ->
                            isDetecting = false
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("detect_gps_maps_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.MyLocation,
                        contentDescription = "GPS",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (isDetecting) "..." else "Live GPS", fontSize = 12.sp)
                }
            }
        }

        Text(
            text = when (currentLang) {
                AppLanguage.KANNADA -> "ಗೂಗಲ್ ಮ್ಯಾಪ್ಸ್‌ನಲ್ಲಿ ನೇರವಾಗಿ ಹುಡುಕಿ (೧-ಕ್ಲಿಕ್):"
                AppLanguage.HINDI -> "गूगल मैप्स में सीधे खोजें (१-क्लिक):"
                AppLanguage.ENGLISH -> "Direct 1-Click Launch in Google Maps:"
            },
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )

        val destinationTargets = listOf(
            Triple(
                if (currentLang == AppLanguage.KANNADA) "ಹತ್ತಿರದ ಎಪಿಎಂಸಿ ಮಾರುಕಟ್ಟೆ" else "Nearest APMC Mandi",
                "APMC Market near $currentDistrict Karnataka",
                Icons.Default.Storefront
            ),
            Triple(
                if (currentLang == AppLanguage.KANNADA) "ರೈತ ಸಂಪರ್ಕ ಕೇಂದ್ರ (RSK)" else "Raitha Samparka Kendra",
                "Raitha Samparka Kendra near $currentDistrict Karnataka",
                Icons.Default.AccountBalance
            ),
            Triple(
                if (currentLang == AppLanguage.KANNADA) "ಮಣ್ಣು ಪರೀಕ್ಷಾ ಕೇಂದ್ರ" else "Soil Testing Laboratory",
                "Soil Testing Laboratory near $currentDistrict Karnataka",
                Icons.Default.Spa
            ),
            Triple(
                if (currentLang == AppLanguage.KANNADA) "ಗೊಬ್ಬರ & ಕೀಟನಾಶಕ ಅಂಗಡಿ" else "Fertilizer & Seed Store",
                "Fertilizer and Pesticide Store near $currentDistrict Karnataka",
                Icons.Default.Grass
            )
        )

        destinationTargets.forEach { (label, query, icon) ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { launchRealGoogleMaps(query) }
                    .testTag("map_target_${label.lowercase().replace(" ", "_")}"),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(RoyalGoldContainer, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = ForestGreenDark,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Column {
                            Text(
                                text = label,
                                fontSize = 14.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Open in Google Maps",
                                fontSize = 11.5.sp,
                                color = ForestGreenPrimary
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.Default.OpenInNew,
                        contentDescription = "Open",
                        tint = ForestGreenPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------
// CATALOG DATA
// -------------------------------------------------------------
private fun getKarnatakaCropCatalog(): List<CropCalendarEntry> = listOf(
    CropCalendarEntry(
        id = "ragi",
        nameEn = "Finger Millet (Ragi)",
        nameKn = "ರಾಗಿ",
        nameHi = "रागी (मंडुआ)",
        season = "Kharif",
        sowingPeriodEn = "July - August",
        sowingPeriodKn = "ಜುಲೈ - ಆಗಸ್ಟ್",
        harvestPeriodEn = "November - December",
        harvestPeriodKn = "ನವೆಂಬರ್ - ಡಿಸೆಂಬರ್",
        seedRatePerAcreEn = "4 - 5 kg",
        seedRatePerAcreKn = "೪ - ೫ ಕೆ.ಜಿ",
        spacingEn = "30 cm x 10 cm",
        durationDays = "105 - 120 days",
        avgYieldPerAcreEn = "12 - 16 Quintals",
        avgYieldPerAcreKn = "೧೨ - ೧೬ ಕ್ವಿಂಟಾಲ್",
        soilTypeEn = "Red loamy, sandy loam",
        soilTypeKn = "ಕೆಂಪು ಮರಳು ಜೇಡಿ ಮಣ್ಣು",
        keyTipsEn = "Treat seeds with Azospirillum (200g/acre). Resistant to drought and stores for years.",
        keyTipsKn = "ಬಿತ್ತನೆಗೆ ಮುನ್ನ ಅಜೋಸ್ಪೈರಿಲಂ ಜೈವಿಕ ಗೊಬ್ಬರದಿಂದ ಬೀಜೋಪಚಾರ ಮಾಡಿ."
    ),
    CropCalendarEntry(
        id = "paddy",
        nameEn = "Paddy (Rice)",
        nameKn = "ಭತ್ತ",
        nameHi = "धान (चावल)",
        season = "Kharif",
        sowingPeriodEn = "June - July",
        sowingPeriodKn = "ಜೂನ್ - ಜುಲೈ",
        harvestPeriodEn = "October - November",
        harvestPeriodKn = "ಅಕ್ಟೋಬರ್ - ನವೆಂಬರ್",
        seedRatePerAcreEn = "20 - 25 kg (Nursery)",
        seedRatePerAcreKn = "೨೦ - ೨೫ ಕೆ.ಜಿ",
        spacingEn = "20 cm x 10 cm",
        durationDays = "120 - 145 days",
        avgYieldPerAcreEn = "25 - 32 Quintals",
        avgYieldPerAcreKn = "೨೫ - ೩೨ ಕ್ವಿಂಟಾಲ್",
        soilTypeEn = "Clay loam, alluvial",
        soilTypeKn = "ಜೇಡಿ ಮಣ್ಣು, ಮೆಕ್ಕಲು ಮಣ್ಣು",
        keyTipsEn = "Transplant seedlings at 21-25 days. Maintain 2-3 cm standing water until grain filling.",
        keyTipsKn = "೨೧-೨೫ ದಿನಗಳ ಸಸಿಗಳನ್ನು ನಾಟಿ ಮಾಡಿ. ತೆನೆ ಕಟ್ಟುವವರೆಗೆ ನೀರಿನ ಮಟ್ಟ ಕಾಪಾಡಿ."
    ),
    CropCalendarEntry(
        id = "maize",
        nameEn = "Maize (Corn)",
        nameKn = "ಮೆಕ್ಕೆಜೋಳ",
        nameHi = "मक्का",
        season = "Kharif",
        sowingPeriodEn = "June - July / Oct",
        sowingPeriodKn = "ಜೂನ್ - ಜುಲೈ / ಅಕ್ಟೋಬರ್",
        harvestPeriodEn = "September - October",
        harvestPeriodKn = "ಸೆಪ್ಟೆಂಬರ್ - ಅಕ್ಟೋಬರ್",
        seedRatePerAcreEn = "7 - 8 kg",
        seedRatePerAcreKn = "೭ - ೮ ಕೆ.ಜಿ",
        spacingEn = "60 cm x 20 cm",
        durationDays = "95 - 110 days",
        avgYieldPerAcreEn = "25 - 35 Quintals",
        avgYieldPerAcreKn = "೨೫ - ೩೫ ಕ್ವಿಂಟಾಲ್",
        soilTypeEn = "Deep well-drained loamy",
        soilTypeKn = "ಆಳವಾದ ಮರಳು ಮಿಶ್ರಿತ ಕಪ್ಪು ಮಣ್ಣು",
        keyTipsEn = "Ensure soil does not get waterlogged. Monitor closely for Fall Armyworm at 15-30 days.",
        keyTipsKn = "ನೀರು ನಿಲ್ಲದಂತೆ ನೋಡಿಕೊಳ್ಳಿ. ಲದ್ದಿ ಹುಳು (Fall Armyworm) ಬಾಧೆ ತಡೆಯಲು ನಿಗಾವಹಿಸಿ."
    ),
    CropCalendarEntry(
        id = "tur_dal",
        nameEn = "Red Gram (Tur Dal)",
        nameKn = "ತೊಗರಿ",
        nameHi = "अरहर (तूर दाल)",
        season = "Kharif",
        sowingPeriodEn = "June - July",
        sowingPeriodKn = "ಜೂನ್ - ಜುಲೈ",
        harvestPeriodEn = "December - January",
        harvestPeriodKn = "ಡಿಸೆಂಬರ್ - ಜನವರಿ",
        seedRatePerAcreEn = "4 - 5 kg",
        seedRatePerAcreKn = "೪ - ೫ ಕೆ.ಜಿ",
        spacingEn = "90 cm x 30 cm",
        durationDays = "150 - 180 days",
        avgYieldPerAcreEn = "7 - 10 Quintals",
        avgYieldPerAcreKn = "೭ - ೧೦ ಕ್ವಿಂಟಾಲ್",
        soilTypeEn = "Medium to deep black soils",
        soilTypeKn = "ಕಪ್ಪು ಮತ್ತು ಕೆಂಪು ಗೋಡು ಮಣ್ಣು",
        keyTipsEn = "Excellent intercrop with Bajra or Groundnut. Fixes atmospheric nitrogen.",
        keyTipsKn = "ಕಡಲೆಕಾಯಿ ಅಥವಾ ಜೋಳದೊಂದಿಗೆ ಉತ್ತಮ ಮಿಶ್ರ ಬೆಳೆಯಾಗಿದೆ. ಮಣ್ಣಿನ ಫಲವತ್ತತೆ ಹೆಚ್ಚಿಸುತ್ತದೆ."
    ),
    CropCalendarEntry(
        id = "cotton",
        nameEn = "Cotton",
        nameKn = "ಹತ್ತಿ",
        nameHi = "कपास",
        season = "Kharif",
        sowingPeriodEn = "May - June",
        sowingPeriodKn = "ಮೇ - ಜೂನ್",
        harvestPeriodEn = "November - February",
        harvestPeriodKn = "ನವೆಂಬರ್ - ಫೆಬ್ರವರಿ",
        seedRatePerAcreEn = "1.5 - 2 kg (Bt hybrid)",
        seedRatePerAcreKn = "೧.೫ - ೨ ಕೆ.ಜಿ",
        spacingEn = "90 cm x 60 cm",
        durationDays = "160 - 190 days",
        avgYieldPerAcreEn = "10 - 14 Quintals",
        avgYieldPerAcreKn = "೧೦ - ೧೪ ಕ್ವಿಂಟಾಲ್",
        soilTypeEn = "Deep black cotton soil",
        soilTypeKn = "ಆಳವಾದ ಕಪ್ಪು ಹತ್ತಿ ಮಣ್ಣು",
        keyTipsEn = "Nipping (terminal bud removal) at 70-80 days improves boll setting.",
        keyTipsKn = "೭೦-೮೦ ದಿನಗಳಲ್ಲಿ ತುದಿಯನ್ನು ಚಿವುಟುವುದರಿಂದ ಕಾಯಿಗಳ ಸಂಖ್ಯೆ ಹೆಚ್ಚುತ್ತದೆ."
    ),
    CropCalendarEntry(
        id = "groundnut",
        nameEn = "Groundnut",
        nameKn = "ಕಡಲೆಕಾಯಿ",
        nameHi = "मूंगफली",
        season = "Kharif",
        sowingPeriodEn = "June - July / Dec",
        sowingPeriodKn = "ಜೂನ್ - ಜುಲೈ / ಡಿಸೆಂಬರ್",
        harvestPeriodEn = "October - November",
        harvestPeriodKn = "ಅಕ್ಟೋಬರ್ - ನವೆಂಬರ್",
        seedRatePerAcreEn = "40 - 50 kg kernels",
        seedRatePerAcreKn = "೪೦ - ೫೦ ಕೆ.ಜಿ ಬೀಜ",
        spacingEn = "30 cm x 10 cm",
        durationDays = "105 - 120 days",
        avgYieldPerAcreEn = "10 - 15 Quintals",
        avgYieldPerAcreKn = "೧೦ - ೧೫ ಕ್ವಿಂಟಾಲ್",
        soilTypeEn = "Sandy loam, loose soil",
        soilTypeKn = "ಸಡಿಲವಾದ ಕೆಂಪು ಮರಳು ಮಣ್ಣು",
        keyTipsEn = "Apply Gypsum at 200 kg/acre during flowering/pegging for bold pods.",
        keyTipsKn = "ಹೂವಾಡುವ ಸಮಯದಲ್ಲಿ ಎಕರೆಗೆ ೨೦೦ ಕೆ.ಜಿ ಜಿಪ್ಸಂ ಹಾಕುವುದರಿಂದ ಕಾಯಿ ಗಟ್ಟಿಯಾಗುತ್ತದೆ."
    ),
    CropCalendarEntry(
        id = "bengal_gram",
        nameEn = "Bengal Gram (Chana)",
        nameKn = "ಕಡಲೆ",
        nameHi = "चना",
        season = "Rabi",
        sowingPeriodEn = "October - November",
        sowingPeriodKn = "ಅಕ್ಟೋಬರ್ - ನವೆಂಬರ್",
        harvestPeriodEn = "January - February",
        harvestPeriodKn = "ಜನವರಿ - ಫೆಬ್ರವರಿ",
        seedRatePerAcreEn = "25 - 30 kg",
        seedRatePerAcreKn = "೨೫ - ೩೦ ಕೆ.ಜಿ",
        spacingEn = "30 cm x 10 cm",
        durationDays = "90 - 105 days",
        avgYieldPerAcreEn = "6 - 9 Quintals",
        avgYieldPerAcreKn = "೬ - ೯ ಕ್ವಿಂಟಾಲ್",
        soilTypeEn = "Residual moisture black soil",
        soilTypeKn = "ತೇವಾಂಶವುಳ್ಳ ಕಪ್ಪು ಮಣ್ಣು",
        keyTipsEn = "Requires very low water. Nip tender shoots at 30 days to boost branching.",
        keyTipsKn = "೩೦ ದಿನಗಳ ಬೆಳೆಗೆ ತುದಿಯನ್ನು ಚಿವುಟುವುದರಿಂದ ಅಧಿಕ ಕವಲುಗಳು ಬರುತ್ತವೆ."
    ),
    CropCalendarEntry(
        id = "sugarcane",
        nameEn = "Sugarcane",
        nameKn = "ಕಬ್ಬು",
        nameHi = "गन्ना",
        season = "All",
        sowingPeriodEn = "January - March / July",
        sowingPeriodKn = "ಜನವರಿ - ಮಾರ್ಚ್ / ಜುಲೈ",
        harvestPeriodEn = "11 - 12 Months later",
        harvestPeriodKn = "೧೧ - ೧೨ ತಿಂಗಳುಗಳ ನಂತರ",
        seedRatePerAcreEn = "10,000 - 12,000 two-bud setts",
        seedRatePerAcreKn = "೧೦,೦೦೦ - ೧೨,೦೦೦ ಕಣ್ಣುಗಳು",
        spacingEn = "120 cm x 30 cm",
        durationDays = "330 - 365 days",
        avgYieldPerAcreEn = "40 - 60 Tonnes",
        avgYieldPerAcreKn = "೪೦ - ೬೦ ಟನ್",
        soilTypeEn = "Fertile, well drained loam",
        soilTypeKn = "ಫಲವತ್ತಾದ ಗೋಡು ಮಣ್ಣು",
        keyTipsEn = "Drip irrigation with fertigation saves 40% water and boosts cane tonnage.",
        keyTipsKn = "ಹನಿ ನೀರಾವರಿ ಪದ್ಧತಿಯಿಂದ ೪೦% ನೀರು ಉಳಿತಾಯವಾಗುವುದಲ್ಲದೆ ಅಧಿಕ ಇಳುವರಿ ಬರುತ್ತದೆ."
    )
)

private fun getKarnatakaGovtSchemes(): List<GovtScheme> = listOf(
    GovtScheme(
        id = "pm_kisan",
        titleEn = "PM-Kisan Samman Nidhi",
        titleKn = "ಪಿಎಂ-ಕಿಸಾನ್ ಸಮ್ಮಾನ್ ನಿಧಿ",
        titleHi = "पीएम-किसान सम्मान निधि",
        benefitEn = "₹6,000 per year directly to bank account in 3 installments of ₹2,000.",
        benefitKn = "ವರ್ಷಕ್ಕೆ ₹೬,೦೦೦ ನೇರವಾಗಿ ಬ್ಯಾಂಕ್ ಖಾತೆಗೆ (೩ ಕಂತುಗಳಲ್ಲಿ ₹೨,೦೦೦).",
        eligibilityEn = "All landholder farmer families having cultivable land in their name.",
        eligibilityKn = "ತಮ್ಮ ಹೆಸರಿನಲ್ಲಿ ಸಾಗುವಳಿ ಭೂಮಿ ಹೊಂದಿರುವ ಎಲ್ಲಾ ರೈತ ಕುಟುಂಬಗಳು.",
        documentsEn = "Aadhaar Card, RTC/Pahani, Bank Passbook linked with NPCI/Aadhaar.",
        documentsKn = "ಆಧಾರ್ ಕಾರ್ಡ್, ಪಹಣಿ (RTC), ಆಧಾರ್ ಜೋಡಣೆಯಾದ ಬ್ಯಾಂಕ್ ಪಾಸ್‌ಬುಕ್.",
        officialLink = "https://pmkisan.gov.in",
        helpline = "155261"
    ),
    GovtScheme(
        id = "raitha_siri",
        titleEn = "Karnataka Raitha Siri Scheme",
        titleKn = "ಕರ್ನಾಟಕ ರೈತ ಸಿರಿ ಯೋಜನೆ",
        titleHi = "कर्नाटक रैथा सिरी योजना",
        benefitEn = "Financial assistance of ₹10,000 per hectare for minor millet cultivation.",
        benefitKn = "ಸಿರಿಧಾನ್ಯ (ರಾಗಿ, ನವಣೆ, ಕೊರಲೆ, ಸಾಮೆ) ಬೆಳೆಯಲು ಹೆಕ್ಟೇರ್‌ಗೆ ₹೧೦,೦೦೦ ಪ್ರೋತ್ಸಾಹಧನ.",
        eligibilityEn = "Karnataka farmers cultivating Siridhanya / millets registered on FRUITS portal.",
        eligibilityKn = "ಫ್ರೂಟ್ಸ್ (FRUITS) ಪೋರ್ಟಲ್‌ನಲ್ಲಿ ನೋಂದಾಯಿತ ಸಿರಿಧಾನ್ಯ ಬೆಳೆಯುವ ರೈತರು.",
        documentsEn = "FID Number, RTC showing millet crop survey entry, Bank Details.",
        documentsKn = "ಎಫ್‌ಐಡಿ (FID) ಸಂಖ್ಯೆ, ಬೆಳೆ ದರ್ಶಕ್ ಸಮೀಕ್ಷೆ ಪಹಣಿ, ಬ್ಯಾಂಕ್ ವಿವರ.",
        officialLink = "https://fruits.karnataka.gov.in",
        helpline = "18004253553"
    ),
    GovtScheme(
        id = "krishi_bhagya",
        titleEn = "Krishi Bhagya Scheme",
        titleKn = "ಕೃಷಿ ಭಾಗ್ಯ ಯೋಜನೆ (ಕೃಷಿ ಹೊಂಡ & ನೆಟ್‌ಗಳು)",
        titleHi = "कृषि भाग्य योजना (कृषि तालाब)",
        benefitEn = "80% to 90% subsidy for constructing Farm Ponds (Krishi Honda) & polythene lining.",
        benefitKn = "ಕೃಷಿ ಹೊಂಡ ನಿರ್ಮಾಣ, ಪಾಲಿಥೀನ್ ಹೊದಿಕೆ ಮತ್ತು ಡೀಸೆಲ್ ಪಂಪ್‌ಗೆ ೮೦% - ೯೦% ಸಬ್ಸಿಡಿ.",
        eligibilityEn = "Dryland farmers in rainfed districts of Karnataka.",
        eligibilityKn = "ಮಳೆ ಆಶ್ರಿತ ಒಣಭೂಮಿ ಹೊಂದಿರುವ ಕರ್ನಾಟಕದ ರೈತರು.",
        documentsEn = "RTC, Aadhaar, Caste certificate (for SC/ST higher subsidy), FID.",
        documentsKn = "ಪಹಣಿ, ಆಧಾರ್, ಜಾತಿ ಪ್ರಮಾಣಪತ್ರ (ಪ.ಜಾ/ಪ.ಪಂ ರೈತರಿಗೆ ಗರಿಷ್ಠ ಸಬ್ಸಿಡಿ), FID.",
        officialLink = "https://raitamitra.karnataka.gov.in",
        helpline = "18004253553"
    ),
    GovtScheme(
        id = "ganga_kalyana",
        titleEn = "Ganga Kalyana Borewell Scheme",
        titleKn = "ಗಂಗಾ ಕಲ್ಯಾಣ ಯೋಜನೆ (ಉಚಿತ ಕೊಳವೆಬಾವಿ)",
        titleHi = "गंगा कल्याण योजना",
        benefitEn = "Free borewell drilling, submersible pump and electricity energization.",
        benefitKn = "ಉಚಿತ ಕೊಳವೆಬಾವಿ ಕೊರೆಯುವಿಕೆ, ಪಂಪ್ ಮತ್ತು ವಿದ್ಯುತ್ ಸಂಪರ್ಕ ಸೌಲಭ್ಯ.",
        eligibilityEn = "Small & marginal farmers belonging to SC, ST, OBC and Minorities.",
        eligibilityKn = "ಪ.ಜಾತಿ, ಪ.ಪಂಗಡ, ಹಿಂದುಳಿದ ವರ್ಗ ಹಾಗೂ ಅಲ್ಪಸಂಖ್ಯಾತ ಸಣ್ಣ/ಅತಿ ಸಣ್ಣ ರೈತರು.",
        documentsEn = "Caste & Income certificate, Pahani (RTC), Aadhaar, Small Farmer Certificate.",
        documentsKn = "ಜಾತಿ ಮತ್ತು ಆದಾಯ ಪ್ರಮಾಣಪತ್ರ, ಪಹಣಿ, ಆಧಾರ್, ಸಣ್ಣ ರೈತ ಪ್ರಮಾಣಪತ್ರ.",
        officialLink = "https://kswdc.karnataka.gov.in",
        helpline = "08022864303"
    ),
    GovtScheme(
        id = "pmfby",
        titleEn = "PM Fasal Bima Yojana (Crop Insurance)",
        titleKn = "ಪ್ರಧಾನ ಮಂತ್ರಿ ಫಸಲ್ ಬಿಮಾ ಯೋಜನೆ",
        titleHi = "प्रधानमंत्री फसल बीमा योजना",
        benefitEn = "Comprehensive crop insurance against drought, flood, pests at only 1.5% - 2% premium.",
        benefitKn = "ಬರ, ಅಕಾಲಿಕ ಮಳೆ ಮತ್ತು ಕೀಟ ಬಾಧೆಗೆ ಕೇವಲ ೧.೫% - ೨% ಪ್ರೀಮಿಯಂ ದರದಲ್ಲಿ ಬೆಳೆ ವಿಮೆ ಪರಿಹಾರ.",
        eligibilityEn = "All farmers cultivating notified crops in notified panchayats.",
        eligibilityKn = "ಅಧಿಸೂಚಿತ ಗ್ರಾಮ ಪಂಚಾಯತಿಗಳಲ್ಲಿ ಅಧಿಸೂಚಿತ ಬೆಳೆ ಬೆಳೆಯುವ ಎಲ್ಲಾ ರೈತರು.",
        documentsEn = "Land RTC with crop sowing certificate, Aadhaar, Bank passbook.",
        documentsKn = "ಬಿತ್ತನೆ ದೃಢೀಕರಣ ಪಹಣಿ, ಆಧಾರ್ ಕಾರ್ಡ್, ಬ್ಯಾಂಕ್ ಪಾಸ್‌ಬುಕ್.",
        officialLink = "https://pmfby.gov.in",
        helpline = "14447"
    ),
    GovtScheme(
        id = "pm_kusum",
        titleEn = "PM-KUSUM Solar Agri Pumps",
        titleKn = "ಕುಸುಮ್ ಸೋಲಾರ್ ಪಂಪ್ ಯೋಜನೆ",
        titleHi = "पीएम-कुसुम सौर कृषि पंप योजना",
        benefitEn = "Up to 70% subsidy for off-grid 3HP to 7.5HP solar agricultural water pumps.",
        benefitKn = "೩ ರಿಂದ ೭.೫ ಹೆಚ್.ಪಿ ಸಾಮರ್ಥ್ಯದ ಸೋಲಾರ್ ಕೃಷಿ ಪಂಪ್‌ಸೆಟ್‌ಗಳಿಗೆ ೭೦% ವರೆಗೆ ಸಬ್ಸಿಡಿ.",
        eligibilityEn = "Farmers lacking regular grid power or seeking day-time solar irrigation.",
        eligibilityKn = "ದಿನದ ವೇಳೆಯಲ್ಲಿ ನಿರಂತರ ಸೌರ ನೀರಾವರಿ ಬಯಸುವ ಎಲ್ಲಾ ರೈತರು.",
        documentsEn = "RTC, Water source certificate, Aadhaar, Bank Details.",
        documentsKn = "ಪಹಣಿ, ನೀರಿನ ಮೂಲ ಪ್ರಮಾಣಪತ್ರ, ಆಧಾರ್ ಕಾರ್ಡ್.",
        officialLink = "https://kredl.karnataka.gov.in",
        helpline = "08022208109"
    )
)
