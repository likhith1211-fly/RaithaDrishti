package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppLanguage
import com.example.ui.components.VoiceAssistantDialog
import com.example.ui.screens.AgriMapScreen
import com.example.ui.screens.CropDoctorScreen
import com.example.ui.screens.CropNutritionScreen
import com.example.ui.screens.HistoryProfileScreen
import com.example.ui.screens.MarketArbitrageScreen
import com.example.ui.screens.WeatherAdvisoryScreen
import com.example.ui.theme.ForestGreenDark
import com.example.ui.theme.ForestGreenPrimary
import com.example.ui.theme.RaithaDrishtiTheme
import com.example.ui.viewmodel.RaithaDrishtiViewModel

enum class NavigationTab(
    val englishLabel: String,
    val kannadaLabel: String,
    val hindiLabel: String,
    val icon: ImageVector,
    val tag: String
) {
    CROP_DOCTOR("Doctor", "ರೋಗ", "जांच", Icons.Default.Agriculture, "tab_crop_doctor"),
    CROP_NUTRITION("Fertilizers", "ಗೊಬ್ಬರ", "खाद", Icons.Default.Science, "tab_crop_nutrition"),
    MARKET_ARBITRAGE("Mandi", "ಮಾರುಕಟ್ಟೆ", "मंडी", Icons.Default.Storefront, "tab_market_arbitrage"),
    AGRI_MAP("Map", "ಭೂಪಟ", "नक्शा", Icons.Default.Map, "tab_agri_map"),
    WEATHER_HUB("Weather", "ಹವಾಮಾನ", "मौसम", Icons.Default.Cloud, "tab_weather_hub"),
    HISTORY_PROFILE("Profile", "ಖಾತೆ", "खाता", Icons.Default.History, "tab_history_profile")
}

