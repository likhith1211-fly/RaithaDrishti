package com.example.ui.screens

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
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Sell
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingFlat
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppLanguage
import com.example.data.model.DailyPriceRecord
import com.example.data.model.MandiPriceInfo
import com.example.data.model.SellDecision
import com.example.data.model.WeeklyMarketAnalysis
import com.example.ui.components.MarketPriceChart
import com.example.ui.theme.ForestGreenPrimary
import com.example.ui.theme.MandiBengaluruColor
import com.example.ui.theme.MandiChikkamagaluruColor
import com.example.ui.theme.MandiMysuruColor
import com.example.ui.viewmodel.RaithaDrishtiViewModel

@Composable
fun MarketArbitrageScreen(
    viewModel: RaithaDrishtiViewModel,
    modifier: Modifier = Modifier
) {
    val currentLang by viewModel.currentLanguage.collectAsState()
    val selectedCommodity by viewModel.selectedCommodity.collectAsState()
    val analytics by viewModel.marketAnalytics.collectAsState()
    val weeklyAnalysis by viewModel.weeklyMarketAnalysis.collectAsState()
    val commodities = viewModel.supportedCommodities

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // 1. Prominent Today's Date & Live APMC Session Bar
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("market_live_date_banner"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = com.example.ui.theme.ForestGreenDark),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, com.example.ui.theme.AmberLight.copy(alpha = 0.6f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
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
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = "Date",
                            tint = com.example.ui.theme.AmberLight,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Column {
                        Text(
                            text = when (currentLang) {
                                AppLanguage.KANNADA -> "ಇಂದಿನ ಅಧಿಕೃತ ಮಾರುಕಟ್ಟೆ ದಿನಾಂಕ"
                                AppLanguage.HINDI -> "आज की आधिकारिक तारीख"
                                AppLanguage.ENGLISH -> "Official Market Date"
                            },
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.5.sp),
                            color = com.example.ui.theme.AmberLight.copy(alpha = 0.9f)
                        )
                        Text(
                            text = when (currentLang) {
                                AppLanguage.KANNADA -> weeklyAnalysis.formattedDateLongKn
                                AppLanguage.HINDI -> weeklyAnalysis.formattedDateLongHi
                                AppLanguage.ENGLISH -> weeklyAnalysis.formattedDateLongEn
                            },
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.5.sp
                            ),
                            color = Color.White
                        )
                    }
                }

                // Live Market Indicator Pill
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFF065F46),
                    border = androidx.compose.foundation.BorderStroke(1.dp, com.example.ui.theme.AmberLight)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(9.dp)
                                .background(Color(0xFF34D399), CircleShape)
                        )
                        Text(
                            text = when (currentLang) {
                                AppLanguage.KANNADA -> "ಲೈವ್ ಎಪಿಎಂಸಿ"
                                AppLanguage.HINDI -> "लाइव मंडी"
                                AppLanguage.ENGLISH -> "Live APMC"
                            },
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }

        // 2. Hero Header Card (Royal Forest Green & Gold)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("market_arbitrage_hero_card"),
            colors = CardDefaults.cardColors(containerColor = com.example.ui.theme.ForestGreenPrimary),
            shape = RoundedCornerShape(18.dp),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, com.example.ui.theme.AmberLight.copy(alpha = 0.5f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = when (currentLang) {
                        AppLanguage.KANNADA -> "ಮಾರುಕಟ್ಟೆ ದರ ವಿಶ್ಲೇಷಣೆ • ಎಪಿಎಂಸಿ ಕರ್ನಾಟಕ"
                        AppLanguage.HINDI -> "मंडी भाव विश्लेषण • एपीएमसी कर्नाटक"
                        AppLanguage.ENGLISH -> "Market Price Analytics • APMC Karnataka"
                    },
                    color = com.example.ui.theme.AmberLight,
                    style = MaterialTheme.typography.labelLarge.copy(fontSize = 14.sp),
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = when (currentLang) {
                        AppLanguage.KANNADA -> "ಬೆಲೆ ವ್ಯತ್ಯಾಸ ಮತ್ತು ಲಾಭದಾಯಕ ಮಾರುಕಟ್ಟೆ"
                        AppLanguage.HINDI -> "भाव का अंतर व अधिक मुनाफ़े वाली मंडी"
                        AppLanguage.ENGLISH -> "Price Curves & Arbitrage Engine"
                    },
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 21.sp
                    )
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = when (currentLang) {
                        AppLanguage.KANNADA -> "ಬೆಂಗಳೂರು, ಮೈಸೂರು ಮತ್ತು ಚಿಕ್ಕಮಗಳೂರು ಮಾರುಕಟ್ಟೆಗಳ ದೈನಂದಿನ ಸಗಟು ದರಗಳನ್ನು ಹೋಲಿಸಿ ನಿಮ್ಮ ಬೆಳೆಗೆ ಗರಿಷ್ಠ ಬೆಲೆ ಪಡೆಯಿರಿ."
                        AppLanguage.HINDI -> "बेंगलुरु, मैसूरु और चिक्कमगलुरु मंडियों के दैनिक थोक भावों की तुलना करें और अधिक मुनाफा कमाएं।"
                        AppLanguage.ENGLISH -> "Compare daily wholesale modal prices across Bengaluru, Mysuru, and Chikkamagaluru to maximize crop returns."
                    },
                    color = Color.White.copy(alpha = 0.9f),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 15.sp,
                        lineHeight = 22.sp
                    )
                )
            }
        }

        // 3. Commodity Selector Horizontal Chips
        Column {
            Text(
                text = when (currentLang) {
                    AppLanguage.KANNADA -> "ಪ್ರಮುಖ ಕೃಷಿ ಉತ್ಪನ್ನವನ್ನು ಆರಿಸಿ:"
                    AppLanguage.HINDI -> "प्रमुख कृषि उपज चुनें:"
                    AppLanguage.ENGLISH -> "Select Key Commodity:"
                },
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                commodities.forEach { comm ->
                    val isSelected = comm == selectedCommodity
                    val commDisplayName = getCommodityDisplayName(comm, currentLang)
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (isSelected) ForestGreenPrimary else MaterialTheme.colorScheme.surfaceVariant,
                        border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, com.example.ui.theme.AmberLight) else androidx.compose.foundation.BorderStroke(1.dp, com.example.ui.theme.LightBorder),
                        modifier = Modifier
                            .clickable { viewModel.setCommodity(comm) }
                            .testTag("commodity_chip_${comm.lowercase().replace(" ", "_")}")
                    ) {
                        Text(
                            text = commDisplayName,
                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontSize = 15.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            ),
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                        )
                    }
                }
            }
        }

        // 4. 14-Day Comparative Price Chart
        MarketPriceChart(
            trendData = analytics.trendHistory,
            commodityName = analytics.commodity
        )

        // 5. Inter-Mandi Arbitrage Recommendation Banner (Royal Gold Theme)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("arbitrage_recommendation_card"),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEB)), // Warm Royal Gold tint
            border = androidx.compose.foundation.BorderStroke(1.5.dp, com.example.ui.theme.AmberLight),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocalShipping,
                        contentDescription = "Arbitrage",
                        tint = Color(0xFFB45309),
                        modifier = Modifier.size(26.dp)
                    )
                    Text(
                        text = when (currentLang) {
                            AppLanguage.KANNADA -> "ಎಪಿಎಂಸಿ ಲಾಭದಾಯಕ ಮಾರುಕಟ್ಟೆ ಶಿಫಾರಸು"
                            AppLanguage.HINDI -> "एपीएमसी अधिक लाभ वाली मंडी अलर्ट"
                            AppLanguage.ENGLISH -> "APMC Arbitrage Opportunity Alert"
                        },
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF92400E),
                        style = MaterialTheme.typography.titleLarge.copy(fontSize = 18.5.sp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = analytics.recommendation,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 15.5.sp,
                        lineHeight = 23.sp
                    ),
                    color = Color(0xFF78350F)
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    ArbitrageMetricPill(
                        label = when (currentLang) {
                            AppLanguage.KANNADA -> "ಮಾರುಕಟ್ಟೆ ವ್ಯತ್ಯಾಸ"
                            AppLanguage.HINDI -> "बाजार अंतर"
                            AppLanguage.ENGLISH -> "Market Spread"
                        },
                        value = "₹${analytics.priceSpread.toInt()}/Q"
                    )
                    ArbitrageMetricPill(
                        label = when (currentLang) {
                            AppLanguage.KANNADA -> "ಉತ್ತಮ ಮಾರುಕಟ್ಟೆ"
                            AppLanguage.HINDI -> "सर्वोत्तम मंडी"
                            AppLanguage.ENGLISH -> "Top Mandi"
                        },
                        value = analytics.bestMandi
                    )
                    ArbitrageMetricPill(
                        label = when (currentLang) {
                            AppLanguage.KANNADA -> "ನಿವ್ವಳ ಲಾಭ"
                            AppLanguage.HINDI -> "शुद्ध लाभ"
                            AppLanguage.ENGLISH -> "Net Gain"
                        },
                        value = "+₹${analytics.arbitrageGain.toInt()}/Q",
                        highlight = true
                    )
                }
            }
        }

        // 6. Individual Mandi Price Cards (Modal, Min, Max, % change)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = when (currentLang) {
                    AppLanguage.KANNADA -> "ಇಂದಿನ ಎಪಿಎಂಸಿ ಮಾರುಕಟ್ಟೆ ದರಗಳು"
                    AppLanguage.HINDI -> "आज के एपीएमसी मंडी भाव"
                    AppLanguage.ENGLISH -> "Today's APMC Mandi Rates"
                },
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 19.sp
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "₹ / Quintal",
                style = MaterialTheme.typography.labelMedium.copy(fontSize = 13.5.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        analytics.mandiPrices.forEach { mandi ->
            val accentColor = when {
                mandi.mandiName.contains("Bengaluru") -> MandiBengaluruColor
                mandi.mandiName.contains("Mysuru") -> MandiMysuruColor
                else -> MandiChikkamagaluruColor
            }
            MandiSnapshotCard(
                mandi = mandi,
                accentColor = accentColor,
                currentLang = currentLang,
                reportDate = weeklyAnalysis.reportDate
            )
        }

        // -----------------------------------------------------------------------------------------
        // 7. [NEW FEATURE BELOW PRICES TAB] 1-WEEK MARKET PRICE ANALYSIS & WHEN TO SELL ADVISORY
        // -----------------------------------------------------------------------------------------
        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("one_week_market_analysis_section"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Section Title Row
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
                            imageVector = Icons.Default.Insights,
                            contentDescription = "Analysis",
                            tint = ForestGreenPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                        Column {
                            Text(
                                text = when (currentLang) {
                                    AppLanguage.KANNADA -> "೧ ವಾರದ ಮಾರುಕಟ್ಟೆ ಬೆಲೆ ವಿಶ್ಲೇಷಣೆ"
                                    AppLanguage.HINDI -> "1 सप्ताह का मंडी भाव विश्लेषण"
                                    AppLanguage.ENGLISH -> "1-Week Market Price Analysis"
                                },
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = getCommodityDisplayName(weeklyAnalysis.commodity, currentLang) + " • " + weeklyAnalysis.reportDate,
                                style = MaterialTheme.typography.bodySmall,
                                color = ForestGreenPrimary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = ForestGreenPrimary.copy(alpha = 0.1f)
                    ) {
                        Text(
                            text = when (currentLang) {
                                AppLanguage.KANNADA -> "೭ ದಿನಗಳ ದಾಖಲೆ"
                                AppLanguage.HINDI -> "7 दिनों का रिकॉर्ड"
                                AppLanguage.ENGLISH -> "7-Day Record"
                            },
                            color = ForestGreenPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)

                // SMART "WHEN TO SELL?" HARVEST ADVISORY CARD
                WhenToSellAdvisoryCard(
                    weeklyAnalysis = weeklyAnalysis,
                    currentLang = currentLang
                )

                // 7-Day High, Low, Average, and Net Movement Summary Grid
                WeeklyStatsSummaryGrid(
                    weeklyAnalysis = weeklyAnalysis,
                    currentLang = currentLang
                )

                // Day-by-Day 7-Day Price History Table
                Text(
                    text = when (currentLang) {
                        AppLanguage.KANNADA -> "ದಿನವಾರು ಬೆಲೆ ವಿವರ (ಕಳೆದ ೭ ದಿನಗಳು):"
                        AppLanguage.HINDI -> "दैनिक भाव विवरण (पिछले 7 दिन):"
                        AppLanguage.ENGLISH -> "Daily Price Breakdown (Past 7 Days):"
                    },
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                            RoundedCornerShape(12.dp)
                        )
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val maxWeekPrice = weeklyAnalysis.weeklyHighPrice.coerceAtLeast(1.0)
                    weeklyAnalysis.dailyRecords.reversed().forEachIndexed { index, record ->
                        DailyPriceRow(
                            record = record,
                            maxWeekPrice = maxWeekPrice,
                            isLatestToday = index == 0,
                            currentLang = currentLang
                        )
                    }
                }

                // Volume Summary Note
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF1F5F9), RoundedCornerShape(8.dp))
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AccessTime,
                        contentDescription = "Volume",
                        tint = Color(0xFF475569),
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = when (currentLang) {
                            AppLanguage.KANNADA -> weeklyAnalysis.marketVolumeSummaryKn
                            AppLanguage.HINDI -> weeklyAnalysis.marketVolumeSummaryHi
                            AppLanguage.ENGLISH -> weeklyAnalysis.marketVolumeSummaryEn
                        },
                        fontSize = 11.sp,
                        color = Color(0xFF334155),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))
    }
}

