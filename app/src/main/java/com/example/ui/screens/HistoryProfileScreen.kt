package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.local.DiagnosisEntity
import com.example.data.model.AppLanguage
import com.example.data.model.CropDiagnosisResult
import com.example.ui.components.PrescriptionSlipDialog
import com.example.ui.components.sharePrescriptionText
import com.example.ui.theme.ForestGreenPrimary
import com.example.ui.viewmodel.RaithaDrishtiViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistoryProfileScreen(
    viewModel: RaithaDrishtiViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val currentLang by viewModel.currentLanguage.collectAsState()
    val historyList by viewModel.diagnosesHistory.collectAsState()
    val currentAccount by viewModel.currentAccount.collectAsState()
    val allAccounts by viewModel.allFarmerAccounts.collectAsState()
    val activeFarmerName by viewModel.activeFarmerName.collectAsState()
    val activeVillage by viewModel.activeVillageName.collectAsState()
    val activeBirthYear by viewModel.activeBirthYear.collectAsState()
    val activeFarmerId by viewModel.activeFarmerId.collectAsState()

    var showEditProfileDialog by remember { mutableStateOf(false) }
    var viewingDiagnosisResult by remember { mutableStateOf<Pair<CropDiagnosisResult, String>?>(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Farmer Cloud / Local Sign-In Card
        item {
            if (activeFarmerName.isBlank()) {
                // New farmer card - let farmers enter newly
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("farmer_account_auth_card"),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .background(Color(0xFF2E7D32).copy(alpha = 0.12f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = "New Farmer",
                                tint = Color(0xFF2E7D32),
                                modifier = Modifier.size(32.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = when (currentLang) {
                                AppLanguage.KANNADA -> "ರೈತರ ಪ್ರೊಫೈಲ್ ರಚಿಸಿ"
                                AppLanguage.HINDI -> "किसान प्रोफ़ाइल दर्ज करें"
                                AppLanguage.ENGLISH -> "Enter Your Farmer Profile"
                            },
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = when (currentLang) {
                                AppLanguage.KANNADA -> "ನಿಮ್ಮ ಸ್ವಂತ ಹೆಸರು, ಗ್ರಾಮ ಮತ್ತು ಮುಖ್ಯ ಬೆಳೆಗಳನ್ನು ನಮೂದಿಸಿ ನಿಮ್ಮ ಬೆಳೆ ರೋಗ ವರದಿಗಳನ್ನು ಸುರಕ್ಷಿತವಾಗಿರಿಸಿ."
                                AppLanguage.HINDI -> "फसल रोग और मंडी रिकॉर्ड सुरक्षित रखने के लिए अपना नाम, गांव और मुख्य फसलें दर्ज करें।"
                                AppLanguage.ENGLISH -> "Enter your name, village, and primary crops to personalize your farm pathology prescriptions."
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { showEditProfileDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("enter_farmer_details_button")
                        ) {
                            Icon(imageVector = Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                when (currentLang) {
                                    AppLanguage.KANNADA -> "ಪ್ರೊಫೈಲ್ ನಮೂದಿಸಿ"
                                    AppLanguage.HINDI -> "विवरण दर्ज करें"
                                    AppLanguage.ENGLISH -> "Enter Details"
                                }
                            )
                        }
                    }
                }
            } else {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("farmer_account_auth_card"),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(50.dp)
                                        .background(Color(0xFF2E7D32).copy(alpha = 0.15f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = "Farmer Account",
                                        tint = Color(0xFF2E7D32),
                                        modifier = Modifier.size(30.dp)
                                    )
                                }
                                Column {
                                    Text(
                                        text = activeFarmerName,
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    val locText = buildString {
                                        if (activeVillage.isNotBlank()) append("ಗ್ರಾಮ: $activeVillage")
                                        if (activeBirthYear > 0) {
                                            if (isNotEmpty()) append(" • ")
                                            append("ಜನನ: $activeBirthYear")
                                        }
                                    }
                                    if (locText.isNotBlank()) {
                                        Text(
                                            text = locText,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = ForestGreenPrimary,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                    val dist = currentAccount?.district ?: ""
                                    if (dist.isNotBlank()) {
                                        Text(
                                            text = "ಜಿಲ್ಲೆ: $dist",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(
                                    onClick = { showEditProfileDialog = true },
                                    modifier = Modifier.testTag("edit_profile_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Edit Profile",
                                        tint = Color(0xFF2E7D32)
                                    )
                                }
                                IconButton(
                                    onClick = { viewModel.resetFarmerProfile() },
                                    modifier = Modifier.testTag("reset_profile_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Change / Reset Farmer",
                                        tint = MaterialTheme.colorScheme.outline
                                    )
                                }
                            }
                        }

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        )

                        if ((currentAccount?.primaryCrops ?: "").isNotBlank() || (currentAccount?.landSizeAcres ?: 0.0) > 0.0) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                if ((currentAccount?.primaryCrops ?: "").isNotBlank()) {
                                    Column {
                                        Text(
                                            text = when (currentLang) {
                                                AppLanguage.KANNADA -> "ಬೆಳೆಗಳು"
                                                AppLanguage.HINDI -> "मुख्य फसलें"
                                                AppLanguage.ENGLISH -> "Primary Crops"
                                            },
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            text = currentAccount?.primaryCrops ?: "",
                                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }

                                if ((currentAccount?.landSizeAcres ?: 0.0) > 0.0) {
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = when (currentLang) {
                                                AppLanguage.KANNADA -> "ಜಮೀನಿನ ವಿಸ್ತೀರ್ಣ"
                                                AppLanguage.HINDI -> "जमीन का रकबा"
                                                AppLanguage.ENGLISH -> "Land Holding"
                                            },
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            text = "${currentAccount?.landSizeAcres} Acres",
                                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                            color = Color(0xFF2E7D32)
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(14.dp))
                        }

                        Button(
                            onClick = { showEditProfileDialog = true },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = null,
                                tint = Color(0xFF2E7D32),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = when (currentLang) {
                                    AppLanguage.KANNADA -> "ಪ್ರೊಫೈಲ್ ಬದಲಾಯಿಸಿ"
                                    AppLanguage.HINDI -> "प्रोफ़ाइल संपादित करें"
                                    AppLanguage.ENGLISH -> "Edit Profile"
                                },
                                fontSize = 12.sp,
                                color = Color(0xFF2E7D32),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Section Title: Diagnoses History scoped to this farmer
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = "History",
                        tint = Color(0xFF2E7D32),
                        modifier = Modifier.size(22.dp)
                    )
                    Text(
                        text = when (currentLang) {
                            AppLanguage.KANNADA -> "ಉಳಿಸಲಾದ ರೋಗ ದಾಖಲೆಗಳು (${historyList.size})"
                            AppLanguage.HINDI -> "सुरक्षित रोग इतिहास (${historyList.size})"
                            AppLanguage.ENGLISH -> "Saved Diagnoses History (${historyList.size})"
                        },
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Surface(
                    color = Color(0xFFE8F5E9),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "${historyList.size}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2E7D32),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }

        if (historyList.isEmpty()) {
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = when (currentLang) {
                                AppLanguage.KANNADA -> "ಇನ್ನೂ ಯಾವುದೇ ರೋಗ ತಪಾಸಣೆ ದಾಖಲೆಗಳಿಲ್ಲ."
                                AppLanguage.HINDI -> "कोई सुरक्षित रोग रिकॉर्ड नहीं है।"
                                AppLanguage.ENGLISH -> "No saved crop diagnoses yet."
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = when (currentLang) {
                                AppLanguage.KANNADA -> "ಬೆಳೆ ವೈದ್ಯ ಟ್ಯಾಬ್‌ನಿಂದ ಫೋಟೋ ತೆಗೆದು ತಪಾಸಣೆ ಮಾಡಿ, ವರದಿಗಳು ಇಲ್ಲಿ ಉಳಿಯುತ್ತವೆ."
                                AppLanguage.HINDI -> "फसल डॉक्टर टैब से फोटो खींचकर जांचें, रिपोर्ट यहां सुरक्षित रहेगी।"
                                AppLanguage.ENGLISH -> "Diagnose crops in the Crop Doctor tab to see your prescriptions saved here."
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(historyList, key = { it.id }) { item ->
                DiagnosisHistoryCard(
                    item = item,
                    language = currentLang,
                    onViewPrescription = {
                        viewingDiagnosisResult = Pair(item.toDomain(), item.weatherContext)
                    },
                    onShare = {
                        sharePrescriptionText(context, item.toDomain(), item.weatherContext)
                    },
                    onDelete = { viewModel.deleteHistoryItem(item.id) }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Prescription Slip Dialog from History
    viewingDiagnosisResult?.let { (result, weatherContext) ->
        PrescriptionSlipDialog(
            result = result,
            weatherContext = weatherContext,
            onDismiss = { viewingDiagnosisResult = null }
        )
    }

    // Edit Profile Dialog
    if (showEditProfileDialog) {
        EditProfileDialog(
            currentName = activeFarmerName,
            currentVillage = activeVillage,
            currentBirthYear = activeBirthYear,
            currentDistrict = currentAccount?.district ?: "Bengaluru Rural",
            currentCrops = currentAccount?.primaryCrops ?: "",
            currentAcres = currentAccount?.landSizeAcres ?: 0.0,
            onDismiss = { showEditProfileDialog = false },
            onSave = { name, village, birthYear, district, crops, acres ->
                viewModel.saveOrAlterFarmerProfile(
                    name = name,
                    village = village,
                    birthYear = birthYear,
                    district = district,
                    crops = crops,
                    acres = acres
                )
                showEditProfileDialog = false
            }
        )
    }
}

@Composable
private fun DiagnosisHistoryCard(
    item: DiagnosisEntity,
    language: AppLanguage,
    onViewPrescription: () -> Unit,
    onShare: () -> Unit,
    onDelete: () -> Unit
) {
    val dateStr = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.ENGLISH).format(Date(item.createdAt))
    val domain = item.toDomain()
    val severityColor = domain.getSeverityColor()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("history_item_${item.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = dateStr,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Surface(
                    color = severityColor.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = item.severity,
                        color = severityColor,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "${item.cropName}: ${item.diagnosis}",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = item.summary,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = onViewPrescription,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Print, contentDescription = "View Rx", modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = when (language) {
                                AppLanguage.KANNADA -> "ಚೀಟಿ ನೋಡಿ (Rx)"
                                AppLanguage.HINDI -> "पर्चा देखें (Rx)"
                                AppLanguage.ENGLISH -> "View Rx"
                            },
                            fontSize = 12.sp
                        )
                    }

                    IconButton(onClick = onShare, modifier = Modifier.size(36.dp)) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share",
                            tint = ForestGreenPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                IconButton(onClick = onDelete, modifier = Modifier.size(36.dp)) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = Color(0xFFD32F2F),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun EditProfileDialog(
    currentName: String,
    currentVillage: String,
    currentBirthYear: Int,
    currentDistrict: String,
    currentCrops: String,
    currentAcres: Double,
    onDismiss: () -> Unit,
    onSave: (String, String, Int, String, String, Double) -> Unit
) {
    var name by remember { mutableStateOf(currentName) }
    var village by remember { mutableStateOf(currentVillage) }
    var birthYearText by remember { mutableStateOf(if (currentBirthYear > 0) currentBirthYear.toString() else "") }
    var district by remember { mutableStateOf(currentDistrict) }
    var crops by remember { mutableStateOf(currentCrops) }
    var acresText by remember { mutableStateOf(if (currentAcres > 0.0) currentAcres.toString() else "") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Farmer Profile (ರೈತರ ವಿವರಗಳು)",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Full Name (ರೈತರ ಹೆಸರು)") },
                    placeholder = { Text("Enter farmer name...") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = village,
                        onValueChange = { village = it },
                        label = { Text("Village (ಗ್ರಾಮ)") },
                        placeholder = { Text("Village") },
                        singleLine = true,
                        modifier = Modifier.weight(1.2f)
                    )
                    OutlinedTextField(
                        value = birthYearText,
                        onValueChange = { birthYearText = it },
                        label = { Text("DOB Year (ಜನನ ವರ್ಷ)") },
                        placeholder = { Text("YYYY") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(0.8f)
                    )
                }

                OutlinedTextField(
                    value = district,
                    onValueChange = { district = it },
                    label = { Text("District (ಜಿಲ್ಲೆ)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = crops,
                    onValueChange = { crops = it },
                    label = { Text("Primary Crops (ಬೆಳೆಗಳು)") },
                    placeholder = { Text("e.g. Maize, Tomato, Ragi") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = acresText,
                    onValueChange = { acresText = it },
                    label = { Text("Land Size in Acres (ಜಮೀನು ಎಕರೆ)") },
                    placeholder = { Text("e.g. 3.5") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val acres = acresText.toDoubleOrNull() ?: 0.0
                            val year = birthYearText.toIntOrNull() ?: 0
                            onSave(name.trim(), village.trim(), year, district.trim(), crops.trim(), acres)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary)
                    ) {
                        Text("Save Profile")
                    }
                }
            }
        }
    }
}

