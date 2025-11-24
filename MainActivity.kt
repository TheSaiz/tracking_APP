package com.tracker.tracking

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import com.tracker.tracking.services.LocationService
import com.tracker.tracking.ui.auth.LoginScreen
import com.tracker.tracking.ui.home.AppNavigation
import com.tracker.tracking.ui.theme.TrackingTheme

class MainActivity : ComponentActivity() {

    private lateinit var locationService: LocationService

    // Launcher para solicitar permisos
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true -> {
                Toast.makeText(this, "✓ Permisos concedidos", Toast.LENGTH_SHORT).show()
            }
            else -> {
                Toast.makeText(this, "⚠️ Se necesitan permisos de ubicación", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializar servicio de ubicación
        locationService = LocationService(this)

        // Configurar ventana para diseño edge-to-edge
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.decorView.systemUiVisibility = (
                android.view.View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                        android.view.View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                )

        // Solicitar permisos si no están concedidos
        checkAndRequestPermissions()

        setContent {
            TrackingTheme {
                var isLoggedIn by remember { mutableStateOf(false) }

                if (isLoggedIn) {
                    AppNavigation(
                        onSendSignal = { responseCallback ->
                            // AQUÍ SE ENVÍA LA SEÑAL REAL AL SERVIDOR
                            locationService.sendLocationToServer(
                                connStatus = "Connected",
                                onSuccess = { response ->
                                    responseCallback(response)
                                },
                                onError = { error ->
                                    responseCallback("❌ Error: $error")
                                }
                            )
                        },
                        deviceUuid = getDeviceUuid(),
                        onNavigateToScanner = {
                            Toast.makeText(this, "Navegando a Escáner QR", Toast.LENGTH_SHORT).show()
                        },
                        onNavigateToSettings = {
                            // Aquí puedes implementar logout
                            // isLoggedIn = false
                        }
                    )
                } else {
                    LoginScreen(onLoginSuccess = { isLoggedIn = true })
                }
            }
        }
    }

    /**
     * Verifica y solicita permisos necesarios
     */
    private fun checkAndRequestPermissions() {
        val permissionsToRequest = mutableListOf<String>()

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionsToRequest.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionsToRequest.add(Manifest.permission.ACCESS_COARSE_LOCATION)
        }

        if (permissionsToRequest.isNotEmpty()) {
            requestPermissionLauncher.launch(permissionsToRequest.toTypedArray())
        }
    }

    /**
     * Obtiene el UUID único del dispositivo
     */
    private fun getDeviceUuid(): String {
        return Settings.Secure.getString(
            contentResolver,
            Settings.Secure.ANDROID_ID
        )
    }
}
