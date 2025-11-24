package com.tracker.tracking.services

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.BatteryManager
import android.provider.Settings
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

/**
 * Servicio para obtener ubicación GPS y enviar datos al servidor
 */
class LocationService(private val context: Context) {

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    private val client = OkHttpClient()

    // IMPORTANTE: Cambia esta IP por la de tu servidor
    private val SERVER_URL = "http://10.116.220.239/recibir_senal.php"

    /**
     * Obtiene la ubicación actual y envía los datos al servidor
     */
    fun sendLocationToServer(
        connStatus: String = "Connected",
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        // Verificar permisos
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            onError("Permiso de ubicación no concedido")
            return
        }

        // Obtener última ubicación conocida
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                // Obtener datos del dispositivo
                val deviceUuid = getDeviceUuid()
                val batteryPercent = getBatteryLevel()
                val speedKph = (location.speed * 3.6).toFloat() // Convertir m/s a km/h

                // Crear JSON con los datos
                val jsonData = JSONObject().apply {
                    put("uuid", deviceUuid)
                    put("conn_status", connStatus)
                    put("latitude", location.latitude)
                    put("longitude", location.longitude)
                    put("speed_kph", speedKph)
                    put("battery_percent", batteryPercent)
                }

                // Enviar datos al servidor
                sendDataToServer(jsonData.toString(), onSuccess, onError)
            } else {
                // Si no hay ubicación, solicitar actualización
                requestNewLocation(connStatus, onSuccess, onError)
            }
        }.addOnFailureListener { exception ->
            onError("Error al obtener ubicación: ${exception.message}")
        }
    }

    /**
     * Solicita una nueva ubicación GPS (útil cuando lastLocation es null)
     */
    private fun requestNewLocation(
        connStatus: String,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            onError("Permiso de ubicación no concedido")
            return
        }

        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            5000L // Cada 5 segundos
        ).build()

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let { location ->
                    val deviceUuid = getDeviceUuid()
                    val batteryPercent = getBatteryLevel()
                    val speedKph = (location.speed * 3.6).toFloat()

                    val jsonData = JSONObject().apply {
                        put("uuid", deviceUuid)
                        put("conn_status", connStatus)
                        put("latitude", location.latitude)
                        put("longitude", location.longitude)
                        put("speed_kph", speedKph)
                        put("battery_percent", batteryPercent)
                    }

                    sendDataToServer(jsonData.toString(), onSuccess, onError)

                    // Detener actualizaciones después de obtener la primera ubicación
                    fusedLocationClient.removeLocationUpdates(this)
                }
            }
        }

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            context.mainLooper
        )
    }

    /**
     * Envía datos al servidor PHP usando OkHttp
     */
    private fun sendDataToServer(
        jsonData: String,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val mediaType = "application/json; charset=utf-8".toMediaType()
                val body = jsonData.toRequestBody(mediaType)

                val request = Request.Builder()
                    .url(SERVER_URL)
                    .post(body)
                    .build()

                client.newCall(request).execute().use { response ->
                    withContext(Dispatchers.Main) {
                        if (response.isSuccessful) {
                            val responseBody = response.body?.string() ?: ""
                            onSuccess("✓ Señal enviada correctamente\n$responseBody")
                        } else {
                            onError("Error del servidor: ${response.code}")
                        }
                    }
                }
            } catch (e: IOException) {
                withContext(Dispatchers.Main) {
                    onError("Error de conexión: ${e.message}")
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onError("Error: ${e.message}")
                }
            }
        }
    }

    /**
     * Obtiene el UUID único del dispositivo
     */
    private fun getDeviceUuid(): String {
        return Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ANDROID_ID
        )
    }

    /**
     * Obtiene el nivel de batería del dispositivo
     */
    private fun getBatteryLevel(): Int {
        val batteryManager = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
        return batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
    }
}
