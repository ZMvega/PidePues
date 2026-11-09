package co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.auth

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import co.edu.udea.compumovil.labs20262_gr08.pidepues.databinding.ActivityLoginBinding
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.location.LocationPermissionActivity
import co.edu.udea.compumovil.pidepues.util.applySystemBarsInsets

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginContent.applySystemBarsInsets()

        setupListeners()
    }

    private fun setupListeners() {
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (email.isEmpty()) {
                binding.etEmail.error = "Ingresa tu correo electrónico"
                binding.etEmail.requestFocus()
                return@setOnClickListener
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.etEmail.error = "Ingresa un correo válido (ej: usuario@email.com)"
                binding.etEmail.requestFocus()
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                binding.etPassword.error = "Ingresa tu contraseña"
                binding.etPassword.requestFocus()
                return@setOnClickListener
            }

            if (password.length < 6) {
                binding.etPassword.error = "La contraseña debe tener al menos 6 caracteres"
                binding.etPassword.requestFocus()
                return@setOnClickListener
            }

            Toast.makeText(this, "¡Bienvenido a PidePues!", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LocationPermissionActivity::class.java))
        }

        binding.tvForgot.setOnClickListener {
            Toast.makeText(this, "Se han enviado instrucciones de recuperación a tu correo", Toast.LENGTH_LONG).show()
        }

        binding.tvGoRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    companion object {
        fun newIntent(context: Context): Intent =
            Intent(context, LoginActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
    }
}
