package com.example.ui.components

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppLanguage
import com.example.data.model.VoiceChatMessage
import com.example.ui.viewmodel.RaithaDrishtiViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun VoiceAssistantBottomSheet(
    viewModel: RaithaDrishtiViewModel,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val context = LocalContext.current
    val currentLang by viewModel.currentLanguage.collectAsState()
    val messages by viewModel.voiceChatMessages.collectAsState()
    val isThinking by viewModel.isVoiceProcessing.collectAsState()
    val listState = rememberLazyListState()

    var manualInput by remember { mutableStateOf("") }
    var isListeningMic by remember { mutableStateOf(false) }

    // Android Native TextToSpeech Engine
    var ttsEngine by remember { mutableStateOf<TextToSpeech?>(null) }
    var isSpeakingText by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        val tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                // Initialize default language
                try {
                    val locale = when (currentLang) {
                        AppLanguage.KANNADA -> Locale("kn", "IN")
                        AppLanguage.HINDI -> Locale("hi", "IN")
                        AppLanguage.ENGLISH -> Locale("en", "IN")
                    }
                    ttsEngine?.language = locale
                } catch (e: Exception) {
                    // fallback
                }
            }
        }
        ttsEngine = tts

        onDispose {
            tts.stop()
            tts.shutdown()
        }
    }

    // Scroll to latest message on receive
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    // Speech Recognizer Launcher
    val speechLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        isListeningMic = false
        if (result.resultCode == Activity.RESULT_OK) {
            val spoken = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull()
            if (!spoken.isNullOrBlank()) {
                viewModel.sendVoiceQuery(spoken)
            }
        }
    }

    fun launchVoiceInput() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, currentLang.localeCode)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, currentLang.localeCode)
            putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, currentLang.localeCode)
            putExtra(RecognizerIntent.EXTRA_PROMPT, when (currentLang) {
                AppLanguage.KANNADA -> "ನಿಮ್ಮ ಬೆಳೆಯ ಬಗ್ಗೆ ಮಾತನಾಡಿ..."
                AppLanguage.HINDI -> "अपनी फसल के बारे में बोलें..."
                AppLanguage.ENGLISH -> "Ask about your crop or weather..."
            })
        }
        try {
            isListeningMic = true
            speechLauncher.launch(intent)
        } catch (e: Exception) {
            isListeningMic = false
            Toast.makeText(context, "Voice input not supported on this device. You can type below.", Toast.LENGTH_SHORT).show()
        }
    }

    fun speakAloud(text: String, lang: AppLanguage) {
        ttsEngine?.let { engine ->
            val loc = when (lang) {
                AppLanguage.KANNADA -> Locale("kn", "IN")
                AppLanguage.HINDI -> Locale("hi", "IN")
                AppLanguage.ENGLISH -> Locale("en", "IN")
            }
            engine.language = loc
            engine.speak(text, TextToSpeech.QUEUE_FLUSH, null, "agri_voice_${System.currentTimeMillis()}")
            isSpeakingText = true
        }
    }

    fun stopSpeaking() {
        ttsEngine?.stop()
        isSpeakingText = false
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = null,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.92f)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // Header with Multilingual Switcher & Close
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(Color(0xFF1B5E20), Color(0xFF4CAF50))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "AI Voice",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = when (currentLang) {
                                AppLanguage.KANNADA -> "ರೈತ ದೃಷ್ಟಿ ಧ್ವನಿ ಸಹಾಯಕ"
                                AppLanguage.HINDI -> "किसान दृष्टि आवाज सहायक"
                                AppLanguage.ENGLISH -> "RaithaDrishti Voice AI"
                            },
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = when (currentLang) {
                                AppLanguage.KANNADA -> "ಕನ್ನಡ • ಹಿಂದಿ • ಇಂಗ್ಲಿಷ್‌ನಲ್ಲಿ ಮಾತನಾಡಿ"
                                AppLanguage.HINDI -> "कन्नड़ • हिंदी • अंग्रेजी में बोलें"
                                AppLanguage.ENGLISH -> "Powered by Gemini 2.5 Agronomist"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { viewModel.clearVoiceChat(); stopSpeaking() }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Clear Chat",
                            tint = MaterialTheme.colorScheme.outline
                        )
                    }
                    IconButton(onClick = { stopSpeaking(); onDismiss() }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            // Quick Language Switcher Bar inside the dialog
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AppLanguage.values().forEach { lang ->
                        val isSelected = currentLang == lang
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                viewModel.setLanguage(lang)
                                stopSpeaking()
                            },
                            label = {
                                Text(
                                    text = "${lang.nativeName} (${lang.displayName})",
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 12.sp
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFF2E7D32),
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }

            // Quick Farmer Query Suggestions
            val quickQuestions = when (currentLang) {
                AppLanguage.KANNADA -> listOf(
                    "ಚಿಕ್ಕಮಗಳೂರಿನಲ್ಲಿ ಕುಂಬಳಕಾಯಿ ಬೆಲೆ ಎಷ್ಟು?",
                    "ಮೆಕ್ಕೆಜೋಳದ ಬಿಳಿ ಎಲೆಗೆ ಔಷಧಿ ಏನು?",
                    "ಬೆಳೆ ವೇಗವಾಗಿ ಬೆಳೆಯಲು ಯಾವ ಗೊಬ್ಬರ?",
                    "ಇಂದು ಮಳೆ ಬರುತ್ತಾ? ಸ್ಪ್ರೇ ಮಾಡಬಹುದೇ?",
                    "ಇಂದಿನ ಟೊಮೆಟೊ ಮಾರುಕಟ್ಟೆ ದರ ಎಷ್ಟು?"
                )
                AppLanguage.HINDI -> listOf(
                    "चिकमगलूर में कद्दू का क्या भाव है?",
                    "मक्के की सफेद पत्ती का क्या इलाज है?",
                    "फसल तेज बढ़ाने के लिए कौन सी खाद दें?",
                    "आज मौसम कैसा रहेगा? छिड़काव करें?",
                    "टमाटर का सबसे बढ़िया मंडी भाव क्या है?"
                )
                AppLanguage.ENGLISH -> listOf(
                    "What is pumpkin price in Chikmagalur?",
                    "How to cure patchy white leaves in maize?",
                    "Which fertilizer gives fast growth boost?",
                    "Can I spray pesticide in today's weather?",
                    "What are today's tomato APMC prices?"
                )
            }

            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                quickQuestions.forEach { prompt ->
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                        modifier = Modifier.clickable {
                            viewModel.sendVoiceQuery(prompt)
                        }
                    ) {
                        Text(
                            text = prompt,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Messages Stream
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(messages, key = { it.id }) { msg ->
                    ChatBubble(
                        message = msg,
                        onSpeak = { speakAloud(msg.text, msg.language) },
                        onStop = { stopSpeaking() },
                        isSpeaking = isSpeakingText
                    )
                }

                if (isThinking) {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp,
                                color = Color(0xFF2E7D32)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = when (currentLang) {
                                    AppLanguage.KANNADA -> "ರೈತ ದೃಷ್ಟಿ AI ವಿಶ್ಲೇಷಿಸುತ್ತಿದೆ..."
                                    AppLanguage.HINDI -> "किसान दृष्टि AI उत्तर तैयार कर रहा है..."
                                    AppLanguage.ENGLISH -> "AI Agronomist analyzing field query..."
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                }
            }

            // Central Voice Microphone & Manual Input Controls
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Pulsing Mic Button
                val infiniteTransition = rememberInfiniteTransition(label = "pulse")
                val pulseScale by infiniteTransition.animateFloat(
                    initialValue = 1.0f,
                    targetValue = if (isListeningMic) 1.25f else 1.0f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(600),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "pulseScale"
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(68.dp)
                            .scale(pulseScale)
                            .clip(CircleShape)
                            .background(
                                if (isListeningMic) Color(0xFFD32F2F)
                                else Color(0xFF2E7D32)
                            )
                            .clickable { launchVoiceInput() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "Speak Now",
                            tint = Color.White,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }

                Text(
                    text = if (isListeningMic) {
                        when (currentLang) {
                            AppLanguage.KANNADA -> "ಕೇಳಿಸಿಕೊಳ್ಳುತ್ತಿದ್ದೇವೆ... ಮಾತನಾಡಿ"
                            AppLanguage.HINDI -> "सुन रहे हैं... बोलिए"
                            AppLanguage.ENGLISH -> "Listening... Please speak"
                        }
                    } else {
                        when (currentLang) {
                            AppLanguage.KANNADA -> "ಮಾತನಾಡಲು ಮೈಕ್ ಒತ್ತಿರಿ (ಅಥವಾ ಕೆಳಗೆ ಬರೆಯಿರಿ)"
                            AppLanguage.HINDI -> "बोलने के लिए माइक दबाएं (या नीचे लिखें)"
                            AppLanguage.ENGLISH -> "Tap Mic to Speak (or Type below)"
                        }
                    },
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Medium,
                    color = if (isListeningMic) Color(0xFFD32F2F) else MaterialTheme.colorScheme.outline,
                    modifier = Modifier.padding(top = 6.dp, bottom = 8.dp)
                )

                // Bottom typing input fallback
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = manualInput,
                        onValueChange = { manualInput = it },
                        modifier = Modifier.weight(1f),
                        placeholder = {
                            Text(
                                text = when (currentLang) {
                                    AppLanguage.KANNADA -> "ಪ್ರಶ್ನೆ ಟೈಪ್ ಮಾಡಿ..."
                                    AppLanguage.HINDI -> "सवाल टाइप करें..."
                                    AppLanguage.ENGLISH -> "Type question..."
                                },
                                fontSize = 13.sp
                            )
                        },
                        shape = RoundedCornerShape(24.dp),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = {
                            if (manualInput.isNotBlank()) {
                                viewModel.sendVoiceQuery(manualInput)
                                manualInput = ""
                            }
                        },
                        modifier = Modifier
                            .size(46.dp)
                            .background(Color(0xFF2E7D32), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = "Send",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ChatBubble(
    message: VoiceChatMessage,
    onSpeak: () -> Unit,
    onStop: () -> Unit,
    isSpeaking: Boolean
) {
    val isUser = message.sender == "user"
    var speakingThis by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(0.85f),
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isUser) 16.dp else 4.dp,
                bottomEnd = if (isUser) 4.dp else 16.dp
            ),
            colors = CardDefaults.cardColors(
                containerColor = if (isUser)
                    MaterialTheme.colorScheme.primaryContainer
                else
                    MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isUser) "Farmer" else "RaithaDrishti AI",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isUser) MaterialTheme.colorScheme.primary else Color(0xFF2E7D32)
                    )

                    if (!isUser) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = {
                                    if (speakingThis) {
                                        onStop()
                                        speakingThis = false
                                    } else {
                                        onSpeak()
                                        speakingThis = true
                                    }
                                },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = if (speakingThis) Icons.Default.Stop else Icons.Default.VolumeUp,
                                    contentDescription = "Read Aloud",
                                    tint = Color(0xFF2E7D32),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = message.text,
                    style = MaterialTheme.typography.bodyMedium,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
fun VoiceAssistantDialog(
    viewModel: RaithaDrishtiViewModel,
    onDismiss: () -> Unit
) {
    VoiceAssistantBottomSheet(
        viewModel = viewModel,
        onDismiss = onDismiss
    )
}
