package co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.restaurantportal

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import co.edu.udea.compumovil.labs20262_gr08.pidepues.databinding.ActivityRestaurantRegisterBinding
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.home.HomeActivity

class RestaurantRegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRestaurantRegisterBinding
    private var isLogoUploaded = false
    private var isLocationSet = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRestaurantRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener { finish() }
        binding.btnBottomBack.setOnClickListener { finish() }

        binding.cardUploadLogo.setOnClickListener {
            isLogoUploaded = true
            binding.tvLogoStatus.text = "✓ Logo cargado correctamente (PNG)"
            Toast.makeText(this, "Logo seleccionado con éxito", Toast.LENGTH_SHORT).show()
        }

        binding.btnUseLocation.setOnClickListener {
            isLocationSet = true
            binding.tvLocationStatus.text = "📍 Ubicación fijada: Cra 43 #10-22, El Poblado, Medellín"
            Toast.makeText(this, "Ubicación fijada correctamente", Toast.LENGTH_SHORT).show()
        }

        binding.btnCreateMenu.setOnClickListener {
            val name = binding.etBusinessName.text.toString().trim()
            val phone = binding.etBusinessPhone.text.toString().trim()
            val email = binding.etBusinessRegEmail.text.toString().trim()
            val address = binding.etBusinessAddress.text.toString().trim()

            if (name.isEmpty()) {
                binding.etBusinessName.error = "Ingresa el nombre del negocio"
                binding.etBusinessName.requestFocus()
                return@setOnClickListener
            }

            if (phone.isEmpty()) {
                binding.etBusinessPhone.error = "Ingresa el teléfono"
                binding.etBusinessPhone.requestFocus()
                return@setOnClickListener
            }

            if (email.isEmpty()) {
                binding.etBusinessRegEmail.error = "Ingresa el correo"
                binding.etBusinessRegEmail.requestFocus()
                return@setOnClickListener
            }

            if (address.isEmpty()) {
                binding.etBusinessAddress.error = "Ingresa la dirección"
                binding.etBusinessAddress.requestFocus()
                return@setOnClickListener
            }

            Toast.makeText(this, "¡Negocio registrado con éxito! Configurando menú...", Toast.LENGTH_LONG).show()
            val intent = Intent(this, RestaurantDashboardActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
            finish()
        }
    }
}
