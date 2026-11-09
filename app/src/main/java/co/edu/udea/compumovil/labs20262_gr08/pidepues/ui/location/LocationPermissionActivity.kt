package co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.location

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import co.edu.udea.compumovil.labs20262_gr08.pidepues.databinding.ActivityLocationPermissionBinding
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.home.HomeActivity

class LocationPermissionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLocationPermissionBinding

    // Callback para la solicitud de permisos del sistema Android
    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false

        if (fineGranted || coarseGranted) {
            navigateToHome()
        } else {
            Toast.makeText(this, "Se usará ubicación predeterminada", Toast.LENGTH_SHORT).show()
            navigateToHome()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLocationPermissionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        setupListeners()
    }

    private fun setupUI() {
        val subtitleText = "Activa tu ubicación para encontrar los mejores restaurantes cerca de ti, ordenados por <font color='#F04E23'><b>cercanía y calificación</b></font>."
        binding.tvLocationSubtitle.text = HtmlCompat.fromHtml(subtitleText, HtmlCompat.FROM_HTML_MODE_LEGACY)
    }

    private fun setupListeners() {
        binding.btnAllowLocation.setOnClickListener {
            checkAndRequestPermissions()
        }

        binding.tvManualAddress.setOnClickListener {
            navigateToHome()
        }
    }

    private fun checkAndRequestPermissions() {
        val hasFine = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val hasCoarse = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

        if (hasFine || hasCoarse) {
            navigateToHome()
        } else {
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private fun navigateToHome() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }
}