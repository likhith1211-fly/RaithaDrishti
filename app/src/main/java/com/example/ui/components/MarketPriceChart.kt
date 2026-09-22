package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PriceTrendPoint
import com.example.ui.theme.MandiBengaluruColor
import com.example.ui.theme.MandiChikkamagaluruColor
import com.example.ui.theme.MandiMysuruColor

@Composable
fun MarketPriceChart(
    trendData: List<PriceTrendPoint>,
    commodityName: String,
    modifier: Modifier = Modifier
) {
    if (trendData.isEmpty()) return

    var selectedIndex by remember { mutableStateOf<Int?>(null) }
    val selectedPoint = selectedIndex?.let { if (it in trendData.indices) trendData[it] else null }

    val allPrices = trendData.flatMap { listOf(it.bengaluruPrice, it.mysuruPrice, it.chikkamagaluruPrice) }
    val minPrice = (allPrices.minOrNull() ?: 1000.0) * 0.95
    val maxPrice = (allPrices.maxOrNull() ?: 3000.0) * 1.05
    val priceRange = if (maxPrice > minPrice) maxPrice - minPrice else 1.0

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("market_price_chart_card"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header & Legend
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "14-Day Modal Price Trends",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Wholesale Rates (₹ / Quintal) • $commodityName",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Mandi Legend Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                LegendItem(color = MandiBengaluruColor, name = "Bengaluru")
                LegendItem(color = MandiMysuruColor, name = "Mysuru")
                LegendItem(color = MandiChikkamagaluruColor, name = "Chikkamagaluru")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Interactive Tooltip if tapped
            if (selectedPoint != null) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.95f)
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = selectedPoint.displayDate,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text(
                                text = "BLR: ₹${selectedPoint.bengaluruPrice.toInt()}",
                                color = MandiBengaluruColor,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                            Text(
                                text = "MYS: ₹${selectedPoint.mysuruPrice.toInt()}",
                                color = MandiMysuruColor,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                            Text(
                                text = "CKM: ₹${selectedPoint.chikkamagaluruPrice.toInt()}",
                                color = MandiChikkamagaluruColor,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }

            // Canvas Chart
            val gridColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
            val textPaintColor = MaterialTheme.colorScheme.onSurfaceVariant

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
            ) {
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .pointerInput(trendData) {
                            detectTapGestures { offset ->
                                val pointSpacing = size.width / (trendData.size - 1)
                                val tappedIndex = Math.round(offset.x / pointSpacing).toInt().coerceIn(0, trendData.size - 1)
                                selectedIndex = tappedIndex
                            }
                        }
                ) {
                    val width = size.width
                    val height = size.height - 30.dp.toPx()
                    val paddingBottom = 20.dp.toPx()
                    val paddingTop = 10.dp.toPx()
                    val usableHeight = height - paddingTop

                    // Draw 4 horizontal grid lines
                    val steps = 4
                    val pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                    for (i in 0..steps) {
                        val y = paddingTop + (usableHeight / steps) * i
                        drawLine(
                            color = gridColor,
                            start = Offset(0f, y),
                            end = Offset(width, y),
                            strokeWidth = 1.dp.toPx(),
                            pathEffect = pathEffect
                        )
                    }

                    // Calculate point spacing
                    val pointSpacing = width / (trendData.size - 1)

                    fun getY(price: Double): Float {
                        val fraction = (price - minPrice) / priceRange
                        return (height - (fraction * usableHeight)).toFloat()
                    }

                    // Helper to draw a line series
                    fun drawSeries(
                        prices: List<Double>,
                        lineColor: Color
                    ) {
                        val path = Path()
                        prices.forEachIndexed { index, price ->
                            val x = index * pointSpacing
                            val y = getY(price)
                            if (index == 0) {
                                path.moveTo(x, y)
                            } else {
                                val prevX = (index - 1) * pointSpacing
                                val prevY = getY(prices[index - 1])
                                val cx1 = prevX + (x - prevX) / 2
                                val cy1 = prevY
                                val cx2 = prevX + (x - prevX) / 2
                                val cy2 = y
                                path.cubicTo(cx1, cy1, cx2, cy2, x, y)
                            }
                        }
                        drawPath(
                            path = path,
                            color = lineColor,
                            style = Stroke(width = 2.5.dp.toPx())
                        )

                        // Draw active selection indicator point
                        if (selectedIndex != null && selectedIndex in prices.indices) {
                            val selX = selectedIndex!! * pointSpacing
                            val selY = getY(prices[selectedIndex!!])
                            drawCircle(
                                color = Color.White,
                                radius = 5.dp.toPx(),
                                center = Offset(selX, selY)
                            )
                            drawCircle(
                                color = lineColor,
                                radius = 4.dp.toPx(),
                                center = Offset(selX, selY)
                            )
                        }
                    }

                    // Draw the 3 Mandi lines
                    drawSeries(trendData.map { it.bengaluruPrice }, MandiBengaluruColor)
                    drawSeries(trendData.map { it.mysuruPrice }, MandiMysuruColor)
                    drawSeries(trendData.map { it.chikkamagaluruPrice }, MandiChikkamagaluruColor)

                    // Draw vertical scrubber line if selected
                    if (selectedIndex != null) {
                        val scrubberX = selectedIndex!! * pointSpacing
                        drawLine(
                            color = textPaintColor.copy(alpha = 0.5f),
                            start = Offset(scrubberX, paddingTop),
                            end = Offset(scrubberX, height),
                            strokeWidth = 1.dp.toPx(),
                            pathEffect = pathEffect
                        )
                    }
                }

                // X-Axis Date markers
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = trendData.first().displayDate,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = trendData[trendData.size / 2].displayDate,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = trendData.last().displayDate,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Tap along the curve to inspect daily comparative prices across mandis.",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun LegendItem(color: Color, name: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .background(color, CircleShape)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = name,
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
