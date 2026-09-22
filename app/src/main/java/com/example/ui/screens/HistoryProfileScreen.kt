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
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SwitchAccount
import androidx.compose.material.icons.filled.Sync
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
    val activePhone by viewModel.activeFarmerPhone.collectAsState()
    val activeFarmerName by viewModel.activeFarmerName.collectAsState()
    val activeVillage by viewModel.activeVillageName.collectAsState()
    val activeBirthYear by viewModel.activeBirthYear.collectAsState()
    val activeFarmerId by viewModel.activeFarmerId.collectAsState()

    var showSignInDialog by remember { mutableStateOf(false) }
    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showStorageProofDialog by remember { mutableStateOf(false) }
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
                                AppLanguage.KANNADA -> "ರೈತರ ಪ್ರೊಫೈಲ್ ಹೊಸದಾಗಿ ನಮೂದಿಸಿ"
                                AppLanguage.HINDI -> "नया किसान प्रोफ़ाइल दर्ज करें"
                                AppLanguage.ENGLISH -> "Enter Your Farmer Profile"
                            },
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = when (currentLang) {
                                AppLanguage.KANNADA -> "ನಿಮ್ಮ ಸ್ವಂತ ಹೆಸರು, ಗ್ರಾಮ ಮತ್ತು ಮೊಬೈಲ್ ಸಂಖ್ಯೆಯನ್ನು ನಮೂದಿಸಿ ನಿಮ್ಮ ಬೆಳೆ ರೋಗ ವರದಿಗಳು ಮತ್ತು ಮಂಡಿ ವಿವರಗಳನ್ನು ಸುರಕ್ಷಿತವಾಗಿರಿಸಿ."
                                AppLanguage.HINDI -> "फसल रोग और मंडी रिकॉर्ड सुरक्षित रखने के लिए अपना नाम, गांव और मोबाइल नंबर दर्ज करें।"
                                AppLanguage.ENGLISH -> "Enter your name, village, and mobile number to save your pathology prescriptions and mandi records."
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
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
                                        AppLanguage.KANNADA -> "ವಿವರ ಹೊಸದಾಗಿ ನಮೂದಿಸಿ"
                                        AppLanguage.HINDI -> "विवरण नया दर्ज करें"
                                        AppLanguage.ENGLISH -> "Enter Details Newly"
                                    }
                                )
                            }

                            OutlinedButton(
                                onClick = { showSignInDialog = true },
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.testTag("open_signin_dialog_button")
                            ) {
                                Icon(imageVector = Icons.Default.Login, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    when (currentLang) {
                                        AppLanguage.KANNADA -> "ಸೈನ್ ಇನ್"
                                        AppLanguage.HINDI -> "साइन इन"
                                        AppLanguage.ENGLISH -> "Sign In"
                                    }
                                )
                            }
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
                                    if (activePhone.isNotBlank()) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.Phone,
                                                contentDescription = null,
                                                tint = Color(0xFF2E7D32),
                                                modifier = Modifier.size(13.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = "+91 $activePhone",
                                                style = MaterialTheme.typography.bodySmall,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF2E7D32)
                                            )
                                            val dist = currentAccount?.district ?: ""
                                            if (dist.isNotBlank()) {
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    text = "• $dist",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.outline
                                                )
                                            }
                                        }
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

                        // Permanent Storage Status & Identity Key Display
                        Surface(
                            color = ForestGreenPrimary.copy(alpha = 0.08f),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = when (currentLang) {
                                            AppLanguage.KANNADA -> "🔒 ಶಾಶ್ವತ ಸಂಗ್ರಹ ಗ್ಯಾರಂಟಿ (ಖಚಿತ ರಕ್ಷಣೆ)"
                                            AppLanguage.HINDI -> "🔒 स्थायी डेटा सुरक्षा (रिफ्रेश पर सुरक्षित)"
                                            AppLanguage.ENGLISH -> "🔒 Permanent Storage Guarantee"
                                        },
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                        color = Color(0xFF1B5E20)
                                    )
                                    Surface(
                                        color = Color(0xFF2E7D32),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = "PERSISTED",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = when (currentLang) {
                                        AppLanguage.KANNADA -> "ನಿಮ್ಮ ಹೆಸರು ($activeFarmerName)${if (activeVillage.isNotBlank()) ", ಗ್ರಾಮ ($activeVillage)" else ""} ಅಡಿಯಲ್ಲಿ ಎಲ್ಲಾ ರೋಗ ವರದಿಗಳು ಸಾಧನದಲ್ಲಿ ಸುರಕ್ಷಿತವಾಗಿವೆ."
                                        AppLanguage.HINDI -> "आपका सारा डेटा नाम ($activeFarmerName)${if (activeVillage.isNotBlank()) ", गांव ($activeVillage)" else ""} के तहत सुरक्षित सहेजा गया है।"
                                        AppLanguage.ENGLISH -> "All records are securely saved under $activeFarmerName${if (activeVillage.isNotBlank()) ", $activeVillage" else ""} in local Room and device backup storage."
                                    },
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        if ((currentAccount?.primaryCrops ?: "").isNotBlank() || (currentAccount?.landSizeAcres ?: 0.0) > 0.0) {
                            Spacer(modifier = Modifier.height(12.dp))
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
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Buttons: Sign-in / Switch Account AND Verify Storage
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { showSignInDialog = true },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SwitchAccount,
                                    contentDescription = null,
                                    tint = Color(0xFF2E7D32),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = when (currentLang) {
                                        AppLanguage.KANNADA -> "ಖಾತೆ ಬದಲಿಸಿ / ಸೈನ್ ಇನ್"
                                        AppLanguage.HINDI -> "खाता बदलें / साइन इन"
                                        AppLanguage.ENGLISH -> "Switch / Sign In"
                                    },
                                    fontSize = 11.sp,
                                    color = Color(0xFF2E7D32),
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            OutlinedButton(
                                onClick = { showStorageProofDialog = true },
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = when (currentLang) {
                                        AppLanguage.KANNADA -> "ಸಂಗ್ರಹ ಲೆಕ್ಕ ನೋಡಿ"
                                        AppLanguage.HINDI -> "डेटा देखें"
                                        AppLanguage.ENGLISH -> "View Stored File"
                                    },
                                    fontSize = 11.sp
                                )
                            }
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
                        text = "📱 $activePhone",
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
                                AppLanguage.KANNADA -> "ಈ ಮೊಬೈಲ್ ಸಂಖ್ಯೆಯಲ್ಲಿ ಇನ್ನೂ ಯಾವುದೇ ದಾಖಲೆಗಳಿಲ್ಲ."
                                AppLanguage.HINDI -> "इस नंबर पर कोई सुरक्षित रिकॉर्ड नहीं है।"
                                AppLanguage.ENGLISH -> "No saved crop diagnoses for this farmer account yet."
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = when (currentLang) {
                                AppLanguage.KANNADA -> "ರೋಗ ತಪಾಸಣೆ ಟ್ಯಾಬ್‌ನಿಂದ ಚಿತ್ರ ತೆಗೆದು ತಪಾಸಣೆ ಮಾಡಿ, ಇಲ್ಲಿ ಶಾಶ್ವತವಾಗಿ ಉಳಿಯುತ್ತದೆ!"
                                AppLanguage.HINDI -> "फसल जांच टैब से फोटो खींचकर जांचें, यहां सुरक्षित रहेगा।"
                                AppLanguage.ENGLISH -> "Run AI diagnostics from the Crop Doctor tab. It is automatically saved and retrieved on every sign in."
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

    // Farmer Sign-In / Switch Account Dialog
    if (showSignInDialog) {
        var inputPhone by remember { mutableStateOf(activePhone) }
        var inputName by remember { mutableStateOf(activeFarmerName) }
        var inputVillage by remember { mutableStateOf(activeVillage) }
        var inputBirthYear by remember { mutableStateOf(if (activeBirthYear > 0) activeBirthYear.toString() else "") }
        var inputDistrict by remember { mutableStateOf(currentAccount?.district ?: "Bengaluru Rural") }
        var inputCrops by remember { mutableStateOf(currentAccount?.primaryCrops ?: "") }
        var inputAcres by remember { mutableStateOf(if ((currentAccount?.landSizeAcres ?: 0.0) > 0.0) currentAccount?.landSizeAcres.toString() else "") }

        AlertDialog(
            onDismissRequest = { showSignInDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.Login,
                    contentDescription = null,
                    tint = Color(0xFF2E7D32),
                    modifier = Modifier.size(32.dp)
                )
            },
            title = {
                Text(
                    text = when (currentLang) {
                        AppLanguage.KANNADA -> "ರೈತರ ಸೈನ್ ಇನ್ (ಶಾಶ್ವತ ಡೇಟಾಬೇಸ್)"
                        AppLanguage.HINDI -> "किसान साइन इन (स्थायी डेटाबेस)"
                        AppLanguage.ENGLISH -> "Farmer Sign-In (Permanent Storage)"
                    },
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = when (currentLang) {
                            AppLanguage.KANNADA -> "ನಿಮ್ಮ ಹೆಸರು, ಗ್ರಾಮ ಮತ್ತು ಜನನ ವರ್ಷ ನಮೂದಿಸಿ. ನಿಮ್ಮ ಎಲ್ಲಾ ರೋಗ ವರದಿಗಳು ಮತ್ತು ಮಾಹಿತಿ ಸುರಕ್ಷಿತವಾಗಿ ಲೋಡ್ ಆಗುತ್ತವೆ."
                            AppLanguage.HINDI -> "अपना नाम, गांव और जन्म वर्ष दर्ज करें। आपका सारा डेटा लोड हो जाएगा।"
                            AppLanguage.ENGLISH -> "Enter your name, village, and birth year. All your saved prescriptions and farm records will be retrieved."
                        },
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.outline
                    )

                    OutlinedTextField(
                        value = inputName,
                        onValueChange = { inputName = it },
                        label = { Text("Farmer Name (ರೈತರ ಹೆಸರು)") },
                        placeholder = { Text("Enter farmer name...") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = inputVillage,
                            onValueChange = { inputVillage = it },
                            label = { Text("Village (ಗ್ರಾಮ)") },
                            modifier = Modifier.weight(1.2f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = inputBirthYear,
                            onValueChange = { inputBirthYear = it },
                            label = { Text("DOB Year (ಜನನ ವರ್ಷ)") },
                            placeholder = { Text("YYYY") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(0.8f),
                            singleLine = true
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = inputPhone,
                            onValueChange = { inputPhone = it },
                            label = { Text("Mobile (ಮೊಬೈಲ್)") },
                            placeholder = { Text("Mobile number") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = inputDistrict,
                            onValueChange = { inputDistrict = it },
                            label = { Text("District (ಜಿಲ್ಲೆ)") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }

                    // Previously saved accounts chips to quickly switch!
                    if (allAccounts.isNotEmpty()) {
                        Text(
                            text = when (currentLang) {
                                AppLanguage.KANNADA -> "ಸಾಧನದಲ್ಲಿ ಉಳಿಸಲಾದ ಖಾತೆಗಳು:"
                                AppLanguage.HINDI -> "सुरक्षित खाते:"
                                AppLanguage.ENGLISH -> "Saved Accounts on this Device:"
                            },
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            allAccounts.take(3).forEach { acc ->
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (acc.farmerId == activeFarmerId) Color(0xFF2E7D32) else MaterialTheme.colorScheme.surfaceVariant,
                                    modifier = Modifier.clickable {
                                        inputPhone = acc.phoneNumber
                                        inputName = acc.fullName
                                        inputVillage = acc.village
                                        inputBirthYear = if (acc.birthYear > 0) acc.birthYear.toString() else ""
                                        inputDistrict = acc.district
                                        inputCrops = acc.primaryCrops
                                        inputAcres = if (acc.landSizeAcres > 0.0) acc.landSizeAcres.toString() else ""
                                    }
                                ) {
                                    Text(
                                        text = "${acc.fullName} (${acc.village})",
                                        fontSize = 11.sp,
                                        color = if (acc.farmerId == activeFarmerId) Color.White else MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val acres = inputAcres.toDoubleOrNull() ?: 0.0
                        val year = inputBirthYear.toIntOrNull() ?: 0
                        viewModel.signInOrRegisterFarmer(
                            phone = inputPhone.trim(),
                            name = inputName.trim(),
                            village = inputVillage.trim(),
                            district = inputDistrict.trim(),
                            crops = inputCrops.trim(),
                            acres = acres,
                            birthYear = year
                        )
                        showSignInDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                ) {
                    Text(
                        when (currentLang) {
                            AppLanguage.KANNADA -> "ಸೈನ್ ಇನ್ ಮಾಡಿ / ಡೇಟಾ ಪಡೆಯಿರಿ"
                            AppLanguage.HINDI -> "साइन इन करें / डेटा लोड करें"
                            AppLanguage.ENGLISH -> "Sign In & Fetch Data"
                        }
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showSignInDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Edit Profile Dialog
    if (showEditProfileDialog) {
        EditProfileDialog(
            currentName = activeFarmerName,
            currentVillage = activeVillage,
            currentBirthYear = activeBirthYear,
            currentPhone = activePhone,
            currentDistrict = currentAccount?.district ?: "Bengaluru Rural",
            currentCrops = currentAccount?.primaryCrops ?: "",
            currentAcres = currentAccount?.landSizeAcres ?: 0.0,
            onDismiss = { showEditProfileDialog = false },
            onSave = { name, village, birthYear, phone, district, crops, acres ->
                viewModel.saveOrAlterFarmerProfile(
                    name = name,
                    village = village,
                    birthYear = birthYear,
                    district = district,
                    crops = crops,
                    acres = acres,
                    phoneNumber = phone
                )
                showEditProfileDialog = false
            }
        )
    }

    // Permanent Storage Proof Dialog
    if (showStorageProofDialog) {
        AlertDialog(
            onDismissRequest = { showStorageProofDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.Sync,
                    contentDescription = null,
                    tint = Color(0xFF2E7D32),
                    modifier = Modifier.size(28.dp)
                )
            },
            title = {
                Text(
                    text = when (currentLang) {
                        AppLanguage.KANNADA -> "ಸಾಧನದ ಶಾಶ್ವತ ಸಂಗ್ರಹ ಲೆಕ್ಕ"
                        AppLanguage.HINDI -> "डिवाइस स्थायी भंडारण स्थिति"
                        AppLanguage.ENGLISH -> "Persistent Storage Ledger"
                    },
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Identity Key: $activeFarmerId",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = Color(0xFF1B5E20)
                    )
                    Text(
                        text = "• Farmer Name: $activeFarmerName\n• Village: $activeVillage\n• Birth Year: $activeBirthYear\n• Phone: +91 $activePhone\n• Storage Backend: SharedPreferences + Room SQLite Database (raitha_drishti_v3.db) + Disk JSON file (farmer_account_backup.json)",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Surface(
                        color = Color(0xFFE8F5E9),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "✓ Zero Data Loss Guarantee: Data remains stored permanently until you explicitly alter it. Page refresh or app restarts do not erase any settings or diagnosis records.",
                            fontSize = 11.sp,
                            color = Color(0xFF1B5E20),
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showStorageProofDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                ) {
                    Text("OK / ಅರ್ಥವಾಯಿತು")
                }
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
    currentPhone: String,
    currentDistrict: String,
    currentCrops: String,
    currentAcres: Double,
    onDismiss: () -> Unit,
    onSave: (String, String, Int, String, String, String, Double) -> Unit
) {
    var name by remember { mutableStateOf(currentName) }
    var village by remember { mutableStateOf(currentVillage) }
    var birthYearText by remember { mutableStateOf(if (currentBirthYear > 0) currentBirthYear.toString() else "") }
    var phone by remember { mutableStateOf(currentPhone) }
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

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Mobile Number (ಮೊಬೈಲ್ ಸಂಖ್ಯೆ)") },
                    placeholder = { Text("e.g. 9876543210") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
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
                            onSave(name.trim(), village.trim(), year, phone.trim(), district.trim(), crops.trim(), acres)
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

