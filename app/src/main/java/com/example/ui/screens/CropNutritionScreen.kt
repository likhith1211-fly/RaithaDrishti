package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppLanguage
import com.example.data.model.FertilizerRecommendation
import com.example.ui.viewmodel.RaithaDrishtiViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CropNutritionScreen(viewModel: RaithaDrishtiViewModel) {
    val currentLang by viewModel.currentLanguage.collectAsState()
    val searchQuery by viewModel.fertilizerSearch.collectAsState()
    val selectedCategory by viewModel.selectedFertilizerCategory.collectAsState()
    val selectedCrop by viewModel.selectedFertilizerCrop.collectAsState()

    var calculatingFertilizer by remember { mutableStateOf<FertilizerRecommendation?>(null) }
    var enteredAcres by remember { mutableStateOf("3.0") }

    val categories = listOf(
        "All" to when (currentLang) {
            AppLanguage.KANNADA -> "ಎಲ್ಲವೂ"
            AppLanguage.HINDI -> "सभी"
            AppLanguage.ENGLISH -> "All"
        },
        "Fast Growth Booster" to when (currentLang) {
            AppLanguage.KANNADA -> "⚡ ವೇಗದ ಬೆಳವಣಿಗೆ"
            AppLanguage.HINDI -> "⚡ तेज बढ़वार"
            AppLanguage.ENGLISH -> "⚡ Fast Growth"
        },
        "Chlorosis & White Leaf Cure" to when (currentLang) {
            AppLanguage.KANNADA -> "🌿 ಬಿಳಿ ಎಲೆ ನಿವಾರಕ (Zinc)"
            AppLanguage.HINDI -> "🌿 सफेद पत्ती का इलाज"
            AppLanguage.ENGLISH -> "🌿 White Leaf Cure"
        },
        "Organic Vitalizer" to when (currentLang) {
            AppLanguage.KANNADA -> "🌱 ಸಾವಯವ ಟಾನಿಕ್"
            AppLanguage.HINDI -> "🌱 जैविक टॉनिक"
            AppLanguage.ENGLISH -> "🌱 Organic Tonic"
        },
        "Wanted Crop Chemical" to when (currentLang) {
            AppLanguage.KANNADA -> "🧪 ಅನುಮೋದಿತ ಕೀಟ-ಕಳೆನಾಶಕ"
            AppLanguage.HINDI -> "🧪 प्रमाणित रसायन"
            AppLanguage.ENGLISH -> "🧪 Wanted Chemicals"
        }
    )

    val crops = listOf(
        "All" to when (currentLang) {
            AppLanguage.KANNADA -> "ಎಲ್ಲ ಬೆಳೆಗಳು"
            AppLanguage.HINDI -> "सभी फसलें"
            AppLanguage.ENGLISH -> "All Crops"
        },
        "Maize" to when (currentLang) {
            AppLanguage.KANNADA -> "ಮೆಕ್ಕೆಜೋಳ (Maize)"
            AppLanguage.HINDI -> "मक्का (Maize)"
            AppLanguage.ENGLISH -> "Maize"
        },
        "Tomato" to when (currentLang) {
            AppLanguage.KANNADA -> "ಟೊಮೆಟೊ (Tomato)"
            AppLanguage.HINDI -> "टमाटर (Tomato)"
            AppLanguage.ENGLISH -> "Tomato"
        },
        "Potato" to when (currentLang) {
            AppLanguage.KANNADA -> "ಆಲೂಗಡ್ಡೆ (Potato)"
            AppLanguage.HINDI -> "आलू (Potato)"
            AppLanguage.ENGLISH -> "Potato"
        },
        "Pumpkin" to when (currentLang) {
            AppLanguage.KANNADA -> "ಕುಂಬಳಕಾಯಿ (Pumpkin)"
            AppLanguage.HINDI -> "कद्दू (Pumpkin)"
            AppLanguage.ENGLISH -> "Pumpkin"
        },
        "Paddy" to when (currentLang) {
            AppLanguage.KANNADA -> "ಭತ್ತ (Paddy)"
            AppLanguage.HINDI -> "धान (Paddy)"
            AppLanguage.ENGLISH -> "Paddy"
        },
        "Arecanut" to when (currentLang) {
            AppLanguage.KANNADA -> "ಅಡಿಕೆ (Arecanut)"
            AppLanguage.HINDI -> "सुपारी (Arecanut)"
            AppLanguage.ENGLISH -> "Arecanut"
        },
        "Coffee" to when (currentLang) {
            AppLanguage.KANNADA -> "ಕಾಫಿ (Coffee)"
            AppLanguage.HINDI -> "कॉफ़ी (Coffee)"
            AppLanguage.ENGLISH -> "Coffee"
        }
    )

    val filteredList = viewModel.allFertilizers.filter { fert ->
        val matchesCategory = selectedCategory == "All" || fert.category == selectedCategory
        val matchesCrop = selectedCrop == "All" || fert.targetCrops.contains(selectedCrop) || fert.targetCrops.contains("All Crops")
        val matchesSearch = searchQuery.isBlank() ||
                fert.name.contains(searchQuery, ignoreCase = true) ||
                fert.kannadaName.contains(searchQuery, ignoreCase = true) ||
                fert.hindiName.contains(searchQuery, ignoreCase = true) ||
                fert.primaryBenefit.contains(searchQuery, ignoreCase = true)

        matchesCategory && matchesCrop && matchesSearch
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Royal Banner Header
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = com.example.ui.theme.ForestGreenDark),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, com.example.ui.theme.AmberLight.copy(alpha = 0.5f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .background(Color.White.copy(alpha = 0.12f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Science,
                                contentDescription = null,
                                tint = com.example.ui.theme.AmberLight,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Text(
                            text = when (currentLang) {
                                AppLanguage.KANNADA -> "ಬೆಳೆ ಪೋಷಕಾಂಶ ಮತ್ತು ವೇಗದ ಬೆಳವಣಿಗೆ"
                                AppLanguage.HINDI -> "फसल पोषण व तीव्र वृद्धि रसायन"
                                AppLanguage.ENGLISH -> "Crop Nutrition & Fast Growth"
                            },
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 20.sp
                            ),
                            color = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = when (currentLang) {
                            AppLanguage.KANNADA -> "ಮೆಕ್ಕೆಜೋಳದ ಬಿಳಿ ಎಲೆ ನಿವಾರಣೆ (Zinc), ತ್ವರಿತ ಬೆಳವಣಿಗೆಯ ರಸಗೊಬ್ಬರಗಳು (19:19:19, Urea) ಮತ್ತು ಅನುಮೋದಿತ ಕೀಟ-ಶಿಲೀಂಧ್ರನಾಶಕಗಳ ಸಮಗ್ರ ಮಾರ್ಗದರ್ಶಿ."
                            AppLanguage.HINDI -> "मक्के की सफेद पत्ती का इलाज (जिंक), तेज वानस्पतिक बढ़वार खाद (19:19:19, यूरिया) एवं अनुशंसित कीटनाशक व फफूंदनाशक।"
                            AppLanguage.ENGLISH -> "Scientific dosages for Maize white leaf cure (Zinc EDTA), fast foliar vegetative bursts (19:19:19 NPK, GA3), and high-potency crop protectants."
                        },
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 15.sp,
                            lineHeight = 22.sp
                        ),
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }
        }

        // Search Bar
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.setFertilizerSearch(it) },
                modifier = Modifier.fillMaxWidth(),
                textStyle = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp),
                placeholder = {
                    Text(
                        text = when (currentLang) {
                            AppLanguage.KANNADA -> "ಗೊಬ್ಬರ ಅಥವಾ ರಾಸಾಯನಿಕ ಹುಡುಕಿ (ಉದಾ: Zinc, Urea, 19:19:19)..."
                            AppLanguage.HINDI -> "खाद या रसायन खोजें (जैसे: जिंक, यूरिया, 19:19:19)..."
                            AppLanguage.ENGLISH -> "Search fertilizer or chemical (e.g., Zinc, Urea, 19:19:19)..."
                        },
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = com.example.ui.theme.ForestGreenPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                },
                shape = RoundedCornerShape(16.dp),
                singleLine = true
            )
        }

        // Category Filter Chips
        item {
            Text(
                text = when (currentLang) {
                    AppLanguage.KANNADA -> "ವರ್ಗವಾರು ಆಯ್ಕೆ:"
                    AppLanguage.HINDI -> "श्रेणी चुनें:"
                    AppLanguage.ENGLISH -> "Select Category:"
                },
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.5.sp
                ),
                color = MaterialTheme.colorScheme.onSurface
            )

            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { (catKey, catLabel) ->
                    val isSelected = selectedCategory == catKey
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.setFertilizerCategory(catKey) },
                        label = {
                            Text(
                                catLabel,
                                fontSize = 14.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = com.example.ui.theme.ForestGreenPrimary,
                            selectedLabelColor = Color.White
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = if (isSelected) com.example.ui.theme.AmberLight else com.example.ui.theme.LightBorder
                        ),
                        shape = RoundedCornerShape(14.dp)
                    )
                }
            }
        }

        // Crop Filter Chips
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.FilterList,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = com.example.ui.theme.ForestGreenPrimary
                )
                Text(
                    text = when (currentLang) {
                        AppLanguage.KANNADA -> "ಬೆಳೆ ಆಯ್ಕೆ:"
                        AppLanguage.HINDI -> "फसल फ़िल्टर:"
                        AppLanguage.ENGLISH -> "Crop Filter:"
                    },
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.5.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                crops.forEach { (cKey, cLabel) ->
                    val isSelected = selectedCrop == cKey
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.setFertilizerCrop(cKey) },
                        label = {
                            Text(
                                cLabel,
                                fontSize = 13.5.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                modifier = Modifier.padding(vertical = 2.dp)
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = com.example.ui.theme.ForestGreenPrimary,
                            selectedLabelColor = Color.White
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = if (isSelected) com.example.ui.theme.AmberLight else com.example.ui.theme.LightBorder
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }
        }

        // Results count
        item {
            Text(
                text = "${filteredList.size} " + when (currentLang) {
                    AppLanguage.KANNADA -> "ಸೂತ್ರಗಳು ಲಭ್ಯವಿದೆ"
                    AppLanguage.HINDI -> "अनुशंसित उत्पाद उपलब्ध"
                    AppLanguage.ENGLISH -> "Formulations Available"
                },
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 14.5.sp,
                    fontWeight = FontWeight.SemiBold
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // List of Fertilizer & Chemical Cards
        items(filteredList, key = { it.id }) { fert ->
            FertilizerDetailCard(
                fert = fert,
                language = currentLang,
                onCalculate = {
                    calculatingFertilizer = fert
                }
            )
        }
    }

    // Interactive Farm Acreage Fertilizer Calculator Dialog
    calculatingFertilizer?.let { fert ->
        val acresNum = enteredAcres.toDoubleOrNull() ?: 1.0
        AlertDialog(
            onDismissRequest = { calculatingFertilizer = null },
            icon = {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(com.example.ui.theme.AmberLight.copy(alpha = 0.2f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Calculate,
                        contentDescription = null,
                        tint = com.example.ui.theme.ForestGreenPrimary,
                        modifier = Modifier.size(30.dp)
                    )
                }
            },
            title = {
                Text(
                    text = when (currentLang) {
                        AppLanguage.KANNADA -> "ನಿಮ್ಮ ಜಮೀನಿನ ಗೊಬ್ಬರ ಲೆಕ್ಕಾಚಾರ"
                        AppLanguage.HINDI -> "खेत के लिए खाद की मात्रा"
                        AppLanguage.ENGLISH -> "Farm Acreage Dosage Calculator"
                    },
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = fert.name,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 18.sp,
                        color = com.example.ui.theme.ForestGreenPrimary
                    )
                    Text(
                        text = when (currentLang) {
                            AppLanguage.KANNADA -> fert.kannadaName
                            AppLanguage.HINDI -> fert.hindiName
                            AppLanguage.ENGLISH -> fert.category
                        },
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = enteredAcres,
                        onValueChange = { enteredAcres = it },
                        label = {
                            Text(
                                when (currentLang) {
                                    AppLanguage.KANNADA -> "ಜಮೀನಿನ ವಿಸ್ತೀರ್ಣ (ಎಕರೆ)"
                                    AppLanguage.HINDI -> "जमीन का क्षेत्रफल (एकड़)"
                                    AppLanguage.ENGLISH -> "Land Size (in Acres)"
                                },
                                fontSize = 14.5.sp
                            )
                        },
                        textStyle = MaterialTheme.typography.titleMedium.copy(fontSize = 17.sp, fontWeight = FontWeight.Bold),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
                        shape = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, com.example.ui.theme.LightBorder)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = when (currentLang) {
                                        AppLanguage.KANNADA -> "ಪ್ರಮಾಣ (1 ಎಕರೆಗೆ):"
                                        AppLanguage.HINDI -> "मात्रा (1 एकड़):"
                                        AppLanguage.ENGLISH -> "Per Acre Rate:"
                                    },
                                    fontSize = 14.5.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = fert.dosagePerAcre,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = com.example.ui.theme.ForestGreenPrimary
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = when (currentLang) {
                                        AppLanguage.KANNADA -> "ಸಿಂಪಡಣಾ ನೀರು:"
                                        AppLanguage.HINDI -> "पानी की मात्रा:"
                                        AppLanguage.ENGLISH -> "Water Volume:"
                                    },
                                    fontSize = 14.5.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "${(acresNum * 150).toInt()} - ${(acresNum * 200).toInt()} Litres",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1565C0)
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = "💡 ${fert.dilutionPerLiter}",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { calculatingFertilizer = null },
                    modifier = Modifier.height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = com.example.ui.theme.ForestGreenPrimary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        when (currentLang) {
                            AppLanguage.KANNADA -> "ಸರಿ (ಲೆಕ್ಕಾಚಾರ ಪೂರ್ಣ)"
                            AppLanguage.HINDI -> "ठीक है"
                            AppLanguage.ENGLISH -> "Done"
                        },
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        )
    }
}

@Composable
fun FertilizerDetailCard(
    fert: FertilizerRecommendation,
    language: AppLanguage,
    onCalculate: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, com.example.ui.theme.LightBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (fert.curesWhiteLeaves) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFDCFCE7),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF86EFAC))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Color(0xFF166534),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = when (language) {
                                    AppLanguage.KANNADA -> "ಬಿಳಿ ಎಲೆ ರೋಗ ನಿವಾರಕ"
                                    AppLanguage.HINDI -> "सफेद पत्ती नाशक"
                                    AppLanguage.ENGLISH -> "Cures White Leaves"
                                },
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF166534)
                            )
                        }
                    }
                } else if (fert.isFastGrowthBooster) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFFEF3C7),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFCD34D))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.FlashOn,
                                contentDescription = null,
                                tint = Color(0xFFB45309),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = when (language) {
                                    AppLanguage.KANNADA -> "ವೇಗದ ಬೆಳವಣಿಗೆ ಬೂಸ್ಟರ್"
                                    AppLanguage.HINDI -> "फास्ट ग्रोथ बूस्टर"
                                    AppLanguage.ENGLISH -> "Fast Growth Booster"
                                },
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFB45309)
                            )
                        }
                    }
                } else {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFEDE7F6),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFD1C4E9))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Science,
                                contentDescription = null,
                                tint = Color(0xFF512DA8),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            val localizedCategory = when (fert.category) {
                                "Fast Growth Booster" -> when (language) {
                                    AppLanguage.KANNADA -> "ವೇಗದ ಬೆಳವಣಿಗೆ"
                                    AppLanguage.HINDI -> "तेज बढ़वार"
                                    AppLanguage.ENGLISH -> "Fast Growth"
                                }
                                "Chlorosis & White Leaf Cure" -> when (language) {
                                    AppLanguage.KANNADA -> "ಬಿಳಿ ಎಲೆ ನಿವಾರಕ"
                                    AppLanguage.HINDI -> "सफेद पत्ती इलाज"
                                    AppLanguage.ENGLISH -> "White Leaf Cure"
                                }
                                "Organic Vitalizer" -> when (language) {
                                    AppLanguage.KANNADA -> "ಸಾವಯವ ಟಾನಿಕ್"
                                    AppLanguage.HINDI -> "जैविक टॉनिक"
                                    AppLanguage.ENGLISH -> "Organic Tonic"
                                }
                                "Wanted Crop Chemical" -> when (language) {
                                    AppLanguage.KANNADA -> "ಅನುಮೋದಿತ ರಾಸಾಯನಿಕ"
                                    AppLanguage.HINDI -> "प्रमाणित रसायन"
                                    AppLanguage.ENGLISH -> "Wanted Chemical"
                                }
                                else -> fert.category
                            }
                            Text(
                                text = localizedCategory,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF512DA8)
                            )
                        }
                    }
                }

                val localizedCrops = fert.targetCrops.take(3).map { crop ->
                    when (crop) {
                        "Maize" -> when (language) {
                            AppLanguage.KANNADA -> "ಮೆಕ್ಕೆಜೋಳ"
                            AppLanguage.HINDI -> "मक्का"
                            AppLanguage.ENGLISH -> "Maize"
                        }
                        "Tomato" -> when (language) {
                            AppLanguage.KANNADA -> "ಟೊಮೆಟೊ"
                            AppLanguage.HINDI -> "टमाटर"
                            AppLanguage.ENGLISH -> "Tomato"
                        }
                        "Potato" -> when (language) {
                            AppLanguage.KANNADA -> "ಆಲೂಗಡ್ಡೆ"
                            AppLanguage.HINDI -> "आलू"
                            AppLanguage.ENGLISH -> "Potato"
                        }
                        "Pumpkin" -> when (language) {
                            AppLanguage.KANNADA -> "ಕುಂಬಳಕಾಯಿ"
                            AppLanguage.HINDI -> "कद्दू"
                            AppLanguage.ENGLISH -> "Pumpkin"
                        }
                        "All Crops" -> when (language) {
                            AppLanguage.KANNADA -> "ಎಲ್ಲ ಬೆಳೆಗಳು"
                            AppLanguage.HINDI -> "सभी फसलें"
                            AppLanguage.ENGLISH -> "All Crops"
                        }
                        "Paddy" -> when (language) {
                            AppLanguage.KANNADA -> "ಭತ್ತ"
                            AppLanguage.HINDI -> "धान"
                            AppLanguage.ENGLISH -> "Paddy"
                        }
                        "Arecanut" -> when (language) {
                            AppLanguage.KANNADA -> "ಅಡಿಕೆ"
                            AppLanguage.HINDI -> "सुपारी"
                            AppLanguage.ENGLISH -> "Arecanut"
                        }
                        "Coffee" -> when (language) {
                            AppLanguage.KANNADA -> "ಕಾಫಿ"
                            AppLanguage.HINDI -> "कॉफ़ी"
                            AppLanguage.ENGLISH -> "Coffee"
                        }
                        else -> crop
                    }
                }.joinToString(", ")

                Text(
                    text = localizedCrops,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Primary Name
            Text(
                text = fert.name,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 19.sp
                ),
                color = MaterialTheme.colorScheme.onSurface
            )

            // Localized Name
            val localizedName = when (language) {
                AppLanguage.KANNADA -> fert.kannadaName
                AppLanguage.HINDI -> fert.hindiName
                AppLanguage.ENGLISH -> ""
            }
            if (localizedName.isNotBlank()) {
                Text(
                    text = localizedName,
                    fontSize = 15.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = com.example.ui.theme.ForestGreenPrimary,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Dosages Grid
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                border = androidx.compose.foundation.BorderStroke(1.dp, com.example.ui.theme.LightBorder)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = when (language) {
                                AppLanguage.KANNADA -> "ಎಕರೆಗೆ ಪ್ರಮಾಣ:"
                                AppLanguage.HINDI -> "प्रति एकड़:"
                                AppLanguage.ENGLISH -> "Dosage / Acre:"
                            },
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = fert.dosagePerAcre,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = com.example.ui.theme.ForestGreenPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = when (language) {
                                AppLanguage.KANNADA -> "ನೀರಿನ ಅನುಪಾತ:"
                                AppLanguage.HINDI -> "पानी में अनुपात:"
                                AppLanguage.ENGLISH -> "Per Litre Water:"
                            },
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = fert.dilutionPerLiter,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1565C0)
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    val localizedMethod = when {
                        fert.applicationMethod.contains("Foliar", ignoreCase = true) -> when (language) {
                            AppLanguage.KANNADA -> "ಎಲೆ ಸಿಂಪಡಣೆ (Foliar Spray)"
                            AppLanguage.HINDI -> "पर्णीय छिड़काव (Foliar Spray)"
                            AppLanguage.ENGLISH -> fert.applicationMethod
                        }
                        fert.applicationMethod.contains("Drip", ignoreCase = true) || fert.applicationMethod.contains("Soil", ignoreCase = true) -> when (language) {
                            AppLanguage.KANNADA -> "ಬುಡಕ್ಕೆ / ಹನಿ ನೀರಾವರಿ (Soil/Drip)"
                            AppLanguage.HINDI -> "जड़ों में / ड्रिप सिंचाई"
                            AppLanguage.ENGLISH -> fert.applicationMethod
                        }
                        fert.applicationMethod.contains("Basal", ignoreCase = true) -> when (language) {
                            AppLanguage.KANNADA -> "ಬಿತ್ತನೆ ಸಮಯದಲ್ಲಿ ಮಣ್ಣಿಗೆ (Basal)"
                            AppLanguage.HINDI -> "बुवाई के समय मिट्टी में"
                            AppLanguage.ENGLISH -> fert.applicationMethod
                        }
                        else -> fert.applicationMethod
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = when (language) {
                                AppLanguage.KANNADA -> "ಕ್ರಮ:"
                                AppLanguage.HINDI -> "विधि:"
                                AppLanguage.ENGLISH -> "Method:"
                            },
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = localizedMethod,
                            fontSize = 14.5.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Primary Benefit
            Text(
                text = fert.primaryBenefit,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 15.sp,
                    lineHeight = 22.sp
                ),
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Precaution / Stage
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = null,
                    tint = Color(0xFFD32F2F),
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = fert.precautions,
                    fontSize = 13.5.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 19.sp
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action: Calculator
            OutlinedButton(
                onClick = onCalculate,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, com.example.ui.theme.ForestGreenPrimary)
            ) {
                Icon(
                    imageVector = Icons.Default.Calculate,
                    contentDescription = null,
                    tint = com.example.ui.theme.ForestGreenPrimary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = when (language) {
                        AppLanguage.KANNADA -> "ನನ್ನ ಜಮೀನಿಗೆ ಲೆಕ್ಕಹಾಕಿ (ಎಕರೆ ಲೆಕ್ಕ)"
                        AppLanguage.HINDI -> "मेरे खेत के लिए मात्रा निकालें"
                        AppLanguage.ENGLISH -> "Calculate for my Farm"
                    },
                    fontSize = 14.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = com.example.ui.theme.ForestGreenPrimary
                )
            }
        }
    }
}
