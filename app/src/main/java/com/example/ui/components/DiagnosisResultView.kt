package com.example.ui.components

import android.content.Context
import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.AppLanguage
import com.example.data.model.CropDiagnosisResult
import com.example.ui.theme.ForestGreenPrimary
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
                    ShareManager.shareCropDiagnosisGeneral(context, result, language)
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
                    text = "Crop: ${if (result.cropName.isNotBlank()) result.cropName else "General"}",
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

                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = Color(0xFFE0E0E0))

                // Rx Treatments
                Text(
                    text = "PRESCRIBED TREATMENTS:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF757575)
                )
                Spacer(modifier = Modifier.height(6.dp))

                result.chemicalFertilizers.forEach { chem ->
                    Text(text = "• $chem", fontSize = 12.sp, color = Color(0xFF0D47A1), fontWeight = FontWeight.Medium)
                }
                result.organicFertilizers.forEach { org ->
                    Text(text = "• $org", fontSize = 12.sp, color = Color(0xFF1B5E20))
                }

                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "SAFETY PROTOCOL:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF757575)
                )
                result.safety.take(2).forEach { s ->
                    Text(text = "⚠ $s", fontSize = 11.sp, color = Color(0xFFB71C1C))
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFE0E0E0))

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
                            ShareManager.shareCropDiagnosisWhatsApp(context, result, language)
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
                            ShareManager.shareCropDiagnosisGeneral(context, result, language)
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
