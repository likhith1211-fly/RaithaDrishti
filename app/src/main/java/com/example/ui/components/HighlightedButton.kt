package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AmberLight
import com.example.ui.theme.ButtonBlueGradientEnd
import com.example.ui.theme.ButtonBlueGradientStart
import com.example.ui.theme.ButtonEmeraldGradientEnd
import com.example.ui.theme.ButtonEmeraldGradientStart
import com.example.ui.theme.ButtonGoldBorder
import com.example.ui.theme.ButtonGoldGradientEnd
import com.example.ui.theme.ButtonGoldGradientStart
import com.example.ui.theme.ButtonHighlightBorder
import com.example.ui.theme.ForestGreenDark
import com.example.ui.theme.ForestGreenPrimary

/**
 * Premium highlighted button with vibrant gradient, glowing border, elevation shadow,
 * and high-contrast bold typography to replace dull/mubbed buttons.
 */
@Composable
fun RaithaHighlightedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    height: Dp = 50.dp,
    fontSize: TextUnit = 15.sp,
    gradientColors: List<Color> = listOf(ButtonEmeraldGradientStart, ButtonEmeraldGradientEnd),
    borderColor: Color = ButtonHighlightBorder.copy(alpha = 0.6f),
    contentColor: Color = Color.White
) {
    val shape = RoundedCornerShape(14.dp)

    Surface(
        modifier = modifier
            .defaultMinSize(minHeight = 48.dp)
            .height(height)
            .shadow(
                elevation = if (enabled) 4.dp else 0.dp,
                shape = shape,
                ambientColor = gradientColors.first().copy(alpha = 0.35f),
                spotColor = gradientColors.last().copy(alpha = 0.5f)
            ),
        shape = shape,
        color = Color.Transparent,
        border = if (enabled) BorderStroke(1.2.dp, borderColor) else null
    ) {
        Box(
            modifier = Modifier
                .background(
                    brush = if (enabled) {
                        Brush.horizontalGradient(gradientColors)
                    } else {
                        Brush.horizontalGradient(listOf(Color(0xFF9E9E9E), Color(0xFF757575)))
                    }
                )
                .clickable(
                    enabled = enabled && !isLoading,
                    onClick = onClick
                )
                .padding(horizontal = 18.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            if (isLoading) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = contentColor,
                        strokeWidth = 2.5.dp
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "ದಯವಿಟ್ಟು ಕಾಯಿರಿ / Loading...",
                        color = contentColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = fontSize
                    )
                }
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    if (icon != null) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = contentColor,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text(
                        text = text,
                        color = contentColor,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = fontSize,
                        letterSpacing = 0.3.sp
                    )
                }
            }
        }
    }
}

/**
 * Attractive Golden Amber Highlighted Button for prominent CTAs (Save, Voice, Quick Actions).
 */
@Composable
fun RaithaGoldButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    height: Dp = 50.dp,
    fontSize: TextUnit = 15.sp
) {
    RaithaHighlightedButton(
        text = text,
        onClick = onClick,
        modifier = modifier,
        icon = icon,
        enabled = enabled,
        isLoading = isLoading,
        height = height,
        fontSize = fontSize,
        gradientColors = listOf(ButtonGoldGradientStart, ButtonGoldGradientEnd),
        borderColor = ButtonGoldBorder.copy(alpha = 0.85f),
        contentColor = Color(0xFF1E1300)
    )
}

/**
 * Attractive Sky Blue Highlighted Button for GPS, Maps, and Navigation.
 */
@Composable
fun RaithaBlueButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    height: Dp = 50.dp,
    fontSize: TextUnit = 15.sp
) {
    RaithaHighlightedButton(
        text = text,
        onClick = onClick,
        modifier = modifier,
        icon = icon,
        enabled = enabled,
        isLoading = isLoading,
        height = height,
        fontSize = fontSize,
        gradientColors = listOf(ButtonBlueGradientStart, ButtonBlueGradientEnd),
        borderColor = Color(0xFFBAE6FD),
        contentColor = Color.White
    )
}

/**
 * Highlighted Outlined Button with vibrant border, soft luminous tint, and elevation.
 */
@Composable
fun RaithaOutlinedHighlightedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    enabled: Boolean = true,
    height: Dp = 48.dp,
    borderColor: Color = ForestGreenPrimary,
    textColor: Color = ForestGreenPrimary,
    backgroundColor: Color = ForestGreenPrimary.copy(alpha = 0.08f)
) {
    val shape = RoundedCornerShape(13.dp)
    Surface(
        modifier = modifier
            .defaultMinSize(minHeight = 48.dp)
            .height(height)
            .shadow(1.5.dp, shape),
        shape = shape,
        color = backgroundColor,
        border = BorderStroke(1.5.dp, borderColor)
    ) {
        Box(
            modifier = Modifier
                .clickable(enabled = enabled, onClick = onClick)
                .padding(horizontal = 14.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = textColor,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                }
                Text(
                    text = text,
                    color = textColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.5.sp
                )
            }
        }
    }
}

/**
 * Compact Action Button for item cards (e.g. Save Mandi Price, Share, Pin)
 */
@Composable
fun RaithaCardActionButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    isHighlighted: Boolean = true,
    containerColor: Color = if (isHighlighted) ForestGreenPrimary else MaterialTheme.colorScheme.surfaceVariant,
    contentColor: Color = if (isHighlighted) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = containerColor,
        border = if (isHighlighted) BorderStroke(1.dp, ButtonHighlightBorder.copy(alpha = 0.5f)) else BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
        shadowElevation = if (isHighlighted) 2.dp else 0.dp,
        modifier = modifier
            .defaultMinSize(minHeight = 36.dp)
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(16.dp)
                )
            }
            Text(
                text = text,
                color = contentColor,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
