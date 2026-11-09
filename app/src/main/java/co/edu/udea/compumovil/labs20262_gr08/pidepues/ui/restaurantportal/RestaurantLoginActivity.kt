package co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.restaurantportal

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import co.edu.udea.compumovil.labs20262_gr08.pidepues.databinding.ActivityRestaurantLoginBinding
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.home.HomeActivity

class RestaurantLoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRestaurantLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRestaurantLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
    }

    private fun setupListeners() {
        binding.btnLoginRestaurant.setOnClickListener {
            val email = binding.etBusinessEmail.text.toString().trim()
            val password = binding.etBusinessPassword.text.toString().trim()

            if (email.isEmpty()) {
                binding.etBusinessEmail.error = "Ingresa el correo del negocio"
                binding.etBusinessEmail.requestFocus()
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                binding.etBusinessPassword.error = "Ingresa tu contraseña"
                binding.etBusinessPassword.requestFocus()
                return@setOnClickListener
            }

            Toast.makeText(this, "¡Bienvenido de nuevo a tu cocina!", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, RestaurantDashboardActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
            finish()
        }

        binding.btnRegisterRestaurant.setOnClickListener {
            val intent = Intent(this, RestaurantRegisterActivity::class.java)
            startActivity(intent)
        }

        binding.tvForgotPassword.setOnClickListener {
            Toast.makeText(this, "Se han enviado instrucciones a tu correo", Toast.LENGTH_LONG).show()
        }
    }
}