/**
 * Smart "When to Sell?" Advisory Card based on 7-Day price trend momentum,
 * arrivals, and weekly peaks.
 */
@Composable
private fun WhenToSellAdvisoryCard(
    weeklyAnalysis: WeeklyMarketAnalysis,
    currentLang: AppLanguage
) {
    val decision = weeklyAnalysis.sellDecision

    val (cardBg, borderColor, iconTint, badgeText) = when (decision) {
        SellDecision.SELL_NOW_PEAK -> Quadruple(
            Color(0xFFECFDF5), // Emerald light
            Color(0xFF10B981),
            Color(0xFF047857),
            when (currentLang) {
                AppLanguage.KANNADA -> "ಇಂದೇ ಮಾರಾಟ ಮಾಡಿ - ವಾರದ ಗರಿಷ್ಠ ಬೆಲೆ!"
                AppLanguage.HINDI -> "आज ही बेचें - सप्ताह का उच्चतम भाव!"
                AppLanguage.ENGLISH -> "Best Time to Sell Now (Peak Price!)"
            }
        )
        SellDecision.HOLD_FOR_HIGHER -> Quadruple(
            Color(0xFFFFFBEB), // Amber light
            Color(0xFFF59E0B),
            Color(0xFFB45309),
            when (currentLang) {
                AppLanguage.KANNADA -> "2-3 ದಿನ ನಿರೀಕ್ಷಿಸಿ - ಬೆಲೆ ಏರುತ್ತಿದೆ!"
                AppLanguage.HINDI -> "2-3 दिन रुकें - भाव बढ़ रहा है!"
                AppLanguage.ENGLISH -> "Hold for 2-3 Days (Rising Trend)"
            }
        )
        SellDecision.SELL_IMMEDIATELY_DROPPING -> Quadruple(
            Color(0xFFFFF1F2), // Rose light
            Color(0xFFF43F5E),
            Color(0xFFBE123C),
            when (currentLang) {
                AppLanguage.KANNADA -> "ತಕ್ಷಣ ಮಾರಾಟ ಮಾಡಿ - ಆವಕ ಹೆಚ್ಚಾಗಿದೆ!"
                AppLanguage.HINDI -> "तुरंत बेचें - आवक बढ़ रही है!"
                AppLanguage.ENGLISH -> "Sell Promptly (Supply Flooding)"
            }
        )
        SellDecision.HARVEST_AND_SELL -> Quadruple(
            Color(0xFFEFF6FF), // Blue light
            Color(0xFF3B82F6),
            Color(0xFF1D4ED8),
            when (currentLang) {
                AppLanguage.KANNADA -> "ಸಾಮಾನ್ಯ ಮಾರಾಟ - ಬೆಲೆ ಸ್ಥಿರವಾಗಿದೆ"
                AppLanguage.HINDI -> "नियमित बिक्री - भाव स्थिर हैं"
                AppLanguage.ENGLISH -> "Normal Phased Harvesting & Sale"
            }
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("when_to_sell_advisory_card"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, borderColor)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = when (decision) {
                            SellDecision.SELL_NOW_PEAK -> Icons.Default.CheckCircle
                            SellDecision.HOLD_FOR_HIGHER -> Icons.Default.Schedule
                            SellDecision.SELL_IMMEDIATELY_DROPPING -> Icons.Default.Warning
                            SellDecision.HARVEST_AND_SELL -> Icons.Default.Sell
                        },
                        contentDescription = "Sell Advice",
                        tint = iconTint,
                        modifier = Modifier.size(22.dp)
                    )
                    Text(
                        text = when (currentLang) {
                            AppLanguage.KANNADA -> "ಮಾರಾಟ ತೀರ್ಮಾನ (ಯಾವಾಗ ಮಾರಾಟ ಮಾಡಬೇಕು?)"
                            AppLanguage.HINDI -> "बिक्री सलाह (कब बेचें?)"
                            AppLanguage.ENGLISH -> "Harvest & Sell Advisory (When to Sell?)"
                        },
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = iconTint
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = borderColor
                ) {
                    Text(
                        text = badgeText,
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = when (currentLang) {
                    AppLanguage.KANNADA -> weeklyAnalysis.advisoryKn
                    AppLanguage.HINDI -> weeklyAnalysis.advisoryHi
                    AppLanguage.ENGLISH -> weeklyAnalysis.advisoryEn
                },
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF1E293B),
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Forecast Price Pill
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White.copy(alpha = 0.8f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = when (currentLang) {
                        AppLanguage.KANNADA -> "ಮುಂದಿನ ೨-೩ ದಿನಗಳ ನಿರೀಕ್ಷಿತ ದರ:"
                        AppLanguage.HINDI -> "अगले 2-3 दिनों का अनुमानित भाव:"
                        AppLanguage.ENGLISH -> "Projected Price (Next 2-3 Days):"
                    },
                    fontSize = 12.sp,
                    color = Color(0xFF475569)
                )
                Text(
                    text = weeklyAnalysis.projectedPriceNextDays,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = iconTint
                )
            }
        }
    }
}

