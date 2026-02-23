package com.crypticsamsara.spacedash.ui.screens

import android.view.RoundedCorner
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.crypticsamsara.spacedash.model.Weapon

@Composable
fun WeaponSelectionWheel(
    unlockedWeapons: List<Weapon>,
    currentWeapon: Weapon,
    onWeaponSelected: (Weapon) -> Unit,
    modifier: Modifier = Modifier
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        unlockedWeapons.forEach { weapon ->
            WeaponButton(
                weapon = weapon,
                isSelected = weapon.type == currentWeapon.type,
                onClick = { onWeaponSelected(weapon) }
            )
        }
    }
}

@Composable
private fun WeaponButton(
    weapon: Weapon,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.2f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "weapon_scale"
    )

    val alpha by animateFloatAsState(
        targetValue = if (isSelected) 1f else 0.6f,
        label = "weapon_alpha"
    )

    Box(
        modifier = Modifier
            .size(60.dp)
            .scale(scale)
            .alpha(alpha)
            .clip(CircleShape)
            .background(
                brush = if (isSelected) {
                    Brush.radialGradient(
                        colors = listOf(
                            weapon.bulletColor.copy(alpha = 0.3f),
                            Color.Black.copy(alpha = 0.8f)
                        )
                    )
                } else {
                    Brush.radialGradient(
                        colors = listOf(
                            Color.Gray.copy(alpha = 0.3f),
                            Color.Black.copy(alpha = 0.6f)
                        )
                    )
                }
            )
            .border(
                width = if (isSelected) 3.dp else 1.dp,
                color = if (isSelected) weapon.bulletColor else Color.Gray,
                shape = CircleShape
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = weapon.icon,
            fontSize = 24.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun WeaponInfoHUD(
    currentWeapon: Weapon,
    currentAmmo: Int,
    maxAmmo: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = Modifier
            .background(
                Color.Black.copy(alpha = 0.7f),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(8.dp),
        horizontalAlignment = Alignment.Start
    ) {
        // weapon name and icon
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = currentWeapon.icon,
                fontSize = 20.sp
            )
            Text(
                text = currentWeapon.name,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = currentWeapon.bulletColor
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Ammo bar
        AmmoBar(
            currentAmmo = currentAmmo,
            maxAmmo = maxAmmo,
            color = currentWeapon.bulletColor
        )
    }
}

@Composable
private fun AmmoBar(
    currentAmmo: Int,
    maxAmmo: Int,
    color: Color
) {
    val ammoPercentage = (currentAmmo.toFloat() / maxAmmo.toFloat()).coerceIn(0f, 1f)

    Column {
        Text(
            text = "$currentAmmo / $maxAmmo",
            fontSize = 12.sp,
            color = Color.White
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(Color.Gray.copy(alpha = 0.3f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(ammoPercentage)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(3.dp))
                    .background(
                        when {
                            ammoPercentage > 0.5f -> Color.Green
                            ammoPercentage > 0.25f -> Color.Yellow
                            else -> Color.Red
                        }
                    )
            )
        }
    }
}

@Composable
fun WeaponSelectionScreen(
    allWeapons: List<Weapon>,
    currentWeapon: Weapon,
    onWeaponSelected: (Weapon) -> Unit,
    onClose: ()-> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.9f))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "SELECT WEAPON",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(24.dp))

        allWeapons.forEach { weapon ->
            WeaponCard(
                weapon = weapon,
                isSelected = weapon.type == currentWeapon.type,
                onClick = {
                    if (weapon.isUnlocked) {
                        onWeaponSelected(weapon)
                        onClose()
                    }
                }
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun WeaponCard(
    weapon: Weapon,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (isSelected) {
                    Brush.horizontalGradient(
                        colors = listOf(
                            weapon.bulletColor.copy(alpha = 0.3f),
                            Color.Black
                        )
                    )
                } else {
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color.Gray.copy(alpha = 0.2f),
                            Color.Black
                        )
                    )
                }
            )
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) weapon.bulletColor else Color.Gray,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(enabled = weapon.isUnlocked, onClick = onClick)
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left: Icon and Name
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = weapon.icon,
                    fontSize = 32.sp,
                    modifier = Modifier.alpha(if (weapon.isUnlocked) 1f else 0.3f)
                )

                Column {
                    Text(
                        text = weapon.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (weapon.isUnlocked) Color.White else Color.Gray
                    )
                    Text(
                        text = weapon.description,
                        fontSize = 12.sp,
                        color = if (weapon.isUnlocked) Color.LightGray else Color.DarkGray
                    )
                }
            }

            // Right: Stats or Lock
            if (weapon.isUnlocked) {
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "DMG: ${weapon.damage}",
                        fontSize = 12.sp,
                        color = Color.White
                    )
                    Text(
                        text = "FIRE: ${1000 / weapon.fireRate}/s",
                        fontSize = 12.sp,
                        color = Color.White
                    )
                }
            } else {
                Text(
                    text = "🔒 ${weapon.cost}",
                    fontSize = 14.sp,
                    color = Color.Yellow
                )
            }
        }
    }
}






