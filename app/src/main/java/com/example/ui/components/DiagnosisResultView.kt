package com.example.ui.components

import android.content.Context
import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.AppLanguage
import com.example.data.model.CropDiagnosisResult
import com.example.data.model.VerifiedFertilizerItem
import com.example.ui.theme.AmberLight
import com.example.ui.theme.AmberSecondary
import com.example.ui.theme.ForestGreenDark
import com.example.ui.theme.ForestGreenPrimary
import com.example.ui.theme.LightBorder
import com.example.util.ShareManager
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DiagnosisResultView(
    result: CropDiagnosisResult,
    weatherContext: String,
    language: AppLanguage = AppLanguage.KANNADA,
    onSaveToHistory: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showPrescriptionDialog by remember { mutableStateOf(false) }

    // Land Size Dosage & Spray Mixing Calculator States
    var enteredLandValue by remember { mutableStateOf("2.0") }
    var landUnitIsAcres by remember { mutableStateOf(true) } // true for Acres, false for Guntas (40 Guntas = 1 Acre)
    var selectedFertilizerIndex by remember { mutableIntStateOf(0) }

    val verifiedFertilizers = if (result.verifiedLocalMarketFertilizers.isNotEmpty()) {
        result.verifiedLocalMarketFertilizers
    } else {
        com.example.data.remote.GeminiDiagnosisService().getStandardVerifiedFertilizersForCrop(result.cropName)
    }

    val enteredLandNum = enteredLandValue.toDoubleOrNull() ?: 1.0
    val effectiveAcres = if (landUnitIsAcres) enteredLandNum else (enteredLandNum / 40.0).coerceAtLeast(0.025)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("diagnosis_result_view"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Main Diagnosis Summary Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(18.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, com.example.ui.theme.LightBorder),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                // Severity and Confidence Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Severity Badge
                    val severityColor = result.getSeverityColor()
                    Surface(
                        color = severityColor.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(20.dp),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, severityColor.copy(alpha = 0.5f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .background(severityColor, CircleShape)
                            )
                            Text(
                                text = "${result.severity} Severity",
                                color = severityColor,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }

                    // Confidence Indicator
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "AI Confidence",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "${result.confidence}%",
                            fontWeight = FontWeight.ExtraBold,
                            color = ForestGreenPrimary,
                            style = MaterialTheme.typography.titleLarge.copy(fontSize = 20.sp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Confidence Bar
                LinearProgressIndicator(
                    progress = { result.confidence / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = ForestGreenPrimary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Primary Diagnosis Name
                Text(
                    text = result.diagnosis,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        letterSpacing = 0.25.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )

                if (result.cropName.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Crop Target: ${result.cropName}",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Environmental Summary
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, com.example.ui.theme.LightBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Summary",
                            tint = ForestGreenPrimary,
                            modifier = Modifier.size(22.dp)
                        )
                        Text(
                            text = result.summary,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontSize = 15.5.sp,
                                lineHeight = 24.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        // =====================================================================
        // SECTION 1: EXACT ISSUE & ROOT CAUSE IDENTIFIED FROM CAPTURED PHOTO
        // =====================================================================
        val exactIssueText = if (result.exactProblemIdentified.isNotBlank()) {
            result.exactProblemIdentified
        } else {
            "${result.diagnosis}. Specific foliar symptoms and pathological vectors diagnosed from the captured photo under local microclimate humidity."
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("exact_issue_identified_card"),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFE65100).copy(alpha = 0.55f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color(0xFFFFE0B2), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Psychology,
                            contentDescription = "Exact Issue",
                            tint = Color(0xFFE65100),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Column {
                        Text(
                            text = when (language) {
                                AppLanguage.KANNADA -> "ಖಚಿತ ಸಮಸ್ಯೆಯ ಗುರುತಿಸುವಿಕೆ (Exact Issue)"
                                AppLanguage.HINDI -> "पहचाना गया मुख्य रोग व कारण (Exact Issue)"
                                AppLanguage.ENGLISH -> "Exact Issue & Root Cause Identified"
                            },
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.5.sp
                            ),
                            color = Color(0xFFBF360C)
                        )
                        Text(
                            text = when (language) {
                                AppLanguage.KANNADA -> "ಫೋಟೋ ಹಾಗೂ ಎಲೆಯ ಲಕ್ಷಣಗಳಿಂದ ಪತ್ತೆಯಾದ ನಿಖರ ರೋಗ ಮತ್ತು ಕೊರತೆ"
                                AppLanguage.HINDI -> "पत्ती की फोटो और लक्षणों से पहचाना गया सटीक रोग व पोषक तत्व की कमी"
                                AppLanguage.ENGLISH -> "Root pathogen and nutritional deficiency verified from photo"
                            },
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Surface(
                    color = Color(0xFFFFF8E1),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFD54F)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text("🔬", fontSize = 18.sp)
                        Text(
                            text = exactIssueText,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                lineHeight = 23.sp
                            ),
                            color = Color(0xFF3E2723)
                        )
                    }
                }
            }
        }

        // =====================================================================
        // SECTION 2: WHAT EXACTLY TO DO (STEP-BY-STEP ACTION PROTOCOL)
        // =====================================================================
        val actionSteps = if (result.stepByStepActionPlan.isNotEmpty()) {
            result.stepByStepActionPlan
        } else if (result.immediateActions.isNotEmpty()) {
            result.immediateActions
        } else {
            listOf(
                "Day 1 (Sanitation): Rogue out heavily diseased leaves and burn away from field to stop spore spread.",
                "Day 2 (Targeted Spray): Prepare foliar spray of prescribed verified fertilizer with sticker in cool hours.",
                "Day 4 (Drainage): Check irrigation furrows; ensure zero standing water around root zones.",
                "Day 7 (Vitalizer): Apply bio-organic booster or 19:19:19 foliar spray to stimulate fresh healthy leaves."
            )
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("step_by_step_action_plan_card"),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, ForestGreenPrimary.copy(alpha = 0.5f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color(0xFFE8F5E9), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.FormatListNumbered,
                            contentDescription = "Action Protocol",
                            tint = ForestGreenPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Column {
                        Text(
                            text = when (language) {
                                AppLanguage.KANNADA -> "ರೈತರು ಮಾಡಬೇಕಾದ ಕ್ರಮಗಳು (What Exactly To Do)"
                                AppLanguage.HINDI -> "किसान क्या करें - चरणबद्ध कार्य योजना"
                                AppLanguage.ENGLISH -> "What Exactly To Do (Action Protocol)"
                            },
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.5.sp
                            ),
                            color = ForestGreenPrimary
                        )
                        Text(
                            text = when (language) {
                                AppLanguage.KANNADA -> "ಪೂರ್ಣ ಪರಿಹಾರಕ್ಕಾಗಿ ಹಂತ-ಹಂತವಾಗಿ ಪಾಲಿಸಬೇಕಾದ ಕ್ರಮಗಳು"
                                AppLanguage.HINDI -> "पूर्ण सुधार के लिए क्रमबद्ध कृषि निर्देश"
                                AppLanguage.ENGLISH -> "Chronological step-by-step treatment directives"
                            },
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    actionSteps.forEachIndexed { idx, step ->
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, com.example.ui.theme.LightBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.Top,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Surface(
                                    color = ForestGreenPrimary,
                                    shape = CircleShape,
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = "${idx + 1}",
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp
                                        )
                                    }
                                }
                                Text(
                                    text = step,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontSize = 14.5.sp,
                                        lineHeight = 22.sp,
                                        fontWeight = FontWeight.Medium
                                    ),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        }

        // =====================================================================
        // SECTION 3: CHOSEN & ENSURED FERTILIZERS (LOCAL KARNATAKA MARKET)
        // =====================================================================
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("verified_local_fertilizers_card"),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF1565C0).copy(alpha = 0.5f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color(0xFFE3F2FD), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Storefront,
                            contentDescription = "Market Fertilizers",
                            tint = Color(0xFF1565C0),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Column {
                        Text(
                            text = when (language) {
                                AppLanguage.KANNADA -> "ಸ್ಥಳೀಯ ಮಾರುಕಟ್ಟೆಯಲ್ಲಿ ಲಭ್ಯವಿರುವ ಖಚಿತ ಗೊಬ್ಬರಗಳು"
                                AppLanguage.HINDI -> "स्थानीय बाजार में उपलब्ध सुनिश्चित खाद"
                                AppLanguage.ENGLISH -> "Chosen & Ensured Local Market Fertilizers"
                            },
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.5.sp
                            ),
                            color = Color(0xFF0D47A1)
                        )
                        Text(
                            text = when (language) {
                                AppLanguage.KANNADA -> "ರೈತ ಸೇವಾ ಕೇಂದ್ರ (RSK) & APMC ಮಂಡಿಗಳಲ್ಲಿ ಲಭ್ಯವಿರುವ ಸರ್ಕಾರಿ ಮಾನ್ಯತೆಯ ಬ್ರಾಂಡ್‌ಗಳು"
                                AppLanguage.HINDI -> "कृषि सेवा केंद्र (RSK) और APMC में आसानी से मिलने वाले प्रामाणिक ब्रांड"
                                AppLanguage.ENGLISH -> "Government-approved brands available across Karnataka Raitha Seva Kendras"
                            },
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    verifiedFertilizers.forEachIndexed { index, fert ->
                        val isSelectedForMixing = selectedFertilizerIndex == index
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("verified_fertilizer_item_$index"),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelectedForMixing) Color(0xFFF0FDF4) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                            ),
                            border = androidx.compose.foundation.BorderStroke(
                                if (isSelectedForMixing) 1.5.dp else 1.dp,
                                if (isSelectedForMixing) ForestGreenPrimary else com.example.ui.theme.LightBorder
                            )
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = fert.fertilizerName,
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp
                                        ),
                                        color = if (isSelectedForMixing) ForestGreenPrimary else MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.weight(1f)
                                    )

                                    Surface(
                                        color = Color(0xFFDCFCE7),
                                        shape = RoundedCornerShape(8.dp),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF86EFAC))
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = null,
                                                tint = Color(0xFF166534),
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Text(
                                                text = "Ensured",
                                                color = Color(0xFF166534),
                                                fontSize = 11.5.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                // Availability tag
                                Surface(
                                    color = Color(0xFFFEF3C7),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "📍 ${fert.localBrandAvailability}",
                                        fontSize = 12.5.sp,
                                        color = Color(0xFF92400E),
                                        fontWeight = FontWeight.Medium,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                // Target nutrient
                                Text(
                                    text = "🎯 ${fert.targetNutrient}",
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.SemiBold
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                // Dosage standard
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Dilution: ${fert.dosagePerLiter} ${fert.dosageUnit}",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF0D47A1)
                                    )
                                    Text(
                                        text = "Acre Rate: ${fert.standardDosePerAcreKgOrL} ${if (fert.dosageUnit.startsWith("g")) "kg" else "L"}/acre",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = ForestGreenPrimary
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = "⚠️ ${fert.precaution}",
                                    fontSize = 12.sp,
                                    color = Color(0xFFC62828),
                                    lineHeight = 17.sp
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                OutlinedButton(
                                    onClick = { selectedFertilizerIndex = index },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = if (isSelectedForMixing) ForestGreenPrimary else MaterialTheme.colorScheme.onSurface
                                    ),
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        if (isSelectedForMixing) ForestGreenPrimary else MaterialTheme.colorScheme.outline
                                    )
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Calculate,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (isSelectedForMixing) {
                                            when (language) {
                                                AppLanguage.KANNADA -> "✓ ಲೆಕ್ಕಾಚಾರಕ್ಕೆ ಆಯ್ಕೆಯಾಗಿದೆ"
                                                AppLanguage.HINDI -> "✓ गणना के लिए चयनित"
                                                AppLanguage.ENGLISH -> "✓ Selected for Land Mixing"
                                            }
                                        } else {
                                            when (language) {
                                                AppLanguage.KANNADA -> "ಜಮೀನಿನ ವಿಸ್ತೀರ್ಣಕ್ಕೆ ಲೆಕ್ಕಹಾಕಿ"
                                                AppLanguage.HINDI -> "इस खाद की मात्रा का हिसाब लगाएं"
                                                AppLanguage.ENGLISH -> "Calculate Mixing for My Land"
                                            }
                                        },
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.5.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // =====================================================================
        // SECTION 4: INTERACTIVE LAND SIZE FERTILIZER & SPRAY MIXING CALCULATOR
        // =====================================================================
        val activeMixingFert = verifiedFertilizers.getOrNull(selectedFertilizerIndex) ?: verifiedFertilizers.firstOrNull()

        if (activeMixingFert != null) {
            val totalFertQuantity = activeMixingFert.standardDosePerAcreKgOrL * effectiveAcres
            val fertUnitStr = if (activeMixingFert.dosageUnit.startsWith("g")) "kg" else "Liters"
            val totalWaterLiters = (activeMixingFert.waterPerAcreLiters * effectiveAcres).toInt().coerceAtLeast(16)
            val knapsackSprayersCount = Math.max(1, Math.round(totalWaterLiters / 16.0).toInt())
            val tankDoseAmount = activeMixingFert.dosagePerLiter * 16.0
            val tankDoseUnit = if (activeMixingFert.dosageUnit.startsWith("g")) "g" else "ml"

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("land_size_mixing_calculator_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(2.dp, com.example.ui.theme.AmberSecondary),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    // Header
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .background(Color(0xFFFFF3E0), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Calculate,
                                contentDescription = "Mixing Calculator",
                                tint = Color(0xFFE65100),
                                modifier = Modifier.size(26.dp)
                            )
                        }
                        Column {
                            Text(
                                text = when (language) {
                                    AppLanguage.KANNADA -> "ಜಮೀನಿನ ವಿಸ್ತೀರ್ಣಕ್ಕೆ ಗೊಬ್ಬರ & ನೀರಿನ ಮಿಶ್ರಣ ಲೆಕ್ಕಾಚಾರ"
                                    AppLanguage.HINDI -> "जमीन के अनुसार खाद व पानी का सटीक घोल"
                                    AppLanguage.ENGLISH -> "Land Size Dosage & Spray Mixing Calculator"
                                },
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 17.5.sp
                                ),
                                color = Color(0xFFE65100)
                            )
                            Text(
                                text = activeMixingFert.fertilizerName,
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = ForestGreenPrimary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Unit Selection Toggle (Acres vs Guntas)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = when (language) {
                                AppLanguage.KANNADA -> "ಅಳತೆ ಮಾನ:"
                                AppLanguage.HINDI -> "इकाई:"
                                AppLanguage.ENGLISH -> "Land Unit:"
                            },
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.5.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        FilterChip(
                            selected = landUnitIsAcres,
                            onClick = { landUnitIsAcres = true },
                            label = { Text("Acres (ಎಕರೆ)", fontWeight = FontWeight.Bold) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = ForestGreenPrimary,
                                selectedLabelColor = Color.White
                            )
                        )

                        FilterChip(
                            selected = !landUnitIsAcres,
                            onClick = { landUnitIsAcres = false },
                            label = { Text("Guntas (ಗುಂಟೆ)", fontWeight = FontWeight.Bold) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = ForestGreenPrimary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Quick Presets
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val presets = if (landUnitIsAcres) {
                            listOf("0.5", "1.0", "2.0", "3.0", "5.0")
                        } else {
                            listOf("10", "20", "40", "80", "120")
                        }

                        presets.forEach { presetVal ->
                            val isPresetSelected = enteredLandValue == presetVal
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isPresetSelected) AmberSecondary else MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { enteredLandValue = presetVal }
                                    .testTag("preset_land_$presetVal")
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.padding(vertical = 7.dp)
                                ) {
                                    Text(
                                        text = presetVal,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = if (isPresetSelected) Color(0xFF1E1500) else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Custom input
                    OutlinedTextField(
                        value = enteredLandValue,
                        onValueChange = { enteredLandValue = it },
                        label = {
                            Text(
                                if (landUnitIsAcres) {
                                    when (language) {
                                        AppLanguage.KANNADA -> "ನಿಮ್ಮ ಜಮೀನಿನ ವಿಸ್ತೀರ್ಣ (ಎಕರೆಗಳಲ್ಲಿ)"
                                        AppLanguage.HINDI -> "जमीन का क्षेत्रफल (एकड़)"
                                        AppLanguage.ENGLISH -> "Enter Land Area (in Acres)"
                                    }
                                } else {
                                    when (language) {
                                        AppLanguage.KANNADA -> "ನಿಮ್ಮ ಜಮೀನಿನ ವಿಸ್ತೀರ್ಣ (ಗುಂಟೆಗಳಲ್ಲಿ)"
                                        AppLanguage.HINDI -> "जमीन का क्षेत्रफल (गुंठा)"
                                        AppLanguage.ENGLISH -> "Enter Land Area (in Guntas)"
                                    }
                                }
                            )
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("land_size_input_field"),
                        shape = RoundedCornerShape(12.dp),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Agriculture,
                                contentDescription = null,
                                tint = ForestGreenPrimary
                            )
                        }
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Real-time Calculated Mixing Results Card
                    Surface(
                        color = Color(0xFFF1F8F1),
                        shape = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFA5D6A7)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "🧪 " + when (language) {
                                    AppLanguage.KANNADA -> "ಲೆಕ್ಕಹಾಕಲಾದ ಮಿಶ್ರಣದ ಪ್ರಮಾಣ (%.2f ಎಕರೆಗೆ):".format(effectiveAcres)
                                    AppLanguage.HINDI -> "घोल की कुल मात्रा (%.2f एकड़ हेतु):".format(effectiveAcres)
                                    AppLanguage.ENGLISH -> "Calculated Mixing Quantity (for %.2f Acres):".format(effectiveAcres)
                                },
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 15.sp,
                                color = ForestGreenPrimary
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            // 2x2 Grid of quantities
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // Fertilizer Total
                                Surface(
                                    color = Color.White,
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1f),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFC8E6C9))
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Text(
                                            text = when (language) {
                                                AppLanguage.KANNADA -> "ಒಟ್ಟು ಗೊಬ್ಬರ"
                                                AppLanguage.HINDI -> "कुल खाद"
                                                AppLanguage.ENGLISH -> "Total Fertilizer"
                                            },
                                            fontSize = 11.5.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            text = "%.2f %s".format(totalFertQuantity, fertUnitStr),
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 17.sp,
                                            color = ForestGreenPrimary
                                        )
                                    }
                                }

                                // Water Total
                                Surface(
                                    color = Color.White,
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1f),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFC8E6C9))
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Text(
                                            text = when (language) {
                                                AppLanguage.KANNADA -> "ಒಟ್ಟು ನೀರು"
                                                AppLanguage.HINDI -> "कुल पानी"
                                                AppLanguage.ENGLISH -> "Total Water"
                                            },
                                            fontSize = 11.5.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            text = "$totalWaterLiters Liters",
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 17.sp,
                                            color = Color(0xFF1565C0)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // Knapsack Tank count
                                Surface(
                                    color = Color.White,
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1f),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFC8E6C9))
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Text(
                                            text = when (language) {
                                                AppLanguage.KANNADA -> "೧೬L ಸ್ಪ್ರೇ ಪಂಪ್"
                                                AppLanguage.HINDI -> "16L स्प्रे पंप"
                                                AppLanguage.ENGLISH -> "16L Sprayers"
                                            },
                                            fontSize = 11.5.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            text = "$knapsackSprayersCount Pumps",
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 17.sp,
                                            color = Color(0xFF6A1B9A)
                                        )
                                    }
                                }

                                // Dose per tank
                                Surface(
                                    color = Color.White,
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1f),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFC8E6C9))
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Text(
                                            text = when (language) {
                                                AppLanguage.KANNADA -> "೧ ಪಂಪ್‌ಗೆ ಪ್ರಮಾಣ"
                                                AppLanguage.HINDI -> "प्रति पंप मात्रा"
                                                AppLanguage.ENGLISH -> "Per 16L Pump"
                                            },
                                            fontSize = 11.5.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            text = "%.0f %s".format(tankDoseAmount, tankDoseUnit),
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 17.sp,
                                            color = Color(0xFFD84315)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Step-by-Step Knapsack Tank Mixing Directions
                            Text(
                                text = "💧 " + when (language) {
                                    AppLanguage.KANNADA -> "೧೬ ಲೀಟರ್ ಸ್ಪ್ರೇ ಪಂಪ್‌ನಲ್ಲಿ ಸರಿಯಾಗಿ ಮಿಶ್ರಣ ಮಾಡುವ ವಿಧಾನ:"
                                    AppLanguage.HINDI -> "16 लीटर स्प्रे पंप में घोल बनाने की सही विधि:"
                                    AppLanguage.ENGLISH -> "Exact 16L Knapsack Sprayer Tank Mixing Directions:"
                                },
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.5.sp,
                                color = Color(0xFF1B5E20)
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            val mixingStepsList = listOf(
                                "1. Take 2 Liters of clean water in a plastic bucket (ಪ್ಲಾಸ್ಟಿಕ್ ಬಕೆಟ್‌ನಲ್ಲಿ ೨ ಲೀಟರ್ ನೀರು ತೆಗೆದುಕೊಳ್ಳಿ).",
                                "2. Add exactly %.0f %s of %s and stir with a stick until fully dissolved.".format(tankDoseAmount, tankDoseUnit, activeMixingFert.fertilizerName),
                                "3. Pour the pre-dissolved solution into the 16L sprayer tank through the mesh strainer filter.",
                                "4. Fill the sprayer tank with clean water up to the 16 Liter mark and stir gently.",
                                "5. Add 5-10 ml wetting sticker (Apsa-80 / Wetcit) to prevent rain wash-off and ensure uniform foliar spread.",
                                "6. Spray uniformly on top and bottom leaf surfaces during cool hours (7-10 AM or after 4 PM)."
                            )

                            mixingStepsList.forEach { stepItem ->
                                Text(
                                    text = stepItem,
                                    fontSize = 12.5.sp,
                                    color = Color(0xFF2E7D32),
                                    lineHeight = 18.sp,
                                    modifier = Modifier.padding(vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Section: Immediate Actions Checklist
        if (result.immediateActions.isNotEmpty()) {
            DiagnosticSectionCard(
                title = "Immediate Containment Actions",
                icon = Icons.Default.Warning,
                iconTint = Color(0xFFE65100),
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    result.immediateActions.forEachIndexed { idx, action ->
                        Row(
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .padding(top = 2.dp)
                                    .size(20.dp)
                                    .background(Color(0xFFFFE0B2), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${idx + 1}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFE65100)
                                )
                            }
                            Text(
                                text = action,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }

        // Section: Weeds & Selective Herbicides
        if (result.weeds.isNotEmpty() || result.selectiveHerbicides.isNotEmpty()) {
            DiagnosticSectionCard(
                title = "Weed Pressure & Selective Herbicides",
                icon = Icons.Default.Agriculture,
                iconTint = ForestGreenPrimary,
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    if (result.weeds.isNotEmpty()) {
                        Text(
                            text = "Identified Weed Flora:",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            result.weeds.forEach { weed ->
                                Surface(
                                    color = Color(0xFFE8F5E9),
                                    shape = RoundedCornerShape(8.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFC8E6C9))
                                ) {
                                    Text(
                                        text = weed,
                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                                        color = Color(0xFF1B5E20),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }

                    if (result.selectiveHerbicides.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Prescribed Selective Herbicide:",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        result.selectiveHerbicides.forEach { herbicide ->
                            Text(
                                text = "• $herbicide",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }

        // Section: Dual Prescriptions (Organic Bio vs Chemical)
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Integrated Pest & Disease Prescriptions",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Dual strategy: Natural bio-defense combined with target chemicals",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Organic Bio-Treatments Subcard
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F8F1)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Eco,
                                contentDescription = "Organic",
                                tint = ForestGreenPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "Organic & Biological Treatments",
                                fontWeight = FontWeight.Bold,
                                color = ForestGreenPrimary,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        result.organicFertilizers.forEach { item ->
                            Row(
                                modifier = Modifier.padding(vertical = 3.dp),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Text("🌱", fontSize = 13.sp)
                                Text(
                                    text = item,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF1B5E20)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Chemical Treatments Subcard
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F4F8)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Science,
                                contentDescription = "Chemical",
                                tint = Color(0xFF1565C0),
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "Target Chemical Interventions (Exact Dosages)",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1565C0),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        result.chemicalFertilizers.forEach { item ->
                            Row(
                                modifier = Modifier.padding(vertical = 3.dp),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Text("🧪", fontSize = 13.sp)
                                Text(
                                    text = item,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF0D47A1)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Section: Safety & PPE Instructions
        if (result.safety.isNotEmpty()) {
            DiagnosticSectionCard(
                title = "Farmer PPE & Spraying Directives",
                icon = Icons.Default.Security,
                iconTint = Color(0xFFD32F2F),
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    result.safety.forEach { rule ->
                        Row(
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Rule",
                                tint = Color(0xFFD32F2F),
                                modifier = Modifier.size(16.dp).padding(top = 2.dp)
                            )
                            Text(
                                text = rule,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }

        // Action Buttons: Prescription Slip & Share (Large 52dp touch targets)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = { showPrescriptionDialog = true },
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
                    .testTag("view_prescription_slip_button"),
                colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Print,
                    contentDescription = "Prescription",
                    tint = com.example.ui.theme.AmberLight,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Rx Slip",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // WhatsApp Share Button in Preferred Language
            Button(
                onClick = {
                    ShareManager.shareCropDiagnosisWhatsApp(context, result, language)
                },
                modifier = Modifier
                    .weight(1.3f)
                    .height(52.dp)
                    .testTag("whatsapp_share_diagnosis_button"),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = "WhatsApp Share",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = when (language) {
                        AppLanguage.KANNADA -> "ವಾಟ್ಸಾಪ್ Rx"
                        AppLanguage.HINDI -> "व्हाट्सएप Rx"
                        AppLanguage.ENGLISH -> "WhatsApp Rx"
                    },
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            OutlinedButton(
                onClick = {
                    ShareManager.shareCropDiagnosisGeneral(context, result, language, "Farmer", effectiveAcres)
                },
                modifier = Modifier
                    .weight(0.8f)
                    .height(52.dp)
                    .testTag("share_diagnosis_button"),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, ForestGreenPrimary)
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = "Share",
                    tint = ForestGreenPrimary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Share",
                    color = ForestGreenPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }

    // Prescription Slip Printable Dialog
    if (showPrescriptionDialog) {
        PrescriptionSlipDialog(
            result = result,
            weatherContext = weatherContext,
            effectiveAcres = effectiveAcres,
            verifiedFertilizers = verifiedFertilizers,
            language = language,
            onDismiss = { showPrescriptionDialog = false }
        )
    }
}

@Composable
private fun DiagnosticSectionCard(
    title: String,
    icon: ImageVector,
    iconTint: Color,
    containerColor: Color,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        shape = RoundedCornerShape(18.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, com.example.ui.theme.LightBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = iconTint,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.5.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
fun PrescriptionSlipDialog(
    result: CropDiagnosisResult,
    weatherContext: String,
    effectiveAcres: Double = 1.0,
    verifiedFertilizers: List<VerifiedFertilizerItem> = emptyList(),
    language: AppLanguage = AppLanguage.KANNADA,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val dateStr = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.ENGLISH).format(Date(result.timestamp))

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .testTag("prescription_slip_dialog"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth()
            ) {
                // Header Prescription Branding
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "RaithaDrishti (ರೈತ ದೃಷ್ಟಿ)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = ForestGreenPrimary
                        )
                        Text(
                            text = "Karnataka Crop Health Advisory Rx",
                            fontSize = 11.sp,
                            color = Color(0xFF616161)
                        )
                    }
                    Text(
                        text = "℞",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = FontFamily.Serif,
                        color = ForestGreenPrimary
                    )
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = Color(0xFFE0E0E0))

                // Patient/Crop Metadata
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Date: $dateStr", fontSize = 11.sp, color = Color(0xFF424242))
                    Text(
                        text = "Severity: ${result.severity}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = result.getSeverityColor()
                    )
                }
                Text(
                    text = "Crop: ${if (result.cropName.isNotBlank()) result.cropName else "General"} • Land: %.2f Acres".format(effectiveAcres),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF212121),
                    modifier = Modifier.padding(top = 4.dp)
                )
                Text(
                    text = "Diagnosis: ${result.diagnosis} (${result.confidence}% conf.)",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = ForestGreenPrimary,
                    modifier = Modifier.padding(top = 2.dp)
                )

                if (result.exactProblemIdentified.isNotBlank()) {
                    Text(
                        text = "Exact Issue: ${result.exactProblemIdentified}",
                        fontSize = 11.sp,
                        color = Color(0xFFE65100),
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = Color(0xFFE0E0E0))

                // Ensured Local Fertilizers & Land Size Mixing
                if (verifiedFertilizers.isNotEmpty()) {
                    Text(
                        text = "ENSURED FERTILIZERS & LAND MIXING (for %.2f Acres):".format(effectiveAcres),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1B5E20)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    verifiedFertilizers.take(2).forEach { fert ->
                        val totalAmt = fert.standardDosePerAcreKgOrL * effectiveAcres
                        val unit = if (fert.dosageUnit.startsWith("g")) "kg" else "L"
                        val totalW = (fert.waterPerAcreLiters * effectiveAcres).toInt()
                        val tanks = Math.max(1, Math.round(totalW / 16.0).toInt())
                        Text(
                            text = "• ${fert.fertilizerName}: %.2f %s in %d L water (%d pumps of 16L)".format(totalAmt, unit, totalW, tanks),
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF0D47A1)
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                }

                // Rx Treatments
                Text(
                    text = "PRESCRIBED TREATMENTS:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF757575)
                )
                Spacer(modifier = Modifier.height(4.dp))

                result.chemicalFertilizers.take(2).forEach { chem ->
                    Text(text = "• $chem", fontSize = 11.5.sp, color = Color(0xFF0D47A1), fontWeight = FontWeight.Medium)
                }
                result.organicFertilizers.take(2).forEach { org ->
                    Text(text = "• $org", fontSize = 11.5.sp, color = Color(0xFF1B5E20))
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "SAFETY PROTOCOL:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF757575)
                )
                result.safety.take(2).forEach { s ->
                    Text(text = "⚠ $s", fontSize = 10.5.sp, color = Color(0xFFB71C1C))
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = Color(0xFFE0E0E0))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(0.8f)
                    ) {
                        Text("Close", fontSize = 12.sp)
                    }

                    Button(
                        onClick = {
                            ShareManager.shareCropDiagnosisWhatsApp(context, result, language, "Farmer", effectiveAcres)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1.3f)
                    ) {
                        Icon(imageVector = Icons.Default.Share, contentDescription = "WhatsApp Share", tint = Color.White, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = when (language) {
                                AppLanguage.KANNADA -> "ವಾಟ್ಸಾಪ್ Rx"
                                AppLanguage.HINDI -> "व्हाट्सएप Rx"
                                AppLanguage.ENGLISH -> "WhatsApp"
                            },
                            color = Color.White,
                            fontSize = 12.sp
                        )
                    }

                    Button(
                        onClick = {
                            ShareManager.shareCropDiagnosisGeneral(context, result, language, "Farmer", effectiveAcres)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(imageVector = Icons.Default.Share, contentDescription = "Share", modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Share", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

fun sharePrescriptionText(context: Context, result: CropDiagnosisResult, weatherContext: String) {
    val text = buildString {
        appendLine("🌿 RaithaDrishti (ರೈತ ದೃಷ್ಟಿ) - Crop Pathology Prescription Slip")
        appendLine("=========================================")
        appendLine("Crop: ${result.cropName}")
        appendLine("Diagnosis: ${result.diagnosis}")
        appendLine("Severity: ${result.severity} | AI Confidence: ${result.confidence}%")
        appendLine("Summary: ${result.summary}")
        appendLine()
        appendLine("💊 Chemical Interventions:")
        result.chemicalFertilizers.forEach { appendLine("  - $it") }
        appendLine()
        appendLine("🌱 Organic Bio-treatments:")
        result.organicFertilizers.forEach { appendLine("  - $it") }
        if (result.selectiveHerbicides.isNotEmpty()) {
            appendLine()
            appendLine("🌾 Selective Herbicides:")
            result.selectiveHerbicides.forEach { appendLine("  - $it") }
        }
        appendLine()
        appendLine("⚠ Safety Directives:")
        result.safety.forEach { appendLine("  - $it") }
        appendLine("=========================================")
        appendLine("Generated by RaithaDrishti AI Agronomist for Karnataka Farmers")
    }

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, "RaithaDrishti Crop Prescription - ${result.cropName}")
        putExtra(Intent.EXTRA_TEXT, text)
    }
    context.startActivity(Intent.createChooser(intent, "Share Agronomist Prescription Slip"))
}
