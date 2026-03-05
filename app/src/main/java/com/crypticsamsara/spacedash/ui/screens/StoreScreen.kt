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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.crypticsamsara.spacedash.model.Weapon
import com.crypticsamsara.spacedash.model.WeaponFactory
import com.crypticsamsara.spacedash.model.WeaponType
import com.crypticsamsara.spacedash.ui.theme.SpaceDashTheme
import com.crypticsamsara.spacedash.viewmodel.GameViewModel
import kotlinx.coroutines.delay

@Composable
fun StoreScreen(
    viewModel: GameViewModel,
    onBackPressed: () -> Unit,
    modifier: Modifier = Modifier
){
    val gameState = viewModel.gameState
    val currentCredits = gameState.credits
    val unlockedWeapons = gameState.unlockedWeapons

    var selectedWeapon by remember { mutableStateOf<Weapon?>(null) }
    var showInsufficientCredits by remember { mutableStateOf(false) }
    var insufficientAmount by remember { mutableIntStateOf(0) }
    var showPurchaseSuccess by remember { mutableStateOf<String?>(null) }

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
                .padding(20.dp)
        ) {
            // Header
            StoreHeader(
                currentCredits = currentCredits,
                onBackPressed = {
                    viewModel.onButtonClick()
                    onBackPressed()
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

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
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Weapons List
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(allWeapons) { weapon ->
                    val isUnlocked = unlockedWeapons.contains(weapon.type)

                    WeaponStoreCard(
                        weapon = weapon,
                        isUnlocked = isUnlocked,
                        isSelected = selectedWeapon == weapon,
                        canAfford = currentCredits >= weapon.cost,
                        onClick = {
                            viewModel.onButtonClick()
                            selectedWeapon = weapon

                            if (isUnlocked) {
                                // Already owned — equip it
                                viewModel.changeWeapon(weapon.type)
                            } else {
                                // Try to purchase
                                if (currentCredits >= weapon.cost) {
                                    val purchased = viewModel.purchaseWeapon(weapon)
                                    if (purchased) {
                                        showPurchaseSuccess = weapon.name
                                        selectedWeapon = null
                                    }
                                } else {
                                    insufficientAmount = weapon.cost
                                    showInsufficientCredits = true
                                }
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
                    .padding(32.dp)
            )
            LaunchedEffect(Unit) {
                delay(3000)
                showInsufficientCredits = false
            }
        }

        // Purchase Success Message
       showPurchaseSuccess?.let { weaponName ->
           Box(
               modifier = Modifier
                   .align(Alignment.Center)
                   .padding(32.dp)
                   .clip(RoundedCornerShape(16.dp))
                   .background(Color(0xFF1A3A1A))
                   .border(2.dp, Color.Green, RoundedCornerShape(16.dp))
                   .padding(24.dp),
               contentAlignment = Alignment.Center
           ) {
               Column(horizontalAlignment = Alignment.CenterHorizontally) {
                   Text("✅", fontSize = 40.sp)
                   Spacer(modifier = Modifier.height(8.dp))
                   Text(
                       text = "$weaponName Unlocked!",
                       fontSize = 20.sp,
                       fontWeight = FontWeight.Bold,
                       color = Color.Green
                   )
                   Text(
                       text = "Equipped automatically",
                       fontSize = 13.sp,
                       color = Color.Gray
                   )
               }
           }
           LaunchedEffect(weaponName) {
               delay(2500)
               showPurchaseSuccess = null
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
            shape = RoundedCornerShape(12.dp),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
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
    canAfford: Boolean,
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

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .clip(RoundedCornerShape(20.dp))
            .background(
                brush = if (isUnlocked) {
                    Brush.horizontalGradient(
                        colors = listOf(
                            weapon.bulletColor.copy(alpha = 0.15f),
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
                color = when {
                    isUnlocked -> weapon.bulletColor
                    !canAfford -> Color.Gray.copy(alpha = 0.4f)
                    else -> Color.Gray
                },
                shape = RoundedCornerShape(20.dp)
            )
            .clickable(onClick = onClick)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top: Icon and name
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
                // Weapon Icon
                Box(
                    modifier = Modifier
                        .size(72.dp)
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
                        fontSize = 36.sp,
                        modifier = Modifier.scale(if (isUnlocked) 1f else 0.6f)
                    )
                    // Lock overlay
                    if (!isUnlocked) {
                        Text(
                            text = "🔒",
                            fontSize = 28.sp,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }

                // Weapon Info
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = weapon.name,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isUnlocked) Color.White else Color.Gray
                    )

                    // Status Badge
                    if (isUnlocked) {
                        Box(
                            modifier = Modifier
                                .background(
                                    Color.Green.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(6.dp)
                                )
                                .padding(horizontal = 10.dp, vertical = 4.dp),
                        ) {
                            Text(
                                text = "✓ UNLOCKED",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Green
                            )
                        }
                    }
                }
            }

            // Middle: Description
            Text(
                text = weapon.description,
                fontSize = 13.sp,
                color = if (isUnlocked) Color.LightGray else Color.DarkGray,
                lineHeight = 18.sp,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth()
            )

            // Bottom: Stats or Price + button
            if (isUnlocked) {
                // Unlocked Badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatChip(
                        label = "DAMAGE",
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
                        value = "${weapon.ammoConsumption}/shot",
                        color = Color(0xFFBF40BF)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Equip button
                Button(
                    onClick = onClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = weapon.bulletColor
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Text(
                        text = "⚡ EQUIP WEAPON",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
            } else {
                // Price
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Price Display
                    Column {
                        Text(
                            text = "COST",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "💰",
                                fontSize = 24.sp
                            )
                            Text(
                                text = weapon.cost.toString(),
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (canAfford) Color(0xFFFFD700) else Color(0xFFFF4444)
                            )
                        }
                    }

                    // Purchase Button
                    Button(
                        onClick = onClick,
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 16.dp)
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (canAfford) Color(0xFFFFD700) else Color.DarkGray,
                            contentColor = if (canAfford) Color.Black else Color.Gray
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = if (canAfford) "🛒 PURCHASE" else "💸 TOO EXPENSIVE",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                        )
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
    Column(
        modifier = Modifier
            .background(
                color.copy(alpha = 0.15f),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            fontSize = 10.sp,
            color = Color.Gray,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
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

@Preview(showBackground = true, backgroundColor = 0xFF1A1A2E)
@Composable
fun StorePreviewPreview() {
    val sampleWeapons = listOf(
        Weapon(
            type = WeaponType.BASIC_LASER,
            name = "Laser",
            icon = "🔫",
            cost = 100,
            description = "Basic laser",
            damage = 1,
            fireRate = 300L,
            ammoConsumption = 1,
            bulletSpeed = 15f,
            bulletColor = Color.Cyan
        ),
        Weapon(
            type = WeaponType.MISSILE,
            name = "Missile",
            icon = "🚀",
            cost = 250,
            description = "Homing missile",
            damage = 3,
            fireRate = 800L,
            ammoConsumption = 2,
            bulletSpeed = 10f,
            bulletColor = Color.Red
        ),
        Weapon(
            type = WeaponType.PLASMA_CANNON,
            name = "Plasma",
            icon = "⚛️",
            cost = 500,
            description = "Plasma beam",
            damage = 2,
            fireRate = 100L,
            ammoConsumption = 2,
            bulletSpeed = 20f,
            bulletColor = Color.Green
        )
    )

    val unlockedWeapons = setOf(WeaponType.BASIC_LASER)

    StorePreview(
        weapons = sampleWeapons,
        unlockedWeapons = unlockedWeapons,
        onWeaponClick = {}
    )
}