/**
 * 7-Day Stats Summary Grid: High, Low, Average, and Net Movement
 */
@Composable
private fun WeeklyStatsSummaryGrid(
    weeklyAnalysis: WeeklyMarketAnalysis,
    currentLang: AppLanguage
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // High
        StatTile(
            modifier = Modifier.weight(1f),
            label = when (currentLang) {
                AppLanguage.KANNADA -> "ವಾರದ ಗರಿಷ್ಠ"
                AppLanguage.HINDI -> "सप्ताह उच्चतम"
                AppLanguage.ENGLISH -> "Weekly High"
            },
            value = "₹${weeklyAnalysis.weeklyHighPrice.toInt()}",
            subtext = weeklyAnalysis.weeklyHighDate,
            valueColor = Color(0xFF15803D),
            bg = Color(0xFFDCFCE7).copy(alpha = 0.5f)
        )

        // Low
        StatTile(
            modifier = Modifier.weight(1f),
            label = when (currentLang) {
                AppLanguage.KANNADA -> "ವಾರದ ಕನಿಷ್ಠ"
                AppLanguage.HINDI -> "सप्ताह न्यूनतम"
                AppLanguage.ENGLISH -> "Weekly Low"
            },
            value = "₹${weeklyAnalysis.weeklyLowPrice.toInt()}",
            subtext = weeklyAnalysis.weeklyLowDate,
            valueColor = Color(0xFFB91C1C),
            bg = Color(0xFFFEE2E2).copy(alpha = 0.5f)
        )

        // Average
        StatTile(
            modifier = Modifier.weight(1f),
            label = when (currentLang) {
                AppLanguage.KANNADA -> "ವಾರದ ಸರಾಸರಿ"
                AppLanguage.HINDI -> "सप्ताह औसत"
                AppLanguage.ENGLISH -> "Weekly Avg"
            },
            value = "₹${weeklyAnalysis.weeklyAveragePrice.toInt()}",
            subtext = "₹/Qtl",
            valueColor = ForestGreenPrimary,
            bg = ForestGreenPrimary.copy(alpha = 0.08f)
        )

        // Net Change
        val isPositive = weeklyAnalysis.weeklyPriceChange >= 0
        StatTile(
            modifier = Modifier.weight(1f),
            label = when (currentLang) {
                AppLanguage.KANNADA -> "ವಾರದ ವ್ಯತ್ಯಾಸ"
                AppLanguage.HINDI -> "7-दिन बदलाव"
                AppLanguage.ENGLISH -> "7-Day Move"
            },
            value = "${if (isPositive) "+" else ""}₹${weeklyAnalysis.weeklyPriceChange.toInt()}",
            subtext = "${if (isPositive) "+" else ""}${weeklyAnalysis.weeklyPriceChangePercent}%",
            valueColor = if (isPositive) Color(0xFF16A34A) else Color(0xFFDC2626),
            bg = if (isPositive) Color(0xFFDCFCE7).copy(alpha = 0.5f) else Color(0xFFFEE2E2).copy(alpha = 0.5f)
        )
    }
}

