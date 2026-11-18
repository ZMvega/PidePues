package co.edu.udea.compumovil.labs20262_gr08.pidepues.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.core.content.ContextCompat
import co.edu.udea.compumovil.labs20262_gr08.pidepues.model.UserLocation
import co.edu.udea.compumovil.labs20262_gr08.pidepues.util.haversineKm

/**
 * Obtiene la ubicación del dispositivo con el LocationManager del sistema
 * (sin dependencias extra). Si algo falla (sin permiso, GPS apagado, tiempo agotado)
 * devuelve null y la app usa [DEMO_CENTER].
 *
 * Mejora futura (semana 11): migrar a FusedLocationProviderClient (Google Play Services).
 */
object LocationProvider {

    /** Ubicación por defecto: El Poblado, Medellín (donde están los datos de ejemplo). */
    val DEMO_CENTER = UserLocation(latitude = 6.2088, longitude = -75.5671, isFromDevice = false)

    /**
     * Mientras los restaurantes sean datos de ejemplo de Medellín, una ubicación real
     * a más de este radio se ignora (si no, la lista saldría vacía en otra ciudad).
     */
    const val DEMO_RADIUS_KM = 60.0

    private const val TIMEOUT_MS = 8_000L

    fun hasPermission(context: Context): Boolean =
        ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED

    @SuppressLint("MissingPermission")
    fun getCurrentLocation(context: Context, onResult: (UserLocation?) -> Unit) {
        if (!hasPermission(context)) {
            onResult(null)
            return
        }
        val manager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
        if (manager == null) {
            onResult(null)
            return
        }

        val providers = runCatching { manager.getProviders(true) }.getOrDefault(emptyList())

        // 1) Última ubicación conocida (instantánea)
        val last = providers
            .mapNotNull { runCatching { manager.getLastKnownLocation(it) }.getOrNull() }
            .maxByOrNull { it.time }
        if (last != null) {
            onResult(last.toUserLocationOrNull())
            return
        }

        // 2) Si no hay, pedimos una sola actualización (con tiempo máximo)
        val provider = when {
            LocationManager.NETWORK_PROVIDER in providers -> LocationManager.NETWORK_PROVIDER
            LocationManager.GPS_PROVIDER in providers -> LocationManager.GPS_PROVIDER
            else -> null
        }
        if (provider == null) {
            onResult(null)
            return
        }
        SingleUpdate(manager, onResult).start(provider)
    }

    private fun Location.toUserLocationOrNull(): UserLocation? {
        val km = haversineKm(latitude, longitude, DEMO_CENTER.latitude, DEMO_CENTER.longitude)
        return if (km <= DEMO_RADIUS_KM) UserLocation(latitude, longitude, isFromDevice = true) else null
    }

    private class SingleUpdate(
        private val manager: LocationManager,
        private val onResult: (UserLocation?) -> Unit
    ) : LocationListener {

        private val handler = Handler(Looper.getMainLooper())
        private var done = false

        @SuppressLint("MissingPermission")
        @Suppress("DEPRECATION")
        fun start(provider: String) {
            try {
                manager.requestSingleUpdate(provider, this, Looper.getMainLooper())
                handler.postDelayed({ finish(null) }, TIMEOUT_MS)
            } catch (e: SecurityException) {
                finish(null)
            }
        }

        private fun finish(result: UserLocation?) {
            if (done) return
            done = true
            handler.removeCallbacksAndMessages(null)
            runCatching { manager.removeUpdates(this) }
            onResult(result)
        }

        override fun onLocationChanged(location: Location) {
            finish(location.toUserLocationOrNull())
        }

        // En Android 9 o menor estos métodos son abstractos: hay que implementarlos.
        @Deprecated("Deprecated in Java")
        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) = Unit
        override fun onProviderEnabled(provider: String) = Unit
        override fun onProviderDisabled(provider: String) = Unit
    }
}
