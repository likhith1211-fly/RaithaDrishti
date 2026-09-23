package com.example.ui.screens

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.TipsAndUpdates
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.data.model.AppLanguage
import com.example.data.model.KARNATAKA_CROPS_CATALOG
import com.example.data.model.KarnatakaCropCategory
import com.example.data.model.KarnatakaCropItem
import com.example.ui.components.CameraCaptureScreen
import com.example.ui.components.DiagnosisResultView
import com.example.ui.theme.ForestGreenPrimary
import com.example.ui.viewmodel.RaithaDrishtiViewModel

@Composable
fun CropDoctorScreen(
    viewModel: RaithaDrishtiViewModel,
    onNavigateToNutrition: () -> Unit = {},
    onOpenVoice: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val currentLang by viewModel.currentLanguage.collectAsState()
    val targetCrop by viewModel.targetCrop.collectAsState()
    val farmerNotes by viewModel.farmerNotes.collectAsState()
    val selectedImage by viewModel.selectedImageBitmap.collectAsState()
    val isDiagnosing by viewModel.isDiagnosing.collectAsState()
    val latestDiagnosis by viewModel.latestDiagnosis.collectAsState()
    val diagnosisError by viewModel.diagnosisError.collectAsState()
    val currentWeather by viewModel.currentWeather.collectAsState()
    val voiceFeedbackMessage by viewModel.voiceFeedbackMessage.collectAsState()

    var showCameraXScreen by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf(KarnatakaCropCategory.ALL) }
    var expandedCropItem by remember { mutableStateOf<KarnatakaCropItem?>(null) }
    var showVoiceCustomInputDialog by remember { mutableStateOf(false) }
    var customVoiceInputText by remember { mutableStateOf("") }

    // If CameraX Screen is active, display high-resolution camera view overlay
    if (showCameraXScreen) {
        CameraCaptureScreen(
            cropName = targetCrop,
            onCropNameChange = { newCrop ->
                viewModel.setTargetCrop(newCrop)
            },
            onImageCaptured = { bitmap ->
                viewModel.setSelectedImage(bitmap)
                showCameraXScreen = false
            },
            onDiagnoseWithGemini = { bitmap, selectedCropName ->
                viewModel.setTargetCrop(selectedCropName)
                viewModel.setSelectedImage(bitmap)
                viewModel.runCropDiagnosis()
            },
            isDiagnosing = isDiagnosing,
            diagnosisResult = latestDiagnosis,
            diagnosisError = diagnosisError,
            onViewFullDiagnosis = {
                showCameraXScreen = false
            },
            onClose = {
                showCameraXScreen = false
            }
        )
        return
    }

    // Camera permission request launcher for CameraX
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            showCameraXScreen = true
        }
    }

    // Photo pickers
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            viewModel.setImageFromUri(uri)
        }
    }

    // Hands-Free Voice Command Executor
    fun executeDiagnosticVoiceCommand(command: String) {
        viewModel.processDiagnosticVoiceCommand(
            rawCommand = command,
            onNavigateToCamera = {
                val permissionCheck = ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.CAMERA
                )
                if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                    showCameraXScreen = true
                } else {
                    cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                }
            },
            onOpenGallery = {
                photoPickerLauncher.launch(
                    androidx.activity.result.PickVisualMediaRequest(
                        ActivityResultContracts.PickVisualMedia.ImageOnly
                    )
                )
            },
            onStartDiagnosis = {
                viewModel.runCropDiagnosis()
            }
        )
    }

    // Android Speech Recognition Launcher
    val speechRecognitionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { activityResult ->
        if (activityResult.resultCode == Activity.RESULT_OK) {
            val spoken = activityResult.data
                ?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                ?.firstOrNull()
            if (!spoken.isNullOrBlank()) {
                executeDiagnosticVoiceCommand(spoken)
            }
        }
    }

    fun startSpeechRecognizer() {
        try {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_PROMPT, when (currentLang) {
                    AppLanguage.KANNADA -> "ಹೇಳಿ: 'ಕ್ಯಾಮೆರಾ', 'ಗ್ಯಾಲರಿ', 'ಪರೀಕ್ಷಿಸು', ಅಥವಾ ಬೆಲೆಯ ವಿವರ"
                    AppLanguage.HINDI -> "बोलें: 'कैमरा', 'गैलरी', 'जांच', या फसल का भाव"
                    AppLanguage.ENGLISH -> "Speak: 'Camera', 'Gallery', 'Diagnose', or 'Price'"
                })
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, when (currentLang) {
                    AppLanguage.KANNADA -> "kn-IN"
                    AppLanguage.HINDI -> "hi-IN"
                    AppLanguage.ENGLISH -> "en-IN"
                })
            }
            speechRecognitionLauncher.launch(intent)
        } catch (e: Exception) {
            // Speech recognizer not installed; open custom input fallback dialog
            showVoiceCustomInputDialog = true
        }
    }

    // Helper to generate a test synthetic leaf bitmap with symptoms for any selected Karnataka crop
    fun generateSyntheticCropLeafSample(crop: KarnatakaCropItem): Bitmap {
        val bitmap = Bitmap.createBitmap(320, 320, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val baseLeafColor = when (crop.category) {
            KarnatakaCropCategory.FLORICULTURE_SILK -> android.graphics.Color.rgb(56, 142, 60)
            KarnatakaCropCategory.FRUITS -> android.graphics.Color.rgb(46, 125, 50)
            else -> android.graphics.Color.rgb(38, 115, 42)
        }
        val leafPaint = Paint().apply { color = baseLeafColor }
        canvas.drawRect(0f, 0f, 320f, 320f, leafPaint)

        // Draw distinctive lesion pattern based on crop category
        val spotPaint = Paint().apply {
            color = if (crop.id == "maize") {
                android.graphics.Color.rgb(250, 250, 230) // white bleached bands
            } else if (crop.id == "pomegranate" || crop.id == "cotton") {
                android.graphics.Color.rgb(40, 30, 20) // dark angular spots
            } else {
                android.graphics.Color.rgb(180, 100, 30) // necrotic rust / blast
            }
        }

        if (crop.id == "maize") {
            canvas.drawRect(40f, 20f, 90f, 300f, spotPaint)
            canvas.drawRect(140f, 50f, 180f, 280f, spotPaint)
            canvas.drawRect(230f, 10f, 270f, 310f, spotPaint)
        } else {
            canvas.drawCircle(80f, 80f, 35f, spotPaint)
            canvas.drawCircle(160f, 160f, 45f, spotPaint)
            canvas.drawCircle(240f, 230f, 30f, spotPaint)
            canvas.drawCircle(120f, 240f, 25f, spotPaint)
        }

        val haloPaint = Paint().apply { color = android.graphics.Color.rgb(230, 210, 60) } // chlorotic yellow halo
        canvas.drawCircle(80f, 80f, 42f, Paint(haloPaint).apply { style = Paint.Style.STROKE; strokeWidth = 8f })
        canvas.drawCircle(160f, 160f, 52f, Paint(haloPaint).apply { style = Paint.Style.STROKE; strokeWidth = 8f })
        return bitmap
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Header Card (Royal Emerald with Sovereign Gold accents)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("crop_doctor_hero_card"),
            colors = CardDefaults.cardColors(containerColor = ForestGreenPrimary),
            border = androidx.compose.foundation.BorderStroke(1.dp, com.example.ui.theme.AmberLight.copy(alpha = 0.35f)),
            shape = RoundedCornerShape(18.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = when (currentLang) {
                        AppLanguage.KANNADA -> "ರೋಗ ತಪಾಸಣೆ • Crop Pathology AI"
                        AppLanguage.HINDI -> "फसल रोग निदान • AI डॉक्टर"
                        AppLanguage.ENGLISH -> "Royal Pathology AI • Multimodal Diagnostics"
                    },
                    color = com.example.ui.theme.AmberLight,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.4.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = when (currentLang) {
                        AppLanguage.KANNADA -> "ಕ್ಯಾಮೆರಾ / ಧ್ವನಿ ಮೂಲಕ ರೋಗ ಪರೀಕ್ಷೆ"
                        AppLanguage.HINDI -> "कैमरा / आवाज से रोग की जांच"
                        AppLanguage.ENGLISH -> "Multimodal Diagnostics & Prescription"
                    },
                    color = Color.White,
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold, fontSize = 22.sp)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = when (currentLang) {
                        AppLanguage.KANNADA -> "ಎಲೆಗಳ ಫೋಟೋ ತೆಗೆಯಿರಿ ಅಥವಾ ಧ್ವನಿ ಮೂಲಕ ಸಮಸ್ಯೆಯನ್ನು ಹೇಳಿ - ಜೆಮಿನಿ AI ತಕ್ಷಣವೇ ರೋಗ, ಕಾರಣ ಹಾಗೂ ಸರಿಯಾದ ರಸಗೊಬ್ಬರ ಮತ್ತು ಔಷಧಿಯನ್ನು ತಿಳಿಸುತ್ತದೆ."
                        AppLanguage.HINDI -> "पत्ते की फोटो लें या बोलकर बताएं - जेमिनी AI तुरंत रोग, कारण और सही खाद-दवा बताएगा।"
                        AppLanguage.ENGLISH -> "Upload field foliage image or speak to Voice AI for instant pathology analysis, weather-correlated vectors, and dosage prescriptions."
                    },
                    color = Color.White.copy(alpha = 0.92f),
                    style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 24.sp)
                )
            }
        }

        // Active Voice Assistant Feedback Banner
        if (voiceFeedbackMessage != null) {
            Surface(
                color = Color(0xFFE8F5E9),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF43A047)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("voice_feedback_banner")
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
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(ForestGreenPrimary, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.RecordVoiceOver,
                                contentDescription = "Voice Response",
                                tint = com.example.ui.theme.AmberLight,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "🔊 Voice Response (ಧ್ವನಿ ಉತ್ತರ):",
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                color = ForestGreenPrimary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = voiceFeedbackMessage!!,
                                style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 22.sp),
                                color = Color(0xFF1E3A24)
                            )
                        }
                    }
                    IconButton(
                        onClick = { viewModel.clearVoiceFeedback() },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Dismiss",
                            tint = ForestGreenPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        // Hands-Free Field Voice Assistant Card (Royal Gold Container)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("hands_free_voice_card"),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = com.example.ui.theme.RoyalGoldContainer),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, com.example.ui.theme.RoyalGoldBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(com.example.ui.theme.AmberSecondary, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = "Microphone",
                                tint = Color(0xFF1A1300),
                                modifier = Modifier.size(26.dp)
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = when (currentLang) {
                                    AppLanguage.KANNADA -> "ಧ್ವನಿ ನಿಯಂತ್ರಣ (Hands-Free Voice)"
                                    AppLanguage.HINDI -> "आवाज से नियंत्रण (Hands-Free)"
                                    AppLanguage.ENGLISH -> "Hands-Free Voice Field Control"
                                },
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontSize = 18.sp),
                                color = Color(0xFF382300)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = when (currentLang) {
                                    AppLanguage.KANNADA -> "ಹೇಳಿ: 'ಕ್ಯಾಮೆರಾ', 'ಗ್ಯಾಲರಿ', 'ಪರೀಕ್ಷಿಸು', ಅಥವಾ ಬೆಲೆ ಕೇಳಿ"
                                    AppLanguage.HINDI -> "बोलें: 'कैमरा', 'गैलरी', 'जांच', या भाव पूछें"
                                    AppLanguage.ENGLISH -> "Say: 'Camera', 'Gallery', 'Diagnose', or ask crop prices"
                                },
                                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.5.sp),
                                color = Color(0xFF5A411B)
                            )
                        }
                    }

                    Button(
                        onClick = { startSpeechRecognizer() },
                        colors = ButtonDefaults.buttonColors(containerColor = com.example.ui.theme.AmberSecondary),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.testTag("speak_field_command_button")
                    ) {
                        Icon(imageVector = Icons.Default.Mic, contentDescription = "Mic", tint = Color(0xFF1A1300), modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = when (currentLang) {
                                AppLanguage.KANNADA -> "ಮಾತನಾಡಿ"
                                AppLanguage.HINDI -> "बोलें"
                                AppLanguage.ENGLISH -> "Speak"
                            },
                            color = Color(0xFF1A1300),
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 15.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Quick Field Voice Chips (Tap to trigger or simulate spoken commands)
                Text(
                    text = "Quick Voice Commands (ತ್ವರಿತ ಧ್ವನಿ ಆಜ್ಞೆಗಳು):",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color(0xFF4A3204)
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(
                        "📸 ಕ್ಯಾಮೆರಾ ತೆರೆಯಿರಿ (Camera)" to "camera",
                        "🖼️ ಗ್ಯಾಲರಿ (Gallery)" to "gallery",
                        "🔬 ರೋಗ ಪರೀಕ್ಷಿಸು (Diagnose)" to "diagnose",
                        "🍅 ಟೊಮೆಟೊ ದರ (Tomato Price)" to "tomato price",
                        "🌽 ಮೆಕ್ಕೆಜೋಳ ದರ (Maize Price)" to "maize price",
                        "🎃 ಕುಂಬಳಕಾಯಿ ದರ (Pumpkin Price)" to "pumpkin price"
                    ).forEach { (label, cmd) ->
                        Surface(
                            color = Color.White,
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, com.example.ui.theme.RoyalGoldBorder),
                            modifier = Modifier
                                .clickable { executeDiagnosticVoiceCommand(cmd) }
                                .testTag("voice_chip_$cmd")
                        ) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.5.sp,
                                color = Color(0xFF2C1E05),
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                            )
                        }
                    }

                    // Text fallback button for typing command
                    Surface(
                        color = com.example.ui.theme.AmberLight.copy(alpha = 0.35f),
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, com.example.ui.theme.AmberSecondary),
                        modifier = Modifier
                            .clickable { showVoiceCustomInputDialog = true }
                            .testTag("voice_type_fallback_chip")
                    ) {
                        Text(
                            text = "✍️ Type Command",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.5.sp,
                            color = Color(0xFF4A3204),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        )
                    }
                }
            }
        }

        // Live Environmental Context Pill
        currentWeather?.let { weather ->
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(
                            imageVector = Icons.Default.Thermostat,
                            contentDescription = "Weather",
                            tint = Color(0xFFE65100),
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "${weather.location}: ${weather.temperature.toInt()}°C",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium)
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(
                            imageVector = Icons.Default.WaterDrop,
                            contentDescription = "Humidity",
                            tint = Color(0xFF0288D1),
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "RH: ${weather.humidity.toInt()}%",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                            color = if (weather.humidity > 75.0) Color(0xFFD32F2F) else MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Text(
                        text = if (weather.humidity > 75.0) "High Spore Risk" else "Normal Microclimate",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (weather.humidity > 75.0) Color(0xFFD32F2F) else ForestGreenPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Section: Multimodal Image Uploader
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = when (currentLang) {
                        AppLanguage.KANNADA -> "೧. ಬೆಳೆ ಎಲೆ/ಕಾಂಡದ ಚಿತ್ರ ಅಪ್‌ಲೋಡ್"
                        AppLanguage.HINDI -> "1. फसल का फोटो अपलोड करें"
                        AppLanguage.ENGLISH -> "1. Crop Foliage Photo"
                    },
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = when (currentLang) {
                        AppLanguage.KANNADA -> "ರೋಗಪೀಡಿತ ಎಲೆ, ಕಾಂಡದ ಕಲೆಗಳು ಅಥವಾ ಕೀಟಗಳ ಸ್ಪಷ್ಟ ಚಿತ್ರವನ್ನು ತೆಗೆಯಿರಿ ಅಥವಾ ಗ್ಯಾಲರಿಯಿಂದ ಆರಿಸಿ"
                        AppLanguage.HINDI -> "रोगग्रस्त पत्ती, तने के धब्बे या कीटों की स्पष्ट फोटो खींचें या गैलरी से चुनें"
                        AppLanguage.ENGLISH -> "Capture diseased leaves, stem lesions, or weed growth for AI analysis"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(12.dp))

                if (selectedImage != null) {
                    // Image Preview Box with Remove Button
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(12.dp))
                    ) {
                        Image(
                            bitmap = selectedImage!!.asImageBitmap(),
                            contentDescription = "Selected Crop Image",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        IconButton(
                            onClick = { viewModel.setSelectedImage(null) },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(8.dp)
                                .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                                .size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Remove Image",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                } else {
                    // Empty Upload Placeholder with Actions
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .border(
                                width = 1.5.dp,
                                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(12.dp)
                            ),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                OutlinedButton(
                                    onClick = {
                                        photoPickerLauncher.launch(
                                            androidx.activity.result.PickVisualMediaRequest(
                                                ActivityResultContracts.PickVisualMedia.ImageOnly
                                            )
                                        )
                                    },
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .height(48.dp)
                                        .testTag("gallery_picker_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AddPhotoAlternate,
                                        contentDescription = "Gallery",
                                        modifier = Modifier.size(22.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = when (currentLang) {
                                            AppLanguage.KANNADA -> "ಗ್ಯಾಲರಿ"
                                            AppLanguage.HINDI -> "गैलरी"
                                            AppLanguage.ENGLISH -> "Gallery"
                                        },
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Button(
                                    onClick = {
                                        val permissionCheck = ContextCompat.checkSelfPermission(
                                            context,
                                            Manifest.permission.CAMERA
                                        )
                                        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                                            showCameraXScreen = true
                                        } else {
                                            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .height(48.dp)
                                        .testTag("camera_capture_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CameraAlt,
                                        contentDescription = "CameraX High-Res",
                                        modifier = Modifier.size(22.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = when (currentLang) {
                                            AppLanguage.KANNADA -> "ಕ್ಯಾಮೆರಾ (CameraX)"
                                            AppLanguage.HINDI -> "कैमरा (CameraX)"
                                            AppLanguage.ENGLISH -> "CameraX"
                                        },
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = when (currentLang) {
                                    AppLanguage.KANNADA -> "ಕ್ಯಾಮೆರಾ ಅಥವಾ ಗ್ಯಾಲರಿಯಿಂದ ಎಲೆಯ ಫೋಟೋ ತೆಗೆಯಿರಿ"
                                    AppLanguage.HINDI -> "कैमरा या गैलरी से पत्ती की फोटो लें"
                                    AppLanguage.ENGLISH -> "CameraX High-Resolution Field Pathology or Gallery"
                                },
                                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // Section: Target Crop Selection & Notes with ALL Karnataka Crops
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(18.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, com.example.ui.theme.LightBorder),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "2. Karnataka Crops & Field Notes (ಕರ್ನಾಟಕದ ಎಲ್ಲಾ ಬೆಳೆಗಳು)",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, fontSize = 19.sp),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Select any crop grown in Karnataka (Cereals, Cash, Pulses, Plantation, Veggies, Fruits, Silk)",
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.5.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Crop Category Filter Chips
                Text(
                    text = "Category (ವರ್ಗ):",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    KarnatakaCropCategory.values().forEach { cat ->
                        val isCatSelected = selectedCategory == cat
                        val catLabel = when (currentLang) {
                            AppLanguage.KANNADA -> cat.labelKn
                            AppLanguage.HINDI -> cat.labelHi
                            else -> cat.labelEn
                        }
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = if (isCatSelected) ForestGreenPrimary else MaterialTheme.colorScheme.surfaceVariant,
                            border = if (isCatSelected) null else androidx.compose.foundation.BorderStroke(1.dp, com.example.ui.theme.LightBorder),
                            modifier = Modifier
                                .clickable { selectedCategory = cat }
                                .testTag("cat_chip_${cat.name.lowercase()}")
                        ) {
                            Text(
                                text = catLabel,
                                color = if (isCatSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                                fontWeight = if (isCatSelected) FontWeight.Bold else FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Filtered Crop Chips for Karnataka
                val displayedCrops = remember(selectedCategory) {
                    if (selectedCategory == KarnatakaCropCategory.ALL) {
                        KARNATAKA_CROPS_CATALOG
                    } else {
                        KARNATAKA_CROPS_CATALOG.filter { it.category == selectedCategory }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    displayedCrops.forEach { cropItem ->
                        val isSelected = targetCrop.equals(cropItem.nameEn, ignoreCase = true) ||
                                targetCrop.contains(cropItem.nameEn.split("/").first().trim(), ignoreCase = true)
                        val cropDisplayName = when (currentLang) {
                            AppLanguage.KANNADA -> "${cropItem.iconEmoji} ${cropItem.nameKn}"
                            AppLanguage.HINDI -> "${cropItem.iconEmoji} ${cropItem.nameHi}"
                            else -> "${cropItem.iconEmoji} ${cropItem.nameEn}"
                        }

                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = if (isSelected) ForestGreenPrimary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.85f),
                            border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, com.example.ui.theme.AmberLight) else androidx.compose.foundation.BorderStroke(1.dp, com.example.ui.theme.LightBorder),
                            modifier = Modifier
                                .clickable {
                                    viewModel.setTargetCrop(cropItem.nameEn)
                                    if (cropItem.sampleSymptomNotes.isNotBlank() && farmerNotes.isBlank()) {
                                        viewModel.setFarmerNotes(cropItem.sampleSymptomNotes)
                                    }
                                    expandedCropItem = cropItem
                                }
                                .testTag("crop_chip_${cropItem.id}")
                        ) {
                            Text(
                                text = cropDisplayName,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.5.sp),
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = targetCrop,
                    onValueChange = { viewModel.setTargetCrop(it) },
                    label = { Text("Target Crop Name (ಬೆಳೆಯ ಹೆಸರು)") },
                    placeholder = { Text("e.g. Maize, Ragi, Arecanut, Sugarcane, Tomato, Byadagi Chilli, Cotton") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("crop_name_input"),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = farmerNotes,
                    onValueChange = { viewModel.setFarmerNotes(it) },
                    label = { Text("Farmer Observations & Symptoms (ರೈತರ ವೀಕ್ಷಣೆಗಳು)") },
                    placeholder = { Text("e.g. Patchy white bleached bands between midrib and margins, wilting, or yellow mosaic") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .testTag("farmer_notes_input"),
                    shape = RoundedCornerShape(10.dp),
                    maxLines = 3
                )
            }
        }

        // AI-Driven Karnataka Seasonal Growth Tips Module (Weather-Adapted)
        val seasonalGrowthTip = remember(targetCrop, currentWeather, currentLang) {
            viewModel.getSeasonalGrowthTip(targetCrop)
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("karnataka_seasonal_growth_tips_card"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Header row with Tips Icon and Full Title / Subtitle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .background(Color(0xFFE8F5E9), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.TipsAndUpdates,
                            contentDescription = "Growth Tips",
                            tint = Color(0xFF2E7D32),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = when (currentLang) {
                                AppLanguage.KANNADA -> "ಕಾಲೋಚಿತ ಬೆಳೆ ಮಾರ್ಗದರ್ಶಿ (AI Growth Tips)"
                                AppLanguage.HINDI -> "मौसमी फसल सलाह (AI Growth Tips)"
                                AppLanguage.ENGLISH -> "Karnataka Seasonal Growth Tips (AI)"
                            },
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        val cropNameDisplay = when (currentLang) {
                            AppLanguage.KANNADA -> seasonalGrowthTip.cropNameKn
                            AppLanguage.HINDI -> seasonalGrowthTip.cropNameHi
                            AppLanguage.ENGLISH -> seasonalGrowthTip.cropNameEn
                        }
                        val stageDisplay = when (currentLang) {
                            AppLanguage.KANNADA -> seasonalGrowthTip.currentSeasonStageKn
                            AppLanguage.HINDI -> seasonalGrowthTip.currentSeasonStageHi
                            AppLanguage.ENGLISH -> seasonalGrowthTip.currentSeasonStageEn
                        }
                        Text(
                            text = "$cropNameDisplay • $stageDisplay",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color(0xFF2E7D32),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Weather Risk Badge placed safely on its own row to prevent squishing title
                Surface(
                    color = Color(0xFFE0F2F1),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF80CBC4)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "🛡️ " + when (currentLang) {
                                AppLanguage.KANNADA -> seasonalGrowthTip.weatherRiskBadgeKn
                                AppLanguage.HINDI -> seasonalGrowthTip.weatherRiskBadgeHi
                                AppLanguage.ENGLISH -> seasonalGrowthTip.weatherRiskBadgeEn
                            },
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF00695C),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Live Weather Adaptation Alert Banner
                Surface(
                    color = Color(0xFFFFF3E0),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFB74D)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.WbSunny,
                            contentDescription = null,
                            tint = Color(0xFFE65100),
                            modifier = Modifier.size(18.dp)
                        )
                        Column {
                            Text(
                                text = "Weather-Adapted Field Directive:",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = Color(0xFFBF360C)
                            )
                            val weatherAdvice = when (currentLang) {
                                AppLanguage.KANNADA -> seasonalGrowthTip.weatherAdaptedAdviceKn
                                AppLanguage.HINDI -> seasonalGrowthTip.weatherAdaptedAdviceHi
                                AppLanguage.ENGLISH -> seasonalGrowthTip.weatherAdaptedAdviceEn
                            }
                            Text(
                                text = weatherAdvice,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFFE65100)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Growth Stage & Karnataka Field Risk
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = "Karnataka Growth Stage",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            val stageText = when (currentLang) {
                                AppLanguage.KANNADA -> seasonalGrowthTip.currentSeasonStageKn
                                AppLanguage.HINDI -> seasonalGrowthTip.currentSeasonStageHi
                                AppLanguage.ENGLISH -> seasonalGrowthTip.currentSeasonStageEn
                            }
                            Text(
                                text = stageText,
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = "Weather Risk Level",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "${seasonalGrowthTip.weatherRiskSeverity} Risk Zone",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Agronomic Key Practices
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.WaterDrop, contentDescription = null, tint = Color(0xFF0288D1), modifier = Modifier.size(16.dp))
                        Column {
                            Text(text = "Soil Moisture & Irrigation:", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onSurface)
                            val irrText = when (currentLang) {
                                AppLanguage.KANNADA -> seasonalGrowthTip.irrigationAdviceKn
                                AppLanguage.HINDI -> seasonalGrowthTip.irrigationAdviceHi
                                AppLanguage.ENGLISH -> seasonalGrowthTip.irrigationAdviceEn
                            }
                            Text(text = irrText, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.Science, contentDescription = null, tint = Color(0xFF7B1FA2), modifier = Modifier.size(16.dp))
                        Column {
                            Text(text = "Foliar Nutrition & Sprays:", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onSurface)
                            val nutText = when (currentLang) {
                                AppLanguage.KANNADA -> seasonalGrowthTip.nutrientActionKn
                                AppLanguage.HINDI -> seasonalGrowthTip.nutrientActionHi
                                AppLanguage.ENGLISH -> seasonalGrowthTip.nutrientActionEn
                            }
                            Text(text = nutText, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.Psychology, contentDescription = null, tint = Color(0xFFC2185B), modifier = Modifier.size(16.dp))
                        Column {
                            Text(text = "Pest & Disease Scouting:", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onSurface)
                            val pestText = when (currentLang) {
                                AppLanguage.KANNADA -> seasonalGrowthTip.pestPreventionKn
                                AppLanguage.HINDI -> seasonalGrowthTip.pestPreventionHi
                                AppLanguage.ENGLISH -> seasonalGrowthTip.pestPreventionEn
                            }
                            Text(text = pestText, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }

        // Quick Field Test Case for Karnataka Crops (Dynamic selection with Maize Patchy White Leaf & others)
        Surface(
            color = Color(0xFFE8F5E9),
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFA5D6A7)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "🔬 Karnataka Crop Field Test Samples",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color(0xFF1B5E20)
                        )
                        Text(
                            text = "ಕರ್ನಾಟಕದ ಪ್ರಮುಖ ಬೆಳೆಗಳ ರೋಗ ಪರೀಕ್ಷಾ ಮಾದರಿಗಳು (One-tap pathology test)",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF2E7D32)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Horizontal scroll of quick test samples across Karnataka crops
                val featuredTestCrops = remember {
                    KARNATAKA_CROPS_CATALOG.filter {
                        it.id in listOf("maize", "ragi", "paddy", "arecanut", "sugarcane", "tur", "tomato", "chilli", "pomegranate")
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    featuredTestCrops.forEach { crop ->
                        Button(
                            onClick = {
                                viewModel.setTargetCrop(crop.nameEn)
                                viewModel.setFarmerNotes(crop.sampleSymptomNotes)
                                val sampleBitmap = generateSyntheticCropLeafSample(crop)
                                viewModel.setSelectedImage(sampleBitmap)
                                viewModel.runCropDiagnosis()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (crop.id == "maize") ForestGreenPrimary else Color(0xFF1E5638)
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("test_sample_${crop.id}")
                        ) {
                            Text(
                                text = "${crop.iconEmoji} ${crop.nameEn.split("/").first().trim()}",
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Error Banner if any
        if (diagnosisError != null) {
            Surface(
                color = Color(0xFFFFEBEE),
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFCDD2)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = diagnosisError!!,
                    color = Color(0xFFC62828),
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.5.sp),
                    modifier = Modifier.padding(14.dp)
                )
            }
        }

        // Run Diagnosis Action Button (Prominent Royal Emerald 56dp height)
        Button(
            onClick = { viewModel.runCropDiagnosis() },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .testTag("run_diagnosis_button"),
            colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary),
            shape = RoundedCornerShape(16.dp),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 3.dp),
            enabled = !isDiagnosing
        ) {
            if (isDiagnosing) {
                CircularProgressIndicator(
                    color = com.example.ui.theme.AmberLight,
                    modifier = Modifier.size(26.dp),
                    strokeWidth = 3.dp
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Agronomist AI Analyzing Foliage...",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = Color.White,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Psychology,
                    contentDescription = "Diagnose",
                    tint = com.example.ui.theme.AmberLight,
                    modifier = Modifier.size(26.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = when (currentLang) {
                        AppLanguage.KANNADA -> "ರೋಗ ಪರೀಕ್ಷೆ ಪ್ರಾರಂಭಿಸಿ (AI Diagnosis)"
                        AppLanguage.HINDI -> "फसल जांच शुरू करें"
                        AppLanguage.ENGLISH -> "Run AI Agronomist Diagnosis"
                    },
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = Color.White,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.3.sp
                    )
                )
            }
        }

        // Diagnosis Results Section
        latestDiagnosis?.let { result ->
            val weatherContextStr = currentWeather?.let {
                "Temp: ${it.temperature}°C, RH: ${it.humidity}%, Rain: ${it.precipitation}mm in ${it.location}"
            } ?: "Typical semi-arid Karnataka agricultural zone"

            DiagnosisResultView(
                result = result,
                weatherContext = weatherContextStr,
                language = currentLang
            )

            // Direct Link to Suggested Fertilizers & Chemicals Tab
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToNutrition() }
                    .testTag("navigate_fertilizers_card"),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF4CAF50))
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
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .background(Color(0xFF2E7D32), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Science,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Column {
                            Text(
                                text = when (currentLang) {
                                    AppLanguage.KANNADA -> "ಶಿಫಾರಸು ಮಾಡಿದ ರಸಗೊಬ್ಬರಗಳು & ಔಷಧಗಳು"
                                    AppLanguage.HINDI -> "सुझाई गई खाद और कीटनाशक रसायन"
                                    AppLanguage.ENGLISH -> "Suggested Fertilizers & Growth Chemicals"
                                },
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = Color(0xFF1B5E20)
                            )
                            Text(
                                text = when (currentLang) {
                                    AppLanguage.KANNADA -> "ವೇಗವಾಗಿ ಬೆಳೆಯಲು ಜಿಂಕ್, ಯೂರಿಯಾ ಮತ್ತು ಸಿಂಪರಣೆ ಔಷಧಗಳ ವಿವರ ನೋಡಿ ->"
                                    AppLanguage.HINDI -> "तेज बढ़वार हेतु जिंक, यूरिया और छिड़काव रसायन देखें ->"
                                    AppLanguage.ENGLISH -> "Explore foliar Zinc, NPK fast-grow boosters, and dosage calculator ->"
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF2E7D32)
                            )
                        }
                    }
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = "Go to Fertilizers",
                        tint = Color(0xFF1B5E20)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }

    // Voice Command Text Input / Simulation Fallback Dialog
    if (showVoiceCustomInputDialog) {
        AlertDialog(
            onDismissRequest = { showVoiceCustomInputDialog = false },
            title = {
                Text(
                    text = "🎙️ Voice Command Simulation",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Enter spoken instruction (e.g., 'camera', 'gallery', 'diagnose', or 'tomato price in Kolar'):",
                        style = MaterialTheme.typography.bodySmall
                    )
                    OutlinedTextField(
                        value = customVoiceInputText,
                        onValueChange = { customVoiceInputText = it },
                        placeholder = { Text("e.g. maize price / ಕ್ಯಾಮೆರಾ / ಪರೀಕ್ಷಿಸು") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (customVoiceInputText.isNotBlank()) {
                            executeDiagnosticVoiceCommand(customVoiceInputText)
                            customVoiceInputText = ""
                        }
                        showVoiceCustomInputDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary)
                ) {
                    Text("Execute Command")
                }
            },
            dismissButton = {
                TextButton(onClick = { showVoiceCustomInputDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