@Composable
private fun StatTile(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    subtext: String,
    valueColor: Color,
    bg: Color
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        color = bg
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                fontSize = 10.sp,
                color = Color(0xFF475569),
                maxLines = 1
            )
            Text(
                text = value,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = valueColor
            )
            Text(
                text = subtext,
                fontSize = 9.sp,
                color = Color(0xFF64748B),
                maxLines = 1
            )
        }
    }
}

/**
 * Individual Day Price Row inside the 7-day breakdown table
 */
@Composable
private fun DailyPriceRow(
    record: DailyPriceRecord,
    maxWeekPrice: Double,
    isLatestToday: Boolean,
    currentLang: AppLanguage
) {
    val isPositive = record.dailyChangePercent >= 0
    val progress = (record.modalPrice / maxWeekPrice).toFloat().coerceIn(0f, 1f)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isLatestToday) Color(0xFFF0FDF4) else MaterialTheme.colorScheme.surface
        ),
        border = if (isLatestToday) androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF86EFAC)) else null
    ) {
        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Day and Date
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = record.dateString,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "(${when (currentLang) {
                            AppLanguage.KANNADA -> record.dayOfWeekKn
                            AppLanguage.HINDI -> record.dayOfWeekHi
                            AppLanguage.ENGLISH -> record.dayOfWeekEn
                        }})",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (isLatestToday) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFF15803D)
                        ) {
                            Text(
                                text = when (currentLang) {
                                    AppLanguage.KANNADA -> "ಇಂದು"
                                    AppLanguage.HINDI -> "आज"
                                    AppLanguage.ENGLISH -> "Today"
                                },
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                // Modal Price & Change
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "₹${record.modalPrice.toInt()}/Q",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.ExtraBold),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (isPositive) Color(0xFFDCFCE7) else Color(0xFFFEE2E2)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Icon(
                                imageVector = if (isPositive) Icons.AutoMirrored.Filled.TrendingUp else Icons.AutoMirrored.Filled.TrendingDown,
                                contentDescription = null,
                                tint = if (isPositive) Color(0xFF166534) else Color(0xFF991B1B),
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                text = "${if (isPositive) "+" else ""}${record.dailyChangePercent}%",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isPositive) Color(0xFF166534) else Color(0xFF991B1B)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Proportional relative bar comparing day's price to weekly peak
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp),
                color = if (isLatestToday) ForestGreenPrimary else Color(0xFF64748B),
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                strokeCap = StrokeCap.Round
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Subtext: Min-Max range and volume
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${when (currentLang) {
                        AppLanguage.KANNADA -> "ವ್ಯಾಪ್ತಿ: "
                        AppLanguage.HINDI -> "दायरा: "
                        AppLanguage.ENGLISH -> "Range: "
                    }}₹${record.minPrice.toInt()} - ₹${record.maxPrice.toInt()}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${record.arrivalVolumeQtl} ${when (currentLang) {
                        AppLanguage.KANNADA -> "ಕ್ವಿಂಟಾಲ್ ಆವಕ"
                        AppLanguage.HINDI -> "क्विंटल आवक"
                        AppLanguage.ENGLISH -> "Qtl arrivals"
                    }}",
                    fontSize = 11.sp,
                    color = Color(0xFF475569)
                )
            }
        }
    }
}

