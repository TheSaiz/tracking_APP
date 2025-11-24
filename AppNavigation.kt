package com.tracker.tracking.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import kotlinx.coroutines.delay

/**
 * Interfaz principal con envío automático de señales cada 30 segundos
 */
@Composable
fun AppNavigation(
    onSendSignal: ((response: String) -> Unit) -> Unit,
    deviceUuid: String,
    onNavigateToScanner: () -> Unit,
    onNavigateToHome: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    userName: String = "Usuario Demo"
) {
    var statusMessage by remember { mutableStateOf("🔄 Iniciando...") }
    var isUserOnBreak by remember { mutableStateOf(false) }
    var lastUpdateTime by remember { mutableStateOf("") }

    // Efecto para enviar señales automáticamente cada 30 segundos
    LaunchedEffect(isUserOnBreak) {
        while (true) {
            val connStatus = if (isUserOnBreak) "Resting" else "Connected"

            // Enviar señal al servidor
            onSendSignal { response ->
                statusMessage = response
                lastUpdateTime = java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault())
                    .format(java.util.Date())
            }

            // Esperar 30 segundos antes del siguiente envío
            delay(30000L)
        }
    }

    Box(modifier = Modifier
        .fillMaxSize()
        .systemBarsPadding()) {

        // --- Contenido Principal (Centro de la pantalla) ---
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Indicador de estado visual
            Card(
                modifier = Modifier
                    .size(120.dp)
                    .padding(bottom = 24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isUserOnBreak)
                        MaterialTheme.colorScheme.errorContainer
                    else
                        MaterialTheme.colorScheme.primaryContainer
                ),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isUserOnBreak) Icons.Default.Pause else Icons.Default.GpsFixed,
                        contentDescription = null,
                        modifier = Modifier.size(60.dp),
                        tint = if (isUserOnBreak)
                            MaterialTheme.colorScheme.error
                        else
                            MaterialTheme.colorScheme.primary
                    )
                }
            }

            Text(
                text = "ID: ${deviceUuid.take(12)}...",
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            if (lastUpdateTime.isNotEmpty()) {
                Text(
                    text = "Última actualización: $lastUpdateTime",
                    fontSize = 11.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Estado del Sistema",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                    Text(
                        text = statusMessage,
                        textAlign = TextAlign.Center,
                        fontSize = 13.sp,
                        color = if (statusMessage.contains("✓"))
                            Color(0xFF4CAF50)
                        else if (statusMessage.contains("❌"))
                            Color(0xFFF44336)
                        else
                            Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Botón manual para enviar señal inmediatamente
            Button(
                onClick = {
                    val connStatus = if (isUserOnBreak) "Resting" else "Connected"
                    onSendSignal { response ->
                        statusMessage = response
                        lastUpdateTime = java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault())
                            .format(java.util.Date())
                    }
                },
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(50.dp)
            ) {
                Icon(Icons.Default.Send, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Enviar Señal Ahora")
            }
        }

        // --- Contenido Superior (Saludo y Botones de Turno) ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            Text(
                text = "Hola, $userName",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(if (isUserOnBreak) Color.Red else Color.Green)
                )
                Text(
                    text = if (isUserOnBreak) "En Descanso" else "Turno Activo",
                    color = if (isUserOnBreak) Color.Red else Color.Green,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { isUserOnBreak = !isUserOnBreak },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isUserOnBreak)
                            MaterialTheme.colorScheme.error
                        else
                            MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Icon(
                        if (isUserOnBreak) Icons.Default.PlayArrow else Icons.Default.Pause,
                        contentDescription = null
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(if (isUserOnBreak) "Reanudar" else "Descanso")
                }

                OutlinedButton(
                    onClick = {
                        statusMessage = "⚠️ Turno finalizado"
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Logout, contentDescription = null)
                    Spacer(Modifier.width(4.dp))
                    Text("Fin Turno")
                }
            }
        }

        // --- Barra de Navegación Inferior ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .background(MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp)),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onNavigateToHome,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                ) {
                    Icon(Icons.Default.Home, contentDescription = "Home", modifier = Modifier.size(24.dp))
                }

                Spacer(modifier = Modifier.weight(1f))

                IconButton(
                    onClick = onNavigateToSettings,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                ) {
                    Icon(Icons.Default.Settings, contentDescription = "Configuración", modifier = Modifier.size(24.dp))
                }
            }

            Card(
                shape = CircleShape,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = (-40).dp)
                    .size(80.dp)
                    .zIndex(1f),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.primary)
                        .clickable(onClick = onNavigateToScanner),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.QrCodeScanner,
                        contentDescription = "Escanear QR",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }
        }
    }
}
