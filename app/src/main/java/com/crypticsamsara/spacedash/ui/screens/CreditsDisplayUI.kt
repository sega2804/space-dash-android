package com.crypticsamsara.spacedash.ui.screens

import android.graphics.Paint
import android.util.Log.i
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.crypticsamsara.spacedash.data.game.CreditsManager
import kotlinx.coroutines.delay

@Composable
fun CreditsDisplay(
    credits: Int,
    modifier: Modifier = Modifier,
    large: Boolean = false
) {
    val fontSize = if (large) 24.sp else 16.sp
    val iconSize = if (large) 32.dp else 24.dp

    Row(
        modifier = modifier
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xFFFFD700).copy(alpha = 0.2f),
                        Color.Black.copy(alpha = 0.6f)
                    )
            ),
                shape = RoundedCornerShape(16.dp)
    )
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "💰",
            fontSize = fontSize
        )

        Text(
            text = credits.toString(),
            fontSize = fontSize,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFFFD700)
        )
    }
}

// credits earned popup
@Composable
fun CreditsEarnedPopup(
    creditsEarned: Int,
    visible: Boolean,
    modifier: Modifier = Modifier
) {
    val scale by animateFloatAsState(
        targetValue = if (visible) 1f else 0.5f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "credits_scale"
    )

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + slideInVertically(),
        exit = fadeOut() + slideOutVertically(),
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .scale(scale)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFFFD700).copy(alpha = 0.8f),
                            Color(0xFFFF8C00).copy(alpha = 0.4f),
                            Color.Transparent
                        )
                    ),
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "💰",
                    fontSize = 32.sp
                )
                Text(
                    text = "+$creditsEarned",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFFD700)
                )
                Text(
                    text = "CREDITS",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun CreditsBreakdownDisplay(
    breakdown: CreditsManager.CreditsBreakdown,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(
                Color.Black.copy(alpha = 0.8f),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "CREDITS EARNED",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFFFD700)
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Destroy bonus
        BreakdownRow(
            label = "Obstacles Destroyed",
            value = "+${breakdown.destroyBonus}",
            color = Color(0xFF00F0FF)
        )

        // Score bonus
        BreakdownRow(
            label = "Score Bonus",
            value = "+${breakdown.scoreBonus}",
            color = Color(0xFFBF40BF)
        )

        // Subtotal
        BreakdownRow(
            label = "Subtotal",
            value = "+${breakdown.baseTotal}",
            color = Color.White,
            isBold = true
        )

        // Multiplier
        BreakdownRow(
            label = "Difficulty Multiplier",
            value = "×${breakdown.difficultyMultiplier}",
            color = Color(0xFFFFD700)
        )

        // Divider
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color.Gray)
        )

        // final total
        BreakdownRow(
            label = "TOTAL",
            value = "${breakdown.finalTotal}",
            color = Color(0xFFFFD700),
            isBold = true,
            isLarge = true
        )
    }
}

@Composable
fun BreakdownRow(
    label: String,
    value: String,
    color: Color,
    isBold: Boolean = true,
    isLarge: Boolean = true
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = if (isLarge) 18.sp else 14.sp,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
            color = Color.LightGray
        )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (value.startsWith("+") || label == "TOTAL") {
                    Text(
                        text = "💰",
                        fontSize = if (isLarge) 20.sp else 16.sp
                    )
                }
                Text(
                    text = value,
                    fontSize = if (isLarge) 20.sp else 16.sp,
                    fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
                    color = color
                )
            }
        }
    }

@Composable
fun MiniCreditsDisplay(
    credits: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .background(
                Color.Black.copy(alpha = 0.6f),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = "💰",
            fontSize = 14.sp
        )
        Text(
            text = credits.toString(),
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFFFD700)
        )
    }
}

@Composable
fun CreditsInsufficientWarning(
    visible: Boolean,
    required: Int,
    current: Int,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + slideInVertically(),
        exit = fadeOut() + slideOutVertically(),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .background(
                    color = Color.Red.copy(alpha = 0.8f),
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "⚠️ INSUFFICIENT CREDITS",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Text(
                text = "Required: 💰 $required",
                fontSize = 14.sp,
                color = Color.White
            )

            Text(
                text = "You have: 💰 $current",
                fontSize = 14.sp,
                color = Color.White
            )

            Text(
                text = "Need: 💰 ${required - current} more",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFFD700)
            )
        }
    }
}

@Composable
fun FloatingCreditsAnimation(
    amount: Int,
    startPosition: Pair<Float, Float>,
    modifier: Modifier = Modifier
) {
    var visible by remember { mutableStateOf(false) }

    val offsetY by animateFloatAsState(
        targetValue = if (visible) -200f else 0f,
        animationSpec = tween(durationMillis = 1500, easing = EaseOut),
        label = "credits_float"
    )

    LaunchedEffect(Unit) {
        delay(1500)
        visible = false
    }

    if (visible) {
        Box(
            modifier = modifier
                .offset(
                    x = startPosition.first.dp,
                    y = (startPosition.second + offsetY).dp
                )
        ) {
            Text(
                text = "+$amount 💰",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFFD700)
            )
        }
    }
}