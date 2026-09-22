package com.example.ui.components

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.util.Log
import android.view.ViewGroup
import androidx.camera.core.Camera
import androidx.camera.core.CameraControl
import androidx.camera.core.CameraInfo
import androidx.camera.core.CameraSelector
import androidx.camera.core.FocusMeteringAction
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.core.SurfaceOrientedMeteringPointFactory
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.CenterFocusStrong
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FlashAuto
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Highlight
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.ZoomIn
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
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.data.model.CropDiagnosisResult
import com.example.data.model.KARNATAKA_CROPS_CATALOG
import com.example.ui.theme.AmberLight
import com.example.ui.theme.AmberSecondary
import com.example.ui.theme.ForestGreenDark
import com.example.ui.theme.ForestGreenPrimary
import com.example.ui.theme.RoyalGold
import com.example.ui.theme.RoyalGoldContainer
import com.example.ui.theme.SovereignGold
import java.util.concurrent.Executor
import java.util.concurrent.Executors

/**
 * CameraX Interface Component for Crop Foliage Pathology Diagnostics
 * Features:
 * - Live CameraX Preview with ProcessCameraProvider lifecycle binding
 * - High-resolution capture (ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
 * - Front/Back Lens switching
 * - Flash modes: Auto, On, Off
 * - Torch (flashlight) toggle for shadow & under-leaf inspection
 * - Zoom controls (1x, 2x, 3x) & pinch-to-zoom
 * - Tap-to-focus on foliage lesion with animated focus ring
 * - Pathology alignment reticle with bilingual guidance (Kannada & English)
 * - Seamless integration with Gemini API multimodal diagnostic pipeline
 * - In-camera review, immediate "Diagnose with Gemini AI" action, and live diagnostic result card
 * - Fallback simulation mode for testing on emulators without physical camera
 */
