package com.crypticsamsara.spacedash.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.crypticsamsara.spacedash.model.Weapon
import com.crypticsamsara.spacedash.model.WeaponFactory
import com.crypticsamsara.spacedash.model.WeaponType
import com.crypticsamsara.spacedash.ui.screens.CreditsDisplay
import com.crypticsamsara.spacedash.ui.screens.CreditsInsufficientWarning
import kotlinx.coroutines.delay

@Composable
fun StoreScreen(
    currentCredits: Int,
    unlockedWeapons: Set<WeaponType>,
    onPurchaseWeapon: (Weapon) -> Unit,
    onBackPressed: () -> Unit,
    onWeaponSelected: (Weapon) -> Unit = {},
    modifier: Modifier = Modifier
){
    var selectedWeapon by remember { mutableStateOf<Weapon?>(null) }
    var showInsufficientCredits by remember { mutableStateOf(false) }
    var insufficientAmount by remember { mutableIntStateOf(0) }

    // get all weapons
    val allWeapons = remember { WeaponFactory.getAllWeapons() }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0A0E27),
                        Color.Black
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Header
            StoreHeader(
                currentCredits = currentCredits,
                onBackPressed = onBackPressed
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Title
            Text(
                text = "WEAPON STORE",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Text(
                text = "Upgrade your arsenal",
                fontSize = 16.sp,
                color = Color.Gray,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Weapons List
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(allWeapons) { weapon ->
                    val isUnlocked = unlockedWeapons.contains(weapon.type)

                    WeaponStoreCard(
                        weapon = weapon,
                        isUnlocked = isUnlocked,
                        isSelected = selectedWeapon == weapon,
                        onClick = {
                            selectedWeapon = weapon
                            if (!isUnlocked) {
                                if (currentCredits >= weapon.cost) {
                                    onPurchaseWeapon(weapon)
                                    selectedWeapon = null
                                } else {
                                    insufficientAmount = weapon.cost
                                    showInsufficientCredits = true
                                }
                            } else {
                                onWeaponSelected(weapon)
                            }
                        }
                    )
                }
            }
        }

        // Insufficient Credits Warning
        if (showInsufficientCredits) {
            CreditsInsufficientWarning(
                visible = true,
                required = insufficientAmount,
                current = currentCredits,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(24.dp)
            )

            LaunchedEffect(Unit) {
                delay(3000)
                showInsufficientCredits = false
            }
        }
    }
}

@Composable
private fun StoreHeader(
    currentCredits: Int,
    onBackPressed: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Back Button
        Button(
            onClick = onBackPressed,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.DarkGray
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "← BACK",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Credits Display
        CreditsDisplay(
            credits = currentCredits,
            large = true
        )
    }
}

@Composable
private fun WeaponStoreCard(
    weapon: Weapon,
    isUnlocked: Boolean,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    // Animation for selection
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.02f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "card_scale"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .clip(RoundedCornerShape(16.dp))
            .background(
                brush = if (isUnlocked) {
                    Brush.horizontalGradient(
                        colors = listOf(
                            weapon.bulletColor.copy(alpha = 0.2f),
                            Color.Black.copy(alpha = 0.8f)
                        )
                    )
                } else {
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color.DarkGray.copy(alpha = 0.3f),
                            Color.Black.copy(alpha = 0.8f)
                        )
                    )
                }
            )
            .border(
                width = if (isSelected) 3.dp else 1.dp,
                color = if (isUnlocked) weapon.bulletColor else Color.Gray,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left: Icon, Name, Description
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Weapon Icon
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(
                            if (isUnlocked) {
                                weapon.bulletColor.copy(alpha = 0.3f)
                            } else {
                                Color.Gray.copy(alpha = 0.2f)
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = weapon.icon,
                        fontSize = 32.sp,
                        modifier = Modifier.scale(if (isUnlocked) 1f else 0.5f)
                    )

                    // Lock overlay
                    if (!isUnlocked) {
                        Text(
                            text = "🔒",
                            fontSize = 24.sp,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }

                // Weapon Info
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = weapon.name,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isUnlocked) Color.White else Color.Gray
                    )

                    Text(
                        text = weapon.description,
                        fontSize = 12.sp,
                        color = if (isUnlocked) Color.LightGray else Color.DarkGray,
                        lineHeight = 16.sp
                    )

                    // Stats
                    if (isUnlocked) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            StatChip(
                                label = "DMG",
                                value = weapon.damage.toString(),
                                color = Color(0xFFFF6B35)
                            )
                            StatChip(
                                label = "RATE",
                                value = "${1000 / weapon.fireRate}/s",
                                color = Color(0xFF00F0FF)
                            )
                            StatChip(
                                label = "AMMO",
                                value = weapon.ammoConsumption.toString(),
                                color = Color(0xFFBF40BF)
                            )
                        }
                    }
                }
            }

            // Right: Price or Status
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (isUnlocked) {
                    // Unlocked Badge
                    Box(
                        modifier = Modifier
                            .background(
                                Color.Green.copy(alpha = 0.3f),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "✓ UNLOCKED",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Green
                        )
                    }

                    // Equip button
                    Button(
                        onClick = onClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = weapon.bulletColor
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Text(
                            text = "EQUIP",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }
                } else {
                    // Price
                    Column(
                        horizontalAlignment = Alignment.End
                    ) {
                        Text(
                            text = "COST",
                            fontSize = 10.sp,
                            color = Color.Gray
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "💰",
                                fontSize = 20.sp
                            )
                            Text(
                                text = weapon.cost.toString(),
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFFD700)
                            )
                        }
                    }

                    // Purchase button
                    Button(
                        onClick = onClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFFD700)
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Text(
                            text = "PURCHASE",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StatChip(
    label: String,
    value: String,
    color: Color
) {
    Box(
        modifier = Modifier
            .background(
                color.copy(alpha = 0.2f),
                shape = RoundedCornerShape(4.dp)
            )
            .padding(horizontal = 6.dp, vertical = 3.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                fontSize = 10.sp,
                color = Color.White
            )
            Text(
                text = value,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}

 // Simple Store Preview - Compact version for testing

@Composable
fun StorePreview(
    weapons: List<Weapon>,
    unlockedWeapons: Set<WeaponType>,
    onWeaponClick: (Weapon) -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(weapons) { weapon ->
            val isUnlocked = unlockedWeapons.contains(weapon.type)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.DarkGray, RoundedCornerShape(8.dp))
                    .clickable { onWeaponClick(weapon) }
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${weapon.icon} ${weapon.name}",
                    color = Color.White
                )

                if (isUnlocked) {
                    Text("✓", color = Color.Green)
                } else {
                    Text("💰 ${weapon.cost}", color = Color.Yellow)
                }
            }
        }
    }
}