class MainActivity : ComponentActivity() {
    private val viewModel: RaithaDrishtiViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RaithaDrishtiTheme {
                RaithaDrishtiApp(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RaithaDrishtiApp(viewModel: RaithaDrishtiViewModel) {
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    val currentLang by viewModel.currentLanguage.collectAsState()
    var showLanguageMenu by remember { mutableStateOf(false) }
    var showVoiceDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                navigationIcon = {
                    // Language Switcher Button (3 languages: Kannada, Hindi, English)
                    Box {
                        Surface(
                            onClick = { showLanguageMenu = true },
                            shape = RoundedCornerShape(14.dp),
                            color = Color.White.copy(alpha = 0.15f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, com.example.ui.theme.AmberLight.copy(alpha = 0.6f)),
                            modifier = Modifier
                                .padding(start = 10.dp)
                                .testTag("language_toggle_button")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Language,
                                    contentDescription = "Change Language",
                                    tint = com.example.ui.theme.AmberLight,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = currentLang.displayName,
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.3.sp
                                )
                            }
                        }

                        DropdownMenu(
                            expanded = showLanguageMenu,
                            onDismissRequest = { showLanguageMenu = false }
                        ) {
                            AppLanguage.values().forEach { lang ->
                                DropdownMenuItem(
                                    text = {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                                        ) {
                                            Text(
                                                text = "${lang.displayName} (${lang.nativeName})",
                                                fontWeight = if (lang == currentLang) FontWeight.Bold else FontWeight.Medium,
                                                fontSize = 15.sp,
                                                color = if (lang == currentLang) ForestGreenPrimary else Color.Unspecified
                                            )
                                            if (lang == currentLang) {
                                                Icon(
                                                    imageVector = Icons.Default.Check,
                                                    contentDescription = null,
                                                    tint = ForestGreenPrimary,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }
                                        }
                                    },
                                    onClick = {
                                        viewModel.setLanguage(lang)
                                        showLanguageMenu = false
                                    }
                                )
                            }
                        }
                    }
                },
                title = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        Text(
                            text = when (currentLang) {
                                AppLanguage.KANNADA -> "ರೈತ ದೃಷ್ಟಿ • RaithaDrishti"
                                AppLanguage.HINDI -> "रैत दृष्टि • RaithaDrishti"
                                AppLanguage.ENGLISH -> "RaithaDrishti • ರೈತ ದೃಷ್ಟಿ"
                            },
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 19.sp,
                                letterSpacing = 0.3.sp
                            ),
                            color = Color.White
                        )
                        Text(
                            text = when (currentLang) {
                                AppLanguage.KANNADA -> "ಕೃಷಿ ರೋಗ ತಪಾಸಣೆ & APMC ಮಾರುಕಟ್ಟೆ"
                                AppLanguage.HINDI -> "कृषि रोग निदान और APMC मंडी भाव"
                                AppLanguage.ENGLISH -> "Royal Agri-Pathology & APMC Mandi"
                            },
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp,
                                letterSpacing = 0.35.sp
                            ),
                            color = com.example.ui.theme.AmberLight
                        )
                    }
                },
                actions = {
                    // Voice Assistant Quick Button (Royal Gold Accent)
                    IconButton(
                        onClick = { showVoiceDialog = true },
                        modifier = Modifier
                            .padding(end = 10.dp)
                            .background(com.example.ui.theme.AmberLight, CircleShape)
                            .size(42.dp)
                            .testTag("top_bar_voice_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "Voice Assistant",
                            tint = Color(0xFF1E1500),
                            modifier = Modifier.size(22.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = ForestGreenPrimary
                ),
                modifier = Modifier.testTag("app_top_bar")
            )
        },
        floatingActionButton = {
            // Prominent Voice Assistant Floating Button with Royal Gold Styling
            ExtendedFloatingActionButton(
                onClick = { showVoiceDialog = true },
                containerColor = com.example.ui.theme.AmberSecondary,
                contentColor = Color(0xFF1C1300),
                shape = RoundedCornerShape(18.dp),
                icon = {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "Voice Assistant",
                        modifier = Modifier.size(24.dp)
                    )
                },
                text = {
                    Text(
                        text = when (currentLang) {
                            AppLanguage.KANNADA -> "ಧ್ವನಿ ಸಹಾಯಕ (ಮಾತನಾಡಿ)"
                            AppLanguage.HINDI -> "बोलकर पूछें (आवाज)"
                            AppLanguage.ENGLISH -> "Voice AI (Speak)"
                        },
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        letterSpacing = 0.3.sp
                    )
                },
                modifier = Modifier.testTag("global_voice_fab")
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp,
                modifier = Modifier.testTag("bottom_nav_bar")
            ) {
                NavigationTab.values().forEachIndexed { index, tab ->
                    val isSelected = selectedTab == index
                    val tabLabel = when (currentLang) {
                        AppLanguage.KANNADA -> tab.kannadaLabel
                        AppLanguage.HINDI -> tab.hindiLabel
                        AppLanguage.ENGLISH -> tab.englishLabel
                    }

                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { selectedTab = index },
                        icon = {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = tabLabel,
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        label = {
                            Text(
                                text = tabLabel,
                                fontSize = if (isSelected) 12.sp else 11.5.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                letterSpacing = 0.25.sp,
                                maxLines = 1
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = ForestGreenPrimary,
                            selectedTextColor = ForestGreenPrimary,
                            indicatorColor = com.example.ui.theme.RoyalGoldContainer,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier.testTag(tab.tag)
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                0 -> CropDoctorScreen(
                    viewModel = viewModel,
                    onNavigateToNutrition = { selectedTab = 1 },
                    onOpenVoice = { showVoiceDialog = true }
                )
                1 -> CropNutritionScreen(viewModel = viewModel)
                2 -> MarketArbitrageScreen(viewModel = viewModel)
                3 -> AgriMapScreen(viewModel = viewModel)
                4 -> WeatherAdvisoryScreen(viewModel = viewModel)
                5 -> HistoryProfileScreen(viewModel = viewModel)
            }
        }
    }

    // Voice Assistant BottomSheet / Dialog
    if (showVoiceDialog) {
        VoiceAssistantDialog(
            viewModel = viewModel,
            onDismiss = { showVoiceDialog = false }
        )
    }
}