@Composable
private fun ArbitrageMetricPill(
    label: String,
    value: String,
    highlight: Boolean = false
) {
    Column {
        Text(
            text = label,
            fontSize = 13.5.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF92400E)
        )
        Text(
            text = value,
            fontWeight = FontWeight.Bold,
            fontSize = 17.sp,
            color = if (highlight) Color(0xFF15803D) else Color(0xFF78350F)
        )
    }
}

@Composable
private fun MandiSnapshotCard(
    mandi: MandiPriceInfo,
    accentColor: Color,
    currentLang: AppLanguage = AppLanguage.KANNADA,
    reportDate: String = ""
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(18.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, com.example.ui.theme.LightBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Icon(
                        imageVector = Icons.Default.Storefront,
                        contentDescription = "Mandi",
                        tint = accentColor,
                        modifier = Modifier.size(24.dp)
                    )
                    Column {
                        Text(
                            text = when {
                                mandi.mandiName.contains("Bengaluru") -> when (currentLang) {
                                    AppLanguage.KANNADA -> "ಬೆಂಗಳೂರು (ಯಶವಂತಪುರ)"
                                    AppLanguage.HINDI -> "बेंगलुरु (यशवंतपुर)"
                                    AppLanguage.ENGLISH -> mandi.mandiName
                                }
                                mandi.mandiName.contains("Mysuru") -> when (currentLang) {
                                    AppLanguage.KANNADA -> "ಮೈಸೂರು (ಬಂಡಿಪಾಳ್ಯ)"
                                    AppLanguage.HINDI -> "मैसूरु (बंडीपाल्या)"
                                    AppLanguage.ENGLISH -> mandi.mandiName
                                }
                                mandi.mandiName.contains("Chikkamagaluru") -> when (currentLang) {
                                    AppLanguage.KANNADA -> "ಚಿಕ್ಕಮಗಳೂರು ಎಪಿಎಂಸಿ"
                                    AppLanguage.HINDI -> "चिक्कमगलुरु मंडी"
                                    AppLanguage.ENGLISH -> mandi.mandiName
                                }
                                else -> mandi.mandiName
                            },
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleLarge.copy(fontSize = 17.5.sp),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (reportDate.isNotBlank()) {
                            Text(
                                text = "${when (currentLang) {
                                    AppLanguage.KANNADA -> "ದಿನಾಂಕ: "
                                    AppLanguage.HINDI -> "तारीख: "
                                    AppLanguage.ENGLISH -> "Date: "
                                }}$reportDate",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Daily change percentage pill
                val isPositive = mandi.dailyChangePercent >= 0
                val changeBg = if (isPositive) Color(0xFFDCFCE7) else Color(0xFFFEE2E2)
                val changeText = if (isPositive) Color(0xFF166534) else Color(0xFF991B1B)

                Surface(
                    color = changeBg,
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = if (isPositive) Icons.AutoMirrored.Filled.TrendingUp else Icons.AutoMirrored.Filled.TrendingDown,
                            contentDescription = "Trend",
                            tint = changeText,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "${if (isPositive) "+" else ""}${mandi.dailyChangePercent}%",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = changeText
                        )
                    }
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = MaterialTheme.colorScheme.surfaceVariant)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = when (currentLang) {
                            AppLanguage.KANNADA -> "ಮಾದರಿ ದರ"
                            AppLanguage.HINDI -> "मॉडल भाव"
                            AppLanguage.ENGLISH -> "Modal Price"
                        },
                        fontSize = 13.5.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "₹${mandi.modalPrice.toInt()}",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 21.sp
                        ),
                        color = accentColor
                    )
                }

                Column {
                    Text(
                        text = when (currentLang) {
                            AppLanguage.KANNADA -> "ಕನಿಷ್ಠ ದರ"
                            AppLanguage.HINDI -> "न्यूनतम भाव"
                            AppLanguage.ENGLISH -> "Min Price"
                        },
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "₹${mandi.minPrice.toInt()}",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.5.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Column {
                    Text(
                        text = when (currentLang) {
                            AppLanguage.KANNADA -> "ಗರಿಷ್ಠ ದರ"
                            AppLanguage.HINDI -> "अधिकतम भाव"
                            AppLanguage.ENGLISH -> "Max Price"
                        },
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "₹${mandi.maxPrice.toInt()}",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.5.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

private fun getCommodityDisplayName(commodity: String, currentLang: AppLanguage): String {
    return when (commodity) {
        "Pumpkin" -> when (currentLang) {
            AppLanguage.KANNADA -> "ಕುಂಬಳಕಾಯಿ (Pumpkin)"
            AppLanguage.HINDI -> "कद्दू (Pumpkin)"
            AppLanguage.ENGLISH -> "Pumpkin"
        }
        "Tomato" -> when (currentLang) {
            AppLanguage.KANNADA -> "ಟೊಮೆಟೊ (Tomato)"
            AppLanguage.HINDI -> "टमाटर (Tomato)"
            AppLanguage.ENGLISH -> "Tomato"
        }
        "Potato" -> when (currentLang) {
            AppLanguage.KANNADA -> "ಆಲೂಗಡ್ಡೆ (Potato)"
            AppLanguage.HINDI -> "आलू (Potato)"
            AppLanguage.ENGLISH -> "Potato"
        }
        "Onion" -> when (currentLang) {
            AppLanguage.KANNADA -> "ಈರುಳ್ಳಿ (Onion)"
            AppLanguage.HINDI -> "प्याज (Onion)"
            AppLanguage.ENGLISH -> "Onion"
        }
        "Green Chilli" -> when (currentLang) {
            AppLanguage.KANNADA -> "ಹಸಿಮೆಣಸಿನಕಾಯಿ (Chilli)"
            AppLanguage.HINDI -> "हरी मिर्च (Chilli)"
            AppLanguage.ENGLISH -> "Green Chilli"
        }
        "Ash Gourd" -> when (currentLang) {
            AppLanguage.KANNADA -> "ಬೂದು ಕುಂಬಳ (Ash Gourd)"
            AppLanguage.HINDI -> "पेठा (Ash Gourd)"
            AppLanguage.ENGLISH -> "Ash Gourd"
        }
        "Drumstick" -> when (currentLang) {
            AppLanguage.KANNADA -> "ನುಗ್ಗೆಕಾಯಿ (Drumstick)"
            AppLanguage.HINDI -> "सहजन (Drumstick)"
            AppLanguage.ENGLISH -> "Drumstick"
        }
        "Beans" -> when (currentLang) {
            AppLanguage.KANNADA -> "ಹುರುಳಿಕಾಯಿ (Beans)"
            AppLanguage.HINDI -> "बीन्स (Beans)"
            AppLanguage.ENGLISH -> "Beans"
        }
        "Cabbage" -> when (currentLang) {
            AppLanguage.KANNADA -> "ಎಲೆಕೋಸು (Cabbage)"
            AppLanguage.HINDI -> "पत्तागोभी (Cabbage)"
            AppLanguage.ENGLISH -> "Cabbage"
        }
        "Capsicum" -> when (currentLang) {
            AppLanguage.KANNADA -> "ದಪ್ಪ ಮೆಣಸಿನಕಾಯಿ (Capsicum)"
            AppLanguage.HINDI -> "शिमला मिर्च (Capsicum)"
            AppLanguage.ENGLISH -> "Capsicum"
        }
        "Ginger" -> when (currentLang) {
            AppLanguage.KANNADA -> "ಶುಂಠಿ (Ginger)"
            AppLanguage.HINDI -> "अदरक (Ginger)"
            AppLanguage.ENGLISH -> "Ginger"
        }
        "Garlic" -> when (currentLang) {
            AppLanguage.KANNADA -> "ಬೆಳ್ಳುಳ್ಳಿ (Garlic)"
            AppLanguage.HINDI -> "लहसुन (Garlic)"
            AppLanguage.ENGLISH -> "Garlic"
        }
        "Arecanut (Rashi)" -> when (currentLang) {
            AppLanguage.KANNADA -> "ಅಡಿಕೆ ರಾಶಿ (Arecanut)"
            AppLanguage.HINDI -> "सुपारी (Arecanut)"
            AppLanguage.ENGLISH -> "Arecanut (Rashi)"
        }
        "Coffee (Arabica Parchment)" -> when (currentLang) {
            AppLanguage.KANNADA -> "ಕಾಫಿ ಪಾರ್ಚ್‌ಮೆಂಟ್ (Coffee)"
            AppLanguage.HINDI -> "कॉफ़ी (Coffee)"
            AppLanguage.ENGLISH -> "Coffee (Arabica)"
        }
        "Ragi" -> when (currentLang) {
            AppLanguage.KANNADA -> "ರಾಗಿ (Ragi)"
            AppLanguage.HINDI -> "रागी (Ragi)"
            AppLanguage.ENGLISH -> "Ragi"
        }
        "Maize" -> when (currentLang) {
            AppLanguage.KANNADA -> "ಮೆಕ್ಕೆಜೋಳ (Maize)"
            AppLanguage.HINDI -> "मक्का (Maize)"
            AppLanguage.ENGLISH -> "Maize"
        }
        else -> commodity
    }
}

private data class Quadruple<A, B, C, D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D
)

