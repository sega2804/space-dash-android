package com.crypticsamsara.spacedash.ui.screens

import android.view.RoundedCorner
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.crypticsamsara.spacedash.ui.theme.NeonCyan
import com.crypticsamsara.spacedash.ui.theme.NeonPurple
import com.crypticsamsara.spacedash.ui.theme.SpaceBlack
import com.crypticsamsara.spacedash.ui.theme.StarWhite

@Composable
fun PauseMenuOverlay(
    onResume: () -> Unit,
    onRestart: () -> Unit,
    onMainMenu: () -> Unit,
    onButtonClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.85f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment =  Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(32.dp)
        ) {
            // Pause title
            Text(
                text = "PAUSED",
                color = NeonCyan,
                fontSize = 56.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Resume Button
            Button(
                onClick = {
                    onButtonClick()
                    onResume()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = NeonCyan
                ),
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(60.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Resume",
                    tint = SpaceBlack,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "RESUME",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = SpaceBlack
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Restart Button
            Button(
                onClick = {
                    onButtonClick()
                    onRestart()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = StarWhite.copy(alpha = 0.2f)
                ),
                modifier = Modifier
                    .fillMaxSize(0.8f)
                    .height(60.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Restart",
                        tint = StarWhite,
                        modifier = Modifier.size(28.dp)
                    )
            Spacer( modifier = Modifier.width(12.dp))
                Text(
                    text = "RESTART",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = StarWhite
                )
        }

            Spacer(modifier = Modifier.height(16.dp))

            // Main menu button
            Button(
                onClick = {
                    onButtonClick()
                    onMainMenu
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = NeonPurple.copy(alpha = 0.3f)
                ),
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(60.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Main Menu",
                    tint = NeonPurple,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "MAIN MENU",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeonPurple
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Hint text
            Text(
                text = "Tap pause to resume",
                color = StarWhite.copy(alpha = 0.5f),
                fontSize = 12.sp
            )
        }
    }
}