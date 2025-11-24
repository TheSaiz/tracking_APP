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

/**
 * Interfaz con barra de navegación inferior personalizada, saludo de usuario y botones de turno.
 */
@Composable
fun SignalSenderScreen(
    onSendSignal: ((response: String) -> Unit) -> Unit,
    deviceUuid: String,
    onNavigateToScanner: () -> Unit,
    onNavigateToHome: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    // Nuevo parámetro requerido para el nombre de usuario
    userName: String
) {
    var statusMessage by remember { mutableStateOf("Esperando acción...") }
    // Estado para controlar si el usuario está en descanso
    var isUserOnBreak by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {

        // --- Contenido Principal (Centro de la pantalla) ---
        Column(
            modifier = Modifier
                .fillMaxSize()
                // Asegura que el contenido superior e inferior respeten las barras
                .padding(top = 130.dp) // Espacio para el saludo y botones superiores
                .padding(bottom = 100.dp) // Espacio para la barra inferior
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // ... (Tus elementos centrales: botón GPS, estado y UUID) ...
            Text(
                text = "ID del Dispositivo: $deviceUuid",
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            Button(
                onClick = { onSendSignal { response -> statusMessage = response } },
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Text("Enviar Ubicación Actual")
            }

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Text(
                    text = "Estado: $statusMessage",
                    modifier = Modifier.padding(16.dp),
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
        }

        // --- Contenido Superior (Saludo y Botones de Turno) ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .background(MaterialTheme.colorScheme.background) // Fondo para asegurar visibilidad
                .padding(16.dp)
        ) {
            Text(
                text = "Hola, $userName",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = if (isUserOnBreak) "En Descanso" else "Turno Activo",
                color = if (isUserOnBreak) Color.Red else Color.Green,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Botón Descanso
                Button(
                    onClick = { isUserOnBreak = !isUserOnBreak },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isUserOnBreak) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Icon(if (isUserOnBreak) Icons.Default.PlayArrow else Icons.Default.Pause, contentDescription = null)
                    Spacer(Modifier.width(4.dp))
                    Text(if (isUserOnBreak) "Reanudar" else "Descanso")
                }

                // Botón Finalizar Turno
                OutlinedButton(
                    onClick = {
                        // Lógica para finalizar el turno (navegación, API call, etc.)
                        statusMessage = "Turno finalizado (simulado)"
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Logout, contentDescription = null)
                    Spacer(Modifier.width(4.dp))
                    Text("Fin Turno")
                }
            }
        }


        // --- Barra de Navegación Inferior Personalizada (Mismo código anterior) ---
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
