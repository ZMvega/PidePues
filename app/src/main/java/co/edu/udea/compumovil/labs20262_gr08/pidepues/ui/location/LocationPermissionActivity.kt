package co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.location


import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import co.edu.udea.compumovil.labs20262_gr08.pidepues.R
import co.edu.udea.compumovil.labs20262_gr08.pidepues.databinding.ActivityLocationPermissionBinding
import co.edu.udea.compumovil.labs20262_gr08.pidepues.location.LocationProvider
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.main.MainActivity
import co.edu.udea.compumovil.labs20262_gr08.pidepues.util.applySystemBarsInsets

/**
 * Pantalla "¿Dónde estás?": pide el permiso de ubicación antes de entrar a la app.
 * Si el permiso ya estaba concedido, salta directo a la pantalla principal.
 */
class LocationPermissionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLocationPermissionBinding

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        val granted = result[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                result[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) {
            openMain()
        } else {
            Toast.makeText(this, R.string.location_denied_hint, Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        if (LocationProvider.hasPermission(this)) {
            openMain()
            return
        }

        binding = ActivityLocationPermissionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.locationContent.applySystemBarsInsets()

        // "cercanía y calificación" resaltado
        binding.tvDescription.text = HtmlCompat.fromHtml(
            getString(R.string.location_description),
            HtmlCompat.FROM_HTML_MODE_COMPACT
        )

        binding.btnAllow.setOnClickListener {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
        // Sin permiso se usa la ubicacion por defecto = El Poblado todo: mas adelante: buscador de direcciones.
        binding.tvManual.setOnClickListener {
            Toast.makeText(this, R.string.location_manual_hint, Toast.LENGTH_LONG).show()
            openMain()
        }
    }

    private fun openMain() {
        startActivity(MainActivity.newIntent(this))
    }

    companion object {
        fun newIntent(context: Context): Intent = Intent(context, LocationPermissionActivity::class.java)
    }
}