@Composable
fun CameraCaptureScreen(
    cropName: String = "",
    onCropNameChange: (String) -> Unit = {},
    onImageCaptured: (Bitmap) -> Unit,
    onDiagnoseWithGemini: ((Bitmap, String) -> Unit)? = null,
    isDiagnosing: Boolean = false,
    diagnosisResult: CropDiagnosisResult? = null,
    diagnosisError: String? = null,
    onViewFullDiagnosis: (() -> Unit)? = null,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // Camera states
    var lensFacing by remember { mutableIntStateOf(CameraSelector.LENS_FACING_BACK) }
    var flashMode by remember { mutableIntStateOf(ImageCapture.FLASH_MODE_AUTO) }
    var isTorchOn by remember { mutableStateOf(false) }
    var zoomRatio by remember { mutableFloatStateOf(0f) } // 0f (1x) to 1f (max)
    var isCapturing by remember { mutableStateOf(false) }
    var cameraError by remember { mutableStateOf<String?>(null) }
    var activeCamera by remember { mutableStateOf<Camera?>(null) }

    // Tap-to-focus state
    var focusRingOffset by remember { mutableStateOf<Offset?>(null) }
    var showFocusRing by remember { mutableStateOf(false) }

    // Review & Gemini AI inspection states
    var capturedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var activeCropName by remember { mutableStateOf(if (cropName.isNotBlank()) cropName else "Tomato / ಟೊಮೆಟೊ") }
    var quickNotes by remember { mutableStateOf("") }
    var showCropPicker by remember { mutableStateOf(false) }

    // Update active crop name if parent prop changes
    LaunchedEffect(cropName) {
        if (cropName.isNotBlank()) {
            activeCropName = cropName
        }
    }

    // Auto-dismiss focus ring after 2.5 seconds
    LaunchedEffect(focusRingOffset) {
        if (focusRingOffset != null) {
            showFocusRing = true
            kotlinx.coroutines.delay(2200)
            showFocusRing = false
        }
    }

    val imageCapture = remember {
        ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
            .setFlashMode(flashMode)
            .build()
    }

    val cameraExecutor: Executor = remember { Executors.newSingleThreadExecutor() }

    LaunchedEffect(flashMode) {
        imageCapture.flashMode = flashMode
    }

    LaunchedEffect(isTorchOn, activeCamera) {
        try {
            activeCamera?.cameraControl?.enableTorch(isTorchOn)
        } catch (e: Exception) {
            Log.w("CameraCaptureScreen", "Torch not supported or failed: ${e.message}")
        }
    }

    // Infinite animation for Gemini AI Scanning Beam
    val infiniteTransition = rememberInfiniteTransition(label = "gemini_scanner")
    val scannerOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scanner_beam"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .testTag("camera_capture_screen")
    ) {
        // -------------------------------------------------------------
        // 1. LIVE CAMERAX PREVIEW & GESTURE LAYER
        // -------------------------------------------------------------
        if (capturedBitmap == null) {
            var previewViewInstance by remember { mutableStateOf<PreviewView?>(null) }

            AndroidView(
                factory = { ctx ->
                    val pView = PreviewView(ctx).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        scaleType = PreviewView.ScaleType.FILL_CENTER
                    }
                    previewViewInstance = pView

                    val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                    cameraProviderFuture.addListener({
                        try {
                            val cameraProvider = cameraProviderFuture.get()
                            val preview = Preview.Builder().build().also {
                                it.setSurfaceProvider(pView.surfaceProvider)
                            }

                            val cameraSelector = CameraSelector.Builder()
                                .requireLensFacing(lensFacing)
                                .build()

                            cameraProvider.unbindAll()
                            val camera = cameraProvider.bindToLifecycle(
                                lifecycleOwner,
                                cameraSelector,
                                preview,
                                imageCapture
                            )
                            activeCamera = camera
                            camera.cameraControl.setLinearZoom(zoomRatio)
                        } catch (exc: Exception) {
                            Log.e("CameraCaptureScreen", "CameraX init failed", exc)
                            cameraError = "Camera hardware unavailable: ${exc.localizedMessage}"
                        }
                    }, ContextCompat.getMainExecutor(ctx))

                    pView
                },
                update = { pView ->
                    // Rebind camera if lens facing changed
                    val cameraProviderFuture = ProcessCameraProvider.getInstance(pView.context)
                    cameraProviderFuture.addListener({
                        try {
                            val cameraProvider = cameraProviderFuture.get()
                            val preview = Preview.Builder().build().also {
                                it.setSurfaceProvider(pView.surfaceProvider)
                            }
                            val cameraSelector = CameraSelector.Builder()
                                .requireLensFacing(lensFacing)
                                .build()

                            cameraProvider.unbindAll()
                            val camera = cameraProvider.bindToLifecycle(
                                lifecycleOwner,
                                cameraSelector,
                                preview,
                                imageCapture
                            )
                            activeCamera = camera
                            camera.cameraControl.setLinearZoom(zoomRatio)
                            if (isTorchOn) {
                                camera.cameraControl.enableTorch(true)
                            }
                        } catch (e: Exception) {
                            Log.w("CameraCaptureScreen", "Lens switch failed: ${e.message}")
                        }
                    }, ContextCompat.getMainExecutor(pView.context))
                },
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(activeCamera, previewViewInstance) {
                        detectTapGestures { tapOffset ->
                            focusRingOffset = tapOffset
                            val pView = previewViewInstance
                            if (pView != null && activeCamera != null) {
                                try {
                                    val factory = SurfaceOrientedMeteringPointFactory(
                                        pView.width.toFloat(),
                                        pView.height.toFloat()
                                    )
                                    val point = factory.createPoint(tapOffset.x, tapOffset.y)
                                    val action = FocusMeteringAction.Builder(point).build()
                                    activeCamera?.cameraControl?.startFocusAndMetering(action)
                                } catch (e: Exception) {
                                    Log.w("CameraCaptureScreen", "Focus failed: ${e.message}")
                                }
                            }
                        }
                    }
                    .pointerInput(activeCamera) {
                        detectTransformGestures { _, _, zoom, _ ->
                            if (activeCamera != null) {
                                val currentLinear = activeCamera?.cameraInfo?.zoomState?.value?.linearZoom ?: 0f
                                val updatedZoom = (currentLinear + (zoom - 1f) * 0.4f).coerceIn(0f, 1f)
                                zoomRatio = updatedZoom
                                activeCamera?.cameraControl?.setLinearZoom(updatedZoom)
                            }
                        }
                    }
            )

            // Animated Tap-To-Focus Reticle Ring
            if (showFocusRing && focusRingOffset != null) {
                Box(
                    modifier = Modifier
                        .offset {
                            IntOffset(
                                (focusRingOffset!!.x - 36.dp.toPx()).toInt(),
                                (focusRingOffset!!.y - 36.dp.toPx()).toInt()
                            )
                        }
                        .size(72.dp)
                        .border(2.dp, SovereignGold, CircleShape)
                        .padding(4.dp)
                        .border(1.dp, Color.White.copy(alpha = 0.8f), CircleShape)
                )
            }

            // -------------------------------------------------------------
            // 2. PATHOLOGY TARGET RETICLE OVERLAY
            // -------------------------------------------------------------
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 32.dp, vertical = 96.dp),
                contentAlignment = Alignment.Center
            ) {
                // Outer target frame with corner brackets
                Box(
                    modifier = Modifier
                        .size(310.dp)
                        .border(
                            width = 2.5.dp,
                            color = SovereignGold.copy(alpha = 0.85f),
                            shape = RoundedCornerShape(28.dp)
                        )
                        .background(Color.Black.copy(alpha = 0.08f), RoundedCornerShape(28.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    // Central crosshair ring
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .border(1.dp, AmberLight.copy(alpha = 0.6f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CenterFocusStrong,
                            contentDescription = "Center Focus",
                            tint = Color.White.copy(alpha = 0.7f),
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    // Guidance caption inside frame
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 16.dp, start = 12.dp, end = 12.dp)
                    ) {
                        Surface(
                            color = Color.Black.copy(alpha = 0.65f),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, SovereignGold.copy(alpha = 0.4f))
                        ) {
                            Text(
                                text = "Align diseased foliage inside frame\nರೋಗಗ್ರಸ್ತ ಎಲೆಯನ್ನು ಚೌಕಟ್ಟಿನಲ್ಲಿರಿಸಿ",
                                color = Color.White,
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }

            // -------------------------------------------------------------
            // 3. TOP ACTION BAR (Close, Crop Tag, Flash, Torch)
            // -------------------------------------------------------------
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter),
                color = Color.Black.copy(alpha = 0.65f)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(
                        onClick = onClose,
                        modifier = Modifier
                            .size(44.dp)
                            .background(Color.White.copy(alpha = 0.15f), CircleShape)
                            .testTag("camera_close_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close Camera",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    // Target Crop Pill (Clickable to switch crop)
                    Surface(
                        color = ForestGreenPrimary,
                        shape = RoundedCornerShape(20.dp),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, SovereignGold),
                        modifier = Modifier
                            .clickable { showCropPicker = !showCropPicker }
                            .testTag("camera_crop_selector_chip")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "🌿 $activeCropName",
                                color = AmberLight,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.5.sp
                            )
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        // Flash mode toggle (Auto -> On -> Off)
                        IconButton(
                            onClick = {
                                flashMode = when (flashMode) {
                                    ImageCapture.FLASH_MODE_AUTO -> ImageCapture.FLASH_MODE_ON
                                    ImageCapture.FLASH_MODE_ON -> ImageCapture.FLASH_MODE_OFF
                                    else -> ImageCapture.FLASH_MODE_AUTO
                                }
                            },
                            modifier = Modifier
                                .size(44.dp)
                                .background(Color.White.copy(alpha = 0.15f), CircleShape)
                                .testTag("camera_flash_toggle")
                        ) {
                            Icon(
                                imageVector = when (flashMode) {
                                    ImageCapture.FLASH_MODE_ON -> Icons.Default.FlashOn
                                    ImageCapture.FLASH_MODE_OFF -> Icons.Default.FlashOff
                                    else -> Icons.Default.FlashAuto
                                },
                                contentDescription = "Flash Mode",
                                tint = if (flashMode == ImageCapture.FLASH_MODE_ON) AmberSecondary else Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        // Torch continuous light toggle
                        IconButton(
                            onClick = { isTorchOn = !isTorchOn },
                            modifier = Modifier
                                .size(44.dp)
                                .background(
                                    if (isTorchOn) AmberSecondary else Color.White.copy(alpha = 0.15f),
                                    CircleShape
                                )
                                .testTag("camera_torch_toggle")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Highlight,
                                contentDescription = "Toggle Torch",
                                tint = if (isTorchOn) Color(0xFF1A1300) else Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }
            }

            // Quick Crop Selection Dropdown / Horizontal Tray
            AnimatedVisibility(
                visible = showCropPicker,
                enter = slideInVertically() + fadeIn(),
                exit = slideOutVertically() + fadeOut(),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 68.dp)
            ) {
                Surface(
                    color = Color(0xFF0F261B),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, SovereignGold),
                    modifier = Modifier
                        .fillMaxWidth(0.92f)
                        .padding(8.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Select Crop for Pathology Diagnostic (ಬೆಳೆ ಆಯ್ಕೆ):",
                            color = AmberLight,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(
                                "Tomato / ಟೊಮೆಟೊ",
                                "Ragi / ರಾಗಿ",
                                "Paddy / ಭತ್ತ",
                                "Cotton / ಹತ್ತಿ",
                                "Arecanut / ಅಡಿಕೆ",
                                "Chilli / ಮೆಣಸಿನಕಾಯಿ",
                                "Maize / ಮೆಕ್ಕೆಜೋಳ",
                                "Pomegranate / ದಾಳಿಂಬೆ",
                                "Sugarcane / ಕಬ್ಬು",
                                "Banana / ಬಾಳೆ"
                            ).forEach { cropLabel ->
                                Surface(
                                    color = if (activeCropName == cropLabel) AmberSecondary else Color(0xFF1E3A28),
                                    shape = RoundedCornerShape(10.dp),
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        if (activeCropName == cropLabel) SovereignGold else Color.Gray.copy(alpha = 0.3f)
                                    ),
                                    modifier = Modifier
                                        .clickable {
                                            activeCropName = cropLabel
                                            onCropNameChange(cropLabel)
                                            showCropPicker = false
                                        }
                                        .testTag("crop_chip_${cropLabel.take(6)}")
                                ) {
                                    Text(
                                        text = cropLabel,
                                        color = if (activeCropName == cropLabel) Color(0xFF1A1300) else Color.White,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // -------------------------------------------------------------
            // 4. BOTTOM CONTROLS (Zoom Chips, Shutter, Simulator, Lens Flip)
            // -------------------------------------------------------------
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter),
                color = Color.Black.copy(alpha = 0.75f)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Zoom quick buttons (1x, 2x, 3x)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        listOf(
                            Triple("1x", 0.0f, "camera_zoom_1x"),
                            Triple("2x", 0.5f, "camera_zoom_2x"),
                            Triple("3x", 1.0f, "camera_zoom_3x")
                        ).forEach { (label, ratio, tag) ->
                            val isSelected = Math.abs(zoomRatio - ratio) < 0.2f
                            Surface(
                                color = if (isSelected) AmberSecondary else Color.White.copy(alpha = 0.2f),
                                shape = CircleShape,
                                modifier = Modifier
                                    .size(38.dp)
                                    .clickable {
                                        zoomRatio = ratio
                                        activeCamera?.cameraControl?.setLinearZoom(ratio)
                                    }
                                    .testTag(tag)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = label,
                                        color = if (isSelected) Color(0xFF1A1300) else Color.White,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Primary Shutter Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        // Switch Lens button
                        IconButton(
                            onClick = {
                                lensFacing = if (lensFacing == CameraSelector.LENS_FACING_BACK) {
                                    CameraSelector.LENS_FACING_FRONT
                                } else {
                                    CameraSelector.LENS_FACING_BACK
                                }
                            },
                            modifier = Modifier
                                .size(50.dp)
                                .background(Color.White.copy(alpha = 0.2f), CircleShape)
                                .testTag("camera_switch_lens")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Cameraswitch,
                                contentDescription = "Switch Lens",
                                tint = Color.White,
                                modifier = Modifier.size(26.dp)
                            )
                        }

                        // High-Resolution Shutter Button
                        Box(
                            modifier = Modifier
                                .size(82.dp)
                                .border(4.dp, SovereignGold, CircleShape)
                                .padding(6.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.radialGradient(
                                        listOf(
                                            if (isCapturing) Color.Gray else AmberSecondary,
                                            ForestGreenPrimary
                                        )
                                    )
                                )
                                .testTag("camera_shutter_button"),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isCapturing) {
                                CircularProgressIndicator(
                                    color = Color.White,
                                    modifier = Modifier.size(34.dp),
                                    strokeWidth = 3.dp
                                )
                            } else {
                                IconButton(
                                    onClick = {
                                        if (!isCapturing) {
                                            isCapturing = true
                                            takePhoto(
                                                imageCapture = imageCapture,
                                                executor = cameraExecutor,
                                                onSuccess = { bitmap ->
                                                    ContextCompat.getMainExecutor(context).execute {
                                                        isCapturing = false
                                                        capturedBitmap = bitmap
                                                    }
                                                },
                                                onError = { exc ->
                                                    ContextCompat.getMainExecutor(context).execute {
                                                        isCapturing = false
                                                        cameraError = "Capture failed: ${exc.localizedMessage}"
                                                    }
                                                }
                                            )
                                        }
                                    },
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PhotoCamera,
                                        contentDescription = "Capture High-Resolution Foliage Photo",
                                        tint = Color.White,
                                        modifier = Modifier.size(38.dp)
                                    )
                                }
                            }
                        }

                        // Test Simulator Leaf Trigger (Ensures emulator never gets stuck)
                        IconButton(
                            onClick = {
                                capturedBitmap = generateFieldLeafBitmap(activeCropName)
                            },
                            modifier = Modifier
                                .size(50.dp)
                                .background(Color.White.copy(alpha = 0.2f), CircleShape)
                                .testTag("camera_simulate_leaf_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Science,
                                contentDescription = "Simulate Leaf",
                                tint = AmberLight,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Hold 15-25cm steady • Sharp daylight aids lesion detection",
                        color = Color.White.copy(alpha = 0.75f),
                        fontSize = 11.5.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Camera error warning banner if any
            if (cameraError != null) {
                Surface(
                    color = Color(0xFFD32F2F).copy(alpha = 0.95f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(24.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Camera Notice: $cameraError",
                            color = Color.White,
                            textAlign = TextAlign.Center,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = {
                                cameraError = null
                                capturedBitmap = generateFieldLeafBitmap(activeCropName)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = AmberSecondary)
                        ) {
                            Text("Use Simulated Field Leaf", color = Color(0xFF1A1300), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // -------------------------------------------------------------
        // 5. CAPTURED REVIEW & GEMINI AI DIAGNOSTICS OVERLAY
        // -------------------------------------------------------------
        if (capturedBitmap != null) {
            Surface(
                color = Color(0xFF071B12),
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("camera_captured_review_overlay")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Header Bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        IconButton(
                            onClick = {
                                capturedBitmap = null
                            },
                            modifier = Modifier
                                .size(40.dp)
                                .background(Color.White.copy(alpha = 0.15f), CircleShape)
                                .testTag("camera_review_back_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Discard and Return to Camera",
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Text(
                            text = "Pathology Inspection • Gemini AI",
                            color = AmberLight,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )

                        Spacer(modifier = Modifier.size(40.dp))
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Captured Image Frame with Gemini Scanning Animation
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(280.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .border(2.dp, SovereignGold, RoundedCornerShape(20.dp))
                            .background(Color.Black),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            bitmap = capturedBitmap!!.asImageBitmap(),
                            contentDescription = "Captured Crop Foliage",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )

                        // Active Gemini Diagnostic Scanning Beam
                        if (isDiagnosing) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(4.dp)
                                    .align(Alignment.TopCenter)
                                    .offset { IntOffset(0, (scannerOffset * 276.dp.toPx()).toInt()) }
                                    .background(
                                        Brush.horizontalGradient(
                                            listOf(
                                                Color.Transparent,
                                                AmberSecondary,
                                                SovereignGold,
                                                Color.Transparent
                                            )
                                        )
                                    )
                            )
                        }

                        // Bottom badge on image
                        Surface(
                            color = Color.Black.copy(alpha = 0.7f),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(12.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "🌿 $activeCropName",
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // ---------------------------------------------------------
                    // A. IF DIAGNOSING: SCANNING STATUS OVERLAY
                    // ---------------------------------------------------------
                    if (isDiagnosing) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("camera_gemini_diagnosing_card"),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = ForestGreenPrimary),
                            border = androidx.compose.foundation.BorderStroke(1.5.dp, AmberSecondary)
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                CircularProgressIndicator(
                                    color = AmberLight,
                                    strokeWidth = 3.5.dp,
                                    modifier = Modifier.size(44.dp)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "Gemini AI Analyzing Crop Pathology...",
                                    color = Color.White,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "ಜೆಮಿನಿ ಕೃತಕ ಬುದ್ಧಿಮತ್ತೆ ಎಲೆಯ ಕಲೆಗಳು ಹಾಗೂ ರೋಗಲಕ್ಷಣಗಳನ್ನು ಪರಿಶೀಲಿಸುತ್ತಿದೆ...",
                                    color = AmberLight,
                                    fontSize = 13.sp,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "• Correlating weather microclimate\n• Classifying fungal, bacterial & pest vectors\n• Formulating chemical & organic dosages",
                                    color = Color.White.copy(alpha = 0.85f),
                                    fontSize = 12.sp,
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }
                    // ---------------------------------------------------------
                    // B. IF DIAGNOSIS COMPLETED: IN-CAMERA RESULT CARD
                    // ---------------------------------------------------------
                    else if (diagnosisResult != null) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("camera_gemini_result_card"),
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F3022)),
                            border = androidx.compose.foundation.BorderStroke(1.5.dp, SovereignGold)
                        ) {
                            Column(modifier = Modifier.padding(18.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = "Diagnosed",
                                            tint = SovereignGold,
                                            modifier = Modifier.size(24.dp)
                                        )
                                        Text(
                                            text = "Gemini AI Pathology Result",
                                            color = AmberLight,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp
                                        )
                                    }

                                    // Severity Badge
                                    val severityColor = when (diagnosisResult.severity.lowercase()) {
                                        "severe" -> Color(0xFFD32F2F)
                                        "high" -> Color(0xFFE65100)
                                        "moderate" -> Color(0xFFF57F17)
                                        else -> Color(0xFF388E3C)
                                    }
                                    Surface(
                                        color = severityColor,
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(
                                            text = "${diagnosisResult.severity.uppercase()} SEVERITY",
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Text(
                                    text = diagnosisResult.diagnosis,
                                    color = Color.White,
                                    fontSize = 19.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = diagnosisResult.summary,
                                    color = Color.White.copy(alpha = 0.9f),
                                    fontSize = 13.5.sp,
                                    lineHeight = 20.sp
                                )

                                if (diagnosisResult.immediateActions.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text = "Top Immediate Action:",
                                        color = AmberLight,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                    Text(
                                        text = "• ${diagnosisResult.immediateActions.first()}",
                                        color = Color.White.copy(alpha = 0.95f),
                                        fontSize = 13.sp
                                    )
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                // Button to view full prescription report
                                Button(
                                    onClick = {
                                        onViewFullDiagnosis?.invoke()
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = AmberSecondary),
                                    shape = RoundedCornerShape(14.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp)
                                        .testTag("camera_view_full_report_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ArrowForward,
                                        contentDescription = "View Details",
                                        tint = Color(0xFF1A1300)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "View Full Prescription & Dosage Report",
                                        color = Color(0xFF1A1300),
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 15.sp
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                OutlinedButton(
                                    onClick = {
                                        capturedBitmap = null
                                    },
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("camera_scan_another_button")
                                ) {
                                    Icon(imageVector = Icons.Default.Refresh, contentDescription = "Retake")
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Inspect Another Crop Leaf (ಮತ್ತೊಂದು ಎಲೆ ಪರೀಕ್ಷಿಸಿ)")
                                }
                            }
                        }
                    }
                    // ---------------------------------------------------------
                    // C. IF READY FOR DIAGNOSIS: ACTION FORM
                    // ---------------------------------------------------------
                    else {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("camera_review_actions_card"),
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F3022)),
                            border = androidx.compose.foundation.BorderStroke(1.dp, SovereignGold.copy(alpha = 0.5f))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Pathology Diagnostic Options",
                                    color = AmberLight,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Confirm target crop or trigger Gemini AI multimodal pathology analysis:",
                                    color = Color.White.copy(alpha = 0.85f),
                                    fontSize = 12.5.sp
                                )

                                Spacer(modifier = Modifier.height(14.dp))

                                // Main CTA: Run Gemini API Diagnosis directly
                                Button(
                                    onClick = {
                                        if (capturedBitmap != null) {
                                            onDiagnoseWithGemini?.invoke(capturedBitmap!!, activeCropName)
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = AmberSecondary),
                                    shape = RoundedCornerShape(14.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(54.dp)
                                        .testTag("camera_diagnose_gemini_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Psychology,
                                        contentDescription = "Gemini AI",
                                        tint = Color(0xFF1A1300),
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column(horizontalAlignment = Alignment.Start) {
                                        Text(
                                            text = "Diagnose with Gemini AI",
                                            color = Color(0xFF1A1300),
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 15.sp
                                        )
                                        Text(
                                            text = "ಜೆಮಿನಿ AI ರೋಗ ತಪಾಸಣೆ",
                                            color = Color(0xFF2E1A00),
                                            fontSize = 11.5.sp
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Secondary Actions: Use Image in Form or Retake
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    OutlinedButton(
                                        onClick = {
                                            capturedBitmap = null
                                        },
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(48.dp)
                                            .testTag("camera_retake_button")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Refresh,
                                            contentDescription = "Retake",
                                            tint = Color.White,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Retake", fontSize = 14.sp)
                                    }

                                    Button(
                                        onClick = {
                                            if (capturedBitmap != null) {
                                                onImageCaptured(capturedBitmap!!)
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(48.dp)
                                            .testTag("camera_use_image_button")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Use",
                                            tint = Color.White,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Use Photo", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Capture high-resolution photo from CameraX ImageCapture and decode to Bitmap safely.
 */
private fun takePhoto(
    imageCapture: ImageCapture,
    executor: Executor,
    onSuccess: (Bitmap) -> Unit,
    onError: (ImageCaptureException) -> Unit
) {
    imageCapture.takePicture(
        executor,
        object : ImageCapture.OnImageCapturedCallback() {
            override fun onCaptureSuccess(image: ImageProxy) {
                try {
                    val bitmap = imageProxyToBitmap(image)
                    onSuccess(bitmap)
                } catch (e: Exception) {
                    onError(ImageCaptureException(ImageCapture.ERROR_UNKNOWN, "Conversion failed: ${e.message}", e))
                } finally {
                    image.close()
                }
            }

            override fun onError(exception: ImageCaptureException) {
                Log.e("CameraCaptureScreen", "Photo capture exception", exception)
                onError(exception)
            }
        }
    )
}

/**
 * Robust conversion from ImageProxy to rotated Bitmap across all CameraX formats.
 */
private fun imageProxyToBitmap(image: ImageProxy): Bitmap {
    val bitmap = try {
        image.toBitmap()
    } catch (e: Throwable) {
        // Fallback for raw buffer decoding
        val buffer = image.planes[0].buffer
        val bytes = ByteArray(buffer.remaining())
        buffer.get(bytes)
        BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }

    val rotationDegrees = image.imageInfo.rotationDegrees
    return if (rotationDegrees != 0 && bitmap != null) {
        val matrix = Matrix().apply { postRotate(rotationDegrees.toFloat()) }
        Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    } else {
        bitmap ?: Bitmap.createBitmap(360, 360, Bitmap.Config.ARGB_8888)
    }
}

/**
 * Generates a realistic high-resolution simulated Karnataka crop leaf with diagnostic lesions
 * ensuring tests, emulators, and offline devices can experience the CameraX & Gemini diagnostic flow.
 */
private fun generateFieldLeafBitmap(cropName: String): Bitmap {
    val size = 480
    val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)

    // Leaf background
    val leafColor = when {
        cropName.contains("Ragi", ignoreCase = true) -> android.graphics.Color.rgb(46, 125, 50)
        cropName.contains("Paddy", ignoreCase = true) -> android.graphics.Color.rgb(67, 160, 71)
        cropName.contains("Tomato", ignoreCase = true) -> android.graphics.Color.rgb(56, 142, 60)
        cropName.contains("Cotton", ignoreCase = true) -> android.graphics.Color.rgb(43, 110, 48)
        else -> android.graphics.Color.rgb(50, 130, 55)
    }
    val leafPaint = Paint().apply { color = leafColor }
    canvas.drawRect(0f, 0f, size.toFloat(), size.toFloat(), leafPaint)

    // Central & lateral veins
    val veinPaint = Paint().apply {
        color = android.graphics.Color.rgb(120, 180, 80)
        strokeWidth = 6f
        style = Paint.Style.STROKE
    }
    canvas.drawLine((size / 2).toFloat(), 0f, (size / 2).toFloat(), size.toFloat(), veinPaint)
    canvas.drawLine((size / 2).toFloat(), 120f, (size * 0.9f), 60f, veinPaint)
    canvas.drawLine((size / 2).toFloat(), 120f, (size * 0.1f), 60f, veinPaint)
    canvas.drawLine((size / 2).toFloat(), 260f, (size * 0.95f), 190f, veinPaint)
    canvas.drawLine((size / 2).toFloat(), 260f, (size * 0.05f), 190f, veinPaint)
    canvas.drawLine((size / 2).toFloat(), 390f, (size * 0.9f), 320f, veinPaint)
    canvas.drawLine((size / 2).toFloat(), 390f, (size * 0.1f), 320f, veinPaint)

    // Lesions & Fungal Pathogen Spores
    val spotPaint = Paint().apply {
        color = when {
            cropName.contains("Maize", ignoreCase = true) -> android.graphics.Color.rgb(240, 240, 210) // bleached stripes
            cropName.contains("Cotton", ignoreCase = true) || cropName.contains("Pomegranate", ignoreCase = true) ->
                android.graphics.Color.rgb(50, 30, 20) // angular dark canker
            cropName.contains("Tomato", ignoreCase = true) ->
                android.graphics.Color.rgb(90, 45, 20) // concentric early blight rings
            else -> android.graphics.Color.rgb(160, 70, 25) // blast / rust spindle lesions
        }
    }

    val haloPaint = Paint().apply {
        color = android.graphics.Color.rgb(235, 215, 60) // chlorotic yellow halo
        style = Paint.Style.STROKE
        strokeWidth = 10f
    }

    // Draw lesions
    canvas.drawCircle(150f, 160f, 42f, spotPaint)
    canvas.drawCircle(150f, 160f, 52f, haloPaint)

    canvas.drawCircle(320f, 240f, 55f, spotPaint)
    canvas.drawCircle(320f, 240f, 68f, haloPaint)

    canvas.drawCircle(220f, 360f, 38f, spotPaint)
    canvas.drawCircle(220f, 360f, 48f, haloPaint)

    canvas.drawCircle(370f, 110f, 30f, spotPaint)
    canvas.drawCircle(370f, 110f, 38f, haloPaint)

    return bitmap
}
